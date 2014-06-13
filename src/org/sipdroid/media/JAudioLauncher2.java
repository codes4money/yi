/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.sipdroid.media;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.sipdroid.codecs.Codecs;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Settings;
import org.sipdroid.sipua.ui.Sipdroid;
import org.zoolu.sip.provider.SipStack;
import org.zoolu.tools.Log;
import org.zoolu.tools.LogLevel;

import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/** Audio launcher based on javax.sound  */
public class JAudioLauncher2 implements MediaLauncher
{  
   /** Event logger. */
   Log log=null;

   /** Sample rate [bytes] */
   int sample_rate=8000;
   /** Sample size [bytes] */
   int sample_size=1;
   /** Frame size [bytes] */
   int frame_size=160;
   /** Frame rate [frames per second] */
   int frame_rate=50; //=sample_rate/(frame_size/sample_size);
   boolean signed=false; 
   boolean big_endian=false;

   //String filename="audio.wav"; 

   /** Test tone */
   public static final String TONE="TONE";

   /** Test tone frequency [Hz] */
   public static int tone_freq=100;
   /** Test tone ampliture (from 0.0 to 1.0) */
   public static double tone_amp=1.0;

   /** Runtime media process */
   Process media_process=null;
   
   int dir; // duplex= 0, recv-only= -1, send-only= +1; 
   String SocketAdd="";
   int SocketPort=0;
   DatagramSocket socket=null;
   RtpStreamSender2 sender=null;
   RtpStreamReceiver2 receiver=null;
   Codecs.Map payload_type2=null;
   Timer timer = new Timer();
   
   //change DTMF
   boolean useDTMF = false;  // zero means not use outband DTMF
   
   /** Costructs the audio launcher */
   public JAudioLauncher2(RtpStreamSender2 rtp_sender, RtpStreamReceiver2 rtp_receiver, Log logger)
   {  log=logger;
      sender=rtp_sender;
      receiver=rtp_receiver;
   }
   
   /** Costructs the audio launcher */
   public void initChat(int local_port, String remote_addr, int remote_port, int direction, String audiofile_in, String audiofile_out, int sample_rate, int sample_size, int frame_size, Log logger, Codecs.Map payload_type, int dtmf_pt,int p)
   {  
	   payload_type2=payload_type;
	   log=logger;
      frame_rate=sample_rate/frame_size;
      useDTMF = (dtmf_pt != 0);
      SocketAdd=remote_addr;
      SocketPort=remote_port;
      
      try
      {
    	 CallRecorder call_recorder = null;
    	 if (PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_CALLRECORD,
					org.sipdroid.sipua.ui.Settings.DEFAULT_CALLRECORD))
    		 call_recorder = new CallRecorder(null,payload_type.codec.samp_rate()); // Autogenerate filename from date. 
    	
    	 
    	 socket=new DatagramSocket(local_port);
         dir=direction;

  	   try {
  		    byte[] test=new byte[1];//����
  			udpSend(socket, test, test.length,SocketAdd,SocketPort);
  		   } catch (Exception e) {
  		}
  	   
    	 if(dir>0)//�ص�����
    	 {
    		 if( receiver!=null)
    		 {
    			 receiver.stop();
    			 receiver=null;
    		 }
    	 }
    	 if(dir<0)//�ص��
    	 {
    		 if(sender!=null)
    		 {
    			 sender.stop();
    			 sender=null;
    		 }
    	 }
    	 
         // sender
         if (dir>=0)
         {
        	 if(sender==null)
        	 {
        	   printLog("new audio sender to "+remote_addr+":"+remote_port,LogLevel.MEDIUM);
            //audio_input=new AudioInput();
               sender=new RtpStreamSender2(true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port,call_recorder);
               sender.setSyncAdj(2);
               sender.setDTMFpayloadType(dtmf_pt);
               sender.start();
        	 }
         }
         // receiver
         if (dir<=0)
         {
        	 if(receiver==null)
        	 {
               printLog("new audio receiver on "+local_port,LogLevel.MEDIUM);
               receiver=new RtpStreamReceiver2(socket,payload_type,call_recorder);
               receiver.start();
        	 }
         }
         InitUpdateTime();
      }
      catch (Exception e) {  printException(e,LogLevel.HIGH);  }
   }
   public JAudioLauncher2(int local_port, String remote_addr, int remote_port, int direction, String audiofile_in, String audiofile_out, int sample_rate, int sample_size, int frame_size, Log logger, Codecs.Map payload_type, int dtmf_pt,int p)
   {  
	   payload_type2=payload_type;
	   log=logger;
      frame_rate=sample_rate/frame_size;
      useDTMF = (dtmf_pt != 0);
      SocketAdd=remote_addr;
      SocketPort=remote_port;
      
      try
      {
    	 CallRecorder call_recorder = null;
    	 if (PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_CALLRECORD,
					org.sipdroid.sipua.ui.Settings.DEFAULT_CALLRECORD))
    		 call_recorder = new CallRecorder(null,payload_type.codec.samp_rate()); // Autogenerate filename from date. 
    	
    	 
    	 socket=new DatagramSocket(local_port);
         dir=direction;

  	   try {
  		    byte[] test=new byte[1];//����
  			udpSend(socket, test, test.length,SocketAdd,SocketPort);
  		   } catch (Exception e) {
  		}
  	   
    	 
         // sender
         if (dir>=0)
         {  printLog("new audio sender to "+remote_addr+":"+remote_port,LogLevel.MEDIUM);
            //audio_input=new AudioInput();
            sender=new RtpStreamSender2(true,payload_type,frame_rate,frame_size,socket,remote_addr,remote_port,call_recorder);
            sender.setSyncAdj(2);
            sender.setDTMFpayloadType(dtmf_pt);
         }
         // receiver
         if (dir<=0)
         {
            printLog("new audio receiver on "+local_port,LogLevel.MEDIUM);
            receiver=new RtpStreamReceiver2(socket,payload_type,call_recorder);
         }
         InitUpdateTime();
      }
      catch (Exception e) {  printException(e,LogLevel.HIGH);  }
   }
   
   private void InitUpdateTime()
   {
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if(socket!=null)
				{
				 try {
			  		    byte[] test=new byte[3];//����
			  			udpSend(socket, test, test.length,SocketAdd,SocketPort);
			  		   } catch (Exception e) {
			  		}
				}
				if(receiver==null)
				{
					timer.cancel();
				}
			}
		},20000,10000);
	}
   
   private void udpSend(DatagramSocket udpSocket, byte[] buffer, int sendLength,String sIP,int sPort) {
		try {
			InetAddress ip = InetAddress.getByName(sIP);
			int port = sPort;

			byte[] sendBuffer = new byte[sendLength];
			System.arraycopy(buffer, 0, sendBuffer, 0, sendLength);
			DatagramPacket packet = new DatagramPacket(sendBuffer, sendLength);
			packet.setAddress(ip);
			packet.setPort(port);
			//packet.setPort(port+1);
			udpSocket.send(packet);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
   /** Starts media application */
   public boolean startMedia()
   {  printLog("starting java audio..",LogLevel.HIGH);

   try
   {
      if (sender!=null)
      {  printLog("start sending",LogLevel.LOW);
         sender.start();
      }
      if (receiver!=null)
      {  printLog("start receiving",LogLevel.LOW);
         receiver.start();
      }
   }catch(Exception e){}
      return true;      
   }
   /** Stops media application */
   public boolean startSender()
   { 
	 if (sender==null)
	 {
		 try
		 {
	  CallRecorder call_recorder = null;
  	  if (PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_CALLRECORD,
					org.sipdroid.sipua.ui.Settings.DEFAULT_CALLRECORD))
  		 call_recorder = new CallRecorder(null,payload_type2.codec.samp_rate()); 
  	
	   sender=new RtpStreamSender2(true,payload_type2,frame_rate,frame_size,socket,SocketAdd,SocketPort,call_recorder);
       sender.setSyncAdj(2);
       sender.setDTMFpayloadType(0);
		 }catch(Exception e){}
	 }
     return true;
   }
   /** Stops media application */
   public boolean stopSender()
   { 
	   try {
		    timer.cancel();
 		   } catch (Exception e) {}
	  try
	  {
	  printLog("halting java audio..",LogLevel.HIGH);    
      if (sender!=null)
      {   sender.halt();
          sender=null;
         printLog("sender halted",LogLevel.LOW);
      }  
   }catch(Exception e){}
      return true;
   }
   /** Stops media application */
   public boolean stopMedia()
   { 
	   try {
		    timer.cancel();
 		   } catch (Exception e) {}
	   
	  try
	  {
	   printLog("halting java audio..",LogLevel.HIGH);    
      if (sender!=null)
      {   sender.halt();
          sender=null;
         printLog("sender halted",LogLevel.LOW);
      }      
      if (receiver!=null)
      { 
    	  receiver.halt();
    	  receiver=null;
         printLog("receiver halted",LogLevel.LOW);
      }      
      if (socket != null)
    	  socket.close();
      
   }catch(Exception e){}
      return true;
   }

   /** Stops media application */
   public boolean stopMedia2()
   {
	   try {
	    byte[] test=new byte[2];//�˳�
		udpSend(socket, test, test.length,SocketAdd,SocketPort);
	   } catch (Exception e) {
			e.printStackTrace();
		}
		try
		{
	   printLog("halting java audio..",LogLevel.HIGH);    
      if (sender!=null)
      {  sender.halt(); sender=null;
         printLog("sender halted",LogLevel.LOW);
      }      
      if (receiver!=null)
      {  receiver.halt(); receiver=null;
         printLog("receiver halted",LogLevel.LOW);
      }      
      if (socket != null)
    	  socket.close();
		 }catch(Exception e){}
      return true;
   }
   public boolean muteMedia()
   {
	   if (sender != null)
		   return sender.mute();
	   return false;
   }
   
   public int speakerMedia(int mode)
   {
	   if (receiver != null)
		   return receiver.speaker(mode);
	   return 0;
   }

   public void bluetoothMedia()
   {
	   if (receiver != null)
		   receiver.bluetooth();
   }

   //change DTMF
	/** Send outband DTMF packets **/
  public boolean sendDTMF(char c){
	    if (! useDTMF) return false;
	    sender.sendDTMF(c);
	    return true;
  }
  
   // ****************************** Logs *****************************

   /** Adds a new string to the default Log */
   private void printLog(String str)
   {  printLog(str,LogLevel.HIGH);
   }

   /** Adds a new string to the default Log */
   private void printLog(String str, int level)
   {
	  if (Sipdroid.release) return;
	  if (log!=null) log.println("AudioLauncher: "+str,level+SipStack.LOG_LEVEL_UA);  
      if (level<=LogLevel.HIGH) System.out.println("AudioLauncher: "+str);
   }

   /** Adds the Exception message to the default Log */
   void printException(Exception e,int level)
   { 
	  if (Sipdroid.release) return;
	  if (log!=null) log.printException(e,level+SipStack.LOG_LEVEL_UA);
      if (level<=LogLevel.HIGH) e.printStackTrace();
   }

}
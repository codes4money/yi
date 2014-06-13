package org.sipdroid.sipua.phone;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Settings;
import com.android.internal.telephony.ITelephony;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class MyPhoneBroadcastReceiver extends BroadcastReceiver {

	private final static String TAG ="MyPhone";
	public Context acontext;
    Handler mHandler=new Handler();
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i(TAG, "[Broadcast]"+action);
		
		//����绰
		if(action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
			Log.i(TAG, "[Broadcast]PHONE_STATE");
			doReceivePhone(context,intent);
		}
	}
	
	/**
	 * ����绰�㲥.
	 * @param context
	 * @param intent
	 */
	public void doReceivePhone(Context context, Intent intent) {
		String phoneNumber = intent.getStringExtra(
TelephonyManager.EXTRA_INCOMING_NUMBER);
		TelephonyManager telephony = (TelephonyManager)context.getSystemService(
Context.TELEPHONY_SERVICE);
		int state = telephony.getCallState();
		
		switch(state){
		case TelephonyManager.CALL_STATE_RINGING:
			Log.i(TAG, "[Broadcast]�ȴ�ӵ绰="+phoneNumber);
				//if(phoneNumber.equals("Restricted"))
				//{
			    try
			     {
			        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");
			        String time2=sdf.format(new java.util.Date());  
			       /// if(phoneNumber.equals(Settings.callBackNum))
			      //  {
			         String time1= PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString("sip_Answer_time","");
			         if(time1.equals(time2))
			         {
						  Editor edit = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).edit();
						  edit.putString("sip_Answer_time","");
						  edit.commit();
					  handleTimeToAnswerIncomingCall(context);
			         }
			      //  }
			    }
	    		catch (Exception e)
	    		{}
					//answer(context);//�Զ�����
				//}
			break;
		case TelephonyManager.CALL_STATE_IDLE:
			Log.i(TAG, "[Broadcast]�绰�Ҷ�="+phoneNumber);
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			Log.i(TAG, "[Broadcast]ͨ����="+phoneNumber);
			break;
		}
	}
	
	
	void handleTimeToAnswerIncomingCall(Context context)
	  {
	    AudioManager localAudioManager = (AudioManager)context.getSystemService("audio");
	    if (localAudioManager != null)
	    {
	      int i = localAudioManager.getRingerMode();
	      answer(context);
	      localAudioManager.setRingerMode(i);
	    }
	    answer(context);
	  }
	
	public  void answerRingingCall(Context mcontext)
	{
		
	acontext=mcontext;
	
	sendKeycode(0, 79);
	sendKeycode(1, 79);
		/*
	 acontext=mcontext;
	 Intent localIntent1 = new Intent("android.intent.action.HEADSET_PLUG");
     localIntent1.addFlags(1073741824);
     localIntent1.putExtra("state", 1);
     localIntent1.putExtra("microphone", 1);
     localIntent1.putExtra("name", "Headset");
     acontext.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
     sendKeycode(0, 79);
     sendKeycode(1, 79);
     Intent localIntent2 = new Intent("android.intent.action.HEADSET_PLUG");
     localIntent2.addFlags(1073741824);
     localIntent2.putExtra("state", 0);
     localIntent2.putExtra("microphone", 1);
     localIntent2.putExtra("name", "Headset");
     acontext.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
     */
     
	 mHandler.postDelayed(new Runnable()
     {
       public void run()
       {
    		try
    		{
    		   if (getCallState() == 1)
               {
    	        Intent localIntent1 = new Intent("android.intent.action.HEADSET_PLUG");
    	        localIntent1.addFlags(1073741824);
    	        localIntent1.putExtra("state", 1);
    	        localIntent1.putExtra("microphone", 1);
    	        localIntent1.putExtra("name", "Headset");
    	        acontext.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
    	        sendKeycode(0, 79);
    	        sendKeycode(1, 79);
    	        Intent localIntent2 = new Intent("android.intent.action.HEADSET_PLUG");
    	        localIntent2.addFlags(1073741824);
    	        localIntent2.putExtra("state", 0);
    	        localIntent2.putExtra("microphone", 1);
    	        localIntent2.putExtra("name", "Headset");
    	        acontext.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
              }
    		}
    		catch (Exception e)
    		{
    		}
       }
     }
     , 1000L);
	 
	}
	
    public void answer(Context mcontext)
    {
      try
      {
        tryAnswer(mcontext);

      }
      catch (Exception localException)
      {
    	 Log.i("call", "answer is Exception");
         try
         {
        	  answerRingingCall(mcontext);
         }
          catch (Exception localRuntimeException)
          {
            Log.i("call", "answerPhoneHeadsethook is Exception");
          }
      }
    }
    
    void tryAnswer(Context mcontext) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
    	      TelephonyManager localTelephonyManager = (TelephonyManager)mcontext.getSystemService("phone");
    	      Method localMethod1 = localTelephonyManager.getClass().getDeclaredMethod("getITelephony", new Class[0]);
    	      localMethod1.setAccessible(true);
    	      Object localObject = localMethod1.invoke(localTelephonyManager, new Object[0]);
    	      Method localMethod2 = localObject.getClass().getMethod("answerRingingCall", new Class[0]);
    	      localMethod2.setAccessible(true);
    	      localMethod2.invoke(localObject, new Object[0]);
    }
    
    
    private int getCallState()
    {
      return ((TelephonyManager)acontext.getSystemService("phone")).getCallState();
    }

    private void sendKeycode(int paramInt1, int paramInt2)
    {
      Intent localIntent = new Intent("android.intent.action.MEDIA_BUTTON");
      localIntent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(SystemClock.uptimeMillis(), 0L, paramInt1, paramInt2, 0, 0, 0, 226, 8));
      acontext.sendOrderedBroadcast(localIntent, "android.permission.CALL_PRIVILEGED");
    }

}
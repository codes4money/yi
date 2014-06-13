package com.studio.b56.im;
import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.VCard;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.ui.group.grouplistitem;
import com.studio.b56.im.app.vo.RegisterVo;
import com.studio.b56.im.service.XmppManager;
import com.studio.b56.im.vo.UserInfoVo;

public class CommFun {
	
	public static void regActivityFun(String username,String password)
	{
     	String val=CommFun.posturl(Constants.ApiUrl.SIP_URL+"getcustomer.jsp?account="+username);
     	
		if(val.trim().contains("10007"))
		{
			
		CommFun.posturl(Constants.ApiUrl.SIP_URL+"setcustomer.jsp?account="+username);
		
		CommFun.posturl(Constants.ApiUrl.SIP_URL+"setactivephonecard.jsp?pin="+username+"&password="+password+"&account="+username+"&operationType=0");
		
		CommFun.posturl(Constants.ApiUrl.SIP_URL+"setbindede164.jsp?e164="+username+"&activePhoneCard="+username+"&operationType=0");
		
		CommFun.posturl(Constants.ApiUrl.SIP_URL+"setphone.jsp?e164="+username+"&password="+password+"&displayNumber="+username+"&account="+username+"&type=0");
	
		}
		//nextActivity(registerVo1);
		
	}
	
	   public static String GetUserSIPFeeById(String jid)
	    {
	    	String val=posturl(Constants.ApiUrl.SIP_URL+"getcustomer.jsp?name="+jid.split("@")[0]);
	    	if(!val.trim().contains("10007"))
	    	{
	    	   return val.split("\\|")[1].split(";")[2];
	    	}
	    	else
	    	{
	    		return "";
	    	}
	    }
	   
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {  

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),Bitmap.Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = pixels;  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;  
    }

    //发送管理命令
    public static void udpSend(int local_port, byte[] buffer, int sendLength,String sIP,int sPort) {
 		try {
 			DatagramSocket udpSocket=new DatagramSocket(local_port);
 			InetAddress ip = InetAddress.getByName(sIP);
 			int port = sPort;

 			byte[] sendBuffer = new byte[sendLength];
 			System.arraycopy(buffer, 0, sendBuffer, 0, sendLength);
 			DatagramPacket packet = new DatagramPacket(sendBuffer, sendLength);
 			packet.setAddress(ip);
 			packet.setPort(port);
 			udpSocket.send(packet);
 			udpSocket.send(packet);
 			
 			udpSocket.close();
 		} catch (IOException e) {
 		}
 	}
    
	 public static void UpdateUserOnline(final String username)
	    {
	    	new Thread() {
				@Override
				public void run() {
	    	    // posturl("http://sipapi.xunliao.im/updateonline.aspx?username="+username);
				}
			}.start();
	    }
	 
	public static String Md5(String plainText ) { 
		try { 
		MessageDigest md = MessageDigest.getInstance("MD5"); 
		md.update(plainText.getBytes()); 
		byte b[] = md.digest(); 

		int i; 

		StringBuffer buf = new StringBuffer(""); 
		for (int offset = 0; offset < b.length; offset++) { 
		i = b[offset]; 
		if(i<0) i+= 256; 
		if(i<16) 
		buf.append("0"); 
		buf.append(Integer.toHexString(i)); 
		} 

       return  buf.toString().substring(8,24);

		} catch (NoSuchAlgorithmException e) { 
		return "";
		} 
		} 

	  public static String converterToFirstSpell(String chines){             
	        String pinyinName = "";      
	       char[] nameChar = chines.toCharArray();      
	        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();      
	        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);      
	        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);      
	       for (int i = 0; i < nameChar.length; i++) {      
	           if (nameChar[i] > 128) {      
	               try {      
	                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);      
	                } catch (Exception e) { pinyinName +="#";   }      
	            }else{      
	                pinyinName += nameChar[i];      
	            }
	           break;
	        }
	       if(!"abcdefghijklmnopqrstuvwxyz".contains(pinyinName.toLowerCase()))
	       {
	    	   return "#";
	       }
	       return pinyinName;      
	    } 
	  
	  public static  String posturl(ArrayList<NameValuePair> nameValuePairs,String url){
	        String result = "";
	        String tmp= "";
	        InputStream is = null;
	        try{
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost(url);
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            is = entity.getContent();
	        }catch(Exception e){
	            return "";
	        }
	     
	        try{
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	            is.close();
	     
	            tmp=sb.toString().trim();
	        }catch(Exception e){
	            return "";
	        }
	        result=tmp;
	        /*
	        try{
	            JSONArray jArray = new JSONArray(tmp);
	            for(int i=0;i<jArray.length();i++){
	                JSONObject json_data = jArray.getJSONObject(i);
	                Iterator<?> keys=json_data.keys();
	                while(keys.hasNext()){
	                    result += json_data.getString(keys.next().toString());
	                }
	            }
	        }catch(JSONException e){
	            return "The URL you post is wrong!";
	        }*/
	     
	        return result;
	    }
	     
	    //第二种
	    /**获取参数指定的网页代码，将其返回给调用者，由调用者对其解析
	     * 返回String
	     * Chen.Zhidong
	     * 2011-02-15*/
	    public static  String posturl(String url)
	    {
	        InputStream is = null;
	        String result = "";
	     
	        try{
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost(url);
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            is = entity.getContent();
	        }catch(Exception e){
	            return "";
	        }
	     
	        try{
	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
	            StringBuilder sb = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	            is.close();
	     
	            result=sb.toString();
	        }catch(Exception e){
	            return "";
	        }
	     
	        return result;
	    }
	public static MultiUserChat joinMultiUserChat(XMPPConnection xmppconn,String user, String roomsName, String password) {  
		 
	    if (xmppconn == null)  
	        return null;  
	    try {  
	        MultiUserChat muc = new MultiUserChat(xmppconn, roomsName);  
	        DiscussionHistory history = new DiscussionHistory();  
	        history.setMaxChars(50); 
	       // muc.join(user, password, history, SmackConfiguration.getPacketReplyTimeout()); 
	        
	       muc.join(user);
	        
	        Log.i("MultiUserChat", "会议室【"+roomsName+"】加入成功........");  
	 
	        return muc;  
	 
	    } catch (XMPPException e) {  
	 
	        e.printStackTrace();  
	 
	        Log.i("MultiUserChat", "会议室【"+roomsName+"】加入失败........");  
	 
	        return null;  
	    }  
	} 
	 public static Boolean AddGroup(XMPPConnection xmppconn,String RoomName,String MyUserName,String des)
		{
			MultiUserChat muc= new MultiUserChat(xmppconn, RoomName+"@conference."+xmppconn.getServiceName());        //第二个参数是房间的Jid
			try {
	                muc.create(RoomName);      //房间名称
	              
	        } catch (XMPPException e) {
	        	Log.v("创建错误", "=="+e.getMessage());
	        	return false;
	        }    
	        try {
	              // 获得聊天室的配置表单
	              Form form = muc.getConfigurationForm();
	              // 根据原始表单创建一个要提交的新表单。
	              Form submitForm = form.createAnswerForm();
	              // 向要提交的表单添加默认答复
	              for (Iterator<FormField> fields = form.getFields(); fields.hasNext();) {  
	             try {
	            	 
	                  FormField field = (FormField) fields.next();  
	       
	                  if (!FormField.TYPE_HIDDEN.equals(field.getType())  && field.getVariable() != null) {  
	                      // 设置默认值作为答复  
	                      submitForm.setDefaultAnswer(field.getVariable());  
	                  } 
	                  
	            	  } catch (Exception e) {Log.v("创建错误", "=="+e.getMessage());
	                  }
	              }  
	              
	                      //设置聊天室的新拥有者    
	            List<String> owners = new ArrayList<String>();   
	            owners.add(MyUserName);   
	            submitForm.setAnswer("muc#roomconfig_roomowners", owners);   
	                 // 设置聊天室是持久聊天室，即将要被保存下来  
	            submitForm.setAnswer("muc#roomconfig_persistentroom", true);  

	            // 房间仅对成员开放  
	            submitForm.setAnswer("muc#roomconfig_membersonly", true);  
	  
	            //设置描述    
	            submitForm.setAnswer("muc#roomconfig_roomdesc",des);

	          //  // 允许占有者邀请其他人  
	            submitForm.setAnswer("muc#roomconfig_allowinvites", true);  
	            // 能够发现占有者真实 JID 的角色  
	          //  submitForm.setAnswer("muc#roomconfig_whois", "anyone");  
	            // 登录房间对话  
	            submitForm.setAnswer("muc#roomconfig_enablelogging", true); 
	            // 仅允许注册的昵 称登录  
	            submitForm.setAnswer("x-muc#roomconfig_reservednick", false);  
	            // 允许使用者修改昵称  
	            submitForm.setAnswer("x-muc#roomconfig_canchangenick", true);  
	            submitForm.setAnswer("x-muc#roomconfig_registration", true);
	            
				 muc.sendConfigurationForm(submitForm);
				 return true;
				} catch (XMPPException e) {
					Log.v("创建错误2", "=="+e.getMessage());
					return false;
				}
		}
	 
}

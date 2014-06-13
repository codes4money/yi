package org.sipdroid.sipua.ui;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class callback_postui  extends Activity{
	
	 Timer timer = new Timer();
	 public String num="";
		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
	            if(msg.what==1)//���»�Ա�б���Ϣ
				{
	            	finish();
				}
	            else if(msg.what==2)
	            {
	            	
	            	insertCallLog(num,"3","2","1",1);
	            	
	            	Toast.makeText(callback_postui.this, "回拨提交成功.请等待系统来电！", Toast.LENGTH_SHORT).show();
	            	((TextView)findViewById(R.id.call_screen_current_state)).setText("回拨提交成功.请等待系统来电！");
	            	reExit();
	            }
			    super.handleMessage(msg);
			}
	};
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    	requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.call_screen_activity);
	        num = getIntent().getStringExtra("num").replace("-", "").replace(" ", "");
	        ((TextView)findViewById(R.id.call_screen_contact_txt)).setText(num);

	        ((Button)findViewById(R.id.call_screen_cancel_btn)).setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});
	        
	        ((TextView)findViewById(R.id.call_screen_current_state)).setText("回拨提交中。。。请稍后!");
	        post(); 
	 }
	 
	private void reExit()
	{
			timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				}
			},10000);
	}
	
	private void post()
	{
			timer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					uploadPhone();
				}
			},500);
	}
	private void insertCallLog(String string,String string2, String string3, String string4, long i)
	{
		if(string.substring(0, 1).equals("0") || string.substring(0, 1).equals("86"))
		{
			string=string.substring(2);
		}
		
	    // TODO Auto-generated method stub
	    ContentValues values = new ContentValues();
	    values.put(CallLog.Calls.NUMBER, string);
	    values.put(CallLog.Calls.DATE, System.currentTimeMillis()+i);
	    values.put(CallLog.Calls.DURATION, string2);
	    values.put(CallLog.Calls.TYPE,string3);//δ��
	     values.put(CallLog.Calls.NEW, string4);//0�ѿ�1δ��	    
	    getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
	}
	
	 public void uploadPhone()
	 {
			String sharedPrefsFile = "com.studio.b56.im_preferences";
			SharedPreferences settings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
			if(!settings.getBoolean("isshownum",true))
			{
				num="0"+num;
			}
			else
			{
				num="86"+num;
			}
			
			CommFun.posturl(Constants.ApiUrl.SIP_URL+"callback.jsp?callees="+num+"&caller="+settings.getString("username", ""));

			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");
	        String time2=sdf.format(new java.util.Date());  
			Editor edit = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).edit();
			edit.putString("sip_Answer_time",time2);
			edit.commit();
			
			Message message = new Message();
			message.what = 2;
			handler.sendMessage(message);
	 }
}

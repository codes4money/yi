package com.studio.b56.im.app.ui;


import net.tsz.afinal.annotation.view.ViewInject;

import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.control.BaseDialog;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class pay_cardpay extends Activity {
	private Button leftBtn,rightBtn,loginbnt;
	private ProgressDialog pg =null;
	public String cardname="",cardpass="",userid="";
	@ViewInject(id = R.id.topMiddle)
	private TextView topTitle;
	@ViewInject(id = R.id.topLeft)
	private ImageButton topLeft;
	@ViewInject(id = R.id.topRight)
	private ImageButton topRight;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		//要做的事情
			if(msg.what==1)
			{
				 pg.hide();		
				 new BaseDialog.Builder(pay_cardpay.this).setTitle("提示").setMessage(msg.obj.toString()).setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {
			        	finish();
			        }
			      }).setNoCancel(true).show();
			}
			else if(msg.what==2)
			{
				 pg.hide();		
				 new BaseDialog.Builder(pay_cardpay.this).setTitle("提示").setMessage("充值卡卡号或密码有误!").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {

			        }
			      }).setNoCancel(true).show();
			}
			else if(msg.what==3)
			{
				 pg.hide();		
				 new BaseDialog.Builder(pay_cardpay.this).setTitle("提示").setMessage("操作失败，请稍后再试..").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {

			        }
			      }).setNoCancel(true).show();
			}
		    super.handleMessage(msg);
		}
		};
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.pay_cardpay);
			
			String sharedPrefsFile = "com.studio.b56.im_preferences";
			SharedPreferences settings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
			
			userid=settings.getString("username", "");
			
			topTitle.setText("充值卡充值");
			topRight.setBackgroundResource(R.drawable.reg_hook);
			topRight.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pay();
				}
			});
			
			topLeft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			
			loginbnt=(Button)findViewById(R.id.pay_cardpay__ok_btn);
			loginbnt.setOnClickListener(new OnClickListener() {
				@Override
			public void onClick(View v) {
					pay();
				}
			});
		}
		
		public void pay()
		{
			pg=new ProgressDialog(pay_cardpay.this);
			pg.setTitle("请稍等");
			pg.setMessage("正在更交充值...");
			pg.show();
			
			cardname=((EditText)findViewById(R.id.pay_cardpay__txt_username)).getText().toString();
			cardpass=((EditText)findViewById(R.id.pay_cardpay__txt_password)).getText().toString();
			
			init();
		}
		
		public void init()
		{
			new Thread(new Runnable(){
			     @Override
				public void run()
			    {
			    	   String val=CommFun.posturl(Constants.ApiUrl.SIP_URL+"paybyphonecard.jsp?name="+userid+"&type=2&pin="+cardname+"&password="+cardpass+"&active=1&memo=KMCZ");
			    	  // String val=CommFun.posturl("http://www.uulm.com/xl_pcz/saverc.php?rphoneuser="+userid+"&cardnumber="+cardname+"&cardpass="+cardpass+"&Action=qtPaymoney&mkbh=M010012&movetype=movezt");
	                 //   if(val.trim().equals("0"))//成功!
			        //	{
			        		Message message = new Message();
							message.what = 1;
							message.obj=val.split("|")[1];
							handler.sendMessage(message);
			         //  }
			         }
			}).start();	
			
		}
		
		
}

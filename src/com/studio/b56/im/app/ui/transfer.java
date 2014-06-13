package com.studio.b56.im.app.ui;

import net.tsz.afinal.annotation.view.ViewInject;

import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
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

public class transfer extends Activity {
	private Button leftBtn,rightBtn,loginbnt;
	private ProgressDialog pg =null;
	public String cardname="",cardpass="",userid="",password1="",password2="",tousername="",tonum="";
	private LinearLayout content;
	private LayoutInflater inflater;
	private View content_lv;
    private SharedPreferences mSettings;
    
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
				 new BaseDialog.Builder(transfer.this).setTitle("提示").setMessage("转账成功!").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {
			        	finish();
			        }
			      }).setNoCancel(true).show();
			}
			else if(msg.what==0)
			{
				 pg.hide();		
				 new BaseDialog.Builder(transfer.this).setTitle("提示").setMessage("转账失败，请稍后重试...").setYesListener(new BaseDialog.YesListener()
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
				 new BaseDialog.Builder(transfer.this).setTitle("提示").setMessage("你的话费余额不足，请充值后再操作!").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {
			        	finish();
			        }
			      }).setNoCancel(true).show();
			}
			else if(msg.what==-1)
			{
				 pg.hide();		
				 new BaseDialog.Builder(transfer.this).setTitle("提示").setMessage("输入的密码错误！").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {
			        	finish();
			        }
			      }).setNoCancel(true).show();
			}
		    super.handleMessage(msg);
		}
		};
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			

			setContentView(R.layout.setting_transfer);
			
			topTitle.setText("话费转账");
			topLeft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			
			String sharedPrefsFile = "com.studio.b56.im_preferences";
			mSettings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
			
			userid=mSettings.getString("username", "");
			loginbnt=(Button)findViewById(R.id.setting_transfer_ok_btn);
			
			
			
			topRight.setBackgroundResource(R.drawable.reg_hook);
			topRight.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pay();
				}
			});
			
			leftBtn.setOnClickListener(new OnClickListener() {
				@Override
			public void onClick(View v) {
					finish();
				}
			});
			
			
			loginbnt.setOnClickListener(new OnClickListener() {
				@Override
			public void onClick(View v) {
					pay();
				}
			});
		}
		
		
		public void pay()
		{
			tousername=((EditText)findViewById(R.id.setting_transfer_tousername)).getText().toString();
			tonum=((EditText)findViewById(R.id.setting_transfer_num)).getText().toString();
			password1=((EditText)findViewById(R.id.setting_transfer_password)).getText().toString();
			
			if(password1!="" && password1.equals(mSettings.getString("password", "")))
			{
				
			    if(tousername!="" && tonum!="")
				{
			      pg=new ProgressDialog(transfer.this);
			      pg.setTitle("请稍等");
			      pg.setMessage("正在账号..");
			      pg.show();	
			      
			      init();
			      
			    }
			    else
			    {
			    	new BaseDialog.Builder(transfer.this).setTitle("提示").setMessage("请将对方账号与转账金额填写完整!").setYesListener(new BaseDialog.YesListener()
				      {
				        public void doYes()
				        {

				        }
				      }).setNoCancel(true).show();
			    }
			
			}
			else
			{
				 new BaseDialog.Builder(transfer.this).setTitle("提示").setMessage("密码输入错误!").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {

			        }
			      }).setNoCancel(true).show();
			}
		}
		
		public void init()
		{
			new Thread(new Runnable(){
			     @Override
				public void run()
			    {
			    	   String val=CommFun.posturl("http://www.uulm.com/system/_get/moveaccountsapi.php?Action=Tphonemove&rPhone="+tousername+"&cPhone="+userid+"&cPhonePass="+password1+"&money=" + tonum);
			    	   Log.v("转账调试====", "结果=="+val);
			    	   if(val.trim().equals("1"))
                       {
                    		Message message = new Message();
                    		message.what = 1;
                    		handler.sendMessage(message);
                       }
                       else if(val.trim().equals("2"))
                       {
                    		Message message = new Message();
                    		message.what = 2;
                    		handler.sendMessage(message);
                       }
                       else if(val.trim().equals("-1"))
                       {
                    		Message message = new Message();
                    		message.what = -1;
                    		handler.sendMessage(message);
                       }
                       else
                       {
                    	Message message = new Message();
                   		message.what = 0;
                   		handler.sendMessage(message);
                       }
			         }
			}).start();	
			
		}
		
		
}

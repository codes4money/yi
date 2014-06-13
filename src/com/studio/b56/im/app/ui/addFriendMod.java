package com.studio.b56.im.app.ui;

import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.XMPPConnection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.api.AlbumApi;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.FriendApi;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.api.UserInfoApi;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.control.AlbumControl;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.app.control.DialogMark;
import com.studio.b56.im.app.ui.BaseActivity;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.app.ui.group.group_add;
import com.studio.b56.im.app.ui.group.grouplist;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.command.TextdescTool;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.service.receiver.PushChatMessage;
import com.studio.b56.im.vo.AlbumVo;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;

public class addFriendMod extends BaseActivity {
	
	private ProgressDialog pg =null;
	public String mid="";
	public String roomAddr="",g_name,g_dex;
	private TextView title,friend_name_txt;
	public boolean isCheck=false;
	private PeibanApplication shangwupanlvApplication;
	public  XMPPConnection xmppconn=null;
    public Button addbnt;
    private UserInfoVo userInfoVo;
    public int seachType=0;
    public EditText inputTxt;
    
	 private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
				 super.handleMessage(msg);
			}
			};
			
	@Override
	protected void initTitle() {
		setBtnBack();
		setTitleContent("查找好友");

	}
	
	@Override
	protected void baseInit() {
		super.baseInit();
		
		seachType=getIntent().getIntExtra("addfriendtype",0);
		
		
		addbnt=(Button)findViewById(R.id.group_ok_btn);	
		friend_name_txt=(TextView)findViewById(R.id.add_friend_mod_txt);	
		inputTxt=(EditText)findViewById(R.id.add_friend_mod_input_name);	
		
		if(seachType==0)
		{
			friend_name_txt.setText("请输入手机号或邮箱:");
		}
		else
		{
			friend_name_txt.setText("请输入ID号:");
		}
		
		
		addbnt.setOnClickListener(new OnClickListener() {
			@Override
		public void onClick(View v) {
				 addRoom();
			}
		});
		
		userInfoVo = new UserInfoCache(getBaseContext()).getCacheUserInfo();
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_friend_mod);
		this.baseInit();
		initTitle();
	}
	public void addRoom()
	{
		 if(inputTxt.getText().toString().trim()=="" || inputTxt.getText()==null)
		 {
			 new com.studio.b56.im.app.control.BaseDialog.Builder(addFriendMod.this).setTitle("提示").setMessage("请输入要查询的内容!").setYesListener(new BaseDialog.YesListener()
		      {
		        public void doYes()
		        {
		        }
		      }).setNoCancel(true).show();
		 }
		 else
		 {
			 if(seachType==0)
			 {
				 	Intent intent=new Intent(addFriendMod.this,addFriendbyPhone.class);
					 intent.putExtra("inputtxt", inputTxt.getText().toString());
					startActivity(intent);
		        	finish();
			 }
			 else
			 {
				 Intent intent=new Intent(addFriendMod.this,addFriendbyID.class);
				 intent.putExtra("inputtxt", inputTxt.getText().toString());
				 startActivity(intent);
		         finish();
			 }
			 
		 }
		
		
		
	}

}

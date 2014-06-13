package com.studio.b56.im.app.ui.group;

import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
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
import com.studio.b56.im.app.ui.group.grouplist.DelRoomCallBack;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.command.TextdescTool;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.service.receiver.PushChatMessage;
import com.studio.b56.im.vo.AlbumVo;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.GroupVo;
import com.studio.b56.im.vo.UserInfoVo;

public class group_add extends BaseActivity {
	
	private ProgressDialog pg =null;
	public String mid="";
	public String roomAddr="",g_name,g_dex;
	private TextView title,group_name,group_dex;
	public boolean isCheck=false;
	private PeibanApplication shangwupanlvApplication;
	public  XMPPConnection xmppconn=null;
    public Button addbnt;
    private UserInfoVo userInfoVo;
    
	 private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
				if(msg.what==1)
				{	
					 new com.studio.b56.im.app.control.BaseDialog.Builder(group_add.this).setTitle("提示").setMessage("群组创建成功!").setYesListener(new BaseDialog.YesListener()
				      {
				        public void doYes()
				        {
				        	GroupVo gvo=new GroupVo();
				        	gvo.setGroupname(g_name);
				        	gvo.setRole("1");
				        	gvo.setGroupdes(g_dex);
				        	gvo.setGroupid("100");
				        	gvo.setUid(userInfoVo.getUid());
				        	
				        	FinalDb db = FinalFactory.createFinalDb(group_add.this,getUserInfoVo());
				        	db.save(gvo);
				        	
				        	Intent intent=new Intent(group_add.this,group_main.class);
				        	intent.putExtra("RoomName", g_name);
							startActivity(intent);
				        	finish();
				        }
				      }).setNoCancel(true).show();
					 
				}
				else if(msg.what==2)
				{	
					 new com.studio.b56.im.app.control.BaseDialog.Builder(group_add.this).setTitle("提示").setMessage("群组创建失败,请稍后重试!").setYesListener(new BaseDialog.YesListener()
				      {
				        public void doYes()
				        {
				        	Intent intent=new Intent(group_add.this,grouplist.class);
							startActivity(intent);
				        	finish();
				        }
				      }).setNoCancel(true).show();
					 
					 
				}
				else if(msg.what==3)
				{
					Intent intent = new Intent(PushChatMessage.ACTION_ADD_GROUP);
					intent.putExtra("group_roomname", g_name);
					intent.putExtra("group_myusername",userInfoVo.getUid().toString());
					intent.putExtra("group_roomdes", g_dex);
					sendBroadcast(intent);
					
			    	Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
					
				}
				 super.handleMessage(msg);
			}
			};
			
	@Override
	protected void initTitle() {
		setBtnBack();
		setTitleContent("创建群组");

	}
	
	@Override
	protected void baseInit() {
		super.baseInit();
		
		startService(new Intent(getBaseContext(), SnsService.class));
		
		pg=new ProgressDialog(group_add.this);
		addbnt=(Button)findViewById(R.id.group_ok_btn);	
		group_name=(TextView)findViewById(R.id.group_addgroup_input_name);	
		group_dex=(TextView)findViewById(R.id.group_addgroup_input_dex);	
		
		addbnt.setOnClickListener(new OnClickListener() {
			@Override
		public void onClick(View v) {
				 addRoom();
			}
		});
		userInfoVo = new UserInfoCache(getBaseContext()).getCacheUserInfo();
		shangwupanlvApplication = (PeibanApplication) getApplication();
		xmppconn=shangwupanlvApplication.getXMPPConnection();
	}

	class DelRoomCallBack extends ClientAjaxCallback{
		@Override
		public void onStart() {
			super.onStart();
			getWaitDialog().setMessage("操作中..");
			getWaitDialog().show();
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			
			getWaitDialog().cancel();
			if(t.trim().contains("no"))
			{
				Message message = new Message();
				message.what = 3;
				handler.sendMessage(message);
			}
			else
			{
				 new com.studio.b56.im.app.control.BaseDialog.Builder(group_add.this).setTitle("提示").setMessage("群组已存在，请重新输入群昵称！").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {
			        }
			      }).setNoCancel(true).show();
			}
			
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			getWaitDialog().cancel();
			Toast.makeText(getBaseContext(), "操作失败!", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.group_add);
		this.baseInit();
		initTitle();
	}
	public void addRoom()
	{
		g_name=group_name.getText().toString();
		g_dex=group_dex.getText().toString();
			
	if(g_name.length()>=3)
	{
		
   	 getFinalHttp().get(Constants.ApiUrl.WEB_URL+"checkgroupname.php?roomname="+g_name,new DelRoomCallBack());
	
	}
	else
	{
		 new com.studio.b56.im.app.control.BaseDialog.Builder(group_add.this).setTitle("提示").setMessage("群组名称需三个以上字符！").setYesListener(new BaseDialog.YesListener()
	      {
	        public void doYes()
	        {
	        	
	        }
	      }).setNoCancel(true).show();
	}
	
	
		
	}

}

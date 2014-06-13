package com.studio.b56.im.app.ui.group;

import java.util.ArrayList;

import org.jivesoftware.smack.XMPPConnection;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.app.ui.BaseActivity;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.vo.UserInfoVo;

public class group_userlist  extends BaseActivity {

	private ProgressDialog pg =null;
	public String mid="";
	public String roomAddr="",g_name,g_dex;
	private TextView title,group_name,group_dex;
	public boolean isCheck=false;
	private PeibanApplication shangwupanlvApplication;
	public  XMPPConnection xmppconn=null;
	public boolean isAdmin=false;
	private BroadcastReceiver broadcastReceiver;

  	private static final String TAG = "group_userlist";

  	public String RoomName="";
	private ListView meeting_chatroom_lv;
	private group_groupuserlist_adapter adapter;
	public String ChatUserList="";
    private UserInfoVo userInfoVo;
    public Button addbnt;
    public String adminuser="",delusername="";
	public ArrayList<groupuseritem> alluserlist=new ArrayList<groupuseritem>();
	
    
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
			if(msg.what==0)
			{	
			}
			else if(msg.what==1)//更新会员列表信息
			{
				if(userInfoVo.getUid().equals(adminuser))
				{
			      isAdmin=true;
				}
				initChatUserlist();
			}
			else if(msg.what==2)
			{
				pg.hide();
				 
				new BaseDialog.Builder(group_userlist.this).setTitle("提示").setMessage("操作成功!").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {
			        	init();
			        }
			      }).show();
			}
		    super.handleMessage(msg);
		}
		};
		
		@Override
		protected void initTitle() {
			setBtnBack();
			setTitleContent("成员列表");

		}
		
		@Override
		protected void baseInit() {
			super.baseInit();
			
			startService(new Intent(getBaseContext(), SnsService.class));
			
			pg=new ProgressDialog(group_userlist.this);
			userInfoVo = new UserInfoCache(getBaseContext()).getCacheUserInfo();
			shangwupanlvApplication = (PeibanApplication) getApplication();
			xmppconn=shangwupanlvApplication.getXMPPConnection();
		}

		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.group);
			
			RoomName =getIntent().getStringExtra("roomName");
			
			this.baseInit();
			initTitle();
			
			init();
		}
		
		public void init()
		{
			ChatUserList="";
			
			new Thread(new Runnable(){
			     @Override
					public void run()
				    {
			    		getFinalHttp().get(Constants.ApiUrl.WEB_URL+"getgroupuser.php?username="+RoomName,new LoginAjaxCallBack());
			    	}
				}).start();	
		}
		
		class LoginAjaxCallBack extends ClientAjaxCallback{
			
			@Override
			public void onStart() {
				super.onStart();
				getWaitDialog().setMessage("获取成员。..");
				getWaitDialog().show();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				Log.v("9080", t);
				Log.v("9080", "len1:" + t.length());
				t=t.trim();
				Log.v("9080", "len2:" + t.length());
				try {
					if(t!="")
					{
						
						  if(t.trim()!="")//获取房间信息成功.
				        	{
		                	  
		               	    ChatUserList=t;
		               	    alluserlist.clear();
		               	    
		               	    for(int i=0;i<ChatUserList.split("\\$").length;i++)
		               	    {
		               	    	try
		               	    	{
		               	    
		               	    	String c1=ChatUserList.split("\\$")[i];
		               	    	
		               	    	if(c1!="")
		               	    	{
		               	        
		               	        groupuseritem  ritem=new groupuseritem();
		               	    	ritem.userjid=c1.split("\\|")[0];
		               	    	ritem.username=c1.split("\\|")[1];
		               	    	ritem.role= Integer.parseInt(c1.split("\\|")[2]);
		               	    	//ritem.userface=c1.split("\\|")[3];
		                 	    alluserlist.add(ritem);
		                 	    if(ritem.role==1)
		                 	    {
		                 	    	adminuser=ritem.userjid;
		                 	    }
		                 	    
		               	        }
		               	       } catch (Exception e) {}
		               	    }
		               	    
				        		Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
				           }
		                   else
		                    {
				        		Message message = new Message();
								message.what = 0;
								handler.sendMessage(message);
		                    }
						  
					}
					else
					{
		        		Message message = new Message();
						message.what = 0;
						handler.sendMessage(message);
					}
					getWaitDialog().cancel();
				} catch (Exception e) {
					getWaitDialog().cancel();
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				getWaitDialog().cancel();
				Toast.makeText(getBaseContext(), "成员获取失败!", Toast.LENGTH_SHORT).show();
				
			}
			
		}
		
		public void deluser()
		{
			new Thread(new Runnable(){
			     @Override
				public void run()
			    {
			    	 String val=CommFun.posturl(Constants.ApiUrl.WEB_URL+"delgroupuser.php?username="+delusername+"&roomjid="+ RoomName);
			    	 if(val.trim().equals("1"))//获取房间信息成功.
			         {
			    		    Message message = new Message();
							message.what = 2;
							handler.sendMessage(message);
			         }
			    }
			}).start();	
		}
		
		 public void initChatUserlist()
		   {
			    meeting_chatroom_lv=(ListView)findViewById(R.id.group_grouplist_lstview);
			   //第一次更新用户列表
				adapter=new group_groupuserlist_adapter(this,alluserlist);
				meeting_chatroom_lv.setAdapter(adapter);
				
				 Log.v("用户列表===","==="+alluserlist.size());
				 
				meeting_chatroom_lv.setOnItemLongClickListener(new OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					    	
							final groupuseritem cg=(groupuseritem)view.getTag();
					    	
							if(isAdmin && !adminuser.equals(cg.userjid))
							{
								String[] longClickItems =  new String[] { "将他T出群组"};
								new AlertDialog.Builder(group_userlist.this)
								//.setTitle("好友请球回复")
								.setItems(longClickItems,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
													int which) {
												switch (which) {
												// 设置昵称
												case 0:
													 new BaseDialog.Builder(group_userlist.this).setTitle("提示").setMessage("确认将会员移出群组 ?").setYesListener(new BaseDialog.YesListener()
												      {
												        public void doYes()
												        {
												        	pg=new ProgressDialog(group_userlist.this);
												    		pg.setTitle("请稍等");
												    		pg.setMessage("正在处理中..");
												    		pg.show();
												    		
												    		delusername=cg.userjid;
												    		deluser();
												        	
												        }
												      }).setCancelListener(new BaseDialog.CancelListener(){
														@Override
														public void doCancel() {
														}}).setNoCancel(false).show();
													
													break;
												}
											}
										}).setTitle("操作-"+cg.username).show();
							}
							return true;
							
						}
					});
				
		   }
}

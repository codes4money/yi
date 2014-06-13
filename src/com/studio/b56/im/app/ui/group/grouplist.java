package com.studio.b56.im.app.ui.group;
import java.util.ArrayList;
import java.util.List;
import net.tsz.afinal.FinalDb;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.alibaba.fastjson.JSON;
import com.studio.b56.im.app.control.BaseDialogUtils;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.ui.BaseActivity;
import com.studio.b56.im.app.ui.SessionActivity;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.service.SNSGroupManager;
import com.studio.b56.im.service.receiver.GroupChatMessageReceiver;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.GroupVo;
import com.studio.b56.im.vo.UserInfoVo;


public class grouplist extends BaseActivity {
	private ProgressDialog pg =null;
	public String mid="";
	public String roomAddr="",g_name,g_dex;
	private TextView title,group_name,group_dex;
	public boolean isCheck=false;
	private PeibanApplication shangwupanlvApplication;
	public  XMPPConnection xmppconn=null;
    
	private BroadcastReceiver broadcastReceiver;

  	private static final String TAG = "SNSGroupManager";

  	public String RoomName="";
	public ArrayList<grouplistitem> alluserlist=new ArrayList<grouplistitem>();
	private ListView meeting_chatroom_lv;
	private group_grouplist_adapter adapter;
	public String ChatUserList="";
    private UserInfoVo userInfoVo;
    public Button addbnt;
    public List<GroupVo> cVos;
    public FinalDb db;
    public int actionRoomId=0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
			if(msg.what==0)
			{	
			}
			else if(msg.what==1)//更新会员列表信息
			{
				initChatUserlist();
			}
			else if(msg.what==4)
			{
				init();
			}
		    super.handleMessage(msg);
		}
		};
	
	private void registerReceiver(){
		Log.d(TAG, "registerReceiver()");
		IntentFilter filter = new IntentFilter();
		filter.addAction(GroupChatMessageReceiver.ACTION_GROUP_SEND_STATE+"_"+ RoomName);//发送群信息
		registerReceiver(broadcastReceiver, filter);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
	
	class MyBroadcastReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "MyBroadcast:onReceive()");
			String action = intent.getAction();
			
			if((GroupChatMessageReceiver.ACTION_GROUP_SEND_STATE+"_"+ RoomName).equals(action)){

				 String chatmessage=intent.getStringExtra("GROUP_LOGIN_CODE");
				 
				 Toast.makeText(grouplist.this,chatmessage, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
    @Override
	protected void initTitle() {
		setBtnBack();
		setTitleContent("我的群组");
		setTitleRight("创建");

	}
	@Override
	protected void titleBtnLeft() {
		super.titleBtnRight();
        finish();
	}
	
	@Override
	protected void titleBtnRight() {
		super.titleBtnRight();
		Intent intent = new Intent(grouplist.this,group_add.class);
		startActivity(intent);
	}
	
	@Override
	protected void baseInit() {
		super.baseInit();
		userInfoVo = new UserInfoCache(getBaseContext()).getCacheUserInfo();
		pg=new ProgressDialog(grouplist.this);
		
		this.broadcastReceiver = new MyBroadcastReceiver();//监听发布信息
		registerReceiver();
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.group);
		
		this.baseInit();
		initTitle();
		
		db = FinalFactory.createFinalDb(grouplist.this,getUserInfoVo());
		
		
		 new Thread(new Runnable(){
 		     @Override
 			public void run()
 		    {
 		    	updateGroupList();//更新群组
 		    }
 		}).start();	
		 
	}
	@Override
	protected void onResume() {
	super.onResume();
	
	init();
	}
	
	public void init()
	{
		cVos = db.findAllByWhere(GroupVo.class, "uid='" + userInfoVo.getUid()+"'");
		initChatUserlist();
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
		
		db.deleteByWhere(GroupVo.class, "id="+actionRoomId);
		
		getWaitDialog().cancel();
		Toast.makeText(getBaseContext(), "操作成功!", Toast.LENGTH_SHORT).show();
		Message message = new Message();
		message.what = 4;
		handler.sendMessage(message);
	}

	@Override
	public void onFailure(Throwable t, String strMsg) {
		super.onFailure(t, strMsg);
		getWaitDialog().cancel();
		Toast.makeText(getBaseContext(), "操作失败!", Toast.LENGTH_SHORT).show();
	}
	
}

class DelRoomUserCallBack extends ClientAjaxCallback{
	@Override
	public void onStart() {
		super.onStart();
		getWaitDialog().setMessage("操作中..");
		getWaitDialog().show();
	}

	@Override
	public void onSuccess(String t) {
		super.onSuccess(t);
		
		db.deleteByWhere(GroupVo.class, "id="+actionRoomId);
		
		getWaitDialog().cancel();
		
		Toast.makeText(getBaseContext(), "操作成功!", Toast.LENGTH_SHORT).show();
		Message message = new Message();
		message.what = 4;
		handler.sendMessage(message);
	}

	@Override
	public void onFailure(Throwable t, String strMsg) {
		super.onFailure(t, strMsg);
		getWaitDialog().cancel();
		Toast.makeText(getBaseContext(), "操作失败!", Toast.LENGTH_SHORT).show();
	}
	
}
	 public void initChatUserlist()
	   {
		   meeting_chatroom_lv=(ListView)findViewById(R.id.group_grouplist_lstview);
		   //第一次更新用户列表
			adapter=new group_grouplist_adapter(this,cVos);
			meeting_chatroom_lv.setAdapter(adapter);
			
			meeting_chatroom_lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					String room_name=((GroupVo)view.getTag()).getGroupname();
					Intent intent=new Intent(grouplist.this,group_main.class);
		        	intent.putExtra("RoomName",room_name);
					startActivity(intent);
					
				}
			});
			
			meeting_chatroom_lv.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						
				    	final GroupVo cg=((GroupVo)view.getTag());;
						if(cg.getRole().equals("1"))//群主
						{
							String[] longClickItems =  new String[] { "解撒群组"};
							new AlertDialog.Builder(grouplist.this)
							//.setTitle("好友请球回复")
							.setItems(longClickItems,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											switch (which) {
											case 0:
										
												 new BaseDialog.Builder(grouplist.this).setTitle("提示").setMessage("确认解撒群组 ?").setYesListener(new BaseDialog.YesListener()
											      {
											        public void doYes()
											        {
											   		     actionRoomId=cg.getId();
												    	 getFinalHttp().get(Constants.ApiUrl.WEB_URL+"delmygroup.php?roomname="+cg.getGroupname(),new DelRoomCallBack());
											        }
											      }).setCancelListener(new BaseDialog.CancelListener(){
													@Override
													public void doCancel() {
													}}).setNoCancel(true).show();
												
												break;
											}
										}
									}).setTitle("操作-"+cg.getGroupname()).show();
						}
						else
						{
							String[] longClickItems =  new String[] { "退出群组"};
							new AlertDialog.Builder(grouplist.this)
							//.setTitle("好友请球回复")
							.setItems(longClickItems,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											switch (which) {
											// 设置昵称
											case 0:
												 new BaseDialog.Builder(grouplist.this).setTitle("提示").setMessage("确认退出群组 ?").setYesListener(new BaseDialog.YesListener()
											      {
											        public void doYes()
											        {
											   		     actionRoomId=cg.getId();
												    	 getFinalHttp().get(Constants.ApiUrl.WEB_URL+"delgroupuser.php?roomjid="+cg.getGroupname()+"&username="+userInfoVo.getUid()+"@"+Constants.ApiUrl.HOST,new DelRoomUserCallBack());
											        }
											      }).setCancelListener(new BaseDialog.CancelListener(){
													@Override
													public void doCancel() {
													}}).setNoCancel(true).show();
												
												break;
											}
										}
									}).setTitle("操作-"+cg.getGroupname()).show();
						}
						return true;
						
					}
				});
			
	   }
}

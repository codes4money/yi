package com.studio.b56.im.app.ui;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.studio.b56.im.app.control.BaseDialogUtils;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.app.control.FriendListAction;
import com.studio.b56.im.R;
import com.studio.b56.im.app.cache.UserInfoCache;

import com.studio.b56.im.app.ui.BaseActivity;
import com.studio.b56.im.app.ui.group.ContactPersion;
import com.studio.b56.im.app.ui.group.Meeting_AddUser_Adapter;
import com.studio.b56.im.service.SNSGroupManager;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;


public class SelectFriendList extends BaseActivity {
	public String mid="";
	public String roomAddr="",g_name,g_dex;
	private TextView title,group_name,group_dex;
    private UserInfoVo userInfoVo;
	private ArrayList<ContactPersion> contacts=new ArrayList<ContactPersion>();
	private ArrayList<ContactPersion> userList=new ArrayList<ContactPersion>();
	public boolean isCheck=false;
	public String myuserid="";
	public String addUserids="";
	private ProgressDialog pg =null;
	private ListView main_chatting_lv;
	public String roomName="";
	public static final int HEADLER_START = 101;
	public static final int HEADLER_SUCCESS = 102;
	public static final int HEADLER_ERR = 103;
	public static final int HEADLER_UPDATE = 104;
	public static final int HEADLER_INIT = 100;
	private FriendListAction friendListAction;   // 获取好友列表功能接口
	List<CustomerVo> customerVos;
	public ImageView sureButtom,userSelectFace;
	public int userNum=1;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// pg.hide();		
				 new BaseDialog.Builder(SelectFriendList.this).setTitle("提示").setMessage("处理失败，请稍后重试.").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {
			        	finish();
			        }
			      }).setNoCancel(true).show();
			case HEADLER_START:
				friendListAction.pushFriendList();
				break;
			case HEADLER_SUCCESS:
				customerVos = (List<CustomerVo>) msg.obj;
				if(customerVos != null){
					InitUserList();
				}
				break;
			case HEADLER_ERR:
				break;
			case HEADLER_INIT:
				break;
			default:
				break;
			}
			
		    super.handleMessage(msg);
		}
		};
    @Override
	protected void initTitle() {
    	
		setBtnBack();
		setTitleContent("邀请好友");
		setTitleRight("确定");
		ImageView cb=(ImageView)findViewById(R.id.sure);
		userSelectFace=(ImageView)findViewById(R.id.userSelectFace);
		cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				titleBtnRight();
			}
		});
		
	}
	@Override
	protected void titleBtnLeft() {
		super.titleBtnRight();
        finish();
	}
	
	@Override
	protected void titleBtnRight() {
		super.titleBtnRight();
		addUserids="";
		
		for(int ii=0;ii<main_chatting_lv.getChildCount();ii++)
		{
		    View vv=main_chatting_lv.getChildAt(ii);
		    CheckBox cb=(CheckBox)vv.findViewById(R.id.contactitem_select_cb);
		    
		    if(cb.isChecked())
		    {
		    	String ujid=cb.getTag().toString();
		    	ujid=ujid.split("@")[0];
		    	addUserids+=ujid+";";
		    }
		}
		
		if(addUserids=="")
		{
			new BaseDialog.Builder(SelectFriendList.this).setTitle("提示").setMessage("你还没有选择好友!").setYesListener(new BaseDialog.YesListener()
		      {
		        public void doYes()
		        {
		        }
		      }).setNoCancel(true).show();
			
		}
		else
		{
			addUserids=addUserids.substring(0, addUserids.length()-1);
		}
		Intent intent2 = new Intent("selectfriendback");
		intent2.putExtra("UserIds", addUserids);
		sendBroadcast(intent2);
		
		Intent intent = new Intent();
		intent.putExtra("UserIds", addUserids);
		this.setResult(RESULT_OK, intent);
		finish();

	}
	@Override
	protected void baseInit() {
		super.baseInit();
		userInfoVo = new UserInfoCache(getBaseContext()).getCacheUserInfo();
		friendListAction = getShangwupanlvApplication().getFriendListAction();
		friendListAction.setHandler(handler);
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_selectconec);
		
		this.baseInit();
		initTitle();
		
		initAdapterView();
	}
	
	void initAdapterView(){
		new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				handler.sendEmptyMessage(HEADLER_INIT);
				if(friendListAction.isLocalFriendInfo()){
					Message message = handler.obtainMessage();
					message.what = HEADLER_SUCCESS;
					message.obj = friendListAction.getFriendList();
					handler.sendMessage(message);
				}else{
					handler.sendEmptyMessage(HEADLER_START);
				}
				return null;
			}

		}.execute();
	}
	
	
	public void InitUserList()
	{
		    main_chatting_lv=(ListView)findViewById(R.id.selectList);
		    
			Meeting_AddUser_Adapter cantact_ad=new Meeting_AddUser_Adapter(this, customerVos);
			main_chatting_lv.setAdapter(cantact_ad);
			
			main_chatting_lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					CheckBox cb=(CheckBox)view.findViewById(R.id.contactitem_select_cb);
					if(cb.isChecked())
					{
						 userSelectFace.setImageResource(R.drawable.snum);
						 cb.setChecked(false);
					}
					else
					{
						ImageView cb1=(ImageView)view.findViewById(R.id.contactitem_avatar_iv);
						Drawable dr=cb1.getDrawable();
						userSelectFace.setImageDrawable(cb1.getDrawable());
					    cb.setChecked(true);
					}
				}
			});
	}
	

}

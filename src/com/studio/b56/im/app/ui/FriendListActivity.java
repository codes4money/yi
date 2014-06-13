package com.studio.b56.im.app.ui;

import java.util.LinkedList;
import java.util.List;
import android.content.BroadcastReceiver;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.studio.b56.im.R;
import com.studio.b56.im.adapter.ObjectBaseAdapter;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.FriendApi;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.control.FriendListAction;
import com.studio.b56.im.app.ui.NotifyActivity.MyBroadcast;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.app.ui.group.group_add;
import com.studio.b56.im.app.ui.group.group_main;
import com.studio.b56.im.app.ui.group.grouplist;
import com.studio.b56.im.command.PingYinUtil;
import com.studio.b56.im.command.TextdescTool;
import com.studio.b56.im.service.receiver.NotifySystemMessage;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.GroupVo;
import com.studio.b56.im.vo.NotifiyType;
import com.studio.b56.im.vo.NotifiyVo;
import com.studio.b56.im.vo.UserInfoVo;

/**
 * 
 * 功能： 好友列表 <br />
 * 日期：2013-5-30<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class FriendListActivity extends BaseActivity{
	public static final int HEADLER_START = 101;
	public static final int HEADLER_SUCCESS = 102;
	public static final int HEADLER_ERR = 103;
	public static final int HEADLER_UPDATE = 104;
	public static final int HEADLER_INIT = 100;
	
	@ViewInject(id = R.id.friend_list)
	private ListView mListView;
	
	private MyAdapter adapter;
	private FriendListAction friendListAction;   // 获取好友列表功能接口
	public List<CustomerVo> grouplist=null;
	private BroadcastReceiver receiver;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HEADLER_START:
				initGroupList();
				friendListAction.pushFriendList();
				//refushList();
				break;
			case HEADLER_SUCCESS:
				//refushSuccess();
				List<CustomerVo> customerVos = (List<CustomerVo>) msg.obj;
				if(customerVos != null){
					if(grouplist!=null)
					{
						List<CustomerVo> temp2=grouplist;
						temp2.addAll(customerVos);
						adapter.addList(temp2);
					}
					else
					{
					  adapter.addList(customerVos);
					}
				}
				break;
			case HEADLER_ERR:
				refushError();
				break;
			case HEADLER_INIT:
				
				break;
			default:
				break;
			}
		}
		
	};
	@Override
	protected void onResume() {
	super.onResume();
	
	grouplist=null;
	initGroupList();
	adapter = new MyAdapter();
	mListView.setAdapter(adapter);
	mListView.setOnItemClickListener(new ListItemOnClick());
	friendListAction.clearFriendlist();
	initAdapterView();
	
	}
	
	private void registerReceiver(){
		IntentFilter filter = new IntentFilter("com.cn.reLoadFriendList");
		registerReceiver(receiver, filter);
	}
	
	
	class MyBroadcast extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// 得到系统通知
			if("com.cn.reLoadFriendList".equals(intent.getAction())){
					friendListAction.pushFriendList();
					Log.v("===从网络拉取好友===", "===");
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiver = new MyBroadcast();
		this.setContentView(R.layout.friend_list);
		this.baseInit();
		registerReceiver();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(KeyEvent.KEYCODE_BACK == keyCode)
		{
			try {
//				IndexTabActivity.getInstance().callbackLocation();
				sendBroadcast(new Intent(IndexTabActivity.ACTION_CALLBACK));
				return false;
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void baseInit(){
		super.baseInit();
		
//		friendListAction = IndexTabActivity.getInstance().getFriendListAction();
		friendListAction = getShangwupanlvApplication().getFriendListAction();
		friendListAction.setHandler(handler);
	}
	
	void initGroupList()
	{
		FinalDb db = FinalFactory.createFinalDb(FriendListActivity.this,getUserInfoVo());
		List<GroupVo> cVos = db.findAllByWhere(GroupVo.class, "uid='" + getUserInfoVo().getUid()+"'");
		if(cVos.size()>0)
		{
			
		grouplist= new LinkedList<CustomerVo>();
	    for(int i=0;i<cVos.size();i++)
	    {
	    	CustomerVo sVo=new CustomerVo();
	    	sVo.setUid("0");
	    	sVo.setName(cVos.get(i).getGroupname());
	    	grouplist.add(sVo);
	    }
	    
	    }
		else
		{
			grouplist=null;
		}
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

	@Override
	protected void initTitle() {
		setTitleLeft("增加");
		setTitleContent("我的好友");
	}
	@Override
	protected void titleBtnLeft() {
		super.titleBtnRight();
		Intent intent = new Intent(FriendListActivity.this, friend_add.class);
		startActivity(intent);
	}

	// 刷新列表
	private void refushList(){
		getBtnTitleRight().setVisibility(View.GONE);
		getWaitBar().setVisibility(View.VISIBLE);
	}
	
	// 刷新成功.
	private void refushSuccess(){
		getBtnTitleRight().setVisibility(View.VISIBLE);
		getWaitBar().setVisibility(View.GONE);
		
		sendBroadcast(new Intent(SessionActivity.ACTION_FRIENDED));
	}
	
	// 刷新失败
	private void refushError(){
		getBtnTitleRight().setVisibility(View.VISIBLE);
		getWaitBar().setVisibility(View.GONE);
	}


	class MyAdapter extends ObjectBaseAdapter<CustomerVo>{
		private CancelFriendOnClick onClick = new CancelFriendOnClick();
		
		
		@Override
		public void addList(List<CustomerVo> lists) {
			super.removeAll();
			super.addList(lists);
		}

		@Override
		public int getCount() {
			return this.lists.size();
		}

		@Override
		public CustomerVo getItem(int position) {
			return this.lists.get(position);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if(!getItem(position).getUid().equals("0"))
			{
				
				ViewHolder viewHolder = null;

				convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.friend_item, null);
				viewHolder = ViewHolder.getInstance(convertView);
				
			    convertView.setTag(viewHolder);
			    bindView(viewHolder, position);
			
			}
			else  //群组
			{
				ViewHolder2 viewHolder2 = null;
				convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.friend_grouplist, null);
				viewHolder2 = ViewHolder2.getInstance(convertView);
				if(position==0)
				{
					viewHolder2.txtCatalog.setText("我的群组");
				}
				else
				{
					viewHolder2.txtCatalog.setVisibility(View.GONE);
				}
				viewHolder2.txtGroupName.setText(getItem(position).getName());
			}
			
			return convertView;
		}
		private void bindView2(ViewHolder viewHolder, int position){
			
		}
		private void bindView(ViewHolder viewHolder, int position){
			CustomerVo customerVo = getItem(position);
			
			FinalOnloadBitmap.finalDisplay(getBaseContext(), customerVo, viewHolder.imgHead, getHeadBitmap());
			
			String name = TextdescTool.getCustomerName(customerVo);
			String nickName = name.substring(0, 1);
			String catalog = null;    // 目录
			String lastCatalog = null;
			catalog = PingYinUtil.converterToFirstSpell(nickName);  // 得到目录的第一个字符.
			// 第一列直接画出.
			// 因为 字符是进行排了序的。
			// 所以判断的时候只需要根据顺序取出字符的第一个字母进行匹配就是了
			// 当遇到字符不同时开始分组
			if (position == 0)
			{
				viewHolder.txtCatalog.setVisibility(View.VISIBLE);
				viewHolder.txtCatalog.setText(catalog);
			} else
			{
				String tempNickName = TextdescTool.getCustomerName(getItem(position - 1)).substring(0, 1);
				lastCatalog = PingYinUtil.converterToFirstSpell(tempNickName);
				if (catalog.equals(lastCatalog))
				{
					viewHolder.txtCatalog.setVisibility(View.GONE);
				} else
				{
					viewHolder.txtCatalog.setVisibility(View.VISIBLE);
					viewHolder.txtCatalog.setText(catalog);
				}
			}
			
			if("1".equals(customerVo.getSex())){
				viewHolder.imgSex.setImageResource(R.drawable.sex_man);
			}else{
				viewHolder.imgSex.setImageResource(R.drawable.sex_woman);
			}
			
			String userType = customerVo.getCustomertype();
			if(Constants.CustomerType.CHATTING.equals(userType)){
				// 认证头像
				if("1".equals(customerVo.getAgent())){
					viewHolder.imgSubscript.setImageResource(R.drawable.subscript_economic);
					viewHolder.imgSubscript.setVisibility(View.VISIBLE);
				}else
				if("1".equals(customerVo.getHeadattest())){
					viewHolder.imgSubscript.setImageResource(R.drawable.subscript_auth);
					viewHolder.imgSubscript.setVisibility(View.VISIBLE);
				}else{
					viewHolder.imgSubscript.setVisibility(View.GONE);
					
				}
			}else{
				// 是否是VIP
				if("1".equals(customerVo.getVip())){
					viewHolder.imgSubscript.setImageResource(R.drawable.subscript_vip);
					viewHolder.imgSubscript.setVisibility(View.VISIBLE);
				}else{
					viewHolder.imgSubscript.setVisibility(View.GONE);
					
				}
			}
			
			String age = customerVo.getBirthday();
			String location = customerVo.getLocal();
			String line = customerVo.getOnline();
			String sign = customerVo.getSign();
			
			viewHolder.txtUsername.setText(name);
			viewHolder.txtLocation.setText(location);
			
			int ageint=TextdescTool.dateToAge(age);
			if(ageint>0)
			{
			  viewHolder.txtAge.setText(ageint + "岁");
			}
			else
			{
			  viewHolder.txtAge.setText("");
			}
			
			viewHolder.txtLocation.setText(location);
			viewHolder.txtOnline.setText(line);
			
			viewHolder.txtSign.setText(sign);
			viewHolder.btnCancnel.setTag(customerVo);
			viewHolder.btnCancnel.setOnClickListener(onClick);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		
		
	}
	
	static class ViewHolder2{
		TextView txtCatalog;// 目录
		TextView txtGroupName;
		
		public static ViewHolder2 getInstance(View view){
			ViewHolder2 viewHolder = new ViewHolder2();
			//viewHolder.txtCatalog = (TextView) view.findViewById(R.id.frienditem_group_catalog);
			//viewHolder.txtGroupName = (TextView) view.findViewById(R.id.list_txt_group_title);
			return viewHolder;
		}
	}
	
	static class ViewHolder{
		TextView txtCatalog;// 目录
	
		ImageView imgSubscript, imgHead, imgSex;
		
		TextView txtUsername, txtAge, txtLocation, txtOnline, txtSign;
		
		Button btnCancnel;  // 取消好友.
		
		public static ViewHolder getInstance(View view){
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtCatalog = (TextView) view.findViewById(R.id.frienditem_catalog);
			viewHolder.txtAge = (TextView) view.findViewById(R.id.list_txt_age);
			viewHolder.txtLocation = (TextView) view.findViewById(R.id.list_txt_location);
			viewHolder.txtOnline = (TextView) view.findViewById(R.id.list_txt_state);
			viewHolder.txtUsername = (TextView) view.findViewById(R.id.list_txt_title);
			viewHolder.txtSign = (TextView) view.findViewById(R.id.list_txt_message_info);
			
			
			viewHolder.imgHead = (ImageView) view.findViewById(R.id.list_item_img_head);
			viewHolder.imgSex = (ImageView) view.findViewById(R.id.list_img_sex);
			viewHolder.imgSubscript = (ImageView) view.findViewById(R.id.list_item_img_subscript);
			
			viewHolder.btnCancnel = (Button) view.findViewById(R.id.list_btn_cannel_authorize);
			return viewHolder;
		}
	}
	
	class CancelFriendOnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			final CustomerVo customerVo = (CustomerVo) v.getTag();
			if(customerVo != null){
				getPromptDialog().addCannel();
				getPromptDialog().setMessage("是否要删除好友!");
				getPromptDialog().setConfirmText("删除");
				getPromptDialog().addConfirm(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						cancelFriendAction(getUserInfoVo(), customerVo);
						getPromptDialog().cancel();
					}
				});
				getPromptDialog().show();
				
			}
		}
		
		private void cancelFriendAction(UserInfoVo userInfo, final CustomerVo customerVo){
			new FriendApi().delFriend(userInfo.getUid(), customerVo.getUid(), new ClientAjaxCallback() {

				@Override
				public void onStart() {
					super.onStart();
					getWaitDialog().setMessage("删除好友...");
					getWaitDialog().show();
				}

				@Override
				public void onSuccess(String t) {
					super.onSuccess(t);
					getWaitDialog().cancel();
					String data = ErrorCode.getData(getBaseContext(), t);
					if(data != null){
						if("1".equals(data)){
							getPromptDialog().addCannel();
							getPromptDialog().removeConfirm();
							getPromptDialog().setCannelText("确定");
							getPromptDialog().setMessage("删除成功!");
							getPromptDialog().show();
							delFriendSuccess(customerVo);
						}else{
							getPromptDialog().addCannel();
							getPromptDialog().removeConfirm();
							getPromptDialog().setCannelText("确定");
							getPromptDialog().setMessage("发生错误,不能删除该好友!");
							getPromptDialog().show();
						}
					}
				}

				@Override
				public void onFailure(Throwable t, String strMsg) {
					super.onFailure(t, strMsg);
					getWaitDialog().cancel();
					showToast(strMsg);
				}
				
				
			});
		}
		
		private void delFriendSuccess(CustomerVo customerVo){
			try {
				adapter.removeObject(customerVo);
				//customerVo.setFriend("0");
			   // getFinalDb().update(customerVo);
			    getFinalDb().delete(customerVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class ListItemOnClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CustomerVo customerVo = adapter.getItem(position);
			if(customerVo != null){
				if(!customerVo.getUid().equals("0"))
				{
				Intent intent = null;
				intent = new Intent(FriendListActivity.this, FriendChattingActivity.class);
				intent.putExtra("data", customerVo);
				
				startActivity(intent);
				}
				else
				{
					Intent intent=new Intent(FriendListActivity.this,group_main.class);
		        	intent.putExtra("RoomName",customerVo.getName());
					startActivity(intent);
				}
			}
			
			
		}
		
	}
	
	static class FriendViewOnClick implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			
		}
		
	}
}

package com.studio.b56.im.app.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import net.tsz.afinal.annotation.view.ViewInject;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.control.AddAdapter;
import com.studio.b56.im.app.control.AddBean;
import com.studio.b56.im.app.dao.SessionManager;
import com.studio.b56.im.app.ui.adpater.EmojiUtil;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.app.ui.group.group_add;
import com.studio.b56.im.app.ui.group.grouplist;
import com.studio.b56.im.app.vo.SessionVo;
import com.studio.b56.im.command.SessionComparator;
import com.studio.b56.im.command.TextdescTool;
import com.studio.b56.im.service.receiver.NotifyChatMessage;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.SessionList;
import com.studio.b56.im.R;
import com.zijunlin.Zxing.Demo.CaptureActivity;
/**
 * 
 * 功能： 会话列表 <br />
 * 日期：2013-5-17<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class SessionActivity extends BaseActivity{
	private static final String EDIT = "编辑";
	private static final String END = "完成";

	public static final String ACTION_FRIENDED = "com.intent.ACTION_FRIENDED";

	@ViewInject(id = R.id.session_list_lv)
	private ListView listMsgView;

	private MyAdapter adapter;
	private Handler handler = new Handler();

	private SessionManager sessionManager;
	private AlertDialog listDialog;
	private View listDialogView;

	private OnClickListener onClickListener;

	private MyBroadcastReceiver receiver;
	
	private SessionComparator mSessionComparator = new SessionComparator();
	private PopupWindow pw;
	private ArrayList<AddBean> groups;
	private View view;
	private ListView afPopuList;
	private ImageButton topLeft, topRight;
	private TextView topMiddle;
	private static final int REQUESTCODE=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_list);
		topLeft = (ImageButton) this.findViewById(R.id.topLeft);
		topRight = (ImageButton) this.findViewById(R.id.topRight);
		topMiddle = (TextView) this.findViewById(R.id.topMiddle);
	
//		SessionObserver.getInstance().addObserver(this);
		baseInit();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			try {
//				IndexTabActivity.getInstance().callbackLocation();
				//sendBroadcast(new Intent(IndexTabActivity.ACTION_CALLBACK));
				return false;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void baseInit() {
		super.baseInit();
		adapter = new MyAdapter();
		onClickListener = new ViewOnClik();
		sessionManager = new SessionManager(getFinalDb(), getPhotoBitmap(),
				getBaseContext());
		listMsgView.setAdapter(adapter);
		listMsgView.setOnItemClickListener(new ListItemOnClick());
		// listMsgView.setOnItemLongClickListener(new ItemOnLongClick());
		receiver = new MyBroadcastReceiver();
		this.registerReceiver();
		initAdaperView();
		
		  new Thread(new Runnable(){
			     @Override
				public void run()
			    {
			    	updateGroupList();//更新群组
			    }
			}).start();	
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver();
//		SessionObserver.getInstance().deleteObserver(this);
		super.onDestroy();
	}

	@Override
	protected void initTitle() {
		
		topLeft.setVisibility(View.GONE);
		topMiddle.setText(R.string.app_name);
		topRight.setBackgroundResource(R.drawable.add_btn);
		topRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick( View view) {
				showWindow(topRight);
			}
		});
		
	}
	
	// 编辑
	private void editSessionList() {
		adapter.edit();
	}

	private void endEdit() {
		adapter.end();
	}

	private void btnCancelListDialog() {
		listDialog.cancel();
	}
	
	private int[] resImg = { R.drawable.sweep, R.drawable.sweep, R.drawable.sweep };
	private String[] addContent = { "发起聊天", "添加朋友", "扫一扫" };

	private void showWindow(View parent) {

		if (pw == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = layoutInflater.inflate(R.layout.add_popu, null);

			afPopuList = (ListView) view.findViewById(R.id.afPopuList);
			groups = new ArrayList<AddBean>();
			for (int i = 0; i < resImg.length; i++) {
				AddBean ab = new AddBean();
				ab.setResImg(resImg[i]);
				ab.setContent(addContent[i]);
				groups.add(ab);
			}
			AddAdapter groupAdapter = new AddAdapter(this, groups);
			afPopuList.setAdapter(groupAdapter);
			pw = new PopupWindow(view, 180, 220);
		}

		pw.setFocusable(true);
		pw.setOutsideTouchable(true);

		pw.setBackgroundDrawable(new BitmapDrawable());
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int xPos = windowManager.getDefaultDisplay().getWidth() / 2
				- pw.getWidth() / 2;

		pw.showAsDropDown(parent, xPos, 20);

		afPopuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int position, long id) {
				Intent intent;
				if(position==0)
				{
				    intent = new Intent(SessionActivity.this,SelectFriendList.class);
				    //intent.putExtra("userNum", 1);
				    startActivityForResult(intent, REQUESTCODE); 
				}
				else if(position==1)
				{
					intent = new Intent(SessionActivity.this,friend_add.class);
					startActivity(intent);
				}
				else
				{
					intent = new Intent(SessionActivity.this,CaptureActivity.class);
					startActivity(intent);
				}
				
				if (pw != null) {
					pw.dismiss();
				}
			}
		});
	}
	
	    @Override 
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		 Log.v("protected void onActivityResult", "=====111");
	        if (requestCode==REQUESTCODE) { 
	            switch (resultCode) { 
	            case RESULT_OK: 
	                String dateString=data.getExtras().getString("UserIds"); 
	           	    Log.v("protected void onActivityResult", "=====111"+dateString);
	                if(dateString!=null &&dateString!="" && dateString.split(";").length>0)
	                {
	                	
	                String fuid=dateString.split(";")[0];
	    			CustomerVo customerVo = getFinalDb().findById(fuid, CustomerVo.class);
	    			Intent intent = new Intent(SessionActivity.this,
							ChatMainActivity.class);
					intent.putExtra("data", customerVo);
					startActivity(intent);
					
	                }
	                break; 
	            default: 
	                break; 
	            } 
	        } 
	        super.onActivityResult(requestCode, resultCode, data); 
	    } 
	
	
	private void showInfo(View v) {
		CustomerVo customerVo = (CustomerVo) v.getTag();
		List<CustomerVo> friendList = getShangwupanlvApplication().getFriendListAction().getFriendList();
		try {
			if (friendList != null) {
				if(friendList.contains(customerVo)){
					customerVo.setFriend("1");
				}else{
					customerVo.setFriend("0");
				}
			}else{
				customerVo.setFriend("0");
			}
		} catch (Exception e) {
		}

		if (customerVo != null) {
			Intent intent = null;
			if (getUserInfoVo().getUid().equals(customerVo.getUid())) {
				intent = new Intent(SessionActivity.this,
						MyChattingActivity.class);
			}  else if ("1".equals(customerVo.getFriend())) {
				intent = new Intent(SessionActivity.this,
						FriendChattingActivity.class);
			}  else {
				intent = new Intent(SessionActivity.this,
						StrangerChattingActivity.class);
			}
			intent.putExtra("data", customerVo);

			startActivity(intent);
		}
	}

	private void btnDelListAction(View v) {
		// btnCancelListDialog();
		final SessionVo sessionVo = (SessionVo) v.getTag();
		if (sessionVo != null) {
			new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					getWaitDialog().setMessage("删除列表");
					getWaitDialog().show();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					if (result) {
						getWaitDialog().setMessage("删除成功!");
						adapter.removeSession(sessionVo);
					} else {
						getWaitDialog().setMessage("删除失败!");
					}

					getWaitDialog().cancel();
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					return sessionManager.delSessionList(sessionVo
							.getSessionList());
				}

			}.execute();
		} else {
			showToast("删除失败!");
		}
	}

	private void initAdaperView() {
		adapter.removeSessionListAll();
		new AsyncTask<Void, Void, List<SessionVo>>() {

			@Override
			protected void onPostExecute(List<SessionVo> result) {
				super.onPostExecute(result);
				adapter.removeSessionListAll();
				adapter.addSessionList(result);
			}

			@Override
			protected List<SessionVo> doInBackground(Void... params) {
				List<SessionVo> sessionVos = new ArrayList<SessionVo>();
				// 获取会话列表 SessionList
				List<SessionList> sessionLists = getFinalDb().findAllDESC(
						SessionList.class, "createTime");
				if (sessionLists != null && !sessionLists.isEmpty()) {
					// 获取会话信息
					for (SessionList sessionList : sessionLists) {
						try {
							CustomerVo customerVo = getFinalDb().findById(
									sessionList.getFuid(), CustomerVo.class);
							if (customerVo != null) {
								SessionVo sessionVo = new SessionVo();
								sessionVo.setCustomerVo(customerVo);
								sessionVo.setSessionList(sessionList);
								sessionVos.add(sessionVo);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
				Collections.sort(sessionVos, mSessionComparator);
				return sessionVos;
			}

		}.execute();
	}

	class ListItemOnClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position,
				long id) {
			if (!checkSdcard()) {
				return;
			}
				CustomerVo customerVo = adapter.getItem(position).getCustomerVo();
				Intent intent = new Intent(SessionActivity.this,
						ChatMainActivity.class);
				intent.putExtra("data", customerVo);
				startActivity(intent);
		}

	}

	class ItemOnLongClick implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			SessionVo sessionVo = adapter.getItem(position);
			getListDialog(sessionVo).show();
			return true;
		}

		private AlertDialog getListDialog(SessionVo sessionVo) {
			if (listDialog == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SessionActivity.this);
				builder.setTitle("删除列表");
				View view = LayoutInflater.from(getBaseContext()).inflate(
						R.layout.alert_session_action, null);
				builder.setView(view);
				listDialogView = view;
				listDialog = builder.create();
			}
			Button btnCancel = (Button) listDialogView
					.findViewById(R.id.alert_chat_main_btn_cancel);
			Button btnDelMsg = (Button) listDialogView
					.findViewById(R.id.alert_chat_main_btn_delmsg);

			btnCancel.setOnClickListener(onClickListener);
			btnDelMsg.setOnClickListener(onClickListener);
			btnDelMsg.setTag(sessionVo);

			return listDialog;
		}
	}

	/**
	 * 
	 * 功能： 会话列表适配 <br />
	 * 日期：2013-5-30<br />
	 * 地点：www.uvcims.com<br />
	 * 版本：ver 1.0<br />
	 * 
	 * @author fighter
	 * @since
	 */
	class MyAdapter extends BaseAdapter {
		private List<SessionVo> sessionLists = new ArrayList<SessionVo>();

		private boolean isEdit = false; // 当前是否是编辑的状态

		public void addSessionList(List<SessionVo> list) {
			sessionLists.addAll(list);
			notifyDataSetChanged();
		}

		public void addSession(SessionVo session) {
			sessionLists.add(session);
			notifyDataSetChanged();
		}

		public void removeSessionListAll() {
			sessionLists.clear();
			notifyDataSetChanged();
		}

		public void removeSession(SessionVo session) {
			sessionLists.remove(session);
			notifyDataSetChanged();
		}

		public List<SessionVo> getSessionVos() {
			return sessionLists;
		}

		@Override
		public int getCount() {
			return sessionLists.size();
		}

		@Override
		public SessionVo getItem(int arg0) {
			return sessionLists.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		/**
		 * 编辑
		 * 
		 * 作者:fighter <br />
		 * 创建时间:2013-6-9<br />
		 * 修改时间:<br />
		 */
		public void edit() {
			isEdit = true;
			notifyDataSetInvalidated();
		}

		/** 完成编辑 */
		public void end() {
			isEdit = false;
			notifyDataSetInvalidated();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			SessionVo sessionVo = sessionLists.get(position);

			if (convertView != null) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(SessionActivity.this)
						.inflate(R.layout.chat_list_item, null);
				viewHolder = ViewHolder.getViewHolder(convertView);
			}

			convertView.setTag(viewHolder);

			bindViewHolder(viewHolder, sessionVo);

			return convertView;
		}

		private void bindViewHolder(ViewHolder viewHolder, SessionVo sessionVo) {
			if ("1".equals(sessionVo.getCustomerVo().getSex())) {
				viewHolder.imgSex.setImageResource(R.drawable.sex_man);
			} else {
				viewHolder.imgSex.setImageResource(R.drawable.sex_woman);
			}

			String userType = sessionVo.getCustomerVo().getCustomertype();
			if (Constants.CustomerType.CHATTING.equals(userType)) {
				// 认证头像
				if("1".equals(sessionVo.getCustomerVo().getAgent())){
					viewHolder.imgSubscript.setImageResource(R.drawable.subscript_economic);
					viewHolder.imgSubscript.setVisibility(View.VISIBLE);
				}else
				if ("1".equals(sessionVo.getCustomerVo().getHeadattest())) {
					viewHolder.imgSubscript
							.setImageResource(R.drawable.subscript_auth);
					viewHolder.imgSubscript.setVisibility(View.VISIBLE);
				} else {
					viewHolder.imgSubscript.setVisibility(View.GONE);

				}
			} else {
				// 是否是VIP
				if ("1".equals(sessionVo.getCustomerVo().getVip())) {
					viewHolder.imgSubscript
							.setImageResource(R.drawable.subscript_vip);
					viewHolder.imgSubscript.setVisibility(View.VISIBLE);
				} else {
					viewHolder.imgSubscript.setVisibility(View.GONE);

				}
			}

			long time = sessionVo.getSessionList().getUpdateTime();
			String userName = TextdescTool.getCustomerName(sessionVo
					.getCustomerVo());
			int noReadNum = sessionVo.getSessionList().getNotReadNum();

			String age = sessionVo.getCustomerVo().getBirthday();
			String location = sessionVo.getCustomerVo().getLocal();
			String line = sessionVo.getCustomerVo().getOnline();
			// String sign = sessionVo.getCustomerVo().getInterest();

			viewHolder.txtUsername.setText(userName);
			viewHolder.txtLocation.setText(location);
			if (0 != noReadNum) {
				viewHolder.txtNoreadNum.setVisibility(View.VISIBLE);
				viewHolder.txtNoreadNum.setText(noReadNum + "");
			} else {
				viewHolder.txtNoreadNum.setVisibility(View.GONE);
			}

			if (isEdit) {
				viewHolder.txtTime.setVisibility(View.GONE);
				viewHolder.btnDel.setVisibility(View.VISIBLE);
				viewHolder.btnDel.setOnClickListener(onClickListener);
				viewHolder.btnDel.setTag(sessionVo);
			} else {
				viewHolder.btnDel.setVisibility(View.GONE);
				viewHolder.txtTime.setVisibility(View.VISIBLE);
				viewHolder.txtTime.setText(TextdescTool.timeDifference(time));
			}
			
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
			viewHolder.txtLine.setText(line);

			FinalOnloadBitmap.finalDisplay(getBaseContext(),
					sessionVo.getCustomerVo(), viewHolder.imgHead,
					getHeadBitmap());
			viewHolder.imgHead.setTag(sessionVo.getCustomerVo());
			viewHolder.imgHead.setOnClickListener(onClickListener);
			if (!TextUtils.isEmpty(sessionVo.getSessionList().getLastContent()))
				viewHolder.txtSign.setText(EmojiUtil.getExpressionString(
						getBaseContext(), sessionVo.getSessionList()
								.getLastContent(), ChatMainActivity.EMOJIREX));
		}

	}

	static class ViewHolder {
		ImageView imgSubscript;
		ImageView imgHead;
		ImageView imgSex;

		TextView txtUsername;
		TextView txtNoreadNum; // 未读条数
		TextView txtAge;
		TextView txtLocation;
		TextView txtLine;
		TextView txtSign;

		TextView txtTime;

		Button btnDel;

		public static ViewHolder getViewHolder(View view) {
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.imgSubscript = (ImageView) view
					.findViewById(R.id.list_item_img_subscript);
			viewHolder.imgHead = (ImageView) view
					.findViewById(R.id.list_item_img_head);
			viewHolder.imgSex = (ImageView) view
					.findViewById(R.id.list_img_sex);

			viewHolder.txtUsername = (TextView) view
					.findViewById(R.id.list_txt_title);
			viewHolder.txtNoreadNum = (TextView) view
					.findViewById(R.id.list_txt_type);
			viewHolder.txtAge = (TextView) view.findViewById(R.id.list_txt_age);
			viewHolder.txtLocation = (TextView) view
					.findViewById(R.id.list_txt_location);
			viewHolder.txtLine = (TextView) view
					.findViewById(R.id.list_txt_state);
			viewHolder.txtSign = (TextView) view
					.findViewById(R.id.list_txt_message_info);
			viewHolder.txtTime = (TextView) view
					.findViewById(R.id.list_txt_time);

			viewHolder.btnDel = (Button) view.findViewById(R.id.list_btn_del);

			return viewHolder;
		}
	}

	class ViewOnClik implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.list_btn_del:
				btnDelListAction(v);
				break;
			case R.id.alert_chat_main_btn_delmsg:
				btnDelListAction(v);
				break;
			case R.id.alert_chat_main_btn_cancel:
				btnCancelListDialog();
				break;

			case R.id.list_item_img_head:
				showInfo(v);
				break;

			default:
				break;
			}

		}
	}

	class DelSessionOnclick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			btnDelListAction(v);
		}
	}

//	@Override
	public void update(Observable observable, Object data) {
		if (data instanceof SessionList) {
			SessionList sessionList = (SessionList) data;
			SessionVo tempSessionVo = new SessionVo();
			tempSessionVo.setSessionList(sessionList);
			List<SessionVo> sessionVos = adapter.getSessionVos();
			int index = sessionVos.indexOf(tempSessionVo);
			if (index != -1) {
				SessionVo mySessionVo = sessionVos.get(index);
				tempSessionVo.setCustomerVo(mySessionVo.getCustomerVo());
				sessionVos.remove(mySessionVo);
				sessionVos.add(index, tempSessionVo);
			} else {
				CustomerVo customerVo = getFinalDb().findById(
						sessionList.getFuid(), CustomerVo.class);
				if (customerVo != null) {
					tempSessionVo.setCustomerVo(customerVo);
					sessionVos.add(0, tempSessionVo);
				}
			}

			handler.post(new Runnable() {

				@Override
				public void run() {
					adapter.notifyDataSetInvalidated();
					try {
//						IndexTabActivity.getInstance().sessionPromptUpdate();
						sendBroadcast(new Intent(IndexTabActivity.ACTION_REFRESH_SESSION));
					} catch (Exception e) {

					}
				}
			});
		}

	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_FRIENDED);
		filter.addAction(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
		filter.addAction("selectfriendback");
		registerReceiver(receiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(receiver);
	}

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.d("Session:", intent.getAction());
			if (ACTION_FRIENDED.equals(intent.getAction())) {
				initAdaperView();
			}
			else if(intent.getAction().equals("selectfriendback"))
			{
				    String dateString=intent.getStringExtra("UserIds");
				    
				    if(dateString!=null &&dateString!="" && dateString.split(";").length>0)
	                {
				     String fuid=dateString.split(";")[0];
	    			 CustomerVo customerVo = getFinalDb().findById(fuid, CustomerVo.class);
	    			 Intent intent2 = new Intent(SessionActivity.this,
					 		ChatMainActivity.class);
					 intent2.putExtra("data", customerVo);
					 startActivity(intent2);
	                }
			}
			else if (NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE
					.equals(intent.getAction())) {
				SessionList sessionList = (SessionList) intent
						.getSerializableExtra(NotifyChatMessage.EXTRAS_NOTIFY_SESSION_MESSAGE);
				if (sessionList == null) {
					return;
				}

				SessionVo tempSessionVo = new SessionVo();
				tempSessionVo.setSessionList(sessionList);
				List<SessionVo> sessionVos = adapter.getSessionVos();
				int index = sessionVos.indexOf(tempSessionVo);
				if (index != -1) {
					SessionVo mySessionVo = sessionVos.get(index);
					tempSessionVo.setCustomerVo(mySessionVo.getCustomerVo());
					sessionVos.remove(mySessionVo);
					sessionVos.add(index, tempSessionVo);
				} else {
					CustomerVo customerVo = getFinalDb().findById(
							sessionList.getFuid(), CustomerVo.class);
					if (customerVo != null) {
						tempSessionVo.setCustomerVo(customerVo);
						sessionVos.add(0, tempSessionVo);
					}
				}
				
				Collections.sort(sessionVos, mSessionComparator);

				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter.notifyDataSetInvalidated();
						try {
//							IndexTabActivity.getInstance()
//									.sessionPromptUpdate();
							sendBroadcast(new Intent(IndexTabActivity.ACTION_REFRESH_SESSION));
						} catch (Exception e) {

						}
					}
				});
			}
		}

	}
}

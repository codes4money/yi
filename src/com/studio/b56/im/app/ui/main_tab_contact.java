package com.studio.b56.im.app.ui;

import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;

import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.adapter.ObjectBaseAdapter;
import com.studio.b56.im.app.control.FriendListAction;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.app.ui.group.group_main;
import com.studio.b56.im.command.PingYinUtil;
import com.studio.b56.im.command.TextdescTool;
import com.studio.b56.im.vo.CustomerVo;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import com.studio.b56.im.app.ui.group.grouplist;
public class main_tab_contact  extends BaseActivity{
	
	@ViewInject(id = R.id.main_contat_lv22)
	private ListView mListView;
	
	private LinearLayout content,LinearLayout;
	private Button group_showbnt,searchbnt;
	private TextView title;
	private Intent intent;
	private LayoutInflater inflater;
	private TextView empty_conversation_tv;
	
	public boolean isgroupshow=false;
	private String DelJid="";  
	public int pageId=1;
	public String AllUserNum="";
	public String AllUserHtml="";
    public String delUserNick="";
    public String phone_fee="";
    public String mid="";
    private SharedPreferences mSettings;
	protected Context MContext = this;
	private SideBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	public int curGroupID=-1;//��ǰ����
	public String searchKeyword="";
	
	public static final int HEADLER_START = 101;
	public static final int HEADLER_SUCCESS = 102;
	public static final int HEADLER_ERR = 103;
	public static final int HEADLER_UPDATE = 104;
	public static final int HEADLER_INIT = 100;
	

	private ImageButton topLeft, topRight;
	private TextView topMiddle;
	
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
				//initGroupList();
				friendListAction.pushFriendList();
				//refushList();
				break;
			case HEADLER_SUCCESS:
				//refushSuccess();
				
				List<CustomerVo> customerVos = (List<CustomerVo>) msg.obj;
				 CustomerVo v=new CustomerVo();
				 v.setUid("0");
				 customerVos.add(0, v);
				 customerVos.add(0, v);
				 
				//if(customerVos != null){
					 adapter.addList(customerVos);
				//}
				
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
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		receiver = new MyBroadcast();
		this.setContentView(R.layout.main_contact);
		this.baseInit();
		registerReceiver();
		
	    try
	    {
	    mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		indexBar = (SideBar) findViewById(R.id.sideBar);  
	    indexBar.setListView(mListView); 
	    mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.main_contact_list_position, null);
	    mDialogText.setVisibility(View.INVISIBLE);
	    WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
	            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
	            WindowManager.LayoutParams.TYPE_APPLICATION,
	            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
	                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
	            PixelFormat.TRANSLUCENT);
	    mWindowManager.addView(mDialogText, lp);
	    indexBar.setTextView(mDialogText);
	    }catch(Exception e)	{}
	    
	}
	
	
	@Override
	protected void onResume() {
	super.onResume();
	
	adapter = new MyAdapter();
	mListView.setAdapter(adapter);
	mListView.setOnItemClickListener(new ListItemOnClick());
	friendListAction.clearFriendlist();
	initAdapterView();
	
    
    
	}
	
	class ListItemOnClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			CustomerVo customerVo = adapter.getItem(position);
			if(customerVo != null){
				if(customerVo.getUid().equals("0"))
				{
					Intent intent2;
					if(position==0)
					{
					 intent2 = new Intent(main_tab_contact.this, friend_add.class);
					}
					else
					{
						
					  intent2 = new Intent(main_tab_contact.this, grouplist.class);
					}
					startActivity(intent2);
				}
				else
				{
					Intent intent = null;
					intent = new Intent(main_tab_contact.this, FriendChattingActivity.class);
					intent.putExtra("data", customerVo);
					startActivity(intent);
				}
			}
			
			
		}
		
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
	protected void baseInit(){
		super.baseInit();
		
//		friendListAction = IndexTabActivity.getInstance().getFriendListAction();
		friendListAction = getShangwupanlvApplication().getFriendListAction();
		friendListAction.setHandler(handler);
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
		topLeft = (ImageButton) this.findViewById(R.id.topLeft);
		topRight = (ImageButton) this.findViewById(R.id.topRight);
		topMiddle = (TextView) this.findViewById(R.id.topMiddle);
		
		topLeft.setVisibility(View.GONE);
		topMiddle.setText(getResources().getString(R.string.contact));
		topRight.setBackgroundResource(R.drawable.add_btn);
		topRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick( View view) {
				Intent intent=new Intent(main_tab_contact.this,friend_add.class);
				startActivity(intent);
			}
		});
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
	
		
class MyAdapter extends ObjectBaseAdapter<CustomerVo> implements SectionIndexer{
			
			@Override
			public void addList(List<CustomerVo> lists) {
				super.removeAll();
				super.addList(lists);
				Log.v("public void addList", "==========="+lists.size());
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

				Log.v("public View getView", "==="+position);
				
				if(!getItem(position).getUid().equals("0"))
				{
					
					ViewHolder viewHolder = null;

					convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.contact_item, null);
					viewHolder = ViewHolder.getInstance(convertView);
					
				    convertView.setTag(viewHolder);
				    bindView(viewHolder, position);
				
				}
				else  //群组
				{
					//ViewHolder viewHolder2 = null;
					convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.friend_grouplist, null);
					//viewHolder2 = ViewHolder.getInstance(convertView);
					if(position==0)
					{
					  ((TextView) convertView.findViewById(R.id.grouplist_txt)).setText(getResources().getString(R.string.new_friend));
				      ((ImageView)convertView.findViewById(R.id.grouplist_img)).setImageResource(R.drawable.new_friend);
					}
					else
					{
						  ((TextView) convertView.findViewById(R.id.grouplist_txt)).setText(getResources().getString(R.string.my_crowd));
					      ((ImageView)convertView.findViewById(R.id.grouplist_img)).setImageResource(R.drawable.old_friend);
					}
				}
				return convertView;
			}
			
			private void bindView(ViewHolder viewHolder, int position){
				CustomerVo customerVo = getItem(position);
				
				FinalOnloadBitmap.finalDisplay(getBaseContext(), customerVo, viewHolder.contactitem3_avatar_iv, getHeadBitmap());
				
				String name = TextdescTool.getCustomerName(customerVo);
				String nickName = name.substring(0, 1);
				String catalog = null;    // 目录
				String lastCatalog = null;
				catalog = CommFun.converterToFirstSpell(nickName);  // 得到目录的第一个字符.
				// 第一列直接画出.
				// 因为 字符是进行排了序的。
				// 所以判断的时候只需要根据顺序取出字符的第一个字母进行匹配就是了
				// 当遇到字符不同时开始分组
				
				viewHolder.contactitem3_account.setText(customerVo.getUid());
				viewHolder.contactitem3_nick.setText(name);
				viewHolder.contactitem3_signature.setText(customerVo.getSign());
			
				try
				{
					
				if(position == 2){
					viewHolder.contactitem3_catalog.setVisibility(View.VISIBLE);
					viewHolder.contactitem3_catalog.setText(catalog);
				}else{
					
					CustomerVo customerVo2 = getItem(position-1);
					String name2 = TextdescTool.getCustomerName(customerVo2);
					String nickName2 = name2.substring(0, 1);
					String catalog2 = null;    // 目录
					String lastCatalog2 = null;
					catalog2 = CommFun.converterToFirstSpell(nickName2);  // 得到目录的第一个字符.
					
					if(catalog.equals(catalog2)){
						viewHolder.contactitem3_catalog.setVisibility(View.GONE);
					}else{
						viewHolder.contactitem3_catalog.setVisibility(View.VISIBLE);
						viewHolder.contactitem3_catalog.setText(catalog);
					}
				}
				
				Log.v("private void bindView", "==="+name);
				
				}catch(Exception e){}
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getPositionForSection(int section) {
				
				Log.v("getPositionForSection", "==="+section);
				
				for (int i = 2; i < this.lists.size(); i++)
				{  
					try
					{
						
					CustomerVo customerVo =this.lists.get(i);
		            String l = customerVo.getName();
					l=CommFun.converterToFirstSpell(l);
					
		            char firstChar = l.toUpperCase().charAt(0);  
		            if (firstChar == section) {  
		                return i;  
		            }  
					}catch(Exception e){}
		        } 
				return -1;
			}
			
			@Override
			public int getSectionForPosition(int position) {
				return 0;
			}
			@Override
			public Object[] getSections() {
				return null;
			}
			
			
		}
		
		
		static class ViewHolder{
			public TextView contactitem3_catalog;//����
			public TextView contactitem3_nick;//�ǳ�
			public ImageView contactitem3_avatar_iv;//ͷ��
			public ImageView contactitem3_icon;//
			public ImageView contactitem3_isonline;//
			public TextView contactitem3_account;//
			public TextView contactitem3_signature;//ǩ��
			public CheckBox contactitem3_select_cb;//ѡ��ť
			public Button contactitem3_sendmsg;//ѡ��ť
			public Button contactitem3_callphone;//ѡ��ť
			
			
			public static ViewHolder getInstance(View convertView){
				ViewHolder holder = new ViewHolder();
				holder.contactitem3_catalog=(TextView) convertView.findViewById(R.id.contactitem_catalog);
				holder.contactitem3_avatar_iv=(ImageView)convertView.findViewById(R.id.contactitem_avatar_iv);
				holder.contactitem3_isonline=(ImageView)convertView.findViewById(R.id.contactitem_isonline);
				holder.contactitem3_nick=(TextView)convertView.findViewById(R.id.contactitem_nick);
				holder.contactitem3_icon=(ImageView)convertView.findViewById(R.id.contactitem_icon);
				holder.contactitem3_account=(TextView)convertView.findViewById(R.id.contactitem_account);
				holder.contactitem3_signature=(TextView)convertView.findViewById(R.id.contactitem_signature);
				holder.contactitem3_select_cb=(CheckBox)convertView.findViewById(R.id.contactitem_select_cb);
				
				return holder;
			}
		}

		
}

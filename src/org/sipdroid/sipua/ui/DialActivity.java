package org.sipdroid.sipua.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.sipdroid.sipua.UserAgent;
import org.sipdroid.sipua.phone.MyPhoneBroadcastReceiver;
import org.sipdroid.sipua.record.CallItem;
import org.sipdroid.sipua.record.CallLogs;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewFlipper;
import java.util.Date;
import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.app.ui.IndexTabActivity;
import com.studio.b56.im.app.ui.charge_activity;
import com.studio.b56.im.application.PeibanApplication;

import android.telephony.TelephonyManager; 
import android.util.Log;

public class DialActivity extends Activity implements OnClickListener {
	public static final boolean release = false;
	public static final boolean market = false;
	  public final static String TAG = "MyPhone";   
	     
	public int callback=0;
	public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;   
    private MyPhoneBroadcastReceiver mBroadcastReceiver;  
	public static final int FIRST_MENU_ID = Menu.FIRST;
	public static final int CONFIGURE_MENU_ITEM = FIRST_MENU_ID + 1;
	public static final int ABOUT_MENU_ITEM = FIRST_MENU_ID + 2;
	public static final int EXIT_MENU_ITEM = FIRST_MENU_ID + 3;
	public static final int CHANG_USERACCOUNT = FIRST_MENU_ID + 4;
    SlidingDrawer mDetailLayout = null;
	private static AlertDialog m_AlertDlg;
	AutoCompleteTextView sip_uri_box2;
	Button createButton;
	private EditText phone;
	private ImageView delete;
	LinearLayout dial_pad_layout;
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private SoundPool spool;
	private AudioManager am = null;
	AnimationDrawable mAnim = null;
	ImageView mTitleAnimMark = null;
	LinearLayout call_log_menu_type_layout=null;
	private ListView listView;
	private ExpandableListView listView2;
	private T9Adapter adapter;
	private PeibanApplication application;
	  Animation mAnimation;
	  View mAnimationView;
	  ImageView mCallBtn;
	  int mCurrTabId = 0;
	  int mCurrentViewID = 0;
	  int mDialShowType;
	  int mDuration = 10;
	  final boolean mEnableSlide = false;
	  ViewGroup mGuideLayout = null;
	  ImageView mIndicator = null;
	  LayoutInflater mInflater = null;
	  public static boolean isUpdateCallLog = true;
	  int mClickPosition = -1;
	  public static boolean isUpdateContact;
	  
	  CallLogsAdapter mCallLogAdapter;
	  HashMap<String, CallLogs> mCallLogMap = null;
	  ExpandableListView mCallLogsList = null;
	  List<CallLogs> mGroup;
	  int mCallType = 0;
	  int pad_is_Show=1;
	  int calltype=1;
	  private SharedPreferences mSettings;
	  String phone_fee="";
	  String phone_fee2="";
	  String username="";
	  TextView dial_title_user_ID=null;
	  TextView dial_title_base_account=null;
	  ProgressBar bar=null;
	  PopupWindow mMenuView;
	  TextView mMenuTypeTextView=null;
	  ImageView mMenuTypeImageView=null;
	  LinearLayout mMenuTypeLayout=null;
	  ViewFlipper mViewFlipper;
	  
	  Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
                if(msg.what==11)
				{
					if(!phone_fee2.equals(""))
					{
						phone_fee=phone_fee2;
						Editor edit = mSettings.edit();
						edit.putString("user_phone_num", phone_fee2);	
						edit.commit();
					}
					else
					{
						phone_fee2=phone_fee;
					}
					dial_title_base_account.setText("话费:"+phone_fee.trim());
					bar.setVisibility(8);
				}
                if(msg.what==8)
				{
                	hindDialTitle();
				}
			    super.handleMessage(msg);
			}
			};
			
	  private void initCallLogs()
	  {
	    this.mCallLogMap = new HashMap();
	    ContentResolver localContentResolver = getContentResolver();
	    Uri localUri = CallLog.Calls.CONTENT_URI;
	    String[] arrayOfString = new String[6];
	    arrayOfString[0] = "number";
	    arrayOfString[1] = "name";
	    arrayOfString[2] = "type";
	    arrayOfString[3] = "date";
	    arrayOfString[4] = "duration";
	    arrayOfString[5] = "_id";
	    Cursor localCursor = localContentResolver.query(localUri, arrayOfString, null, null, " date desc");
	    int i = 0;
	    try
	    {
	      int j = localCursor.getCount();
	      while (true)
	      {
	        if (i >= j)
	        {
	          break;
	        }
	        localCursor.moveToPosition(i);
	        CallItem localCallItem = new CallItem();
	        String str1 = localCursor.getString(0);
	        String str2 = localCursor.getString(1);
	        Date localDate = new Date(Long.parseLong(localCursor.getString(3)));
	        long l1 = localCursor.getLong(4);
	        long l2 = localCursor.getLong(5);
	        CallLogs localCallLogs = (CallLogs)this.mCallLogMap.get(str1);
	        if (localCallLogs == null)
	        {
	        	localCallLogs = new CallLogs();
	 	        localCallLogs.setPhone(str1);
	 	        localCallLogs.setName(str2);
	        }
	        	localCallItem.setCallTpye(localCursor.getInt(2));
		        localCallItem.setDate(localDate);
		        localCallItem.setDuration(l1);
		        localCallItem.setId(l2);
		        localCallLogs.putPhone(localCallItem);
		        this.mCallLogMap.put(str1, localCallLogs);
		        
	        i++;
	      }
	    }
	    catch (Exception localException)
	    {}
	    
	    this.mCallLogAdapter = new CallLogsAdapter(DialActivity.this);
        this.mCallLogsList.setAdapter(this.mCallLogAdapter);
        
       /* this.mCallLogsList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener()
        {
          public void onGroupExpand(int paramInt)
          {
            DialActivity.this.mClickPosition = paramInt;
            for (int i = 0; ; i++)
            {
              if (i >= DialActivity.this.mCallLogAdapter.getGroupCount())
                break;
              
              if ((i == paramInt) || (!DialActivity.this.mCallLogsList.isGroupExpanded(i)))
                continue;
              
              DialActivity.this.mCallLogsList.collapseGroup(i);
              
            }
          }
        });*/
        
       // isUpdateCallLog = false;
        
	  }
	  
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (KeyEvent.KEYCODE_BACK == keyCode) {
				try {
//					IndexTabActivity.getInstance().callbackLocation();
					//sendBroadcast(new Intent(IndexTabActivity.ACTION_CALLBACK));
					return false;
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
			return super.onKeyDown(keyCode, event);
		}
		  
	  protected void onResume()
	  {
	    super.onResume();
//	    if (isUpdateCallLog)
//	        initCallLogs();
//	        
//	      dial_pad_layout.setVisibility(View.VISIBLE);
//      	  pad_is_Show=1;
//      	  
//		  GetUserPhoneFee();
	  }
	  
	  void isDeleteGroupCallLog(final int paramInt)
	  {
	    String str1 = ((CallLogs)this.mGroup.get(paramInt)).getPhone();
	    String str2 = null;
	    switch (this.mCallType)
	    {
	    default:
	    case 0:
	    	  str2 = "全部通话记录";
	    case 3:
	    	  str2 = "未接来电";
	    case 1:
	    	 str2 = "已接电话";
	    case 2:
	    	 str2 = "呼出电话";
	    }
	    
	     new BaseDialog.Builder(this).setTitle("删除通话记录").setMessage("将删除号码:" + str1 + " 的" + str2 + "，是否删除？").setYesListener(new BaseDialog.YesListener()
	      {
	        public void doYes()
	        {
	          DialActivity.this.deleteGroupCallLog(paramInt);
	        }
	      }).show();
	  }
	  
	  void deleteGroupCallLog(int paramInt)
	  {
	    String str1 = ((CallLogs)this.mGroup.get(paramInt)).getPhone().replace("'", "''");
	    String str2 = "number='" + str1 + "'";
	    /*
	    switch (this.mCallType)
	    {
	    case 0:
	    default:
	    case 3:
	    	 str2 = str2 + " and type = 3";
	    	 break;
	    case 1:
	    	 str2 = str2 + " and type = 1";
	    	 break;
	    case 2:
	    	 str2 = str2 + " and type = 2";
	    	 break;
	    }*/
	    
  	  getContentResolver().delete(CallLog.Calls.CONTENT_URI, str2, null);
	  initCallLogs();
	    
	  }
	  public void GetUserPhoneFee()
		{
			bar.setVisibility(0);
			   new Thread() {
					@Override
					public void run() {
						phone_fee2=CommFun.GetUserSIPFeeById(username);
						Message message = new Message();
						message.what = 11;
						handler.sendMessage(message);
					}
				
			   }.start();
		   }
		  
		
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dial_activity);
//		application = (PeibanApplication)getApplication();
//		
//		String sharedPrefsFile = "com.studio.b56.im_preferences";
//		mSettings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
//		phone_fee=mSettings.getString("user_phone_num","");
//		username=mSettings.getString(Settings.PREF_USERNAME,"");
//		
//		
//		
//		listView = (ListView) findViewById(R.id.dialer_and_call_logs_contact_list);
//		listView2 = (ExpandableListView) findViewById(R.id.dialer_call_logs_listView);
//		
//
//		
//		
//		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//		mTitleAnimMark=(ImageView) findViewById(R.id.dialer_mark);
//		call_log_menu_type_layout=(LinearLayout) findViewById(R.id.call_log_menu_type_layout);
//		this.mCallLogsList=	(ExpandableListView)findViewById(R.id.dialer_call_logs_listView);
//		
//		
//		spool = new SoundPool(11, AudioManager.STREAM_SYSTEM, 5);
//		map.put(0, spool.load(this, R.raw.dtmf0, 0));
//		map.put(1, spool.load(this, R.raw.dtmf1, 0));
//		map.put(2, spool.load(this, R.raw.dtmf2, 0));
//		map.put(3, spool.load(this, R.raw.dtmf3, 0));
//		map.put(4, spool.load(this, R.raw.dtmf4, 0));
//		map.put(5, spool.load(this, R.raw.dtmf5, 0));
//		map.put(6, spool.load(this, R.raw.dtmf6, 0));
//		map.put(7, spool.load(this, R.raw.dtmf7, 0));
//		map.put(8, spool.load(this, R.raw.dtmf8, 0));
//		map.put(9, spool.load(this, R.raw.dtmf9, 0));
//		map.put(11, spool.load(this, R.raw.dtmf11, 0));
//		map.put(12, spool.load(this, R.raw.dtmf12, 0));
//		
//		View v0 = findViewById(R.id.num_0);
//		v0.setOnClickListener(this);
//		v0.setTag("0");
//		View v1 = findViewById(R.id.num_1);
//		v1.setOnClickListener(this);
//		v1.setTag("1");
//		View v2 = findViewById(R.id.num_2);
//		v2.setOnClickListener(this);
//		v2.setTag("2");
//		View v3 = findViewById(R.id.num_3);
//		v3.setOnClickListener(this);
//		v3.setTag("3");
//		View v4 = findViewById(R.id.num_4);
//		v4.setOnClickListener(this);
//		v4.setTag("4");
//		View v5 = findViewById(R.id.num_5);
//		v5.setOnClickListener(this);
//		v5.setTag("5");
//		View v6 = findViewById(R.id.num_6);
//		v6.setOnClickListener(this);
//		v6.setTag("6");
//		View v7 = findViewById(R.id.num_7);
//		v7.setOnClickListener(this);
//		v7.setTag("7");
//		View v8 = findViewById(R.id.num_8);
//		v8.setOnClickListener(this);
//		v8.setTag("8");
//		View v9 = findViewById(R.id.num_9);
//		v9.setOnClickListener(this);
//		v9.setTag("9");
//		View v10 = findViewById(R.id.num_star);
//		v10.setOnClickListener(this);
//		v10.setTag("*");
//		View v11 = findViewById(R.id.num_jing);
//		v11.setOnClickListener(this);
//		v11.setTag("#");
//		
//		delete = (ImageView) findViewById(R.id.num_delete);
//		delete.setOnClickListener(this);
//		
//
//		
//		mTitleAnimMark.setOnClickListener(this);
//		phone = (EditText)findViewById(R.id.dialer_and_call_logs_title);
//		phone.addTextChangedListener(new TextWatcher() {
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if(null == application.getContactInfo() || application.getContactInfo().size()<1){
//				}else{
//					if(null == adapter){
//						adapter = new T9Adapter(DialActivity.this);
//						adapter.assignment(application.getContactInfo());
//						listView.setAdapter(adapter);
//						listView.setTextFilterEnabled(true);
//					}else{
//						adapter.getFilter().filter(s);
//					}
//				}
//			}
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			}
//			public void afterTextChanged(Editable s) {
//			}
//		});
//		
//		//消息的单击事件
//		listView.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				ContactInfo ci=(ContactInfo)view.getTag();
//				phone.setText(ci.getPhoneNum());
//			}
//		});
//		
//		
//		ImageView call = (ImageView) findViewById(R.id.num_call);
//		call.setOnClickListener(this);
//		
//		ImageView c2 = (ImageView) findViewById(R.id.num_contact);
//		c2.setOnClickListener(this);
//		
//		on(this,true);
//		
//	    dial_pad_layout=(LinearLayout)findViewById(R.id.main_dial_pad_layout);
//		
//		listView2.setOnTouchListener(new View.OnTouchListener()
//        {
//          public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
//          {
//        	  if(pad_is_Show==1)
//        	  {
//        		 hideDialPad();
//        	     pad_is_Show=0;
//        	  }
//        	  return false;
//          }
//          
//        });
//		
//		delete.setOnLongClickListener(new View.OnLongClickListener()
//        {
//          public boolean onLongClick(View paramView)
//          {
//        	phone.setText(""); 
//        	phone.setVisibility(View.GONE);
//  			call_log_menu_type_layout.setVisibility(View.VISIBLE);
//  			listView.setVisibility(8);
//  			listView2.setVisibility(0);
//            return false;
//          }
//        });
//		
//		
//		TextView dial_charge_btn = (TextView) findViewById(R.id.dial_charge_btn);
//		//dial_charge_btn.setOnClickListener(this);
//		
//
//		dial_charge_btn.setOnClickListener(new OnClickListener() {
//			@Override
//		public void onClick(View v) {
//				Log.v("冲值按扭", "===冲值按扭安了");
//				Intent intent=new Intent(DialActivity.this,charge_activity.class);
//				startActivity(intent);
//			}
//		});
//		
//		dial_title_user_ID = (TextView) findViewById(R.id.dial_title_user_ID);
//		dial_title_user_ID.setText("账号:"+username);
//		
//		dial_title_base_account = (TextView) findViewById(R.id.dial_title_base_account);
//		dial_title_base_account.setText("话费:"+phone_fee);
//		bar=(ProgressBar)findViewById(R.id.dial_title_capital_progress);
//		
//		mMenuTypeTextView= (TextView) findViewById(R.id.call_log_menu_type_name);
//		
//		//initMenu();
//		
//		initAndListenDetail();
//		
//		handler.sendEmptyMessageDelayed(8,3000);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.num_0:
			if (phone.getText().length() < 20) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_1:
			if (phone.getText().length() < 20) {
				play(1);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_2:
			if (phone.getText().length() < 20) {
				play(2);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_3:
			if (phone.getText().length() < 20) {
				play(3);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_4:
			if (phone.getText().length() < 20) {
				play(4);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_5:
			if (phone.getText().length() <20) {
				play(5);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_6:
			if (phone.getText().length() < 20) {
				play(6);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_7:
			if (phone.getText().length() < 20) {
				play(7);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_8:
			if (phone.getText().length() < 20) {
				play(8);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_9:
			if (phone.getText().length() < 20) {
				play(9);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_star:
			if (phone.getText().length() < 20) {
				play(11);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_jing:
			if (phone.getText().length() < 20) {
				play(12);
				input(v.getTag().toString());
			}
			break;
		case R.id.num_contact:
			
		//	Intent intent = new Intent("contact.screen.tab");
		//	sendBroadcast(intent);
			
			Intent intent = new Intent(Intent.ACTION_PICK); 
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);//vnd.android.cursor.dir/contact
			startActivityForResult(intent, 1);

			 
			
			break;
		case R.id.num_delete:
			delete();
			break;
		case R.id.dialer_mark:
			getBackAnim();
			break;
		case R.id.num_call:
			if (phone.getText().toString().length() >= 4) {
				
				if(Receiver.isFast(0)||Receiver.isFastGSM(0))
				{
					
			      if(mSettings.getBoolean("iscallback",true))
			      {
			    	  Intent intent2 = new Intent(this, callback_postui.class);
					  intent2.putExtra("num", phone.getText().toString());
					  startActivity(intent2);
					  phone.setText("");
			      }
			      else
			      {
				     call(phone.getText().toString());
			      }
			      
				}
				else
				{
					Intent intent2 = new Intent(this, callback_postui.class);
					intent2.putExtra("num", phone.getText().toString());
					startActivity(intent2);
					phone.setText("");
				}
			}
			break;
		case R.id.dial_charge_btn:
			Intent intent2 = new Intent("charge.screen.tab");
			sendBroadcast(intent2);
			break;	
			/*
		 case R.id.call_log_type_all:
			 this.mMenuTypeTextView.setText("全部通话");
		      this.mCallType = 0;
		      menuDismiss();
		      updateCallLogList();
		      break;
		 case R.id.call_log_type_miss:
		      this.mMenuTypeTextView.setText("未接来电");
		      this.mCallType = 3;
		      menuDismiss();
		      updateCallLogList();
		      break;
		 case R.id.call_log_type_incall:
		      this.mMenuTypeTextView.setText("已接来电");
		      this.mCallType = 1;
		      menuDismiss();
		      updateCallLogList();
		      break;
		 case R.id.call_log_type_outcall:
			 this.mMenuTypeTextView.setText("呼出电话");
		      this.mCallType = 2;
		      menuDismiss();
		      updateCallLogList();
		      break;*/
		 case R.id.call_log_menu_type_layout:
			 showMenu();
			 break;
		default:
			break;
		}
	}
	
	  private void menuDismiss()
	  {
	    if (this.mMenuView.isShowing())
	      this.mMenuView.dismiss();
	  }
	  
	  void updateCallLogList()
	  {
	    try
	    {
	      this.mCallLogAdapter = new CallLogsAdapter(this);
	      this.mCallLogsList.setAdapter(this.mCallLogAdapter);
	      this.mCallLogsList.invalidate();
	      return;
	    }
	    catch (Exception localException)
	    {
	    }
	  }
	  private void showMenu()
	  {
	    if ((this.mMenuView != null) && (!this.mMenuView.isShowing()))
	    {
	      this.mMenuTypeImageView.setImageDrawable(getResources().getDrawable(R.drawable.call_log_popup_down_img));
	      int i = (this.mMenuTypeLayout.getWidth() - this.mMenuView.getWidth()) / 2;
	      this.mMenuView.showAsDropDown(this.mMenuTypeLayout, i, 0);
	      this.mViewFlipper.startFlipping();
	    }
	  }
	  
	 
	private void play(int id) {
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		float value = (float)0.7 / max * current;
		spool.setVolume(spool.play(id, value, value, 0, 0, 1f), value, value);
	}
	private void input(String str) {
		int c = phone.getSelectionStart();
		String p = phone.getText().toString();
		phone.setText(p+str);
		phone.setVisibility(View.VISIBLE);
		call_log_menu_type_layout.setVisibility(View.GONE);
		listView.setVisibility(0);
		listView2.setVisibility(8);
		//phone.setText(p.substring(0, c) + str + p.substring(phone.getSelectionStart(), p.length()));
		//phone.setSelection(c + 1, c + 1);
	}
	private void delete() {
		
	    if(phone.getText().length()>0)
		{
	    	String p = phone.getText().toString();
	        phone.setText(p.substring(0, p.length() - 1));
	    }
	    
	    if(phone.getText().length()<=0)
		{
			phone.setVisibility(View.GONE);
			call_log_menu_type_layout.setVisibility(View.VISIBLE);
			
			listView.setVisibility(8);
			listView2.setVisibility(0);
		}
	    
	}
	private void call(String phone) {
		
		phone=phone.replace("-", "").replace(" ", "");
		if(calltype==1)
		{
		  Receiver.engine(this).call(phone,true);
		}
		else
		{
			Intent intent2 = new Intent(this, callback_postui.class);
			intent2.putExtra("num", phone);
			startActivity(intent2);
		}
	}
	  void hindDialTitle()
	  {
	    if (this.mDetailLayout.isOpened())
	      this.mDetailLayout.animateClose();
	  }
	  
	  void initAndListenDetail()
	  {
	    this.mDetailLayout = ((SlidingDrawer)findViewById(R.id.detial_layout));
	    this.mDetailLayout.open();
	    this.mDetailLayout.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener()
	    {
	      public void onDrawerClosed()
	      {
	        DialActivity.this.popMarkAnim();
	      }
	    });
	    this.mDetailLayout.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener()
	    {
	      public void onDrawerOpened()
	      {
	        DialActivity.this.getBackAnim();
	      }
	    });
	  }
	
	public static boolean on(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Settings.PREF_ON, Settings.DEFAULT_ON);
	}
	public void getBackAnim()
    {
	    this.mTitleAnimMark.setBackgroundResource(R.drawable.title_get_back_anim);
	    this.mAnim = ((AnimationDrawable)this.mTitleAnimMark.getBackground());
	    this.mAnim.start();
	}
	  void popMarkAnim()
	  {
	    this.mTitleAnimMark.setBackgroundResource(R.drawable.title_pop_anim);
	    this.mAnim = ((AnimationDrawable)this.mTitleAnimMark.getBackground());
	    this.mAnim.start();
	  }
	  
	public static void on(Context context,boolean on) {
		Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		edit.putBoolean(Settings.PREF_ON, on);
		edit.commit();
        if (on) Receiver.engine(context).isRegistered();
	}	@Override
	
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		Intent intent = null;

		switch (item.getItemId()) {
		case CHANG_USERACCOUNT:
			intent = null;
		//	intent = new Intent(this, org.sipdroid.sipua.ui.UserLogin.class);
		//	startActivity(intent);
			break;
		case ABOUT_MENU_ITEM:
			if (m_AlertDlg != null) 
			{
				m_AlertDlg.cancel();
			}
			m_AlertDlg = new AlertDialog.Builder(this)
			.setMessage(getString(R.string.about).replace("\\n","\n").replace("${VERSION}", getVersion(this)))
			.setTitle(getString(R.string.menu_about))
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.show();
			break;
			
		case EXIT_MENU_ITEM: 
			on(this,false);
			Receiver.pos(true);
			Receiver.engine(this).halt();
			Receiver.mSipdroidEngine = null;
			Receiver.reRegister(0);
			stopService(new Intent(this,RegisterService.class));
			finish();
			break;
			
		case CONFIGURE_MENU_ITEM: {
			try {
				intent = new Intent(this, org.sipdroid.sipua.ui.Settings.class);
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
			}
		}
			break;
		}

		return result;
	}
	
	public static String getVersion() {
		return getVersion(Receiver.mContext);
	}
	
	public static String getVersion(Context context) {
		final String unknown = "Unknown";
		
		if (context == null) {
			return unknown;
		}
		
		try {
	    	String ret = context.getPackageManager()
			   .getPackageInfo(context.getPackageName(), 0)
			   .versionName;
	    	if (ret.contains(" + "))
	    		ret = ret.substring(0,ret.indexOf(" + "))+"b";
	    	return ret;
		} catch(NameNotFoundException ex) {}
		
		return unknown;		
	}
	void call_menu(AutoCompleteTextView view)
	{
		String target = view.getText().toString();
		if (m_AlertDlg != null) 
		{
			m_AlertDlg.cancel();
		}
		if (target.length() == 0)
			m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage(R.string.empty)
				.setTitle(R.string.app_name)
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();
		else if (!Receiver.engine(this).call(target,true))
			m_AlertDlg = new AlertDialog.Builder(this)
				.setMessage(R.string.notfast)
				.setTitle(R.string.app_name)
				.setIcon(R.drawable.icon22)
				.setCancelable(true)
				.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		MenuItem m = menu.add(0, CHANG_USERACCOUNT, 0, R.string.menu_changaccount);
		m.setIcon(android.R.drawable.ic_menu_preferences);
		m = menu.add(0, ABOUT_MENU_ITEM, 0, R.string.menu_about);
		m.setIcon(android.R.drawable.ic_menu_info_details);
		m = menu.add(0, EXIT_MENU_ITEM, 0, R.string.menu_exit);
		m.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
	//	m = menu.add(0, CONFIGURE_MENU_ITEM, 0, R.string.menu_settings);
	//	m.setIcon(android.R.drawable.ic_menu_preferences);			
		return result;
	}
	
	//读电话本结果
		protected void onActivityResult(int requestCode,int resultCode,Intent data) {
			Cursor c=null;
			if(requestCode==1){//电话本
				
				Log.v("=======", "返回电话");
				
				try{
					if(data!=null){
						Log.v("=======", "返回电话2");
						
						c=getContentResolver().query(data.getData(),null,null,null, null);
						c.moveToFirst(); 
				        String phoneNum=this.getContactPhone(c); 
						phone.setText("");
						input(phoneNum.replace("-", "").replace(" ", ""));
						
					}
					
					if(c!=null){
						c.moveToFirst();
						for(int i=0;i<c.getColumnCount();i++){
							String name=c.getColumnName(i);
							String result=c.getString(i);
							if(name.equalsIgnoreCase("number")){
								phone.setText("");
								input(result.replace("-", "").replace(" ", ""));
								break;
							}
							Log.v("=======", name);
						}
						c.close();
					}
				}catch(Exception e){}   
			}
	}
		//获取联系人电话 
		@TargetApi(5)
		private String getContactPhone(Cursor cursor) 
		{ 
		 
		    int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);   
		    int phoneNum = cursor.getInt(phoneColumn);  
		    String phoneResult=""; 
		    //System.out.print(phoneNum); 
		    if (phoneNum > 0) 
		    { 
		    // 获得联系人的ID号 
		        int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID); 
		        String contactId = cursor.getString(idColumn); 
		            // 获得联系人的电话号码的cursor; 
		            Cursor phones = getContentResolver().query( 
		            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
		            null, 
		            ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId,  
		            null, null); 
		            if (phones.moveToFirst()) 
		            { 
		                    // 遍历所有的电话号码 
		                    for (;!phones.isAfterLast();phones.moveToNext()) 
		                    {                                             
		                        int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER); 
		                        int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE); 
		                        int phone_type = phones.getInt(typeindex); 
		                        String phoneNumber = phones.getString(index); 
		                        switch(phone_type) 
		                        { 
		                            case 2: 
		                                phoneResult=phoneNumber; 
		                            break; 
		                        } 
		                           //allPhoneNum.add(phoneNumber); 
		                    } 
		                    if (!phones.isClosed()) 
		                    { 
		                           phones.close(); 
		                    } 
		            } 
		    } 
		    return phoneResult; 
		}
		
		
  class CallLogsAdapter extends BaseExpandableListAdapter
		  {
		    Context context;
		    LayoutInflater inflater;
		    Resources resources;

		    public CallLogsAdapter(Context arg2)
		    {
		      Context localContext=arg2;
		      this.inflater = LayoutInflater.from(localContext);
		      this.context = localContext;
		      this.resources = localContext.getResources();
		      DialActivity.this.mGroup = new ArrayList();
		      Iterator localIterator = DialActivity.this.mCallLogMap.entrySet().iterator();
		      Map.Entry localEntry=null;
		      
		      Log.v("通话记录调试=========", "总数++"+mCallLogMap.size());
		      
		      while (localIterator.hasNext())
		      {
		        localEntry = (Map.Entry)localIterator.next();
		        if (DialActivity.this.mCallType != 0)
		          break;
		        
		        DialActivity.this.mGroup.add((CallLogs)localEntry.getValue());
		      }
		      
		      
		      CallLogs localCallLogs1 = (CallLogs)localEntry.getValue();
		      ArrayList localArrayList1 = localCallLogs1.getPhoneList();
		      ArrayList localArrayList2 = new ArrayList();
		      ArrayList localArrayList3 = new ArrayList();
		      for (int i = 0;i<localArrayList1.size(); i++)
		      {
		        CallItem localCallItem = (CallItem)localArrayList1.get(i);
		       // localArrayList2.add(localCallItem);
		        if (localCallItem.getCallTpye() == DialActivity.this.mCallType)
		        {
		        //localArrayList3.add(localCallItem);
			        localArrayList2.add(localCallItem);
		        }
		      }
		      
		      if (localArrayList2.size() > 0)
	          {
		      CallLogs localCallLogs2 = new CallLogs();
	          localCallLogs2.setName(localCallLogs1.getName());
	          localCallLogs2.setPhone(localCallLogs1.getPhone());
	         // localArrayList2.removeAll(localArrayList3);
	          localCallLogs2.setPhoneList(localArrayList2);
	          DialActivity.this.mGroup.add(localCallLogs2);
	          
	          }
		      
		      Log.v("通话记录调试222=========", "---总数++"+localCallLogs1.getPhone().length());
		      
	          DialActivity.SortComparator localSortComparator = new DialActivity.SortComparator();
	          Collections.sort(DialActivity.this.mGroup, localSortComparator);
	          
		    }

		    private String getDurdation(long paramLong)
		    {
		      String str = "";
		      if (paramLong == 0L)
		        str = "0秒";

		        long l1 = paramLong / 60L;
		        long l2 = paramLong % 60L;
		        if (l1 != 0L)
		          str = l1 + "分";
		        if (l2 != 0)
		        str = str + l2 + "秒";
		    
		        return str;
		    }

		    private String getSimpleTime(Date paramDate)
		    {
		      Calendar localCalendar1 = Calendar.getInstance();
		      localCalendar1.setTime(paramDate);
		      Date localDate = new Date();
		      Calendar localCalendar2 = Calendar.getInstance();
		      localCalendar2.setTime(localDate);
		      localCalendar2.set(11,0);
		      localCalendar2.set(12, 0);
		      String str;
		      
		      if (localCalendar1.after(localCalendar2))
		      {
		        str = new SimpleDateFormat("HH:mm").format(paramDate);
		      }
		      else
		      {
		        localCalendar2.add(Calendar.DATE, -1);
		        if (localCalendar1.after(localCalendar2))
		        {
		          str = new SimpleDateFormat("昨天").format(paramDate);
		        }
		        else
		        {
		          str = new SimpleDateFormat("M/d").format(paramDate);
		        }
		      }
		        
		       return str;
		   }

		    private String getTime(Date paramDate)
		    {
		      Calendar localCalendar1 = Calendar.getInstance();
		      localCalendar1.setTime(paramDate);
		      Date localDate = new Date();
		      Calendar localCalendar2 = Calendar.getInstance();
		      localCalendar2.setTime(localDate);
		      localCalendar2.set(11, 0);
		      localCalendar2.set(12, 0);
		      String str;
		      if (localCalendar1.after(localCalendar2))
		      {
		        str = new SimpleDateFormat("今天 HH:mm").format(paramDate);
		      
		      }
		      else
		      {
		        localCalendar2.add(Calendar.DATE, 0);
		        if (localCalendar1.after(localCalendar2))
		        {
		          str = new SimpleDateFormat("昨天 HH:mm").format(paramDate);
		        }
		        else
		        {
		          str = new SimpleDateFormat("M月d日 HH:mm").format(paramDate);
		        }
		      }
		      return str;
		    }

		    public java.lang.Object getChild(int paramInt1, int paramInt2)
		    {
		      return ((CallLogs)DialActivity.this.mGroup.get(paramInt1)).getPhoneList().get(paramInt2);
		    }

		    public long getChildId(int paramInt1, int paramInt2)
		    {
		      return paramInt2;
		    }

		    public View getChildView(int paramInt1, int paramInt2, boolean paramBoolean, View paramView, ViewGroup paramViewGroup)
		    {
		    	Log.v("通话记录调试====", " getChildView");
		    	
		    	/*
		      View localView1 = this.inflater.inflate(2130903046, null);
		      ChildrenView localChildrenView = new ChildrenView();
		      localChildrenView.layout = ((LinearLayout)localView1.findViewById(2131427415));
		      ArrayList localArrayList = ((CallLogs)DialActivity.this.mGroup.get(paramInt1)).getPhoneList();
		      int i = (int)DialActivity.this.getResources().getDimension(2131230728);
		      LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(-1, i, 1.0F);
		      int j = 0;
		      int k = localArrayList.size();
		      
		      
		      if ((j >= k) || (j >= 3))
		      {
		        if (localArrayList.size() > 3)
		        {
		          TextView localTextView1 = new TextView(this.context);
		          localTextView1.setClickable(true);
		          localTextView1.setBackgroundResource(2130837813);
		          localTextView1.setText("全部通话记录");
		          localTextView1.setLayoutParams(localLayoutParams1);
		          localTextView1.setTextSize(16.0F);
		          localTextView1.setTextColor(DialActivity.this.getResources().getColor(2131165186));
		          localTextView1.setGravity(17);
		          
		         /* localTextView1.setOnClickListener(new OnClickListener(paramInt1){
		        	public void onClick(View paramView)
		            {
			              List localList = DialActivity.CallLogsAdapter.this.getData(this.val$groupPosition);
			              ListView localListView = new ListView(DialActivity.CallLogsAdapter.this.context);
			              localListView.setFadingEdgeLength(0);
			              int i = DialActivity.this.getWindowManager().getDefaultDisplay().getHeight();
			              localListView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
			              LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, i * 2 / 3);
			              if (localList.size() > 6)
			                localListView.setLayoutParams(localLayoutParams);
			              localListView.setDivider(DialActivity.this.getResources().getDrawable(2130837633));
			              localListView.setScrollBarStyle(0);
			              Context localContext = DialActivity.CallLogsAdapter.this.context;
			              String[] arrayOfString = new String[3];
			              arrayOfString[0] = "img";
			              arrayOfString[1] = "time";
			              arrayOfString[2] = "duration";
			              int[] arrayOfInt = new int[3];
			              arrayOfInt[0] = 2131427416;
			              arrayOfInt[1] = 2131427417;
			              arrayOfInt[2] = 2131427418;
			              
		            //  localListView.setAdapter(new SimpleAdapter(localContext, localList, 2130903047, arrayOfString, arrayOfInt));
		             // localListView.setOnItemClickListener(new DialActivity.CallLogsAdapter.2.1(this, localList, new BaseDialog.Builder(DialActivity.CallLogsAdapter.this.context).addView(localListView).setTitle("全部通话记录").setNoYes(true).setCancelListener("关闭", null).show()));
		            }
		          });
		          
		          localChildrenView.layout.addView(localTextView1);
		          View localView2 = new View(this.context);
		          localView2.setBackgroundResource(2130837633);
		          localChildrenView.layout.addView(localView2);
		        }
		      }
		      
		      CallItem localCallItem = (CallItem)localArrayList.get(j);
		      TextView localTextView2 = new TextView(this.context);
		      TextView localTextView3 = new TextView(this.context);
		      localTextView2.setTextColor(this.resources.getColor(2131165186));
		      localTextView3.setTextColor(this.resources.getColor(2131165186));
		      ImageView localImageView = new ImageView(this.context);
		      LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(-2, -2);
		      LinearLayout.LayoutParams localLayoutParams3 = new LinearLayout.LayoutParams(-2, -2);
		      localLayoutParams2.setMargins(30, 5, 20, 5);
		      localLayoutParams3.setMargins(20, 0, 0, 0);
		      localImageView.setLayoutParams(localLayoutParams2);
		      Date localDate = localCallItem.getDate();
		      long l1 = localCallItem.getId();
		      long l2 = localCallItem.getDuration();
		      int m = localCallItem.getCallTpye();
		      String str1 = getTime(localDate);
		      String str2 = getDurdation(l2);
		      localTextView2.setText(str1);
		      localTextView3.setLayoutParams(localLayoutParams3);
		      switch (m)
		      {
		      default:
		        localImageView.setImageDrawable(this.resources.getDrawable(2130837521));
		        localTextView3.setText("通话时长： " + str2);
		      case 3:
			        localImageView.setImageDrawable(this.resources.getDrawable(2130837533));
			        localTextView3.setText("响铃： " + str2);
		      case 1:
		    	  localImageView.setImageDrawable(this.resources.getDrawable(2130837521));
			        localTextView3.setText("通话时长： " + str2);
		      case 2:
		    	  localImageView.setImageDrawable(this.resources.getDrawable(2130837535));
			      localTextView3.setText("通话时长： " + str2);
		      }
		      if (l2 == -1L)
		      {
		        localTextView3.setText("讯聊拨号 ");
		      }

		        localTextView2.setClickable(false);
		        localTextView3.setClickable(false);
		        localChildrenView.layout.setClickable(false);
		        LinearLayout localLinearLayout = new LinearLayout(this.context);
		        localLinearLayout.setClickable(true);
		        localLinearLayout.setLayoutParams(localLayoutParams1);
		        localLinearLayout.setOrientation(0);
		        localLinearLayout.setGravity(16);
		        localLinearLayout.addView(localImageView);
		        localLinearLayout.addView(localTextView2);
		        localLinearLayout.addView(localTextView3);
		        localLinearLayout.setBackgroundResource(2130837522);
		        
		        localLinearLayout.setOnTouchListener(new View.OnTouchListener(localLinearLayout, localTextView3, localTextView2, l1)
		        {
		          public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
		          {
		            int i = 1;
		            if (paramMotionEvent.getAction() == 0)
		            {
		              this.val$newLayout.setBackgroundResource(2130837814);
		              this.val$durtationTextView.setTextColor(DialActivity.CallLogsAdapter.this.resources.getColor(2131165203));
		              this.val$dateTextView.setTextColor(DialActivity.CallLogsAdapter.this.resources.getColor(2131165203));
		            }
		            while (true)
		            {
		              return i;
		              if (paramMotionEvent.getAction() == i)
		              {
		                this.val$newLayout.setBackgroundResource(2130837695);
		                this.val$durtationTextView.setTextColor(DialActivity.CallLogsAdapter.this.resources.getColor(2131165186));
		                this.val$dateTextView.setTextColor(DialActivity.CallLogsAdapter.this.resources.getColor(2131165186));
		                DialActivity.this.isDeleteChildCallLog(this.val$id);
		                continue;
		              }
		              if (paramMotionEvent.getAction() == 2)
		                continue;
		              this.val$newLayout.setBackgroundResource(2130837695);
		              this.val$durtationTextView.setTextColor(DialActivity.CallLogsAdapter.this.resources.getColor(2131165186));
		              this.val$dateTextView.setTextColor(DialActivity.CallLogsAdapter.this.resources.getColor(2131165186));
		              i = 0;
		            }
		          }
		        });
		        
		        localChildrenView.layout.addView(localLinearLayout);
		        
		        
		        View localView3 = new View(this.context);
		        localView3.setBackgroundResource(2130837523);
		        LinearLayout.LayoutParams localLayoutParams4 = new LinearLayout.LayoutParams(-1, -2, 1.0F);
		        localLayoutParams4.setMargins(15, 0, 15, 0);
		        localView3.setLayoutParams(localLayoutParams4);
		        localChildrenView.layout.addView(localView3);
		        j++;
		        
		        */

		       // return localView1;
		    	
		    	return paramView;
		    }

		    public int getChildrenCount(int paramInt)
		    {
		      return 1;
		    }

		    protected List<?extends Map<String, java.lang.Object>> getData(int paramInt)
		    {
		    	Log.v("通话记录调试====", "方法===List<? extends Map");
		    	
		    	
		      ArrayList localArrayList1 = ((CallLogs)DialActivity.this.mGroup.get(paramInt)).getPhoneList();
		      ArrayList localArrayList2 = new ArrayList();
		      Iterator localIterator = localArrayList1.iterator();
		      if (!localIterator.hasNext())
		        return localArrayList2;
		      
		      CallItem localCallItem = (CallItem)localIterator.next();
		      HashMap localHashMap = new HashMap();
		      int i = localCallItem.getCallTpye();
		      Date localDate = localCallItem.getDate();
		      Long localLong = Long.valueOf(localCallItem.getId());
		      String str1 = getTime(localDate);
		      long l = localCallItem.getDuration();
		      String str2 = getDurdation(l);
		      switch (i)
		      {
		      default:
		        localHashMap.put("img",  R.drawable.call_log_incall_gray);
		        localHashMap.put("time", str1);
		        localHashMap.put("duration", str2);
		        localHashMap.put("id", localLong);
		      case 3:
			        localHashMap.put("img",  R.drawable.call_log_miss_red);
			        localHashMap.put("time", "响铃:" + str1);
			        localHashMap.put("duration", str2);
			        localHashMap.put("id", localLong);
		      case 1:
		    	  localHashMap.put("img",  R.drawable.call_log_incall_gray);
			        localHashMap.put("time", "通话:" + str1);
			        localHashMap.put("duration", str2);
			        localHashMap.put("id", localLong);
		      case 2:
		    	    localHashMap.put("img", R.drawable.call_log_outcall_gray);
			        localHashMap.put("time", "通话:" + str1);
			        if (l == -1L)
			          str2 = "讯聊拨号";
			        localHashMap.put("duration", str2);
			        localHashMap.put("id", localLong);
		      }
		      localArrayList2.add(localHashMap);
		      return localArrayList2;
		    }

		    public java.lang.Object getGroup(int paramInt)
		    {
		      return DialActivity.this.mGroup.get(paramInt);
		    }

		    public int getGroupCount()
		    {
		      return DialActivity.this.mGroup.size();
		    }

		    public long getGroupId(int paramInt)
		    {
		      return paramInt;
		    }

		    public View getGroupView(final int paramInt, boolean paramBoolean, View paramView, ViewGroup paramViewGroup)
		    {
		    	
		    	Log.v("通话记录调试====", "方法===getGroupView");
		    	
		      GroupView localGroupView = new GroupView();
		      if (paramView == null)
		        paramView = this.inflater.inflate(R.layout.call_log_group_layout, null);
		      
		      
		      localGroupView.layout = ((RelativeLayout)paramView.findViewById(R.id.call_log_right_layout));
		      localGroupView.callImageLayout = ((LinearLayout)paramView.findViewById(R.id.call_log_call_layout));
		      localGroupView.typeImageView = ((ImageView)paramView.findViewById(R.id.call_log_type_img));
		      localGroupView.nameTextView = ((TextView)paramView.findViewById(R.id.call_log_name));
		      localGroupView.countTextView = ((TextView)paramView.findViewById(R.id.call_log_count));
		      localGroupView.dateTextView = ((TextView)paramView.findViewById(R.id.call_log_date));
		      localGroupView.phoneTextView = ((TextView)paramView.findViewById(R.id.call_log_phone));
		      String str1 = ((CallLogs)DialActivity.this.mGroup.get(paramInt)).getName();
		      String str2 = ((CallLogs)DialActivity.this.mGroup.get(paramInt)).getPhone();
		      int i = ((CallLogs)DialActivity.this.mGroup.get(paramInt)).getPhoneList().size();
		      int j=1;
		      String str3="";
		      try
		      {
		      j = ((CallItem)((CallLogs)DialActivity.this.mGroup.get(paramInt)).getPhoneList().get(0)).getCallTpye();
		      }catch(Exception e){}
		      
		      try
		      {
		      str3 = getSimpleTime(((CallItem)((CallLogs)DialActivity.this.mGroup.get(paramInt)).getPhoneList().get(0)).getDate());
		      }catch(Exception e){}
		      
		      
		      localGroupView.dateTextView.setText(str3);
		      
		        if ((str1 != null) && (str1.length() != 0))
		        {
		          if (str2.startsWith("-"))
		          {
		            localGroupView.nameTextView.setText("未知号码");
		            localGroupView.phoneTextView.setText(str2);
		          }
		          else
		          {
		        	  localGroupView.nameTextView.setText(str1);
			          localGroupView.phoneTextView.setText(str2);
		          }
		        } 
		        else
		        {
		        	  localGroupView.nameTextView.setText(str2);
			          localGroupView.phoneTextView.setText(str2);
		        }

		        
		          if (i <= 1)
		          {
		        	 localGroupView.countTextView.setText("");
		          }
		          else
		          {
		            localGroupView.countTextView.setText("(" + i + ")");
		          }
		          
		          
		      switch (j)
		      {
		      default:
		      case 3:
		    	localGroupView.typeImageView.setImageDrawable(this.resources.getDrawable(R.drawable.call_log_miss_call));
		        localGroupView.nameTextView.setTextColor(this.resources.getColor(R.color.red));
		        break;
		      case 1:
		    	  localGroupView.typeImageView.setImageDrawable(this.resources.getDrawable(R.drawable.call_log_incall));
			      localGroupView.nameTextView.setTextColor(this.resources.getColor(R.color.black));
			      break;
		      case 2:  
		    	  localGroupView.typeImageView.setImageDrawable(this.resources.getDrawable(R.drawable.call_log_outcall));
			      localGroupView.nameTextView.setTextColor(this.resources.getColor(R.color.black));
		    	  break;
		      }
		      
	    	  if (paramBoolean)
	    	  {
	    		  localGroupView.typeImageView.setVisibility(4);
			      localGroupView.dateTextView.setVisibility(8);
	    	  }
	    	  else
	    	  {
		        localGroupView.typeImageView.setVisibility(0);
		        localGroupView.dateTextView.setVisibility(0);
	    	  }
	    	  
	    	  localGroupView.callImageLayout.setOnClickListener(new View.OnClickListener()
	          {
	            public void onClick(View paramView)
	            {
	              String str2 = ((CallLogs)DialActivity.this.mGroup.get(paramInt)).getPhone();
	             // DialActivity.this.call(str2);
			      if(mSettings.getBoolean("iscallback",true))
			      {
			    	  Intent intent2 = new Intent(DialActivity.this, callback_postui.class);
					  intent2.putExtra("num",str2);
					  startActivity(intent2);
					  phone.setText("");
			      }
			      else
			      {
				     call(str2);
			      }
	            }
	          });
	    	  
		     localGroupView.layout.setClickable(true);
		     localGroupView.callImageLayout.setClickable(true);
		     
		     localGroupView.layout.setOnLongClickListener(new View.OnLongClickListener()
	          {
	            public boolean onLongClick(View paramView)
	            {
	              DialActivity.this.isDeleteGroupCallLog(paramInt);
	              return false;
	            }
	          });
		     
		     return paramView;
		     
		      /*
		      switch (j)
		      {
		      default:
		        if ((str1 == null) || (str1.length() == 0))
		        {
		          if (!str2.startsWith("-"))
		            break;
		          localGroupView.nameTextView.setText("未知号码");
		          label335: if (i <= 1)
		            break label632;
		          localGroupView.countTextView.setText("(" + i + ")");
		          label373: if (!str2.startsWith("-"))
		            break label645;
		          localGroupView.phoneTextView.setText("未知号码");
		         
		          /*
		          label395: localGroupView.layout.setOnClickListener(new View.OnClickListener(paramBoolean, paramInt)
		          {
		            public void onClick(View paramView)
		            {
		              DialActivity.this.hideDialPad();
		              if (!this.val$isExpanded)
		                DialActivity.this.mCallLogsList.expandGroup(this.val$groupPosition);
		              while (true)
		              {
		                return;
		                DialActivity.this.mCallLogsList.collapseGroup(this.val$groupPosition);
		              }
		            }
		          });
		          localGroupView.layout.setOnLongClickListener(new View.OnLongClickListener(paramInt)
		          {
		            public boolean onLongClick(View paramView)
		            {
		              DialActivity.this.isDeleteGroupCallLog(this.val$groupPosition);
		              return false;
		            }
		          });
		          localGroupView.callImageLayout.setOnClickListener(new View.OnClickListener(paramInt)
		          {
		            public void onClick(View paramView)
		            {
		              String str1 = ((CallLogs)DialActivity.this.mGroup.get(this.val$groupPosition)).getName();
		              String str2 = ((CallLogs)DialActivity.this.mGroup.get(this.val$groupPosition)).getPhone();
		              long l = ((CallLogs)DialActivity.this.mGroup.get(this.val$groupPosition)).getContactID();
		              DialActivity.this.call(str1, str2, l);
		            }
		          });
		          
		          
		          if (paramBoolean)
		            break label658;
		          localGroupView.typeImageView.setVisibility(0);
		          localGroupView.dateTextView.setVisibility(0);
		        }
		      case 3:
		      case 1:
		      case 2:
		      }
		      while (true)
		      {
		        localGroupView.layout.setClickable(true);
		        localGroupView.callImageLayout.setClickable(true);
		        return paramView;
		        localGroupView.typeImageView.setImageDrawable(this.resources.getDrawable(2130837532));
		        localGroupView.nameTextView.setTextColor(this.resources.getColor(2131165191));
		        break;
		        localGroupView.typeImageView.setImageDrawable(this.resources.getDrawable(2130837520));
		        localGroupView.nameTextView.setTextColor(this.resources.getColor(2131165186));
		        break;
		        localGroupView.typeImageView.setImageDrawable(this.resources.getDrawable(2130837534));
		        localGroupView.nameTextView.setTextColor(this.resources.getColor(2131165186));
		        break;
		        localGroupView.nameTextView.setText(str2);
		        break label335;
		        localGroupView.nameTextView.setText(str1);
		        break label335;
		        label632: localGroupView.countTextView.setText("");
		        break label373;
		        label645: localGroupView.phoneTextView.setText(str2);
		        break label395;
		        label658: localGroupView.typeImageView.setVisibility(4);
		        localGroupView.dateTextView.setVisibility(8);
		      }
		      */
		      
		      
		      
		    }

		    public boolean hasStableIds()
		    {
		      return false;
		    }

		    public boolean isChildSelectable(int paramInt1, int paramInt2)
		    {
		      return true;
		    }

		    class ChildrenView
		    {
		      LinearLayout layout;

		      ChildrenView()
		      {
		      }
		    }

		    class GroupView
		    {
		      LinearLayout callImageLayout;
		      TextView countTextView;
		      TextView dateTextView;
		      RelativeLayout layout;
		      TextView nameTextView;
		      TextView phoneTextView;
		      ImageView typeImageView;

		      GroupView()
		      {
		      }
		    }
		  }
		
  public void showDialPad()
  {
	 pad_is_Show=1;
     this.dial_pad_layout.setVisibility(0);
     if(calltype==1)
     {
      ((IndexTabActivity)getParent()).changeDialIcon(1);
     }
     else
     {
       ((IndexTabActivity)getParent()).changeDialIcon(1);
     }
  }
  void setCallType(int t)
  {
	  calltype=t;
  }
  
  public void hideDialPad()
  {
	pad_is_Show=0;
    this.dial_pad_layout.setVisibility(8);
    if(calltype==1)
    {
    ((IndexTabActivity)getParent()).changeDialIcon(2);
    }
    else
    {
     ((IndexTabActivity)getParent()).changeDialIcon(2);
    }
  }
  
  
static class SortComparator implements Comparator<java.lang.Object>
{
  public int compare(java.lang.Object paramObject1, java.lang.Object paramObject2)
  {
    CallLogs calllogs = (CallLogs)paramObject1;
    CallLogs calllogs1 = (CallLogs)paramObject2;
    try
    {
	if (((CallItem)calllogs.getPhoneList().get(0)).getDate().after(((CallItem)calllogs1.getPhoneList().get(0)).getDate()))
    {
    	return -1;
    }
    else
    {
    	return 1;    
    }
    }catch(Exception e){return 1;}
 }
}
}

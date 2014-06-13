package com.studio.b56.im.app.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.sipdroid.sipua.ui.DialActivity;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Settings;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.LocationShared;
import com.studio.b56.im.app.MainBroadcastReceiver;
import com.studio.b56.im.app.PromptDialog;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.app.control.FriendListAction;
import com.studio.b56.im.app.ui.RegisterActivity.WebRegAjaxCallBack;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.service.receiver.NotifyChatMessage;
import com.studio.b56.im.service.receiver.NotifySystemMessage;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.NotifiyVo;
import com.studio.b56.im.vo.SessionList;
import com.studio.b56.im.vo.UserInfoVo;


public class IndexTabActivity extends TabActivity  implements TabHost.OnTabChangeListener{
	/** 通过广播属性列表 */
	public static final String ACTION_REFRESH_SESSION = "intent.action.ACTION_REFRESH_SESSION";
	
	/** 通过广播刷新通知 */
	public static final String ACTION_REFRESH_NOTIFIY = "intent.action.ACTION_REFRESH_NOTIFIY";
	/** 切换到首页 */
	public static final String ACTION_CALLBACK = "intent.action.ACTION_CALLBACK";
	/** 退出 */
	public static final String ACTION_EXIT_PEIBAN = "intent.action.ACTION_EXIT_PEIBAN";
	/** 刷新好友列表 */
	public static final String ACTION_REFRESH_FRIEND = "intent.action.ACTION_REFRESH_FRIEND";
	/** 注销 */
	public static final String ACTION_LOGIN_OUT = "intent.action.ACTION_LOGIN_OUT";
	
	
	private TabHost mTabHost;
	private FriendListAction friendListAction;
	private String[] tabTags = new String[]{ "chat", "friend","shop","notation","grzx"};
	private String[] tabDescs = new String[]{"会话","通讯录","拨号","发现","我"};
	private int[] tabImgIds = new int[]{R.drawable.bottom_home_bn_style, R.drawable.bottom_style_bn_style,R.drawable.arrow_down,R.drawable.bottom_cam_bn_style,R.drawable.bottom_shopping_bn_style};
	
	private String currTag = tabTags[0];
	private static IndexTabActivity instance;
	private UserInfoVo userInfoVo;
	
	private MainBroadcastReceiver receiver;
	
	private Handler handler = new MyHandler();
	
	private PromptDialog promptDialog;
	
	private TextView sessionPrompt;			// 会话提示框
	private TextView notifyPrompt;			// 通知提示框
	
	private FinalDb finalDb;
	private PeibanApplication application;
	
	private StartServiceTask serviceTask;
	private Timer timer;
	private ImageView phoneimg;
	public int dialpadImg=1;
	int   fileSize;  
	int   downLoadFileSize;  
	private static final String TAG = "Update";
	public ProgressDialog pBar;
	private int newVerCode = 0;
	private String newVerName = "";
	public int vercode= 0;
	public int keybacknum=0;
    private SharedPreferences mSettings;
    
	Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg)  
        {
          if (!Thread.currentThread().isInterrupted())  
          {  
            switch (msg.what)  
            {  
              case 0:  
            	pBar.setMax(fileSize);  
              case 1:   
            	pBar.setProgress(downLoadFileSize);   
                break; 
              case 2:
            	pBar.cancel();
              case 4:
            	  doNewVersionUpdate();
              case 8:
            	  keybacknum=0;
            	  break; 
            }  
          }
        }
	};
	public static void on(Context context,boolean on) {
		Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		edit.putBoolean(Settings.PREF_ON, on);
		edit.commit();
        if (on) Receiver.engine(context).isRegistered();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.index_tab_mini);
		String sharedPrefsFile = "com.studio.b56.im_preferences";
		mSettings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
		application = (PeibanApplication) getApplication();
		receiver = new MainBroadcastReceiver(getBaseContext(), handler);
		registerRecevier();
		userInfoVo = application.getUserInfoVo();
		if(userInfoVo == null){
			Toast.makeText(getBaseContext(), "内存不足!", Toast.LENGTH_SHORT).show();
			finish();
			System.exit(0);
			return;
		}
		finalDb = FinalFactory.createFinalDb(getBaseContext(), userInfoVo);
		friendListAction = new FriendListAction(getBaseContext(), userInfoVo, null);
		application.setFriendListAction(friendListAction);
		initIndexInfo();
		initWidget();
		getTab();
		
		checkIntent(getIntent());
		serviceTask = new StartServiceTask(IndexTabActivity.this);
		timer = new Timer("启动服务.");
		timer.scheduleAtFixedRate(serviceTask, 0, 5000);
		
		
		//注册sip
		Receiver.engine(this).registerMore();
		on(this,true);
		
		
		//检查升级
		vercode=Config.getVerCode(this);
		checkUpdateFun();
		
		
		AjaxParams params = new AjaxParams();
		params.put("action", "login");
		FinalHttp finalHttp = new FinalHttp();
		finalHttp.post("http://www.uulm.com/system/_api/_software/mobilexlviplevel.api.php?Action=vipCheck&key=19891011&phone="+userInfoVo.getPhone(), params, new WebRegAjaxCallBack());
		
		
	}
	
class WebRegAjaxCallBack extends ClientAjaxCallback{
		@Override
		public void onStart() {
			super.onStart();
		}
		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			t=t.trim();
			if(t.equals("-1"))
			{
				Editor edit = mSettings.edit();
				edit.putBoolean("UserVIPFlag",false);	 	
				edit.commit();
			}
			else
			{
				Editor edit = mSettings.edit();
				edit.putBoolean("UserVIPFlag",true);	 	
				edit.commit();
			}
			
		}
		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
		}
		
	}


	@Override
	protected void onResume() {
	super.onResume();
	
	serviceTask = new StartServiceTask(IndexTabActivity.this);
	timer = new Timer("启动服务.");
	timer.scheduleAtFixedRate(serviceTask, 0, 5000);
	
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		instance = this;
		checkIntent(intent);
	}




	private void checkIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		if(bundle != null){
			String tag = bundle.getString("tag");
			if(!TextUtils.isEmpty(tag)){
				currTag = tag;
				mTabHost.setCurrentTabByTag(currTag);
			}
		}
		
	}

	private void initIndexInfo() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				friendListAction.pushFriendList();
				//if(friendListAction.isLocalFriendInfo()){
				//	friendListAction.getFriendList();
			   // };
			
				return null;
			}
		}.execute();
	}

//	public static IndexTabActivity getInstance(){
//		return instance;
//	}
	
	private void initWidget() {
		mTabHost = getTabHost();
		startService(new Intent(getBaseContext(), SnsService.class));
	}
	
	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(KeyEvent.KEYCODE_BACK == keyCode)
		{
			pointExit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}*/
	
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_BACK) {   
    		keybacknum=keybacknum+1;
    		if(keybacknum>=2)
    		{
    			
    		keybacknum=0;
        	Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
            mHomeIntent.addCategory(Intent.CATEGORY_HOME);
            mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(mHomeIntent);
            
    		}
    		else
    		{
    			handler2.sendEmptyMessageDelayed(8,3000);//两滗后重置
    			Toast.makeText(getBaseContext(), "再按一次【返回键】程序将后台运行!", Toast.LENGTH_SHORT).show();
    		}
            return false;   
       }   
        return false;   
    } 
	
	// 退出程序提示
	void pointExit()
	{
		if(promptDialog == null)
		{
			promptDialog = new PromptDialog(IndexTabActivity.this);
			promptDialog.setMessage("是否要退出？");
			promptDialog.addCannel();
			promptDialog.addConfirm(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				    on(IndexTabActivity.this,false);
					Receiver.pos(true);
					Receiver.engine(IndexTabActivity.this).halt();
					Receiver.mSipdroidEngine = null;
					Receiver.reRegister(0);
					
					finish();
					sendBroadcast(new Intent(BaseActivity.ACTION_EXIT));
					promptDialog.cancel();
				}
			});
		}
		
		promptDialog.show();
	}
	
	
	void callbackLocation(){
		currTag = tabTags[0];
		mTabHost.setCurrentTabByTag(currTag);
	}
	
	/**
	 * 添加tabhost菜单
	 */
	
	private void getTab(){
		Intent[] tabContents = new Intent[]{
				new Intent(this,  SessionActivity.class),
				new Intent(this,  main_tab_contact.class),
				new Intent(this,  DialActivity.class),
				new Intent(this,  ApproximatelyActivity.class),
				new Intent(this,  IndividualCenterActivity.class)
		};
		
		for (int i = 0; i < tabTags.length; i++) {
			View view = LayoutInflater.from(this).inflate(R.layout.tab_mini, null);
			ImageView img = (ImageView) view.findViewById(R.id.tab_img);
			
			if(0 == i){
				sessionPrompt = (TextView) view.findViewById(R.id.txt_subscript);
			}
			
			if(4 == i){
				notifyPrompt = (TextView) view.findViewById(R.id.txt_subscript);
			}
			
			img.setBackgroundResource(tabImgIds[i]);
			TextView desc = (TextView) view.findViewById(R.id.tab_desc);
			desc.setText(tabDescs[i]);
			mTabHost.addTab(mTabHost.newTabSpec(tabTags[i])
					.setIndicator(view).setContent(tabContents[i]))
			;
		}
		
		sessionPromptUpdate();
		notifyPromptUpdate();
		
		mTabHost.setCurrentTabByTag(currTag);
		mTabHost.setOnTabChangedListener(this);
		
		getTabWidget().getChildAt(2).setOnClickListener(new OnClickListener() { 
            @Override 
            public void onClick(View v) { 
            	if(!currTag.equals("shop"))
            	{
        	      mTabHost.setCurrentTabByTag("shop");
        	      currTag="shop";
            	}
		        else
		        {
		        
		          phoneimg=((ImageView)mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_img));
            	  DialActivity localDialActivity = (DialActivity)getCurrentActivity();
          	      if (dialpadImg != 1)
          	      {
          	    	dialpadImg=1;
          	       localDialActivity.showDialPad();
          	       phoneimg.setBackgroundResource(R.drawable.arrow_down);
          	       
          	      }
          	      else
          	      {
          	    	dialpadImg=2;
          	        localDialActivity.hideDialPad();
          	        phoneimg.setBackgroundResource(R.drawable.arrow_up); 
          	      }
          	      
            	//  onTabChanged("shop");
            	  
          	      
		        }
            	
            } 
        }); 

	}
	
	public void changeDialIcon(int paramInt)
	{
	  dialpadImg=paramInt;
	  phoneimg=((ImageView)this.mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_img));
	  if(dialpadImg!=1)
	  {
		  phoneimg.setBackgroundResource(R.drawable.arrow_up);
	  }
	  else
	  {
		  phoneimg.setBackgroundResource(R.drawable.arrow_down); 
	  }
	}
	
	/**
	 * 
	 * @author fighter <br />
	 * 创建时间:2013-6-19<br />
	 * 修改时间:<br />
	 */
	public void sessionPromptUpdate(){
		try {
			List<SessionList> vos = finalDb.findAllByWhere(SessionList.class, "notReadNum != 0 and notReadNum != '0'");
			if(vos == null || vos.isEmpty()){
				sessionPrompt.setVisibility(View.GONE);
			}else{
				int num = 0;
				for (SessionList session : vos) {
					num = num + session.getNotReadNum();
				}
				if(0 == num){
					sessionPrompt.setVisibility(View.GONE);
				}else{
					sessionPrompt.setVisibility(View.VISIBLE);
					sessionPrompt.setText("" + num);
				}
			}
			
			
		} catch (Exception e) {
			sessionPrompt.setVisibility(View.GONE);
		}
	}
	
	public void notifyPromptUpdate(){
		try {
			List<NotifiyVo> vos = finalDb.findAllByWhere(NotifiyVo.class, "state = '" + NotifiyVo.STATE_NO_FINISH + "'");
			if(vos == null || vos.isEmpty()){
				notifyPrompt.setVisibility(View.GONE);
			}else{
				int num = vos.size();
				notifyPrompt.setVisibility(View.VISIBLE);
				notifyPrompt.setText("" + num);
			}
		} catch (Exception e) {
			notifyPrompt.setVisibility(View.GONE);
		}
	}
	
	private void registerRecevier(){
		IntentFilter filter = new IntentFilter(NotifyChatMessage.ACTION_NOTIFY_CHAT_MESSAGE);
		filter.addAction(NotifySystemMessage.ACTION_NOTIFY_SYSTEM_MESSAGE);
		registerReceiver(receiver, filter);
		
		filter = new IntentFilter();
		filter.addAction(ACTION_REFRESH_NOTIFIY);
		filter.addAction(ACTION_REFRESH_SESSION);
		filter.addAction(ACTION_CALLBACK);
		filter.addAction(ACTION_EXIT_PEIBAN);
		filter.addAction(ACTION_REFRESH_FRIEND);
		filter.addAction(ACTION_LOGIN_OUT);
		filter.addAction(NotifySystemMessage.ACTION_VIP_STATE);
		registerReceiver(refreshUiReceiver, filter);
	}
	private void unregisterRecevier(){
		unregisterReceiver(receiver);
		unregisterReceiver(refreshUiReceiver);
	}
	
	@Override
	protected void onDestroy(){
		try {
			timer.cancel();
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancelAll();
		} catch (Exception e) {
		}
		super.onDestroy();
		unregisterRecevier();
		FinalFactory.createFinalBitmap(getBaseContext()).clearMemoryCache();
		LocationShared locationShared= LocationShared.getInstance(IndexTabActivity.this);
		locationShared.setLocationaddr("");
		locationShared.setLocationlat("");
		locationShared.setLocationlon("");
		locationShared.commitLoc();
	}
	
	/**
	 * @return the friendListAction
	 */
	public FriendListAction getFriendListAction() {
		return friendListAction;
	}
	
	class MyHandler extends Handler{
		
	}

	private final class StartServiceTask extends TimerTask{
		private Context context;
		StartServiceTask(Context context){
			this.context = context;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), SnsService.class);
			this.context.startService(intent);
		}
	}
	
	/** 使用广播通知进行修改UI */
	private BroadcastReceiver refreshUiReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if(ACTION_REFRESH_SESSION.equals(action)){
				sessionPromptUpdate();
			}else if(ACTION_REFRESH_NOTIFIY.equals(action)){
				notifyPromptUpdate();
			}else if(ACTION_CALLBACK.equals(action)){
				callbackLocation();
			}else if(ACTION_REFRESH_FRIEND.equals(action)){
				getFriendListAction().pushFriendList();
			}else if(ACTION_EXIT_PEIBAN.equals(action)){
				pointExit();
			}else if(ACTION_LOGIN_OUT.equals(action)){
				try {
					timer.cancel();
				} catch (Exception e) {
				}
			}else if(NotifySystemMessage.ACTION_VIP_STATE.equals(action)){
				CustomerVo customerVo = (CustomerVo) intent.getSerializableExtra(NotifySystemMessage.EXTRAS_VIP);
				application.setCustomerVo(customerVo);
			}
		}
	};

	@Override
	public void onTabChanged(String arg0) {
		
		currTag=arg0;
		if(arg0.equals("shop"))
		{
			
		  phoneimg=((ImageView)this.mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.tab_img));
		  if(dialpadImg==1)
		  {
			  phoneimg.setBackgroundResource(R.drawable.arrow_down);
		  }
		  else
		  {
			  phoneimg.setBackgroundResource(R.drawable.arrow_up); 
		  }
		  
		}
	}
	 
	public void checkUpdateFun()
	   {
		   new Thread() {
				@Override
				public void run() {
					if (getServerVerCode()) {
						if (newVerCode > vercode) {
							sendMsg(4);
						}
					}
				}
			
		   }.start();
	   }
	private void doNewVersionUpdate() {
		int verCode = Config.getVerCode(this);
		String verName = Config.getVerName(this);
		StringBuffer sb = new StringBuffer();
		sb.append("当前版本:");
		sb.append(verName);
		sb.append(", 发现新版本:");
		sb.append(newVerName);
		sb.append(", 是否更新?");
		Dialog dialog = new AlertDialog.Builder(IndexTabActivity.this)
				.setTitle("讯聊更新")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								pBar = new ProgressDialog(IndexTabActivity.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								pBar.setProgress(10);
								//pBar.setMax(100);
								pBar.show();
								
								new Thread(new Runnable(){
								     @Override
									public void run()
								         {
											downFile(Config.UPDATE_SERVER
													+ Config.UPDATE_APKNAME);
								         }
								}).start();	

							}
						})
						
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								//finish();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}
	
	private boolean getServerVerCode() {
		try {
			String verjson = CommFun.posturl(Config.UPDATE_SERVER + Config.UPDATE_VERJSON);
			Log.e(TAG, verjson);
			
			if(verjson!="")
			{
				newVerCode=Integer.parseInt(verjson.split("\\|")[0].toString());
				newVerName =verjson.split("\\|")[1];
			}
			else
			{
				newVerCode = -1;
				newVerName = "";
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}
	
	
	void downFile(final String url) {
		new Thread() {
			@Override
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					fileSize=(int)length;
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {

						File file = new File(
								Environment.getExternalStorageDirectory(),
								Config.UPDATE_SAVENAME);
						fileOutputStream = new FileOutputStream(file);

						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						sendMsg(0);
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							downLoadFileSize+=ch;
							sendMsg(1);
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					sendMsg(2);
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();
	}
	
	void down() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				update();
			}
		});
	}
	
	void update() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), Config.UPDATE_SAVENAME)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	private void sendMsg(int flag)  
    {  
	        Message msg = new Message();  
	        msg.what = flag;  
	        handler2.sendMessage(msg);  
	 }
	
}

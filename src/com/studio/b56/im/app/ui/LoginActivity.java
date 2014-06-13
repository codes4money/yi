package com.studio.b56.im.app.ui;

import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Settings;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.SharedStorage;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.LocalAuth;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.api.UserInfoApi;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.ui.common.RegisterUtil;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.command.NetworkUtils;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;

public class LoginActivity extends BaseRegisterActivity{
	public static final String ACTION_RECEIVER = "android.intent.login.COLSE";
	
	/** 记住密码.. */
	private static final String REMEMBER_KEY = "remember_password";
	
	@ViewInject(id = R.id.userName)
	private EditText editUsername;
	@ViewInject(id = R.id.userPsw)
	private EditText editPassword;
	@ViewInject(id = R.id.login_checkBox_remember_password)
	private CheckBox checkBoxRememberPassword;
	@ViewInject(id = R.id.login_unremember_password)
	private TextView textUnrememberPassword;
	@ViewInject(id = R.id.loginBtn)
	private Button btnLogin;
	@ViewInject(id = R.id.regBtn)
	private Button btnRegister;
	private LoginOnClickLisener onClickLisener;    // 登录页面功能按钮控制
	private SharedPreferences sharedPreferences;   // 配置文件
	private UserInfoCache userInfoCache;		  // 用户缓存
	private UserInfoVo userInfoVo;               // 登录用户模型
	private boolean oldRemenberState;           // 记住密码的状态
	
	private PeibanApplication application;
	public SharedPreferences settings;
	private Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sharedPrefsFile = "com.studio.b56.im_preferences";
		settings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
		setContentView(R.layout.login);
		baseInit();
	}
	

	public static void on(Context context,boolean on) {
		Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		edit.putBoolean(Settings.PREF_ON, on);
		edit.commit();
        if (on) Receiver.engine(context).isRegistered();
	}
	
	
	@Override
	protected void baseInit() {
		super.baseInit();
		
		init();
		initIntent();
		addLisener();
		application = (PeibanApplication) getApplication();
	}
	
	private void initIntent() {
		Bundle bundle = getIntent().getExtras();
		if(bundle == null){
			return;
		}
		boolean action = bundle.getBoolean("action", false);
		if(action){
			String userName = bundle.getString("username");
			String password = bundle.getString("password");
			
			if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)){
				editPassword.setText(password);
				editUsername.setText(userName);
				loginAction();
			}
		}
	}


	private void init() {
		userInfoCache = new UserInfoCache(getBaseContext());
		sharedPreferences = SharedStorage.getConfigSharedPreferences(getBaseContext());
		oldRemenberState  = sharedPreferences.getBoolean(REMEMBER_KEY, false);
		checkBoxRememberPassword.setChecked(oldRemenberState);	
		if(oldRemenberState){
			// TODO 读取本地保存的用户信息到文本框。
			userInfoVo = userInfoCache.getCacheUserInfo();
			if(userInfoVo != null){
				editPassword.setText(userInfoVo.getPassword());
				editPassword.requestFocus();
				editPassword.setSelection(userInfoVo.getPassword().length());
				editUsername.setText(userInfoVo.getPhone());
			}
		}
	}

	private void addLisener() {
		onClickLisener = new LoginOnClickLisener();
		btnLogin.setOnClickListener(onClickLisener);
		btnRegister.setOnClickListener(onClickLisener);
		textUnrememberPassword.setOnClickListener(onClickLisener);
	}
	
	// 登录
	void loginAction(){
		// 关闭所有功能。
		enableLoginOrEdit(false);
		
		String username = editUsername.getText().toString();
		String password = editPassword.getText().toString();
		
		if(TextUtils.isEmpty(username)){
			Toast.makeText(getBaseContext(), "输入帐号", Toast.LENGTH_SHORT).show();
			enableLoginOrEdit(true);
			return;
		}
		
		if(TextUtils.isEmpty(password)){
			Toast.makeText(getBaseContext(), "输入密码", Toast.LENGTH_SHORT).show();
			enableLoginOrEdit(true);
			return;
		}
		
		
		if(!NetworkUtils.isnetWorkAvilable(getBaseContext())){
			NetworkUtils.notWorkToast(getBaseContext());
			enableLoginOrEdit(true);
			return;
		}
		
		
		
		if(userInfoVo == null){
			userInfoVo = new UserInfoVo();
		}
		userInfoVo.setPassword(password);
		userInfoVo.setPhone(username);
		
		AjaxParams params = new AjaxParams();
		params.put("action", "login");
		params.put("username", username);
		params.put("password", password);
		
		getFinalHttp().post(Constants.ApiUrl.LOGIN_REGISTER, params, new LoginAjaxCallBack());
	}
	
	/**
	 * 启动和关闭所有功能。
	 * @param flag 开关  true 开启功能， false，关闭所有功能。
	 * 作者:fighter <br />
	 * 创建时间:2013-6-8<br />
	 * 修改时间:<br />
	 */
	private void enableLoginOrEdit(boolean flag){
		if(!flag){
			hideSoftKeyboard();
		}
		editPassword.setEnabled(flag);
		editUsername.setEnabled(flag);
		
		btnLogin.setClickable(flag);
		btnRegister.setClickable(flag);
		
		textUnrememberPassword.setClickable(flag);
	}
	
	// 注册
	void registerAction(){
		Intent intent = new Intent(this, RegisterProtocolActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	// 忘记密码
	void forgetAction(){
		Intent intent = new Intent(this, GetBackPasswordActivity.class);
		startActivity(intent);
	}
	
	// 记住密码
	void remenberAction(boolean flag){
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(REMEMBER_KEY, flag);
		editor.putBoolean(Constants.Login.LOGIN_STATE, true); // 登录成功!
		editor.commit();
		
		//TODO 用户的登录的信息持久化保存..
	}
	void loginSuccess(){
		getCustomer();
	}
	
	/**
	 * 获取用户信息
	 * 
	 * 作者:fighter <br />
	 * 创建时间:2013-5-24<br />
	 * 修改时间:<br />
	 */
	void getCustomer(){
		new UserInfoApi().getInfo(userInfoVo.getUid(), userInfoVo.getUid(), new ClientAjaxCallback() {

			@Override
			public void onStart() {
				super.onStart();
				handler.post(new Runnable() {
					@Override
					public void run() {
						getWaitDialog().setMessage("加载数据..");
					}
				});

				if(!getWaitDialog().isShowing()){
					getWaitDialog().show();
				}
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				getWaitDialog().cancel();
				Log.v("9080", t);
				try {
					String data = ErrorCode.getData(getBaseContext(), t);
					if(data != null){
						
						CommFun.regActivityFun(userInfoVo.getPhone(),userInfoVo.getPassword());//注册sip
						
						if("".equals(data)){
							mLoginSuccess();
							Intent intent = new Intent(LoginActivity.this, RegisterGrzlActivity.class);
							startActivity(intent);
							finish();
						}else{
							mLoginSuccess();
							CustomerVo customerVo = JSON.toJavaObject(JSON.parseObject(data), CustomerVo.class);
							application.setCustomerVo(customerVo);
							
							new LoginSuccessTask(customerVo).execute();
						}
					}else{
						enableLoginOrEdit(true);
					}
				} catch (Exception e) {
					enableLoginOrEdit(true);
				}
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
				getWaitDialog().cancel();
				Toast.makeText(getBaseContext(), strMsg, Toast.LENGTH_SHORT).show();
			}					
		});
	}
	
	private void mLoginSuccess(){
		new LocalAuth(getBaseContext()).loginSuccess();
		userInfoCache.cacheUserInfo(userInfoVo);
		boolean flag = checkBoxRememberPassword.isChecked();  // 是否记住密码
		if(flag != oldRemenberState){
			remenberAction(flag);
		}
		
		startService(new Intent(getBaseContext(), SnsService.class));
		
		Editor edit = settings.edit();
		edit.putString(Settings.PREF_USERNAME, userInfoVo.getPhone());	 	
		edit.putString(Settings.PREF_FROMUSER, userInfoVo.getPhone());	 
		edit.putString(Settings.PREF_PASSWORD, userInfoVo.getPassword());
		
		edit.putString(Settings.PREF_PORT, "5060");
		edit.putString(Settings.PREF_SERVER, "117.79.230.235");
		edit.putString(Settings.PREF_PROTOCOL, "udp");
		edit.putString(Settings.PREF_PREF, "ASK");
		edit.putBoolean(Settings.PREF_EDGE, true);
		edit.putBoolean(Settings.PREF_3G, true);
		edit.putBoolean(Settings.PREF_WLAN, true);
		edit.commit();
		
		Receiver.engine(this).registerMore();
		on(this,true);
		
		new Thread(new Runnable(){
		     @Override
			public void run()
		         {
		    	    Receiver.engine(LoginActivity.this).halt();
					Receiver.engine(LoginActivity.this).StartEngine();
					Receiver.engine(LoginActivity.this).updateDNS();
		         }
		}).start();	
		
		application.setUserInfoVo(userInfoVo);
	}
	
	class LoginSuccessTask extends AsyncTask<Void, Void, Boolean>{
		private CustomerVo customerVo;
		
		public LoginSuccessTask(CustomerVo customerVo) {
			super();
			this.customerVo = customerVo;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			getWaitDialog().show();
			getWaitDialog().setMessage("初始化中...");
		}



		@Override
		protected Boolean doInBackground(Void... params) {
			FinalDb db = FinalFactory.createFinalDb(getBaseContext(), userInfoVo);
			db.delete(customerVo);
			db.save(customerVo);
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			getWaitDialog().cancel();
			Intent intent = new Intent(LoginActivity.this, IndexTabActivity.class);
			startActivity(intent);
			finish();
		}
		
		
	}
	
	class LoginAjaxCallBack extends ClientAjaxCallback{
		
		@Override
		public void onStart() {
			super.onStart();
			getWaitDialog().setMessage("验证中..");
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
				String data = ErrorCode.getData(getBaseContext(), t);
				if(data != null){
					
					String uid = JSON.parseObject(data).getString("uid");
				
					userInfoVo.setUid(uid);
					getWaitDialog().setMessage("验证成功..");
					loginSuccess();
				}else{
					enableLoginOrEdit(true);
					getWaitDialog().cancel();
				}
			} catch (Exception e) {
				Log.e("888", "onSuccess", e);
				enableLoginOrEdit(true);
				getWaitDialog().cancel();
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			getWaitDialog().cancel();
			Toast.makeText(getBaseContext(), "未找到服务!", Toast.LENGTH_SHORT).show();
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					enableLoginOrEdit(true);
				}
			});
			
		}
		
	}
	
	class LoginOnClickLisener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.loginBtn:
				loginAction();
				break;
			case R.id.regBtn:
				registerAction();
				break;
			case R.id.login_checkBox_remember_password:
				forgetAction();
				break;
			default:
				break;
			}
		}
		
	}

	@Override
	protected void initTitle() {
		
		
	}
}

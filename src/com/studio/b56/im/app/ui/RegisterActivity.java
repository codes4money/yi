package com.studio.b56.im.app.ui;

import org.sipdroid.sipua.ui.Settings;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.api.SundryApi;
import com.studio.b56.im.app.api.UserApi;
import com.studio.b56.im.app.api.UserInfoApi;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.app.control.RegisterControl;
import com.studio.b56.im.app.receiver.RegisterReceiver;
import com.studio.b56.im.app.ui.LoginActivity.LoginAjaxCallBack;
import com.studio.b56.im.app.ui.RegisterGrzlActivity.RegisterGrzlCallBack;
import com.studio.b56.im.app.vo.RegisterVo;
import com.studio.b56.im.command.TextdescTool;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;

public class RegisterActivity extends BaseRegisterActivity {
	private static final String TAG = RegisterActivity.class.getCanonicalName();
	@ViewInject(id = R.id.userName)
	private EditText editUsername;
	@ViewInject(id = R.id.userPsw)
	private EditText editPassword;
	@ViewInject(id = R.id.userPswConfig)
	private EditText editRepassword;
	@ViewInject(id = R.id.userNickName)
	private EditText userNickName;
	@ViewInject(id = R.id.regBtn)
	private Button regBtn;
	
	@ViewInject(id = R.id.sexMale)
	private ImageButton sexMale;
	
	@ViewInject(id = R.id.sexFeMale)
	private ImageButton sexFeMale;
	
	@ViewInject(id = R.id.topMiddle)
	private TextView topTitle;
	@ViewInject(id = R.id.topLeft)
	private ImageButton topLeft;
	@ViewInject(id = R.id.topRight)
	private ImageButton topRight;
	
	private RegisterControl registerControl;
	
	private UserApi mUserApi;
	private CustomerVo customerVo;
	private UserInfoVo userInfo;
	public String UserSex="1";
	public RegisterVo registerVo1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reg);
		this.baseInit();
		addListener();
	}

	protected void baseInit() {
		super.baseInit();
		/*
		new SundryApi().getTips(SundryApi.TIPS_REG, new ClientAjaxCallback() {
			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				Log.v("注册信息:", t);
				try {
					String data = ErrorCode.getData(t);
					if(!TextUtils.isEmpty(data)){
						String value = JSONObject.parseObject(data).getString("value");
						textTips.setText(value);
					}
				} catch (Exception e) {
				}
				
			}
			
		});
		*/
		regBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				registerControl.submit();
			}
		});
		
		sexMale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserSex="1";
				sexMale.setBackgroundResource(R.drawable.reg_hook);
				sexFeMale.setBackgroundResource(R.drawable.reg_unhook);
			}
		});
		
		sexFeMale.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UserSex="2";
				sexFeMale.setBackgroundResource(R.drawable.reg_hook);
				sexMale.setBackgroundResource(R.drawable.reg_unhook);
			}
		});
		
		
	}
	
	 private void sendMessage(String paramString1, String paramString2)
	  {
	    try
	    {
	      Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + paramString1));
	      localIntent.putExtra("sms_body", paramString2);
	      startActivity(localIntent);
	    }
	    catch (Exception localException)
	    {
	    }
	  }
	 
	@Override
	protected void initTitle() {
		//setTitleContent(R.string.register_title);
		//setTitleRight(R.string.title_btn_next);
		topTitle.setText(R.string.register);
		topRight.setBackgroundResource(R.drawable.reg_hook);
		topRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				registerControl.submit();
			}
		});
		
		topLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				//startActivity(intent);
				finish();
			}
		});
		
	}
	
	private void addListener(){
		registerControl = new RegisterControl(this);
	}

	
	
class WebRegAjaxCallBack extends ClientAjaxCallback{
		
		@Override
		public void onStart() {
			super.onStart();
			getWaitDialog().setMessage("连接中..");
			getWaitDialog().show();
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			
			Log.v("9080", t);
			t=t.trim();
			try {
				
				t=t.trim();
				if(t.equals("0"))//注册成功
				{
					//开始注册IM用户
					nextActivity(registerVo1);
					
				}
				else if(t.equals("-1"))
				{
					 getWaitDialog().cancel();
					 new BaseDialog.Builder(RegisterActivity.this).setTitle("提示").setMessage("注册短信验证失败!请确认是否发送验证短信!").setYesListener(new BaseDialog.YesListener()
				      {
				        public void doYes()
				        {
				        	finish();
				        }
				      }).setNoCancel(true).show();
				}
				else if(t.equals("-2"))
				{
				    getWaitDialog().cancel();
					 new BaseDialog.Builder(RegisterActivity.this).setTitle("提示").setMessage("手机号已被注册!").setYesListener(new BaseDialog.YesListener()
				      {
				        public void doYes()
				        {
				        	finish();
				        }
				      }).setNoCancel(true).show();
				}
				
			} catch (Exception e) {
				getWaitDialog().cancel();
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			getWaitDialog().cancel();
			Toast.makeText(getBaseContext(), "登录失败!", Toast.LENGTH_SHORT).show();
			
		}
		
	}


	public void nextActivity(final RegisterVo registerVo){
		if (mUserApi == null) {
			mUserApi = new UserApi();
		}
		
		if(!checkNetWork()){
			return;
		}
		
		AjaxParams params = new AjaxParams();
		params.put("action", "reg");
		params.put("username", registerVo.getUsername());
		params.put("password", registerVo.getPassword());
		params.put("code", registerVo.getValidateCode());

		getFinalHttp().post(Constants.ApiUrl.LOGIN_REGISTER, params,
				new ClientAjaxCallback() {

					ProgressDialog progressDialog = new ProgressDialog(
							RegisterActivity.this);

					@Override
					public void onStart() {
						super.onStart();
						progressDialog.setMessage("提交信息...");
						progressDialog.show();
					}

					@Override
					public void onSuccess(String t) {
						super.onSuccess(t);
						progressDialog.cancel();
						
						try {
							JSONObject json = JSONObject.parseObject(t);
							String error = json.getString("error");
							if("0".equals(error)){
								JSONObject data = json.getJSONObject("data");
								registerVo.setUid(data.getString("uid"));
								
								CommFun.regActivityFun(registerVo.getUsername(),registerVo.getPassword());//注册sip
								
								//注册成功
								registerSuccess(registerVo);
								
								return;
							}
							
							if("107".equals(error)){
								showToast("该帐号已经被注册!");
								return;
							}
							else
							{
								showToast("注册发生错误，账号已被注册!");
							}

						} catch (Exception e) {
							e.printStackTrace();
							Log.e(TAG, t);
						}
						
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						super.onFailure(t, strMsg);
						progressDialog.cancel();
						showToast(strMsg);
					}

				});
	}
	
	// 注册成功
	void registerSuccess(final RegisterVo registerVo) {
		userInfo = new UserInfoVo();
		userInfo.setPassword(registerVo.getPassword());
		userInfo.setPhone(registerVo.getUsername());
		userInfo.setUid(registerVo.getUid());

		new UserInfoCache(getBaseContext()).cacheUserInfo(userInfo);

		getShangwupanlvApplication().setUserInfoVo(userInfo);
		
		customerVo = new CustomerVo();
		customerVo.setName(userNickName.getText().toString());
		customerVo.setSex(UserSex);
		customerVo.setPhone(registerVo.getUsername());
		
		//sip配置。
		String sharedPrefsFile = "com.studio.b56.im_preferences";
	    SharedPreferences settings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
		Editor edit = settings.edit();
		edit.putString(Settings.PREF_USERNAME, userInfo.getPhone());	 	
		edit.putString(Settings.PREF_FROMUSER, userInfo.getPhone());	 
		edit.putString(Settings.PREF_PASSWORD, userInfo.getPassword());
		edit.putString(Settings.PREF_PORT, "5060");
		edit.putString(Settings.PREF_SERVER, "sip.xunliao.im");
		edit.putString(Settings.PREF_PROTOCOL, "udp");
		edit.putString(Settings.PREF_PREF, "ASK");
		edit.putBoolean(Settings.PREF_EDGE, true);
		edit.putBoolean(Settings.PREF_3G, true);
		edit.putBoolean(Settings.PREF_WLAN, true);
		edit.commit();
		
		
		//开始提交用户详细信息
		new UserInfoApi().addInfo(userInfo.getUid(),
				TextdescTool.objectToMap(customerVo),
				new RegisterGrzlCallBack());
		
	}
	
	
	class RegisterGrzlCallBack extends ClientAjaxCallback {

		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			String data = ErrorCode.getData(getBaseContext(), t);
			if (data != null) {
				if ("1".equals(data)) {
					registerGrzlActionSuccess();
				} else {
					registerGrzlActionError();
				}
			}
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			getWaitDialog().cancel();
			Toast.makeText(getBaseContext(), strMsg, Toast.LENGTH_SHORT).show();
		}

	}
	
	void registerGrzlActionError() {
		getPromptDialog().setMessage(R.string.dialog_register_txt_title_tag);
		getPromptDialog().addConfirm(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getPromptDialog().cancel();
				Intent intent = new Intent(RegisterActivity.this,
						RegisterGrzlActivity.class);
				startActivity(intent);
				// 关闭注册填写内容页面
				sendBroadcast(new Intent(RegisterReceiver.ACIONT_INTENT));
				finish();
			}
		});
		getPromptDialog().show();
	}
	
	void registerGrzlActionSuccess() {
		customerVo.setUid(userInfo.getUid());
		getShangwupanlvApplication().setCustomerVo(customerVo);
		// 持久化个人信息
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				//FinalDb db = FinalFactory.createFinalDb(getBaseContext(),
				//		userInfo);
				//db.save(customerVo);
				Log.v("==registerGrzlActionSuccess==", "=="+customerVo.getName());
				return null;
			}

		}.execute();

		getPromptDialog().setMessage(R.string.dialog_register_txt_title_tag);
		getPromptDialog().addConfirm(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getPromptDialog().cancel();
				Intent intent = new Intent(RegisterActivity.this,
						RegisterGrzlActivity.class);
				startActivity(intent);
				// 关闭注册填写内容页面
				sendBroadcast(new Intent(RegisterReceiver.ACIONT_INTENT));
				finish();
			}
		});
		getPromptDialog().show();
		
	}

	
	
	@Override
	protected void titleBtnRight() {
		super.titleBtnRight();
	}

	/**
	 * 用户名
	 */
	public EditText getEditUsername() {
		return editUsername;
	}
	/**
	 * 昵称
	 */
	public EditText getEditNickname() {
		return userNickName;
	}
	/**
	 * 密码
	 */
	public EditText getEditPassword() {
		return editPassword;
	}
	
	/**
	 * 确认密码
	 */
	public EditText getEditRepassword() {
		return editRepassword;
	}

}

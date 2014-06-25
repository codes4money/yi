package com.studio.b56.im.app.ui;

import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.LocalAuth;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.control.FriendListAction;
import com.studio.b56.im.app.ui.Profile.LoginAjaxCallBack;
import com.studio.b56.im.app.ui.Profile.LoginAjaxCallBack2;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;

public class Profile extends BaseActivity{

	@ViewInject(id = R.id.imgAvatar)
	private ImageView imgAvatar;
	@ViewInject(id = R.id.tvNicknname)
	private TextView tvNicknname;
	
	private FinalDb finalDb;
	private UserInfoVo userInfoVo;
	CustomerVo cvo=new CustomerVo();
	
	@Override
	protected void initTitle() {
		setTitleContent(R.string.individual_centerActivity_title);
//		setTitleRight(R.string.individual_centerActivity_logining);
		//setTitleLeft("返回");
//		getBtnTitleRight().setBackgroundResource(R.drawable.btn_logout_style);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		FinalOnloadBitmap.finalDisplay(getBaseContext(), getMyCustomerVo(),imgAvatar, getHeadBitmap());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		userInfoVo= new UserInfoCache(this).getCacheUserInfo();
		finalDb = FinalFactory.createFinalDb(this,userInfoVo);
		cvo=finalDb.findById(userInfoVo.getUid(), CustomerVo.class);
		
		baseInit();
		   
		String name=cvo.getName();
		
	      if(name!="")
	      {
	    	  tvNicknname.setText(name);
			FinalHttp finalHttp = new FinalHttp();
			AjaxParams params = new AjaxParams();
			params.put("username", userInfoVo.getUid());
			params.put("password", userInfoVo.getPassword());
			params.put("nickname",name);
			params.put("email",name);
			finalHttp.post(Constants.ApiUrl.WeiBo_URL+"ajax.php?mod=member&code=member", params,new LoginAjaxCallBack());
	      }
	}
	
	class LoginAjaxCallBack2 extends ClientAjaxCallback{	
		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
		}
	}
	
	class LoginAjaxCallBack extends ClientAjaxCallback{	
		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			
			new Thread(new Runnable(){
			     @Override
				public void run()
			    {
			    	 try
			    	 {
			    	    FriendListAction friendListAction = getShangwupanlvApplication().getFriendListAction();
						if(friendListAction.isLocalFriendInfo())
						{
							List<CustomerVo> customerVos=friendListAction.getFriendList();
							for(int i=0;i<customerVos.size();i++)
							{
								FinalHttp finalHttp = new FinalHttp();
								finalHttp.get(Constants.ApiUrl.WeiBo_URL+"ajax.php?mod=follow&code=add2&myuid="+userInfoVo.getUid()+"&username="+customerVos.get(i).getUid(),new LoginAjaxCallBack2());
							}
							
						}
			    	 }catch(Exception e){}
						
			    }
			}).start();	
			
		}

		@Override
		public void onFailure(Throwable t, String strMsg) {
			super.onFailure(t, strMsg);
			Log.v("群组邀请2===", "==="+ strMsg);
		}
		
	}
	
	/**
	 * 注销功能.
	 * 
	 * 作者:fighter <br />
	 * 创建时间:2013-5-30<br />
	 * 修改时间:<br />
	 */
	void loginAction() {
		new AsyncTask<Void, Void, Void>(){
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				sendBroadcast(new Intent(IndexTabActivity.ACTION_LOGIN_OUT));
				getWaitDialog().setMessage("注销");
				getWaitDialog().show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				stopService(new Intent(getBaseContext(), SnsService.class));
				new LocalAuth(getBaseContext()).logined();
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				getNotificationManager().cancelAll();
				getWaitDialog().cancel();
				Intent intent = new Intent(Profile.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
			
		}.execute();
	}
	
	public void ButtonClick(View v) {
		switch (v.getId()) {
		case R.id.btnAlbum:
			photoAction();
			break;

		case R.id.btnWeibo:
			weiboAction();
			break;
			
		case R.id.btnLocation:
			locationAction();
			break;
			
		case R.id.btnModifyPassword:
			modifyPasswordAction();
			break;
			
		case R.id.btnNotification:
			Intent intent3 = new Intent();
			intent3.setClass(this, NotifyActivity.class);
			startActivity(intent3);
			break;
			
		case R.id.btnDial:
			Intent intent = new Intent();
			intent.setClass(this, setting_phonetype.class);
			startActivity(intent);
			break;
			
		case R.id.btnSettings:
			Intent intent2 = new Intent();
			intent2.setClass(this, setting_shownum.class);
			startActivity(intent2);
			break;
			
		case R.id.btnLogout:
			loginAction();
			break;
			
			default:
				break;
		}
	}

	void modifyPasswordAction() {
		Intent intent = new Intent(this, ModifyPasswordActivity.class);
		startActivity(intent);
	}

	void moreAction() {
		Intent intent = new Intent(this, MoreActivity.class);
		startActivity(intent);
	}

	void locationAction() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}

	void hideAction() {
		Intent intent = new Intent(this, PrivacyActivity.class);
		startActivity(intent);
	}

	void infoAction() {
		CustomerVo customerVo = getMyCustomerVo();
		Intent intent = new Intent(this, MyChattingActivity.class);
		intent.putExtra("data", customerVo);
		startActivity(intent);
//		if (Constants.CustomerType.WITHCHAT
//				.equals(customerVo.getCustomertype())) {
//			Intent intent = new Intent(this, MyFindPartnerActivity.class);
//			intent.putExtra("data", customerVo);
//			startActivity(intent);
//		} else {
//			Intent intent = new Intent(this, MyChattingActivity.class);
//			intent.putExtra("data", customerVo);
//			startActivity(intent);
//		}

	}

	/**
	 * 点击“我的相册”跳转
	 * */
	void photoAction() {
		Intent intent = new Intent();
		intent.setClass(Profile.this, AlbumActivity.class);
		startActivity(intent);
	}
	
	void weiboAction() {
		Intent intent = new Intent();
		intent.setClass(Profile.this, weibo_main.class);
		startActivity(intent);
	}
	/**
	 * 点击我的积分跳转
	 * */
	void pointAction() {
		if (Constants.CustomerType.WITHCHAT.equals(getMyCustomerVo()
				.getCustomertype())) {
			Intent intent = new Intent(Profile.this,
					MyPointActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(Profile.this,
					ChatPointActivity.class);
			startActivity(intent);
		}

	}

	void smsAction() {
		Uri smsToUri = Uri.parse("smsto:");

		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);

		intent.putExtra("sms_body", "嗨，你应该试一下陪伴。这是一款超炫的交友软件，让你可以无论何时何地，都能找到你心仪的伴侣。");

		startActivity(intent);

	}

	/**
	 * 跳转到隐私设置
	 * */
	void privaccyAction() {
		Intent intent = new Intent(Profile.this,
				PrivacyActivity.class);
		startActivity(intent);

	}
}

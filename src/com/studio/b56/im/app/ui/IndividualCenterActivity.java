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
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.LocalAuth;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.control.FriendListAction;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.service.SnsService;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;

public class IndividualCenterActivity extends BaseActivity {
	@ViewInject(id = R.id.individual_center_item_modify_password)
	private View viewItemModifyPassword;
	@ViewInject(id = R.id.individual_center_item_more)
	private View viewItemMore;
	@ViewInject(id = R.id.individual_center_item_location)
	private View viewItemLocation;
	@ViewInject(id = R.id.individual_center_item_hide)
	private View viewItemHide;
	@ViewInject(id = R.id.individual_center_item_info)
	private View viewItemInfo;
	@ViewInject(id = R.id.individual_center_item_photo)
	private View viewItemPhoto;
	@ViewInject(id = R.id.individual_center_item_point)
	private View viewItemPoint;
	@ViewInject(id = R.id.individual_center_item_sms)
	private View viewItemSms;
	@ViewInject(id = R.id.individual_center_item_weibo)
	private View viewItemWeibo;
	@ViewInject(id = R.id.individual_center_item_note)
	private View viewItemNote;
	@ViewInject(id = R.id.individual_center_item_hide)
	private RelativeLayout btnPrivacy;
	@ViewInject(id = R.id.individual_center_item_setting_phonetype)
	private RelativeLayout setting_phonetype;
	@ViewInject(id = R.id.individual_center_item_setting_showum)
	private RelativeLayout setting_showum;
	@ViewInject(id = R.id.individual_center_img_head)
	private ImageView imgHead;
	
	private FinalDb finalDb;
	private View.OnClickListener individualAction;
	
	 private UserInfoVo userInfoVo;
	CustomerVo cvo=new CustomerVo();
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
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.individual_center);
		
		userInfoVo= new UserInfoCache(this).getCacheUserInfo();
		finalDb = FinalFactory.createFinalDb(this,userInfoVo);
		cvo=finalDb.findById(userInfoVo.getUid(), CustomerVo.class);
		
		baseInit();
		   
		String name=cvo.getName();
		
                  if(name!="")
                  {
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

	@Override
	protected void baseInit() {
		super.baseInit();
		addLisener();
	}

	@Override
	protected void onStart() {
		super.onStart();
		FinalOnloadBitmap.finalDisplay(getBaseContext(), getMyCustomerVo(),imgHead, getHeadBitmap());
		
		
	}
	@Override
	protected void titleBtnLeft() {
		super.titleBtnRight();
		finish();
	}
	private void addLisener() {
		individualAction = new IndividualCenterOnClick();

		viewItemHide.setOnClickListener(individualAction);
		viewItemInfo.setOnClickListener(individualAction);
		viewItemLocation.setOnClickListener(individualAction);
		viewItemModifyPassword.setOnClickListener(individualAction);
		viewItemMore.setOnClickListener(individualAction);
		viewItemPoint.setOnClickListener(individualAction);
		viewItemSms.setOnClickListener(individualAction);
		viewItemPhoto.setOnClickListener(individualAction);
		viewItemWeibo.setOnClickListener(individualAction);
		viewItemNote.setOnClickListener(individualAction);
		setting_phonetype.setOnClickListener(individualAction);
		setting_showum.setOnClickListener(individualAction);
	}

	@Override
	protected void initTitle() {
		// TODO Auto-generated method stub
		setTitleContent(R.string.individual_centerActivity_title);
		setTitleRight(R.string.individual_centerActivity_logining);
		//setTitleLeft("返回");
		getBtnTitleRight().setBackgroundResource(R.drawable.btn_logout_style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shangwupanlv.app.ui.BaseActivity#titleBtnRight()
	 */
	@Override
	protected void titleBtnRight() {
		super.titleBtnRight();

		loginAction();
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
				Intent intent = new Intent(IndividualCenterActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
			
		}.execute();

		
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
		intent.setClass(IndividualCenterActivity.this, AlbumActivity.class);
		startActivity(intent);
	}
	
	void weiboAction() {
		Intent intent = new Intent();
		intent.setClass(IndividualCenterActivity.this, weibo_main.class);
		startActivity(intent);
	}
	/**
	 * 点击我的积分跳转
	 * */
	void pointAction() {
		if (Constants.CustomerType.WITHCHAT.equals(getMyCustomerVo()
				.getCustomertype())) {
			Intent intent = new Intent(IndividualCenterActivity.this,
					MyPointActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(IndividualCenterActivity.this,
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
		Intent intent = new Intent(IndividualCenterActivity.this,
				PrivacyActivity.class);
		startActivity(intent);

	}

	class IndividualCenterOnClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.individual_center_item_modify_password:
				modifyPasswordAction();
				break;
			case R.id.individual_center_item_more:
				moreAction();
				break;
			case R.id.individual_center_item_location:
				locationAction();
				break;
			case R.id.individual_center_item_hide:
				hideAction();
				break;
			case R.id.individual_center_item_info:
				infoAction();
				break;
			case R.id.individual_center_item_photo:
				photoAction();
				break;
			case R.id.individual_center_item_point:
				pointAction();
				break;
			case R.id.individual_center_item_sms:
				smsAction();
			case R.id.individual_center_item_weibo:
				weiboAction();
				break;
			case R.id.individual_center_img_hide:
				privaccyAction();
				break;
			case R.id.individual_center_item_setting_phonetype:
				Intent intent = new Intent();
				intent.setClass(IndividualCenterActivity.this, setting_phonetype.class);
				startActivity(intent);
				break;
			case R.id.individual_center_item_setting_showum:
				Intent intent2 = new Intent();
				intent2.setClass(IndividualCenterActivity.this, setting_shownum.class);
				startActivity(intent2);
				break;
			case R.id.individual_center_item_note:
				Intent intent3 = new Intent();
				intent3.setClass(IndividualCenterActivity.this, NotifyActivity.class);
				startActivity(intent3);
				break;
			}
		}

	}
}

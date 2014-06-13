package com.studio.b56.im.app.ui;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.action.ImageInfoAction;
import com.studio.b56.im.app.api.AlbumApi;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.UserInfoApi;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.control.HeadUploadAction;
import com.studio.b56.im.app.ui.LoginActivity.LoginSuccessTask;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.command.NetworkUtils;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;
import com.studio.b56.im.R;

public class RegisterHeadActivity extends BaseActivity {
	@ViewInject(id = R.id.register_head_img_photo)
	private ImageView imgHead;
	@ViewInject(id = R.id.register_head_btn_select_photo)
	private Button btnSelect;
	@ViewInject(id = R.id.register_head_btn_select_camera_photo)
	private Button btnSelectCamera;
	private View.OnClickListener selectOnClickListener;
	private ImageInfoAction imageInfoAction;
	
	private HeadUploadAction headUploadAction;
	private UserInfoVo userInfoVo;
	private PeibanApplication application;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.register_head);
		super.onCreate(savedInstanceState);
		baseInit();
	}

	@Override
	protected void baseInit() {
		super.baseInit();
		addLisener();
		initParam();
		
		application = (PeibanApplication) getApplication();

		userInfoVo = application.getUserInfoVo();
		getCustomer();
	}
	
	void getCustomer(){
		new UserInfoApi().getInfo(userInfoVo.getUid(), userInfoVo.getUid(), new ClientAjaxCallback() {

			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
			
				Log.v("9080", t);
				try {
					String data = ErrorCode.getData(getBaseContext(), t);
					if(data != null){
							
							CustomerVo customerVo = JSON.toJavaObject(JSON.parseObject(data), CustomerVo.class);
							application.setCustomerVo(customerVo);
							
							FinalDb db = FinalFactory.createFinalDb(getBaseContext(), userInfoVo);
							db.delete(customerVo);
							db.save(customerVo);
							
					}
				} catch (Exception e) {
				}
			}
			@Override
			public void onFailure(Throwable t, String strMsg) {
				super.onFailure(t, strMsg);
			}					
		});
	}
	
	private void addLisener() {
		selectOnClickListener = new RegisterHeadOnClickLisener();
		btnSelect.setOnClickListener(selectOnClickListener);
		btnSelectCamera.setOnClickListener(selectOnClickListener);
	}

	private void initParam() {
		headUploadAction = new HeadUploadAction(getApplication(), imgHead
				, null, this, new UserInfoApi(), new AlbumApi(), false){

					/* (non-Javadoc)
					 * @see com.shangwupanlv.app.control.HeadUploadAction#submitHeadSuccess()
					 */
					@Override
					public void submitHeadSuccess() {
						super.submitHeadSuccess();
						RegisterHeadActivity.this.uploadsuccess();
					}
			
		};
		imageInfoAction = new ImageInfoAction(RegisterHeadActivity.this);
		imageInfoAction.setOnBitmapListener(headUploadAction);

	}

	@Override
	protected void initTitle() {
		setTitleContent(R.string.register_head_title);
		setTitleRight(R.string.register_head_title_right_tag);
		setTitleLeft(R.string.register_head_title_left_tag);
	}

	@Override
	protected void titleBtnRight() {
		super.titleBtnRight();
		if (headUploadAction.getSelectHead() == null) {
			Toast.makeText(RegisterHeadActivity.this, "您还未选择图片......",
					Toast.LENGTH_LONG).show();
		} else if (!NetworkUtils.isnetWorkAvilable(getBaseContext())) {
			Toast.makeText(RegisterHeadActivity.this,
					getResources().getString(R.string.toast_network),
					Toast.LENGTH_LONG).show();
		} else {
			headUploadAction.uploadHead(headUploadAction.getSelectHead());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			titleBtnLeft();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 跳过
	 */
	@Override
	protected void titleBtnLeft() {
		super.titleBtnLeft();

		Intent intent = new Intent(this, IndexTabActivity.class);
		startActivity(intent);
		finish();
	}

	void selectPhoto() {
		imageInfoAction.getCropLocolPhoto();
	}
	void selectCamera() {
		imageInfoAction.getCropCameraPhoto();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		imageInfoAction.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 上传成功
	 * */
	private void uploadsuccess() {
			getPromptDialog().setMessage("恭喜您，上传成功！");
			getPromptDialog().addConfirm(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					getPromptDialog().cancel();
					titleBtnLeft();

//					// 寻聊
//					if (Constants.CustomerType.WITHCHAT.equals(customerVo.getCustomertype())) {
//						Intent intent = new Intent(RegisterHeadActivity.this,
//								IndexTabActivity.class);
//						startActivity(intent);
//					} else {
//						// 陪聊用户
//						Intent intent = new Intent(RegisterHeadActivity.this,
//								RegisterHeadAuthActivity.class);
//						startActivity(intent);
//					}

					finish();
				}
			});
			getPromptDialog().removeCannel();
			getPromptDialog().show();
	}

	class RegisterHeadOnClickLisener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(!checkNetWorkOrSdcard()){
				return;
			}
			int id = v.getId();
			switch (id) {
			case R.id.register_head_btn_select_photo:
				selectPhoto();
				break;
			case R.id.register_head_btn_select_camera_photo:
				selectCamera();
				break;
			default:
				break;
			}
		}
	}
}

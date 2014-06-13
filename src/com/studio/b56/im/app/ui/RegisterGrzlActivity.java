package com.studio.b56.im.app.ui;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.studio.b56.im.R;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.api.UserInfoApi;
import com.studio.b56.im.app.control.DatePickerLisenerImpl;
import com.studio.b56.im.app.control.DialogAccountType;
import com.studio.b56.im.app.control.DialogSalaryLisenerImpl;
import com.studio.b56.im.app.control.DialogServiceLoaction;
import com.studio.b56.im.app.control.DialogSexSelect;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.command.TextdescTool;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;

public class RegisterGrzlActivity extends BaseActivity {

	@ViewInject(id = R.id.register_grzl_item_select_brith)
	private RelativeLayout btnBrith; // 出生年月
	@ViewInject(id = R.id.register_grzl_item_select_wages)
	private RelativeLayout btnWages; // 薪资意愿
	@ViewInject(id = R.id.register_grzl_edit_wages)
	private TextView editWages;
	@ViewInject(id = R.id.register_grzl_edit_brith)
	private TextView editBrith;
	@ViewInject(id = R.id.register_grzl_edit_qq)
	private TextView editQQ;
	@ViewInject(id = R.id.register_grzl_edit_body)
	private TextView editBody;
	@ViewInject(id = R.id.register_grzl_edit_height)
	private TextView editHeight;
	@ViewInject(id = R.id.register_grzl_edit_profession)
	private TextView editProfession; // 职业
	@ViewInject(id = R.id.register_grzl_edit_signature)
	private TextView editSignature; // 个性签名
	@ViewInject(id = R.id.register_grzl_edit_phone)
	private TextView editPhone;
	private View.OnClickListener grzlClickListener;
	private CustomerVo customerVo;
	private UserInfoVo userInfoVo;
	private DialogAccountType dialogAccountType;
	private DialogServiceLoaction dialogServiceLoaction;
	private DatePickerLisenerImpl datePickerLisenerImpl;
	private DialogSalaryLisenerImpl dialogSalaryLisenerImpl;
	private DialogSexSelect dialogSexSelect;
	
	private PeibanApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.register_grzl);
		super.onCreate(savedInstanceState);
		baseInit();
		initDialog();
		addLisener();
		
		application = (PeibanApplication) getApplication();
		
		userInfoVo = application.getUserInfoVo();
		
	}

	/**
	 * 初始化Dialog
	 * */
	private void initDialog() {
		// TODO Auto-generated method stub
	//	dialogAccountType = new DialogAccountType(RegisterGrzlActivity.this,
	//			editAccoutType);
	//	dialogServiceLoaction = new DialogServiceLoaction(
	//			RegisterGrzlActivity.this, editServiceLocation);
		datePickerLisenerImpl = new DatePickerLisenerImpl(
			RegisterGrzlActivity.this, editBrith);
		dialogSalaryLisenerImpl = new DialogSalaryLisenerImpl(editWages,
				RegisterGrzlActivity.this);
		//dialogSexSelect = new DialogSexSelect(RegisterGrzlActivity.this,
		//		editSex);
	}

	private void addLisener() {
		// TODO Auto-generated method stub
		grzlClickListener = new RegisterGrzlOnClickLisener();
	//	btnAccoutType.setOnClickListener(grzlClickListener);
	//	btnSerivceLocation.setOnClickListener(grzlClickListener);
	//	btnSex.setOnClickListener(grzlClickListener);
		btnBrith.setOnClickListener(grzlClickListener);
		btnWages.setOnClickListener(grzlClickListener);
	}

	@Override
	protected void initTitle() {
		setTitleContent(getResources().getString(R.string.title_grzl));
		setTitleRight(getResources().getString(R.string.title_btn_next));
		getBtnTitleRight().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submit();
			}
		});
	}

	@Override
	protected void titleBtnRight() {
		// TODO Auto-generated method stub
		super.titleBtnRight();
		submit();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if(KeyEvent.KEYCODE_BACK == keyCode){
	// returnLogin();
	// return false;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	// 未填写完成返回
	void returnLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 选择账户类型
	 * 
	 * 作者:fighter <br />
	 * 创建时间:2013-5-23<br />
	 * 修改时间:<br />
	 */
	void accoutTypeAction() {
		dialogAccountType.show();
		System.out.println("a");
	}

	/**
	 * 服务场合
	 * 
	 * 作者:fighter <br />
	 * 创建时间:2013-5-23<br />
	 * 修改时间:<br />
	 */
	void serviceLocationAction() {
		dialogServiceLoaction.show();
	}

	/**
	 * 性别选择
	 * 
	 * 作者:fighter <br />
	 * 创建时间:2013-5-23<br />
	 * 修改时间:<br />
	 */
	void sexAction() {
		dialogSexSelect.show();
	}

	void wagesAction() {
		dialogSalaryLisenerImpl.show();
	}

	void brithAction() {
		datePickerLisenerImpl.show();
	}

	/** 提交个人资料 */
	public void submit() {
//		String accoutType = (String) editAccoutType.getTag();
		String accoutType = "1";
		//String nickName = editNickname.getText().toString();
		//String sex = (String) editSex.getTag();
	//	String serviceLocation = (String) editServiceLocation.getTag();

		String brith = editBrith.getText().toString();
		String qq = editQQ.getText().toString();
		String body = editBody.getText().toString();
		String height = editHeight.getText().toString();
		String profession = editProfession.getText().toString();
		String signature = editSignature.getText().toString();
		String phone = editPhone.getText().toString();
		String wages = editWages.getText().toString();

	//	if (TextUtils.isEmpty(nickName)) {
	//		Toast.makeText(getBaseContext(), "填写昵称", Toast.LENGTH_SHORT).show();
	//		return;
	//	}

		if (TextUtils.isEmpty(accoutType)) {
			Toast.makeText(getBaseContext(), "选择账户类型", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		//if (TextUtils.isEmpty(serviceLocation)) {
		//	Toast.makeText(getBaseContext(), "选择交友场景", Toast.LENGTH_SHORT)
		//			.show();
		//	return;
		//}

	//	if (TextUtils.isEmpty(sex)) {
	//		Toast.makeText(getBaseContext(), "确定性别", Toast.LENGTH_SHORT).show();
	//		return;
	//	}
		
		if(TextUtils.isEmpty(brith)){
			Toast.makeText(getBaseContext(), "选择年龄", Toast.LENGTH_SHORT).show();
		}

		customerVo = new CustomerVo();
	//	customerVo.setOccasions(serviceLocation);
	//	customerVo.setName(nickName);
	//	customerVo.setSex(sex);
		customerVo.setQq(qq);
		customerVo.setSalary(wages);

		customerVo.setProfession(profession);
		customerVo.setSign(signature);
		customerVo.setCustomertype(accoutType);
		customerVo.setBirthday(brith);
		customerVo.setHeight(height);
		customerVo.setWeight(body);

		new UserInfoApi().editInfo(userInfoVo.getUid(),
				TextdescTool.objectToMap(customerVo),
				new RegisterGrzlCallBack());
	}

	void registerGrzlActionSuccess() {
		customerVo.setUid(userInfoVo.getUid());
		application.setCustomerVo(customerVo);
		// 持久化个人信息
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				FinalDb db = FinalFactory.createFinalDb(getBaseContext(),
						userInfoVo);
				db.save(customerVo);
				return null;
			}

		}.execute();

		Intent intent = new Intent(RegisterGrzlActivity.this,
				RegisterHeadActivity.class);
		startActivity(intent);
		finish();
	}

	void registerGrzlActionError() {
		// 已经添加了..
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	class RegisterGrzlCallBack extends ClientAjaxCallback {

		@Override
		public void onStart() {
			super.onStart();
			getWaitDialog().setMessage("提交信息中...");
			getWaitDialog().show();
		}

		@Override
		public void onSuccess(String t) {
			super.onSuccess(t);
			getWaitDialog().cancel();
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

	class RegisterGrzlOnClickLisener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.register_grzl_item_select_brith:
				brithAction();
				break;
		//	case R.id.register_grzl_item_select_service_location:
		//		serviceLocationAction();
		//		break;
		//	case R.id.register_grzl_item_select_accout_type:
		//		accoutTypeAction();
		//		break;
		//	case R.id.register_grzl_item_select_sex:
		//		sexAction();
		//		break;
			case R.id.register_grzl_item_select_wages:
				wagesAction();
				break;

			default:
				break;
			}
		}

	}
}

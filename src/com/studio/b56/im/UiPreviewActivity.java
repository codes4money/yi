package com.studio.b56.im;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.studio.b56.im.app.ui.AuthorizeActivity;
import com.studio.b56.im.app.ui.BaseActivity;
import com.studio.b56.im.app.ui.ChatMainActivity;
import com.studio.b56.im.app.ui.ConditionFilterActivity;
import com.studio.b56.im.app.ui.EditDataActivity;
import com.studio.b56.im.app.ui.EvaluateActivity;
import com.studio.b56.im.app.ui.GetBackPasswordActivity;
import com.studio.b56.im.app.ui.IndexTabActivity;
import com.studio.b56.im.app.ui.IndividualCenterActivity;
import com.studio.b56.im.app.ui.LoginActivity;
import com.studio.b56.im.app.ui.MapActivity;
import com.studio.b56.im.app.ui.ModifyPasswordActivity;
import com.studio.b56.im.app.ui.MoreActivity;
import com.studio.b56.im.app.ui.MyPointActivity;
import com.studio.b56.im.app.ui.NotifyActivity;
import com.studio.b56.im.app.ui.PrivacyActivity;
import com.studio.b56.im.app.ui.RegisterActivity;
import com.studio.b56.im.app.ui.RegisterGrzlActivity;
import com.studio.b56.im.app.ui.RegisterHeadActivity;
import com.studio.b56.im.app.ui.RegisterHeadAuthActivity;
import com.studio.b56.im.app.ui.RegisterProtocolActivity;
import com.studio.b56.im.app.ui.RegisterValidateActivity;
import com.studio.b56.im.app.ui.SessionActivity;
import com.studio.b56.im.app.ui.ToCashActivity;

public class UiPreviewActivity extends BaseActivity {
	// 名字对应打开的界面
	private Map<String, Intent> uis = new HashMap<String, Intent>();

	private View.OnClickListener l;

	private LinearLayout linearLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.main);
		super.onCreate(savedInstanceState);
		l = new BtnOnclickLisener();
		linearLayout = (LinearLayout) findViewById(R.id.LinearLayout1);
		initUis();
		initButton();
	}

	private void initButton() {
		Set<String> sets = uis.keySet();
		for (String key : sets) {
			Button btn = (Button) LayoutInflater.from(getBaseContext())
					.inflate(R.layout.btn_preview_item, null);
			btn.setTag(key);
			btn.setText(key + "");
			btn.setOnClickListener(l);

			linearLayout.addView(btn);
		}

	}

	@Override
	protected void initTitle() {
		// TODO Auto-generated method stub
		setTitleContent(R.string.title_activity_preview);
	}

	private void initUis() {
		Intent intent = null;
		intent = new Intent(this, IndexTabActivity.class);
		uis.put("主页", intent);
		
		intent = new Intent(this, LoginActivity.class);
		uis.put("登录", intent);

		intent = new Intent(this, RegisterActivity.class);
		uis.put("新用户", intent);

		intent = new Intent(this, RegisterProtocolActivity.class);
		uis.put("注册协议", intent);

		intent = new Intent(this, RegisterGrzlActivity.class);
		uis.put("填写个人资料", intent);

		intent = new Intent(this, RegisterValidateActivity.class);
		uis.put("注册验证", intent);

		intent = new Intent(this, RegisterHeadActivity.class);
		uis.put("上传头像", intent);

		intent = new Intent(this, RegisterHeadAuthActivity.class);
		uis.put("头像认证", intent);

		intent = new Intent(this, MoreActivity.class);
		uis.put("更多", intent);

		intent = new Intent(this, IndividualCenterActivity.class);
		uis.put("个人中心", intent);

		intent = new Intent(this, ModifyPasswordActivity.class);
		uis.put("修改密码", intent);

		intent = new Intent(this, ChatMainActivity.class);
		uis.put("个人会话", intent);

		intent = new Intent(this, SessionActivity.class);
		uis.put("会话列表", intent);

		intent = new Intent(this, AuthorizeActivity.class);
		uis.put("授权用户", intent);

		intent = new Intent(this, NotifyActivity.class);
		uis.put("通知", intent);

		intent = new Intent(this, GetBackPasswordActivity.class);
		uis.put("找回密码", intent);
		intent = new Intent(this, EvaluateActivity.class);
		uis.put("评价", intent);
		intent = new Intent(this, PrivacyActivity.class);
		uis.put("隐私设置", intent);
		intent = new Intent(this, ToCashActivity.class);
		uis.put("提现申请", intent);
		intent = new Intent(this, MyPointActivity.class);
		uis.put("我的积分", intent);
		intent = new Intent(this, ConditionFilterActivity.class);
		uis.put("筛选条件", intent);
		intent = new Intent(this, EditDataActivity.class);
		uis.put("编辑资料", intent);
		intent = new Intent(this, MapActivity.class);
		uis.put("地图", intent);
	}

	class BtnOnclickLisener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			String key = (String) v.getTag();

			if (!TextUtils.isEmpty(key)) {
				Intent intent = uis.get(key);
				if (null != intent) {
					startActivity(intent);
				}
			}

		}

	}

}

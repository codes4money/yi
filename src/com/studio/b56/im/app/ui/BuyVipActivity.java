package com.studio.b56.im.app.ui;

import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.studio.b56.im.R;
import com.studio.b56.im.app.api.CreditApi;
import com.studio.b56.im.app.api.ErrorCode;
import com.studio.b56.im.app.api.PanLvApi.ClientAjaxCallback;
import com.studio.b56.im.app.api.SundryApi;
import com.studio.b56.im.vo.BuyConfig;

public class BuyVipActivity extends BaseActivity {
	@ViewInject(id = R.id.radio_credit)
	private RadioGroup radioGroup;
	@ViewInject(id = R.id.miaoshu)
	private TextView textMiaoshu;

	private int index = 0;
	private CreditApi creditApi;
	private SundryApi sundryApi;

	private int currIndex = -1;

	private List<BuyConfig> configs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.buy_vip);
		this.baseInit();
	}

	@Override
	protected void baseInit() {
		super.baseInit();
		creditApi = new CreditApi();
		sundryApi = new SundryApi();
		getMiaoshu();
		getBuyConfig();
	}

	private void showList() {
		if (configs != null) {
			for (BuyConfig buyConfig : configs) {
				StringBuffer text = new StringBuffer();
				text.append(buyConfig.getCredit()).append("分").append(" / ")
						.append(buyConfig.getValue()).append("天");
				RadioButton child = (RadioButton) LayoutInflater.from(
						getBaseContext()).inflate(R.layout.buy_vip_radiobutton,
						null);
				child.setId(index);
				child.setText(text.toString());
				child.setTag(buyConfig);
				radioGroup.addView(child);
				index++;
			}
			radioGroup.check(0);
			currIndex = 0;
		}

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				currIndex = checkedId;
			}
		});
	}

	private void getBuyConfig() {
		creditApi.getBuyConfig(getUserInfoVo().getUid(),
				new ClientAjaxCallback() {

					@Override
					public void onStart() {
						super.onStart();
						getWaitDialog().setMessage("获取配置信息");
						getWaitDialog().show();
					}

					@Override
					public void onSuccess(String t) {
						super.onSuccess(t);
						getWaitDialog().cancel();
						if (DEBUG) {
							Log.d("获取购买列表:", t);
						}
						try {
							String data = ErrorCode
									.getData(getBaseContext(), t);
							configs = JSONArray.parseArray(data,
									BuyConfig.class);

							showList();
						} catch (Exception e) {
						}
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						super.onFailure(t, strMsg);
						getWaitDialog().cancel();
						showToast("获取列表失败!");
					}

				});
	}

	private void getMiaoshu() {
		sundryApi.getTips(SundryApi.TIPS_BUYCON, new ClientAjaxCallback() {

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				try {
					String data = ErrorCode.getData(t);
					if (!TextUtils.isEmpty(data)) {
						String value = JSONObject.parseObject(data).getString(
								"value");
						textMiaoshu.setText(value);
					}
				} catch (Exception e) {
				}
			}

		});
	}

	@Override
	protected void initTitle() {
		setTitleContent("购买");
		setBtnBack();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goumai:
			goumaiAction();
			break;

		default:
			break;
		}
	}

	private void goumaiAction() {
		if (DEBUG) {
			Log.d("购买会员", "当前选择的下标为:" + currIndex);
		}
		if (configs == null) {
			showToast("没有找到列表!");
		} else if (currIndex == -1) {
			showToast("没有找到列表!");
		} else {
			BuyConfig config = configs.get(currIndex);
			if (config != null) {
				String value = (config.getValue() * 24 * 3600) + "";
				if (DEBUG) {
					Log.d("购买会员", "购买的时间:" + value);
				}
				creditApi.buyVip(getUserInfoVo().getUid(), config.getCredit()
						+ "", value, new ClientAjaxCallback() {

					@Override
					public void onStart() {
						super.onStart();
						getWaitDialog().setMessage("购买");
						getWaitDialog().show();
					}

					@Override
					public void onSuccess(String t) {
						super.onSuccess(t);
						getWaitDialog().cancel();
						if (DEBUG) {
							Log.d("获取购买列表:", t);
						}
						try {
							String data = ErrorCode
									.getData(getBaseContext(), t);
							if("1".equals(data)){
								setResult(Activity.RESULT_OK);
								getPromptDialog().setMessage("购买VIP成功!");
								getPromptDialog().removeCannel();
								getPromptDialog().addConfirm(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										getPromptDialog().cancel();
										finish();
									}
								});
								getPromptDialog().setConfirmText("确定");
								getPromptDialog().show();
							}else if("0".equals(data)){
								getPromptDialog().setMessage("购买VIP失败!");
								getPromptDialog().removeCannel();
								getPromptDialog().addConfirm(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										getPromptDialog().cancel();
										finish();
									}
								});
								getPromptDialog().setConfirmText("确定");
								getPromptDialog().show();
							}
						} catch (Exception e) {
						}
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						super.onFailure(t, strMsg);
						getWaitDialog().cancel();
						showToast("获取列表失败!");
					}
				});
			} else {
				showToast("错误!");
				finish();
			}
		}
	}

}

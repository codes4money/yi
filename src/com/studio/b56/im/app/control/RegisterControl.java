package com.studio.b56.im.app.control;

import android.text.TextUtils;
import android.widget.Toast;

import com.studio.b56.im.app.ui.RegisterActivity;
import com.studio.b56.im.app.ui.common.RegisterUtil;
import com.studio.b56.im.app.vo.RegisterVo;

public class RegisterControl{
	private RegisterActivity registerActivity;

	public RegisterControl(RegisterActivity registerActivity) {
		super();
		this.registerActivity = registerActivity;
	}

	public void submit() {
		RegisterVo registerVo = null;
		String username = registerActivity.getEditUsername().getText()
				.toString();
		String password = registerActivity.getEditPassword().getText()
				.toString();
		String repassword = registerActivity.getEditRepassword().getText()
				.toString();
		String nickname = registerActivity.getEditNickname().getText()
				.toString();

		if (TextUtils.isEmpty(username)) {
			Toast.makeText(registerActivity, "请填写帐号", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (TextUtils.isEmpty(nickname)) {
				Toast.makeText(registerActivity, "请填写昵称", Toast.LENGTH_SHORT)
						.show();
				return;
		} 
		else if (TextUtils.isEmpty(password)) {
			Toast.makeText(registerActivity, "请填写密码", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (TextUtils.isEmpty(repassword)) {
			Toast.makeText(registerActivity, "请确认密码", Toast.LENGTH_SHORT)
					.show();
			return;
	    }
//		else if(TextUtils.isEmpty(nominate)){
//			Toast.makeText(registerActivity, "填写推荐人号码!", Toast.LENGTH_SHORT)
//			.show();
//		}
		else if (!validatePhone(username) && !RegisterUtil.isEmail(username)) {
			Toast.makeText(registerActivity, "请输入正确的手机号或邮箱", Toast.LENGTH_SHORT)
					.show();
			return;
		} else if (!validatePassword(password)) {
			Toast.makeText(registerActivity, "密码长度范围为 6 ~ 16 位",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (!validate(password, repassword)) {
			Toast.makeText(registerActivity, "两次密码输入不正确", Toast.LENGTH_SHORT)
					.show();
			return;
		} 
//		else if (!validatePhone(nominate)) {
//			Toast.makeText(registerActivity, "推荐人手机号有误", Toast.LENGTH_SHORT)
//					.show();
//			return;
//		}
		else{
			registerVo = new RegisterVo(username, password, repassword);
			//registerActivity.nextActivity(registerVo);
			registerActivity.nextActivity(registerVo);
		}
	}
	private boolean validatePassword(String str) {
		return RegisterUtil.validatePassword(str);
	}

	private boolean validatePhone(String str) {
		return RegisterUtil.validatePhone(str);
	}

	private boolean validate(String str1, String str2) {
		return RegisterUtil.validate(str1, str2);
	}

}

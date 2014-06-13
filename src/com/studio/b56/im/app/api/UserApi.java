package com.studio.b56.im.app.api;

import net.tsz.afinal.http.AjaxParams;

import com.studio.b56.im.app.Constants;

public class UserApi extends PanLvApi{

	public UserApi() {
		super(Constants.ApiUrl.LOGIN_REGISTER);
	}
	
	/**
	 * 效验电话号是否存在
	 * */
	public void checkPhone(String phone, ClientAjaxCallback callBack){
		AjaxParams params = getParams("check_phone");
		params.put("phone", phone);
		
		postPanLv(params, callBack);
	}
}

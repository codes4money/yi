package com.studio.b56.im.app.api;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.studio.b56.im.app.Constants;

public class SundryApi extends PanLvApi{
	/** 注册提示信息 */
	public static final String TIPS_REG = "reg";
	/** 我的积分提示 */
	public static final String TIPS_ACREDIT = "acredit";
	
	public static final String TIPS_BUYCON = "buycon";
	
	public SundryApi() {
		super(Constants.ApiUrl.BACKFEED);
	}
	
	public void getTips(String tips, ClientAjaxCallback callBack){
		AjaxParams params = getParams("get_tips");
		params.put("tips", tips);
		postPanLv(params, callBack);
	}
}

package com.studio.b56.im.app.api;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.studio.b56.im.app.Constants;

/**
 * 
 * 功能： 积分查询 <br />
 * 日期：2013-7-10<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class KitingRecordApi extends PanLvApi{

	public KitingRecordApi() {
		super(Constants.ApiUrl.KITING);
	}

	/**
	 * 获取体现积分信息
	 * @param uid
	 * @param page 当前页
	 * @param pageSize 每页显示条数
	 * @param callBack
	 * @author fighter <br />
	 * 创建时间:2013-7-10<br />
	 * 修改时间:<br />
	 */
	public void getKiting(String uid, String page, String pageSize, ClientAjaxCallback callBack){
		AjaxParams params = getParams("kiting_record");
		params.put("uid", uid);
		params.put("page", page);
		params.put("page_size", pageSize);
		
		postPanLv(params, callBack);
	}
	
	/**
	 * 获取消费积分
	 * @param uid
	 * @param page 当前页
	 * @param pageSize 每页显示条数
	 * @param callBack
	 * @author fighter <br />
	 * 创建时间:2013-7-10<br />
	 * 修改时间:<br />
	 */
	public void getCreditRecord(String uid, String page, String pageSize, ClientAjaxCallback callBack){
		AjaxParams params = getParams("credit_record");
		params.put("uid", uid);
		params.put("page", page);
		params.put("page_size", pageSize);
		
		postPanLv(params, callBack);
	}
}

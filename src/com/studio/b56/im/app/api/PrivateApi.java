package com.studio.b56.im.app.api;

import java.util.Map;
import java.util.Set;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.studio.b56.im.app.Constants;

public class PrivateApi extends PanLvApi{

	public PrivateApi() {
		super(Constants.ApiUrl.PRIVATE_URL);
	}

	/**
	 * 
	 * @param uid
	 * @param serch 0 不可以搜索 1 可以搜索。
 	 * @param callBack
	 * 作者:fighter <br />
	 * 创建时间:2013-6-17<br />
	 * 修改时间:<br />
	 */
	public void isSerch(String uid, String serch, ClientAjaxCallback callBack){
		AjaxParams params = getParams("isSerch", uid);
		params.put("serch", serch);
		
		postPanLv(params, callBack);
	}
	
	/**
	 * 
	 * @param uid
	 * @param head <br/>
	 * 1,所有人可见<br/>
	 * 2,vip/认证用户可见<br/>
	 * 3,好友可见<br/>
	 * @param callBack
	 * <br/>作者:fighter <br />
	 * 创建时间:2013-6-17<br />
	 * 修改时间:<br />
	 */
	public void isHead(String uid, String head, ClientAjaxCallback callBack){
		AjaxParams params = getParams("isHead", uid);
		params.put("head", head);
		
		postPanLv(params, callBack);
	}
	
	/**
	 * 
	 * @param uid
	 * @param phone <br/>
	 * 1,所有人可见<br/>
	 * 2,vip/认证用户可见<br/>
	 * 3,好友可见<br/>
	 * @param callBack
	 * <br/>作者:fighter <br />
	 * 创建时间:2013-6-17<br />
	 * 修改时间:<br />
	 */
	public void isPhone(String uid, String phone, ClientAjaxCallback callBack){
		AjaxParams params = getParams("isPhone", uid);
		params.put("phone", phone);
		
		postPanLv(params, callBack);
	}
	
	/**
	 * 
	 * @param uid
	 * @param phone <br/>
	 * 1,所有人可见<br/>
	 * 2,vip/认证用户可见<br/>
	 * 3,好友可见<br/>
	 * @param callBack
	 * <br/>作者:fighter <br />
	 * 创建时间:2013-6-17<br />
	 * 修改时间:<br />
	 */
	public void isQQ(String uid, String qq, ClientAjaxCallback callBack){
		AjaxParams params = getParams("isQQ", uid);
		params.put("qq", qq);
		
		postPanLv(params, callBack);
	}
	
	public void privacy(String uid, Map<String, String> maps, ClientAjaxCallback callBack){
		AjaxParams params = getParams(maps, "privacy");
		params.put("uid", uid);
		Set<String> keys = maps.keySet();
		System.out.println("---------------- 请求参数 ----------------");
		for (String key : keys) {
			System.out.println(key + " ==>> " + maps.get(key));
		}
		postPanLv(params, callBack);
	}
	
}

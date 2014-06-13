package com.studio.b56.im.app.cache;

import com.alibaba.fastjson.JSON;
import com.studio.b56.im.SharedStorage;
import com.studio.b56.im.service.DES3;
import com.studio.b56.im.vo.UserInfoVo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * 功能： 用户信息存储! <br />
 * 日期：2013-5-21<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class UserInfoCache {
	/** 用户信息. */
	private static final String USERINFO_KEY = "cacheuk";
	private Context context;
	private SharedPreferences sharedPreferences;
	public UserInfoCache(Context context) {
		super();
		this.context = context;
		sharedPreferences = SharedStorage.getCacheSharedPreferences(this.context);
	}
	
	/**
	 * 缓存指定的用户信息.
	 * @param userInfo
	 * 作者:fighter <br />
	 * 创建时间:2013-5-21<br />
	 * 修改时间:<br />
	 */
	public void cacheUserInfo(UserInfoVo userInfo){
		String userInfoJson = JSON.toJSONString(userInfo);
		
		try {
			String userInfoEnc = DES3.encrypt(userInfoJson);
			
			Editor editor = sharedPreferences.edit();
			editor.putString(USERINFO_KEY, userInfoEnc);
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取本地缓存的用户信息
	 * @return null,本地没有用户信息。
	 * 作者:fighter <br />
	 * 创建时间:2013-5-21<br />
	 * 修改时间:<br />
	 */
	public UserInfoVo getCacheUserInfo(){
		String userInfoDec = sharedPreferences.getString(USERINFO_KEY, null);
		
		if(userInfoDec != null){
			try {
				String userInfoJson = DES3.decrypt(userInfoDec);
				return JSON.toJavaObject(JSON.parseObject(userInfoJson), UserInfoVo.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}else{
			return null;
		}
	}
	
}

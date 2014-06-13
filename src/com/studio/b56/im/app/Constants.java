package com.studio.b56.im.app;
/**
 * 
 * 功能：常量<br />
 * 日期：2013-5-21<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class Constants {
	
	/** 注册动作添加的附加参数 */
	public static final String ACTION_REGISTER_VO_EXTRA = "registerVoExtra";
	
	public static final String USERINFOCOKIE = "cokie_user";
	public static final String CUSTOMER = "customer";
	/** 账户类型 */
	public final static String[] ACCOUT_TYPE = {"陪聊", "伴聊"};
	
	public final static String[] SEX = {"男", "女"};
	/** 服务场合 */
	public final static String[] SERVICE_LOCATION = {"社交", "独处", "不限"};
	/**用于指定webview显示地址*/
	public final static class WebviewUrl{
		
		public static final String CONTACT_ME="";
		public static final String RECHARGE="";
	}
	public final static class ApiUrl{
		public static final String HOST = "115.236.32.231";
		public static final String BASE_URL = "http://115.236.32.231:8081/index.php";
		public static final String WEB_URL = "http://115.236.32.231:8081/group/";
		public static final String WeiBo_URL = "http://115.236.32.231:8081/weibo/";
		public static final String SIP_URL = "http://117.79.230.235:8888/chs/api/";
		/** 注册(生成效验码/重新获取效验码/ 信息验证/保存等)
 			登录(登录信息和修改密码) */
		public static final String LOGIN_REGISTER = BASE_URL + "/user/Index/action";
		/** 相册url */
		public static final String ALBUM_ACTION = BASE_URL + "/file/Album/action";
		/**上传图片*/
		public static final String UPLOAD_ACTION = BASE_URL + "/file/Album/upload";
		/** 用户信息url */
		public static final String USER_INFO_ACTION = BASE_URL + "/infor/Index/action";
		/** 好友关系url */
		public static final String FRIEND_ACTION = BASE_URL + "/friend/Index/action";
		public static final String PRIVATE_URL = BASE_URL + "/privacy/Index/action";
		public static final String CREDIT_URL = BASE_URL + "/credit/Index/action";
		public static final String BACKFEED = BASE_URL + "/sundry/Index/action";
		/** 提现规则 */
		public static final String CASHRULE = BASE_URL + "/banlv/index.php?app=user&mod=Index&do=cashrule";
		/** 联系我们 */
		public static final String CONTACT_OUR = BASE_URL + "/banlv/index.php?app=user&mod=Index&do=contact_our";
		/** 联系我们 */
		public static final String KITING = BASE_URL + "/admin/Index/action";
		
		
	}
	
	public final static class CustomerType{
		/** 陪聊 */
		public static final String CHATTING = "1";     // 陪聊
		/** 寻伴 */
		public static final String WITHCHAT = "2";     // 寻伴
		
		public static String getServiceType(String key){
			if("1".equals(key)){
				return "独处";
			}else if("2".equals(key)){
				return "社交";
			}else{
				return "不限";
			}
		}
	}
	
	/**
	 * 
	 * 功能：记录用户登录时的状态等<br />
	 * 日期：2013-5-21<br />
	 * 地点：www.uvcims.com<br />
	 * 版本：ver 1.0<br />
	 * 
	 * @author fighter
	 * @since
	 */
	public final static class Login{
		/** 登录的状态 */
		public static final String LOGIN_STATE = "lste";
	}
	/**
	 * 
	 * 功能：用户上传的图片类型<br />
	 * 日期：2013-5-21<br />
	 * 地点：www.uvcims.com<br />
	 * 版本：ver 1.0<br />
	 * 
	 * @author lcy
	 * @since
	 */
	public final static class PhotoType{
		/**头像图片*/
		public static final String PHOTO_HEAD="1";
		/**相片*/
		public static final String PHOTO_NORMAL="2";
	}
	
}

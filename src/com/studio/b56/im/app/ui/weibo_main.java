package com.studio.b56.im.app.ui;

import org.jivesoftware.smack.XMPPConnection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;

import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.ui.group.group_add;
import com.studio.b56.im.app.ui.group.grouplist;
import com.studio.b56.im.application.PeibanApplication;
import com.studio.b56.im.vo.UserInfoVo;

public class weibo_main extends BaseActivity {
	
    private UserInfoVo userInfoVo;
	private ProgressDialog pg =null;
	public String mid="";
	public String roomAddr="",g_name,g_dex;
	private TextView title,group_name,group_dex;
	public boolean isCheck=false;
	private PeibanApplication shangwupanlvApplication;
	public  XMPPConnection xmppconn=null;
    public WebView webview;
    private Handler mHandler = new Handler();  
    
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	   @Override
		protected void initTitle() {
			setBtnBack();
			setTitleContent("微博");

		}
		@Override
		protected void titleBtnLeft() {
			super.titleBtnRight();
	        finish();
		}
		
		public boolean onKeyDown(int keyCode, KeyEvent event) {       
	        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {       
	        	webview.goBack();       
	                   return true;       
	        }       
	        return super.onKeyDown(keyCode, event);       
	    } 
		@Override
		protected void baseInit() {
			super.baseInit();
			userInfoVo = new UserInfoCache(getBaseContext()).getCacheUserInfo();
			
			
			
			webview=(WebView)findViewById(R.id.weibo_main_web);
			webview.getSettings().setJavaScriptEnabled(true);  
			webview.setWebViewClient(new WebViewClient(){       
                public boolean shouldOverrideUrlLoading(WebView view, String url) {       
                    view.loadUrl(url);       
                    return true;       
                }       
    });  
		   
			webview.addJavascriptInterface(new Object() {       
		           public void clickOnAndroid() {       
			               mHandler.post(new Runnable() {       
			                    public void run() {       
			                    	webview.loadUrl("javascript:wave()");       
			                    }       
			               });       
			            }       
		      }, "demo");       

			webview.loadUrl(Constants.ApiUrl.WeiBo_URL+"ajax.php?mod=member&code=login2&username="+userInfoVo.getUid()+"&password="+userInfoVo.getPassword()); 
			
		}

		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.setContentView(R.layout.weibo_main);
			
			this.baseInit();
			initTitle();
		}
}

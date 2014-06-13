package com.studio.b56.im.app.ui;

import android.os.Bundle;

import com.studio.b56.im.R;
/**
 * 
 * 功能：会话列表 <br />
 * 日期：2013-5-30<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class ChatListActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.authorize_list_item);
		baseInit();
	}
	
	@Override
	protected void initTitle() {
		
	}
}

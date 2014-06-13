package com.studio.b56.im.app.ui;

import android.os.Bundle;
import android.view.View;

import com.studio.b56.im.R;

/**
 * 
 * 功能：陌生人(陪聊用户.)<br />
 * 日期：2013-5-29<br />
 * 地点：www.uvcims.com<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class StrangerChattingActivity extends ChattingDetailActivity {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.shangwupanlv.app.ui.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.chatting_info);
		this.baseInit();
		getNetworkAlbum();
	}

	@Override
	protected void baseInit() {
		super.baseInit();
		meInit();
	}
	
	protected void meInit() {
		showApply();
		showInfo();
		getBtnAddFriend().setVisibility(View.VISIBLE);
		getBtnSayHello().setVisibility(View.VISIBLE);

		getRefresh();
	}

	
	protected void getNetworkAlbum() {
		super.getNetworkAlbum();
	}
	
}

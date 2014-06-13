package com.studio.b56.im.app.ui;

import android.content.Intent;
import android.view.View;

import com.studio.b56.im.R;

public class FriendChattingActivity extends StrangerChattingActivity{

	/* (non-Javadoc)
	 * @see com.shangwupanlv.app.ui.StrangerChattingActivity#baseInit()
	 */
	@Override
	protected void baseInit() {
		super.baseInit();
	}
	
	protected void meInit() {
		showApply();
		getBtnAddFriend().setVisibility(View.GONE);
		getBtnSayHello().setVisibility(View.VISIBLE);
		showInfo();
	}
	
	@Override
	protected void getApplyAuth() {
	}
	
	@Override
	protected void initTitle(){
		super.initTitle();
		setTitleRight(R.string.title_btn_bz);
	}
	
	@Override
	protected void titleBtnRight(){
		super.titleBtnRight();
		getDialogMark().show();
	}


	@Override
	protected void markNameSuccess(String markName) {
		try {
//			IndexTabActivity.getInstance().getFriendListAction().pushFriendList();
			sendBroadcast(new Intent(IndexTabActivity.ACTION_REFRESH_FRIEND));
		} catch (Exception e) {
		}
		
		super.markNameSuccess(markName);
	}
	
	
}

package com.studio.b56.im.app.ui;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxParams;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.studio.b56.im.R;
import com.studio.b56.im.app.Constants;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.ui.IndividualCenterActivity.IndividualCenterOnClick;
import com.studio.b56.im.app.ui.IndividualCenterActivity.LoginAjaxCallBack;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;

public class friend_add extends BaseActivity{
	@ViewInject(id = R.id.individual_center_item_addfriend1)
	private View item1;
	@ViewInject(id = R.id.individual_center_item_addfriend2)
	private View item2;
	@ViewInject(id = R.id.individual_center_item_addfriend3)
	private View item3;
	
	private View.OnClickListener individualAction;
	private FinalDb finalDb;
	private UserInfoVo userInfoVo;
	CustomerVo cvo=new CustomerVo();
		
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_add);
		
		userInfoVo= new UserInfoCache(this).getCacheUserInfo();
		finalDb = FinalFactory.createFinalDb(this,userInfoVo);
		cvo=finalDb.findById(userInfoVo.getUid(), CustomerVo.class);
		
		baseInit();
		   
		String name=cvo.getName();    		
	}
	@Override
	protected void initTitle() {
		// TODO Auto-generated method stub
		setTitleContent(R.string.main_addcontact);
//		setTitleLeft("返回");
		setBtnBack();
	}
	
	@Override
	protected void titleBtnLeft() {
		super.titleBtnLeft();
		finish();
	}
	@Override
	protected void baseInit() {
		super.baseInit();
		addLisener();
	}
	private void addLisener() {
		individualAction = new IndividualCenterOnClick();
		item1.setOnClickListener(individualAction);
		item2.setOnClickListener(individualAction);
		item3.setOnClickListener(individualAction);
	}
	class IndividualCenterOnClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			int id = v.getId();
			switch (id) {
			case R.id.individual_center_item_addfriend1:
				intent.setClass(friend_add.this, LocationActivity.class);
				startActivity(intent);
				break;
			case R.id.individual_center_item_addfriend2:
				intent.setClass(friend_add.this, addFriendMod.class);
				intent.putExtra("addfriendtype", 1);
				startActivity(intent);
				break;
			case R.id.individual_center_item_addfriend3:
				intent.setClass(friend_add.this, addFriendMod.class);
				intent.putExtra("addfriendtype", 0);
				startActivity(intent);
				break;
				
			}
		}

	}
}

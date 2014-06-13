package com.studio.b56.im.app.ui;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;

import com.studio.b56.im.R;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.app.dao.SessionManager;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.app.ui.group.group_add;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.MessageInfo;
import com.studio.b56.im.vo.SessionList;
import com.studio.b56.im.vo.UserInfoVo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ChatDetailActivity extends BaseActivity implements OnClickListener {

	private ImageButton topLeft, topRight;
	private TextView topMiddle;
	
	private UserInfoVo userInfoVo;
	CustomerVo cvo=new CustomerVo();
	private FinalDb finalDb;
	SessionList sessionList;
	List<MessageInfo> messageInfos;
	private ImageView meHeader, meTo;
	private SessionManager sessionManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatdetail);
		
		userInfoVo= new UserInfoCache(this).getCacheUserInfo();
		cvo=(CustomerVo) getIntent().getSerializableExtra("data");
		if(cvo == null){
			finish();
			return;
		}
		sessionList=(SessionList) getIntent().getSerializableExtra("sessionList");
		sessionManager = new SessionManager(getFinalDb(), getPhotoBitmap(), getBaseContext());
		
		initView2();
	}

	private void initView2() {
		topLeft = (ImageButton) this.findViewById(R.id.topLeft);
		topRight = (ImageButton) this.findViewById(R.id.topRight);
		topMiddle = (TextView) this.findViewById(R.id.topMiddle);
		

		((TextView) this.findViewById(R.id.chatdetail_nickname)).setText(cvo.getName());
		ImageView myhead=(ImageView)this.findViewById(R.id.heads);
		FinalOnloadBitmap.finalDisplay(getBaseContext(),cvo,myhead,FinalFactory.createFinalBitmap(getBaseContext()));
		
		((RelativeLayout) this.findViewById(R.id.clearChat)).setOnClickListener(this);
		
		((ImageView)this.findViewById(R.id.topMsg)).setOnClickListener(this);
		((ImageView)this.findViewById(R.id.newMsg)).setOnClickListener(this);
		
		
		setListener();
	}

	private void setListener() {
		setTop();
	}

	private void setTop() {
		topRight.setVisibility(View.GONE);
		topMiddle.setText(cvo.getName());
		topLeft.setBackgroundResource(R.drawable.back);
		topLeft.setOnClickListener(this);
	}
	
	/* 清空消息内容 */
	void clearMessageAction(){
		new AsyncTask<Void, Void, Boolean>(){
			@Override
			protected Boolean doInBackground(Void... params) {
				return sessionManager.clearSessionList2(sessionList);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				getWaitDialog().setMessage("清空内容.");
				getWaitDialog().show();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if(result){
					getWaitDialog().setMessage("清除成功");
				}else{
					getWaitDialog().setMessage("清除失败");
				}
				getWaitDialog().dismiss();
			}
		}.execute();
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topLeft:
			finish();
			break;
		case R.id.clearChat:
			 new com.studio.b56.im.app.control.BaseDialog.Builder(ChatDetailActivity.this).setTitle("提示").setMessage("确认清除消息！").setYesListener(new BaseDialog.YesListener()
		      {
		        public void doYes()
		        {
		        	clearMessageAction();
		        }
		      }).setNoCancel(false).setCancelListener(new BaseDialog.CancelListener()
		      {
		        public void doCancel()
		        {
		        	
		        }
		      }).show();
			 
			break;
		}
	}

	@Override
	protected void initTitle() {
		// TODO Auto-generated method stub
		
	}
}

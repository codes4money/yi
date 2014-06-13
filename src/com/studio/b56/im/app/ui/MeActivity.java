package com.studio.b56.im.app.ui;

import java.util.ArrayList;

import net.tsz.afinal.FinalDb;

import com.studio.b56.im.R;
import com.studio.b56.im.app.FinalFactory;
import com.studio.b56.im.app.cache.UserInfoCache;
import com.studio.b56.im.app.ui.common.FinalOnloadBitmap;
import com.studio.b56.im.vo.CustomerVo;
import com.studio.b56.im.vo.UserInfoVo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MeActivity extends Activity implements OnClickListener {

	private ImageButton topLeft, topRight;
	private TextView topMiddle;

	private ImageView meHeader, meTo;
	private TextView meSex, weixinNum, meCoupons, meRed, meIngral, meBalance;
	private ListView meOrder, meSet;

	private UserInfoVo userInfoVo;
	CustomerVo cvo=new CustomerVo();
	private FinalDb finalDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me);
		userInfoVo= new UserInfoCache(this).getCacheUserInfo();
		finalDb = FinalFactory.createFinalDb(this,userInfoVo);
		cvo=finalDb.findById(userInfoVo.getUid(), CustomerVo.class);
		
		initView();
	}

	private void initView() {
		topLeft = (ImageButton) this.findViewById(R.id.topLeft);
		topRight = (ImageButton) this.findViewById(R.id.topRight);
		topMiddle = (TextView) this.findViewById(R.id.topMiddle);

		meOrder = (ListView) this.findViewById(R.id.meOrder);
		meSet = (ListView) this.findViewById(R.id.meSet);
		setListener();
		
		
		((TextView) this.findViewById(R.id.meSex)).setText(cvo.getName());
		((TextView) this.findViewById(R.id.weixinNum)).setText("约架号:"+cvo.getUid());
		ImageView myhead=(ImageView)this.findViewById(R.id.meHeader);
		
		myhead.setOnClickListener(this);
		((TextView) this.findViewById(R.id.meSex)).setOnClickListener(this);
		((TextView) this.findViewById(R.id.weixinNum)).setOnClickListener(this);
		
		FinalOnloadBitmap.finalDisplay(getBaseContext(),cvo,myhead,FinalFactory.createFinalBitmap(getBaseContext()));
		
	}

	private void setListener() {
		setTop();
		topRight.setOnClickListener(this);

		meOrder.setAdapter(new ApproximatelyAdapter(this, getArrOrder()));
		meOrder.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});
		meSet.setAdapter(new ApproximatelyAdapter(this, getArrSet()));
		meSet.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if(arg2==4)
				{
					Intent intent = new Intent(MeActivity.this, NotifyActivity.class);
					startActivity(intent);
				}
				else if(arg2==3)	
				{
					Intent intent=new Intent(MeActivity.this,IndividualCenterActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	private int[] resImg = { R.drawable.me_order, R.drawable.paydey,
			R.drawable.all_order };
	private String[] aContent = { "我的订单", "待付款", "全部订单" };

	private ArrayList<ApproximatelyBean> getArrOrder() {
		ArrayList<ApproximatelyBean> arr = new ArrayList<ApproximatelyBean>();
		for (int i = 0; i < resImg.length; i++) {
			ApproximatelyBean ab = new ApproximatelyBean();
			ab.setResImg(resImg[i]);
			ab.setTitle(aContent[i]);
			arr.add(ab);
		}
		return arr;
	}

	private int[] resImg1 = { R.drawable.me_order, R.drawable.paydey,
			R.drawable.all_order,R.drawable.all_order,R.drawable.all_order};
	private String[] aContent1 = { "我的作品", "我的收藏", "我的银行卡", "设置", "系统通知"};

	private ArrayList<ApproximatelyBean> getArrSet() {
		ArrayList<ApproximatelyBean> arr = new ArrayList<ApproximatelyBean>();
		for (int i = 0; i < resImg1.length; i++) {
			ApproximatelyBean ab = new ApproximatelyBean();
			ab.setResImg(resImg1[i]);
			ab.setTitle(aContent1[i]);
			arr.add(ab);
		}
		return arr;
	}

	private void setTop() {
		topLeft.setVisibility(View.GONE);
		topRight.setVisibility(View.GONE);
		topMiddle.setText("我");
		//topRight.setBackgroundResource(R.drawable.add_btn);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topRight:
			break;
		case R.id.meSex:
		case R.id.weixinNum:
		case R.id.meHeader:
			Intent intent=new Intent(MeActivity.this,IndividualCenterActivity.class);
			startActivity(intent);
			break;
		}
	}
}

package com.studio.b56.im.app.ui;
import java.util.ArrayList;

import com.studio.b56.im.R;
import com.studio.b56.im.app.ui.weibo_main;
import com.zijunlin.Zxing.Demo.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ApproximatelyActivity extends Activity implements OnClickListener {

	private ImageButton topLeft, topRight;
	private TextView topMiddle;

	private ListView aList;
	private ApproximatelyAdapter aAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approximately);
		initView();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			try {
//				IndexTabActivity.getInstance().callbackLocation();
				//sendBroadcast(new Intent(IndexTabActivity.ACTION_CALLBACK));
				return false;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		return super.onKeyDown(keyCode, event);
	}
	private void initView() {
		topLeft = (ImageButton) this.findViewById(R.id.topLeft);
		topRight = (ImageButton) this.findViewById(R.id.topRight);
		topMiddle = (TextView) this.findViewById(R.id.topMiddle);
		aList = (ListView) this.findViewById(R.id.aList);
		setListener();
	}

	private void setListener() {
		setTop();
		topRight.setOnClickListener(this);
		aAdapter = new ApproximatelyAdapter(this, getArr());
		aList.setAdapter(aAdapter);
		aList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if(arg2==0)
				{
					Intent intent = new Intent(ApproximatelyActivity.this, weibo_main.class);
					startActivity(intent);
				}
				else if(arg2==1)
				{
					Intent intent = new Intent(ApproximatelyActivity.this, LocationActivity.class);
					startActivity(intent);
				}
				else  if(arg2==2)
				{
					Intent intent = new Intent(ApproximatelyActivity.this,CaptureActivity.class);
					startActivity(intent);
				}
				
			}
		});
	}

	private int[] resImg = { R.drawable.me_img, R.drawable.wholocal,
			R.drawable.sweep, R.drawable.game };
	private String[] aContent = { "我的圈子", "谁在附近", "扫一扫", "功能有待上线，请关注我们的官方" };

	private ArrayList<ApproximatelyBean> getArr() {
		ArrayList<ApproximatelyBean> arr = new ArrayList<ApproximatelyBean>();
		for (int i = 0; i < resImg.length; i++) {
			ApproximatelyBean ab = new ApproximatelyBean();
			ab.setResImg(resImg[i]);
			ab.setTitle(aContent[i]);
			arr.add(ab);
		}
		return arr;
	}

	private void setTop() {
		topLeft.setVisibility(View.GONE);
		topMiddle.setText("开放功能");
		//topRight.setBackgroundResource(R.drawable.add_btn);
		topRight.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topRight:
			Toast.makeText(getBaseContext(), "add",1).show();
			break;
		}
	}
}

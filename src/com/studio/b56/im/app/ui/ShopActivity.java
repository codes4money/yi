package com.studio.b56.im.app.ui;

import java.util.ArrayList;

import com.studio.b56.im.R;
import com.studio.b56.im.app.control.MyGallery;
import com.studio.b56.im.app.ui.adpater.ShopGridAdapter;
import com.studio.b56.im.app.ui.adpater.ShopGridBean;
import com.studio.b56.im.app.ui.adpater.ShopImgAdapter;
import com.studio.b56.im.app.ui.adpater.ShopImgBean;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ShopActivity extends Activity implements OnClickListener {

	private ImageButton topLeft, topRight;
	private TextView topMiddle;
	private MyGallery galleryImg;
	private ShopImgAdapter siAdapter;
	private GridView shopGrid;
	private ShopGridAdapter sgAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);
		initView();
	}

	private void initView() {
		topLeft = (ImageButton) this.findViewById(R.id.topLeft);
		topRight = (ImageButton) this.findViewById(R.id.topRight);
		topMiddle = (TextView) this.findViewById(R.id.topMiddle);

		galleryImg = (MyGallery) this.findViewById(R.id.galleryImg);
		shopGrid = (GridView) this.findViewById(R.id.shopGrid);
		setListener();
	}

	private void setListener() {
		setTop();
		topRight.setOnClickListener(this);

		siAdapter = new ShopImgAdapter(this, getArr());
		galleryImg.setAdapter(siAdapter);
		galleryImg.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				arg2 = arg2 % getArr().size();
				Toast.makeText(getBaseContext(), "pos:" + arg2, 1).show();
			}
		});
		sgAdapter = new ShopGridAdapter(this, getShopArr());
		shopGrid.setAdapter(sgAdapter);
		shopGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getBaseContext(), "pos:"+arg2,1).show();
			}
		});
	}

	private int[] resImg = { R.drawable.shop_img1, R.drawable.shop_img2,
			R.drawable.shop_img1, R.drawable.shop_img2, R.drawable.shop_img1,
			R.drawable.shop_img2, R.drawable.shop_img1, R.drawable.shop_img2 };

	private ArrayList<ShopGridBean> getShopArr() {
		ArrayList<ShopGridBean> arr = new ArrayList<ShopGridBean>();
		for (int i = 0; i < resImg.length; i++) {
			ShopGridBean sgb = new ShopGridBean();
			sgb.setResImg(resImg[i]);
			arr.add(sgb);
		}
		return arr;
	}

	private int[] gImg = { R.drawable.top_img1, R.drawable.top_img2 };

	private ArrayList<ShopImgBean> getArr() {
		ArrayList<ShopImgBean> arr = new ArrayList<ShopImgBean>();
		for (int i = 0; i < gImg.length; i++) {
			ShopImgBean sib = new ShopImgBean();
			sib.setResImg(gImg[i]);
			arr.add(sib);
		}
		return arr;
	}

	private void setTop() {
		topLeft.setVisibility(View.GONE);
		topMiddle.setText("享购");
		topRight.setBackgroundResource(R.drawable.add_btn);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.topRight:

			break;
		}
	}
}

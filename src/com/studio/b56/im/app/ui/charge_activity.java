package com.studio.b56.im.app.ui;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import com.alipay.android.msp.demo.ExternalPartner;
import com.studio.b56.im.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class charge_activity  extends FinalActivity{
	
	private LinearLayout content;
	private TextView title;
	private Intent intent;
	private LayoutInflater inflater;
	
	@ViewInject(id = R.id.topMiddle)
	private TextView topTitle;
	@ViewInject(id = R.id.topLeft)
	private ImageButton topLeft;
	@ViewInject(id = R.id.topRight)
	private ImageButton topRight;
	
	  @Override
	public void onCreate(Bundle icicle) {
				super.onCreate(icicle);
				
				setContentView(R.layout.charge_activity);
			
				topTitle.setText("充值中心");
				topLeft.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
				
				((TextView)findViewById(R.id.charge_sqt_item)).setOnClickListener(new OnClickListener() {
					@Override
				public void onClick(View v) {

						Intent intent=new Intent(charge_activity.this,pay_cardpay.class);
						startActivity(intent);
						//finish();
					}
				});
				

				((TextView)findViewById(R.id.charge_alipay_item)).setOnClickListener(new OnClickListener() {
					@Override
				public void onClick(View v) {

						Intent intent=new Intent(charge_activity.this,ExternalPartner.class);
						startActivity(intent);
					}
				});
				
	  }
}

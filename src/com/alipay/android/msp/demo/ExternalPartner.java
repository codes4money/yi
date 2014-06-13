package com.alipay.android.msp.demo;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.app.sdk.AliPay;
import com.studio.b56.im.R;
import com.studio.b56.im.app.control.BaseDialog;
import com.studio.b56.im.app.ui.BaseActivity;
import com.studio.b56.im.app.ui.LoginActivity;
import com.studio.b56.im.app.ui.RegisterActivity;

public class ExternalPartner extends Activity{
	@ViewInject(id = R.id.topMiddle)
	private TextView topTitle;
	@ViewInject(id = R.id.topLeft)
	private ImageButton topLeft;
	@ViewInject(id = R.id.topRight)
	private ImageButton topRight;
	
	public static final String TAG = "ExternalPartner";

	private static final int RQF_PAY = 1;

	private Button loginbnt;
	private ProgressDialog mProgress = null;
    public int paynum=0;
    public String phonenum="";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String sharedPrefsFile = "com.studio.b56.im_preferences";
		SharedPreferences settings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
		phonenum=settings.getString("username", "");//手机号码
		
		setContentView(R.layout.pay_alipay);
		
		topTitle.setText("支付宝充值");
		
		topRight.setBackgroundResource(R.drawable.reg_hook);
		topRight.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alipay();
			}
		});
		
		topLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		loginbnt=(Button)findViewById(R.id.pay_alipayok_btn);
		loginbnt.setOnClickListener(new OnClickListener() {
			@Override
		public void onClick(View v) {
				alipay();
			}
		});
		
	}
	
	public void alipay()
	{
		String numstr=((EditText)findViewById(R.id.pay_alipaytxt_username)).getText().toString();
		if(numstr!="")
		{
		try{
		    paynum= Integer.parseInt(numstr);
		}catch(Exception e){}
		
		 if(paynum<10)
		 {
			 new BaseDialog.Builder(ExternalPartner.this).setTitle("提示").setMessage("充值金额必需大于10元!").setYesListener(new BaseDialog.YesListener()
		      {
		        public void doYes()
		        {
		        }
		      }).setNoCancel(true).show();
		}
		else
		{
			pay();
		}
		 
		}
		else
		{
			 new BaseDialog.Builder(ExternalPartner.this).setTitle("提示").setMessage("请输入充值金额!").setYesListener(new BaseDialog.YesListener()
		      {
		        public void doYes()
		        {
		        }
		      }).setNoCancel(true).show();
		}
		
	}
	
	public void pay()
	{
		try {
			Log.i("ExternalPartner", "onItemClick");
			String info = getNewOrderInfo();
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();
			Log.i("ExternalPartner", "start pay");
			// start the pay.
			Result.sResult = null;
			Log.i(TAG, "info = " + info);
			final String orderInfo = info;
			new Thread() {
				public void run() {
					String result = new AliPay(ExternalPartner.this, mHandler).pay(orderInfo);

					Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(ExternalPartner.this, "支付失败!",
					Toast.LENGTH_SHORT).show();
		}
			
	}
	

	private String getNewOrderInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(getOutTradeNo());
		sb.append("\"&subject=\"");
		sb.append("讯聊充值");
		sb.append("\"&body=\"");
		sb.append(phonenum);
		sb.append("\"&total_fee=\"");
		sb.append(paynum);
		sb.append("\"&notify_url=\"");

		sb.append(URLEncoder.encode("http://www.uulm.com/alipaymobile/notify_url.php"));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String key = format.format(date);

		java.util.Random r = new java.util.Random();
		key += r.nextInt();
		key = key.substring(0, 15);
		Log.d(TAG, "outTradeNo: " + key);
		return key;
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}


	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Result.sResult = (String) msg.obj;

			switch (msg.what) {
			case RQF_PAY: {
				Toast.makeText(ExternalPartner.this, Result.getResult(),Toast.LENGTH_SHORT).show();
				if(Result.sResult=="9000")
				{
					 new BaseDialog.Builder(ExternalPartner.this).setTitle("提示").setMessage("充值成功!").setYesListener(new BaseDialog.YesListener()
				      {
				        public void doYes()
				        {
				        	finish();
				        }
				      }).setNoCancel(true).show();
				}
				
			}
				break;
			default:
				break;
			}
		};
	};

	public static class Product {
		public String subject;
		public String body;
		public String price;
	}

	public static Product[] sProducts;
}

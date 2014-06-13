package com.studio.b56.im.app.ui;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import org.sipdroid.sipua.ui.Settings;

import com.studio.b56.im.CommFun;
import com.studio.b56.im.R;
import com.studio.b56.im.app.control.BaseDialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class setting_phonetype extends FinalActivity{
	private RadioButton r1,r2;
	SharedPreferences settings;
	@ViewInject(id = R.id.topMiddle)
	private TextView topTitle;
	@ViewInject(id = R.id.topLeft)
	private ImageButton topLeft;
	@ViewInject(id = R.id.topRight)
	private ImageButton topRight;
	@ViewInject(id = R.id.show_number_btn_open)
	private Button loginbnt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting_phonetype_activity);
		topTitle.setText("拨号方式");
		topLeft.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		String sharedPrefsFile = "com.studio.b56.im_preferences";
		settings = getSharedPreferences(sharedPrefsFile, MODE_PRIVATE);
		
		
		r1=(RadioButton)findViewById(R.id.setting_phonetype_open);
		r2=(RadioButton)findViewById(R.id.setting_phonetype_close);
		
		if(settings.getBoolean("iscallback", true))
		{
			r1.setChecked(true);
			r2.setChecked(false);
		}
		else
		{
			r1.setChecked(false);
			r2.setChecked(true);
		}
		
		loginbnt.setOnClickListener(new OnClickListener() {
			@Override
		public void onClick(View v) {
				
				Editor edit = settings.edit();
				if(r1.isChecked())
				{
				  edit.putBoolean("iscallback", true);
				}
				else
				{
				  edit.putBoolean("iscallback", false);
				}
				edit.commit();
				
				 new BaseDialog.Builder(setting_phonetype.this).setTitle("提示").setMessage("设置成功!").setYesListener(new BaseDialog.YesListener()
			      {
			        public void doYes()
			        {
			        	finish();
			        }
			      }).setNoCancel(true).show();
				 
				
			}
		});
		
		
		topRight.setOnClickListener(new OnClickListener() {
			@Override
		public void onClick(View v) {
				
				finish();
			}
		});
	}

}

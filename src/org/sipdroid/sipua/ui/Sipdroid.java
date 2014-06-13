package org.sipdroid.sipua.ui;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.sipdroid.sipua.UserAgent;
import org.sipdroid.sipua.phone.MyPhoneBroadcastReceiver;

import com.studio.b56.im.R;
import com.studio.b56.im.application.PeibanApplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.Date;

import android.telephony.TelephonyManager; 
import android.util.Log;
public class Sipdroid extends Activity implements OnClickListener {

	public static final boolean release = false;
	public static final boolean market = false;
	  public final static String TAG = "MyPhone";   
	     
	public int callback=0;
	public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;   
	     
   private MyPhoneBroadcastReceiver mBroadcastReceiver;  
	
	
	/* Following the menu item constants which will be used for menu creation */
	public static final int FIRST_MENU_ID = Menu.FIRST;
	public static final int CONFIGURE_MENU_ITEM = FIRST_MENU_ID + 1;
	public static final int ABOUT_MENU_ITEM = FIRST_MENU_ID + 2;
	public static final int EXIT_MENU_ITEM = FIRST_MENU_ID + 3;
	public static final int CHANG_USERACCOUNT = FIRST_MENU_ID + 4;
	
	private static AlertDialog m_AlertDlg;
	AutoCompleteTextView sip_uri_box2;
	Button createButton;
	private EditText phone;
	private Button delete;
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	private SoundPool spool;
	private AudioManager am = null;
	private ListView listView;
	private PeibanApplication application;
	

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		on(this,true);
		
		
	}

	@Override
	protected void onPause() {
	super.onPause();
	}
	@Override
	public void onDestroy() {
	super.onDestroy();
	
		try
		{
		 unregisterReceiver(mBroadcastReceiver);   
	} catch (Exception e) {
	}
		
	}
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}
	

	
	private void play(int id) {
		int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		float value = (float)0.7 / max * current;
		spool.setVolume(spool.play(id, value, value, 0, 0, 1f), value, value);
	}

	private void call(String phone) {
		phone=phone.replace("-", "").replace(" ", "");
		 Receiver.engine(this).call(phone,true);
	}
	

	public static boolean on(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Settings.PREF_ON, Settings.DEFAULT_ON);
	}

	public static void on(Context context,boolean on) {
		Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		edit.putBoolean(Settings.PREF_ON, on);
		edit.commit();
        if (on) Receiver.engine(context).isRegistered();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Receiver.call_state != UserAgent.UA_STATE_IDLE)Receiver.moveTop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
	
		return result;
	}

	void call_menu(AutoCompleteTextView view)
	{

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		Intent intent = null;
		switch (item.getItemId()) {
		case EXIT_MENU_ITEM: 
			on(this,false);
			Receiver.pos(true);
			Receiver.engine(this).halt();
			Receiver.mSipdroidEngine = null;
			Receiver.reRegister(0);
			stopService(new Intent(this,RegisterService.class));
			finish();
			break;
			
		case CONFIGURE_MENU_ITEM: {
			try {
				intent = new Intent(this, org.sipdroid.sipua.ui.Settings.class);
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
			}
		}
			break;
		}

		return result;
	}
	
	public static String getVersion() {
		return getVersion(Receiver.mContext);
	}
	
	public static String getVersion(Context context) {
		final String unknown = "Unknown";
		
		if (context == null) {
			return unknown;
		}
		
		try {
	    	String ret = context.getPackageManager()
			   .getPackageInfo(context.getPackageName(), 0)
			   .versionName;
	    	if (ret.contains(" + "))
	    		ret = ret.substring(0,ret.indexOf(" + "))+"b";
	    	return ret;
		} catch(NameNotFoundException ex) {}
		
		return unknown;		
	}



}

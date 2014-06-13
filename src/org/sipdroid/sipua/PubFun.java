package org.sipdroid.sipua;
import org.sipdroid.sipua.ui.Receiver;
import org.sipdroid.sipua.ui.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class PubFun  extends Activity implements OnDismissListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.v("��ת", "============");
		String login_num = getIntent().getStringExtra("username");
		String login_password= getIntent().getStringExtra("password");
		
	    Receiver.engine(this).registerMore();
		on(this,true);
		
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(Receiver.mContext); 
		Editor edit = settings.edit();
		
		edit.putString(Settings.PREF_USERNAME, login_num);	 	
		edit.putString(Settings.PREF_FROMUSER, login_num);	 
		edit.putString(Settings.PREF_PASSWORD, login_password);
		
		edit.putString(Settings.PREF_PORT, "5060");
		edit.putString(Settings.PREF_SERVER, "ring10086.com");
		edit.putString(Settings.PREF_PROTOCOL, "udp");
		edit.putString(Settings.PREF_PREF, "ASK");
		edit.commit();
		
	    Receiver.engine(this).halt();
		Receiver.engine(this).StartEngine();
		Receiver.engine(this).updateDNS();

		//Intent intent=new Intent(PubFun.this,MainTabUI3.class);
		//startActivity(intent);
		//finish();
	}
	public static void on(Context context,boolean on) {
		Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		edit.putBoolean(Settings.PREF_ON, on);
		edit.commit();
        if (on) Receiver.engine(context).isRegistered();
	}
	@Override
	public void onDismiss(DialogInterface arg0) {
		// TODO Auto-generated method stub
		
	}

}

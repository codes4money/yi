package org.sipdroid.sipua.ui;
import com.studio.b56.im.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;

public class SIPUri extends Activity {

	void call(String target) {
		if (!Receiver.engine(this).call(target,true)) {
			new AlertDialog.Builder(this)
			.setMessage(R.string.notfast)
			.setTitle(R.string.app_name)
			.setIcon(R.drawable.icon22)
			.setCancelable(true)
			.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			})
			.show();
		} else
			finish();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
    	if (Receiver.mContext == null) Receiver.mContext = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//Sipdroid.on(this,true);
		
		Uri uri = getIntent().getData();
		String target;
		if (uri.getScheme().equals("sip") || uri.getScheme().equals("sipdroid"))
			target = uri.getSchemeSpecificPart();
		else {
				target = uri.getLastPathSegment();
		}
		
		if(Receiver.isFast(0))
		{
        Log.v("SIPUri", "sip uri: " + target);
		String isAsk="SIP";
		try
		{
			isAsk=PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.PREF_PREF, Settings.DEFAULT_PREF);
		}catch(Exception e){}
       
       
		if (!target.contains("@") && isAsk.equals(Settings.VAL_PREF_ASK)) {
			final String t = target;
			String items[] = {getString(R.string.pstn_name)};
			
			//for (int p = 0; p < SipdroidEngine.LINES; p++)
				//if (Receiver.isFast(p) || (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Settings.PREF_CALLBACK, Settings.DEFAULT_CALLBACK) &&
				//		PreferenceManager.getDefaultSharedPreferences(this).getString(Settings.PREF_POSURL, Settings.DEFAULT_POSURL).length() > 0)) {
					
			items = new String[3];
			items[0] = getString(R.string.app_name);
			items[1] = getString(R.string.pstn_name);
			items[2] = "讯聊回拨";
			
				//	break;
				//}
					
			final String fitems[] = items;
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon22)
			.setTitle(target)
            .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
					public void onClick(DialogInterface dialog, int whichButton) {
                    	if (fitems[whichButton].equals(getString(R.string.app_name)))
                    	{
                    		call(t);
                    	}else if(fitems[whichButton].equals("讯聊回拨"))
                    	{
                    		Intent intent = new Intent(SIPUri.this,callback_postui.class);
        					intent.putExtra("num", t);
        					startActivity(intent);
        					finish();
                    	}
                    	else {
                			PSTN.callPSTN("sip:"+t);
                			finish();
                    	}
                    }
                })
			.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			})
			.show();
		} else
			call(target); 
	   }
	   else
		{
		   PSTN.callPSTN("sip:"+target);
		   finish();
		}
	}
	
	    @Override
	    public void onPause() {
	        super.onPause();
	        finish();
	    }
	 
}

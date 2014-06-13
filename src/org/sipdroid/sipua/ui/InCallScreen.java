package org.sipdroid.sipua.ui;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.jivesoftware.smack.XMPPException;
import org.sipdroid.media.RtpStreamReceiver;
import org.sipdroid.media.RtpStreamSender;
import org.sipdroid.sipua.UserAgent;
import org.sipdroid.sipua.phone.Call;
import org.sipdroid.sipua.phone.CallCard;
import org.sipdroid.sipua.phone.CallerInfo;
import org.sipdroid.sipua.phone.CallerInfoAsyncQuery;
import org.sipdroid.sipua.phone.Connection;
import org.sipdroid.sipua.phone.ContactsAsyncHelper;
import org.sipdroid.sipua.phone.Phone;
import org.sipdroid.sipua.phone.PhoneUtils;
import org.sipdroid.sipua.phone.SlidingCardManager;

import com.studio.b56.im.R;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Contacts.People;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class InCallScreen extends CallScreen implements View.OnClickListener, SensorEventListener,CallerInfoAsyncQuery.OnQueryCompleteListener,
ContactsAsyncHelper.OnImageLoadCompleteListener {

	final int MSG_ANSWER = 1;
	final int MSG_ANSWER_SPEAKER = 2;
	final int MSG_BACK = 3;
	final int MSG_TICK = 4;
	final int MSG_POPUP = 5;
	
	private WeakReference<Context> mApp;
	private WeakReference<Intent> mIntent;
	private static boolean incoming = false;
	private static boolean isanswer = false;
	private static boolean isFirst = false;
	
	private static int num = 0;
	private static View inview;
	private static ImageButton decline;
	private static ImageButton answer;
	private static ImageButton speaker;
	private static LinearLayout lin1;
	private static LinearLayout lin2;
	private static RelativeLayout tools;
	private static Button endcall;
	//private static Button outendcall;
	private static itemShort onClick;
	private static itemTouch onTouch;
	private static WindowManager.LayoutParams inparams;
	//private static WindowManager.LayoutParams outparams;
	private static TextView name;
	private static TextView status;
	private static TextView slide;
	private static TextView callphonetips;
	private static Chronometer time;
	private static String number;
	private static long timeNum=0;
	final int SCREEN_OFF_TIMEOUT = 12000;
	private static WindowManager wm;
	
	CallCard mCallCard;
	Phone ccPhone;
	int oldtimeout;
	SensorManager sensorManager;
    Sensor proximitySensor;
    boolean first;
	
	void screenOff(boolean off) {
        ContentResolver cr = getContentResolver();
        
        if (proximitySensor != null)
        	return;
        if (off) {
        	if (oldtimeout == 0) {
        		oldtimeout = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, 60000);
	        	Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, SCREEN_OFF_TIMEOUT);
        	}
        } else {
        	if (oldtimeout == 0 && Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, 60000) == SCREEN_OFF_TIMEOUT)
        		oldtimeout = 60000;
        	if (oldtimeout != 0) {
	        	Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, oldtimeout);
        		oldtimeout = 0;
        	}
        }
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mHandler.removeMessages(MSG_BACK);
		if (Receiver.call_state == UserAgent.UA_STATE_IDLE)
			finish();
		sensorManager.unregisterListener(this);
		started = false;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (Receiver.call_state == UserAgent.UA_STATE_IDLE)
     		mHandler.sendEmptyMessageDelayed(MSG_BACK, Receiver.call_end_reason == -1?
    				2000:5000);
	    first = true;
	    pactive = false;
	    pactivetime = SystemClock.elapsedRealtime();
	    sensorManager.registerListener(this,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
	    started = true;
	}

	@Override
	public void onPause() {
		super.onPause();
    	if (!Sipdroid.release) Log.i("SipUA:","on pause");
    	switch (Receiver.call_state) {
    	case UserAgent.UA_STATE_INCOMING_CALL:
    		if (!RtpStreamReceiver.isBluetoothAvailable()) Receiver.moveTop();
    		break;
    	case UserAgent.UA_STATE_IDLE:
    		if (Receiver.ccCall != null)
    		//	mCallCard.displayMainCallStatus(ccPhone,Receiver.ccCall);
     		mHandler.sendEmptyMessageDelayed(MSG_BACK, Receiver.call_end_reason == -1?
    				2000:5000);
    		break;
    	}
		if (t != null) {
			running = false;
			t.interrupt();
		}
		screenOff(false);
	//	if (mCallCard.mElapsedTime != null) mCallCard.mElapsedTime.stop();
		
		
	}
	
	void moveBack() {
		if (Receiver.ccConn != null && !Receiver.ccConn.isIncoming()) {
			// after an outgoing call don't fall back to the contact
			// or call log because it is too easy to dial accidentally from there
	        startActivity(Receiver.createHomeIntent());
		}
		onStop();
	}
	
	Context mContext = this;

	@Override
	public void onResume() {
		super.onResume();
		incoming = false;
    	if (!Sipdroid.release) Log.i("SipUA:","on resume");
		switch (Receiver.call_state) {
		case UserAgent.UA_STATE_INCOMING_CALL:
			incoming = true;
			income();
			if (Receiver.pstn_state == null || Receiver.pstn_state.equals("IDLE"))
				if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_AUTO_ON, org.sipdroid.sipua.ui.Settings.DEFAULT_AUTO_ON) &&
						!mKeyguardManager.inKeyguardRestrictedInputMode())
				{
					mHandler.sendEmptyMessageDelayed(MSG_ANSWER, 1000);
				}
				else if ((PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_AUTO_ONDEMAND, org.sipdroid.sipua.ui.Settings.DEFAULT_AUTO_ONDEMAND) &&
						PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_AUTO_DEMAND, org.sipdroid.sipua.ui.Settings.DEFAULT_AUTO_DEMAND)) ||
						(PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_AUTO_HEADSET, org.sipdroid.sipua.ui.Settings.DEFAULT_AUTO_HEADSET) &&
								Receiver.headset > 0))
				{	
					mHandler.sendEmptyMessageDelayed(MSG_ANSWER_SPEAKER, 10000);
				}
			
			break;
		case UserAgent.UA_STATE_INCALL:
			incoming = true;
			//timeNum=0;
			income2();
			if (Receiver.docked <= 0)
				screenOff(true);
			break;
		case UserAgent.UA_STATE_IDLE:
			if (!mHandler.hasMessages(MSG_BACK))
				moveBack();
			break;
		case UserAgent.UA_STATE_OUTGOING_CALL:
			callout();
			break;
		}

		if (Receiver.ccCall != null)updatePhoneNum(ccPhone,Receiver.ccCall);

		mHandler.sendEmptyMessage(MSG_TICK);
		mHandler.sendEmptyMessage(MSG_POPUP);
		
	    if (t == null && Receiver.call_state != UserAgent.UA_STATE_IDLE) {
			//mDigits.setText("");
			running = true;
	        (t = new Thread() {
				@Override
				public void run() {
					int len = 0;
					long time;
					ToneGenerator tg = null;
	
					if (Settings.System.getInt(getContentResolver(),
							Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1)
						tg = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, (int)(ToneGenerator.MAX_VOLUME*2*org.sipdroid.sipua.ui.Settings.getEarGain()));

					for (;;) {
						if (!running) {
							t = null;
							break;
						}
						/*
						if (len != mDigits.getText().length()) {
							time = SystemClock.elapsedRealtime();
							if (tg != null) tg.startTone(mToneMap.get(mDigits.getText().charAt(len)));
							Receiver.engine(Receiver.mContext).info(mDigits.getText().charAt(len++),250);
							time = 250-(SystemClock.elapsedRealtime()-time);
							try {
								if (time > 0) sleep(time);
							} catch (InterruptedException e) {
							}
							if (tg != null) tg.stopTone();
							try {
								if (running) sleep(250);
							} catch (InterruptedException e) {
							}
							continue;
						}*/
				
						mHandler.sendEmptyMessage(MSG_TICK);
						try {
							sleep(900);
						} catch (InterruptedException e) {
						}
					}
					if (tg != null) tg.release();
					timeNum=0;
				}
			}).start();
	    }
	}
	
    Handler mHandler = new Handler() {
    	@Override
		public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case MSG_ANSWER:
        		if (Receiver.call_state == UserAgent.UA_STATE_INCOMING_CALL)
        			Log.v("�绰", "�绰 MSG_ANSWER");
        			answer();
        		break;
    		case MSG_ANSWER_SPEAKER:
        		if (Receiver.call_state == UserAgent.UA_STATE_INCOMING_CALL) {
        			
        			Log.v("�绰", "�绰 MSG_ANSWER_SPEAKER");
        			
        			
        			answer();
    				Receiver.engine(mContext).speaker(AudioManager.MODE_NORMAL);
        		}
        		break;
    		case MSG_BACK:
    			moveBack();
    			break;
    		case MSG_TICK:
    			timeNum=System.currentTimeMillis()-Receiver.ccConn.date;
    			timeNum=timeNum/1000;
    			
    			/*
    			mCodec.setText(RtpStreamReceiver.getCodec());
    			if (RtpStreamReceiver.good != 0) {
    				if (RtpStreamReceiver.timeout != 0)
    				mStats.setText("�����.");
    				else if (RtpStreamSender.m > 1)
	    				mStats.setText(Math.round(RtpStreamReceiver.loss/RtpStreamReceiver.good*100)+"%lost, "+
	    						Math.round(RtpStreamReceiver.lost/RtpStreamReceiver.good*100)+"%lost, "+
	    						Math.round(RtpStreamReceiver.late/RtpStreamReceiver.good*100)+"%late (>"+
	    						(RtpStreamReceiver.jitter-250*RtpStreamReceiver.mu)/8/RtpStreamReceiver.mu+"ms)");
    				else
	    				mStats.setText(Math.round(RtpStreamReceiver.lost/RtpStreamReceiver.good*100)+"%lost, "+
	    						Math.round(RtpStreamReceiver.late/RtpStreamReceiver.good*100)+"%late (>"+
	    						(RtpStreamReceiver.jitter-250*RtpStreamReceiver.mu)/8/RtpStreamReceiver.mu+"ms)");
    				mStats.setVisibility(View.VISIBLE);
    			} else
    				mStats.setVisibility(View.GONE);*/
    			
    			long timeM=0;
    			long timeS=0;
    			if(timeNum>=60)
    			{
    				timeM=timeNum/60;
    			}
    			
    			timeS=timeNum-(timeM*60);
    			
    			String timeM2="00";
    			if(timeM<=9)
    			{
    				timeM2="0"+String.valueOf(timeM);
    			}
    			else
    			{
    				timeM2=String.valueOf(timeM);
    			}
    			
    			String timeS2="00";
    			if(timeS<=9)
    			{
    				timeS2="0"+String.valueOf(timeS);
    			}
    			else
    			{
    				timeS2=String.valueOf(timeS);
    			}
    			time.setText(timeM2+":"+timeS2);
    			
    			break;
    		case MSG_POPUP:
    	     //   if (mSlidingCardManager != null) mSlidingCardManager.showPopup();
    			break;
    		}
    	}
    };

	ViewGroup mInCallPanel,mMainFrame;
	SlidingDrawer mDialerDrawer;
	public static SlidingCardManager mSlidingCardManager;
	TextView mStats;
	TextView mCodec;
	
    public void initInCallScreen() {
        mInCallPanel = (ViewGroup) findViewById(R.id.inCallPanel);
        mMainFrame = (ViewGroup) findViewById(R.id.mainFrame);
        View callCardLayout = getLayoutInflater().inflate(
                    R.layout.call_card_popup,
                    mInCallPanel);
        mCallCard = (CallCard) callCardLayout.findViewById(R.id.callCard);
        mCallCard.reset();

        mSlidingCardManager = new SlidingCardManager();
        mSlidingCardManager.init(ccPhone, this, mMainFrame);
        SlidingCardManager.WindowAttachNotifierView wanv =
            new SlidingCardManager.WindowAttachNotifierView(this);
	    wanv.setSlidingCardManager(mSlidingCardManager);
	    wanv.setVisibility(View.GONE);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(0, 0);
	    mMainFrame.addView(wanv, lp);

	    mStats = (TextView) findViewById(R.id.stats);
	    mCodec = (TextView) findViewById(R.id.codec);
        mDialerDrawer = (SlidingDrawer) findViewById(R.id.dialer_container);
        mCallCard.displayOnHoldCallStatus(ccPhone,null);
        mCallCard.displayOngoingCallStatus(ccPhone,null);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        	mCallCard.updateForLandscapeMode();
        
        // Have the WindowManager filter out touch events that are "too fat".
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES);

	    mDigits = (EditText) findViewById(R.id.digits);
        mDisplayMap.put(R.id.one, '1');
        mDisplayMap.put(R.id.two, '2');
        mDisplayMap.put(R.id.three, '3');
        mDisplayMap.put(R.id.four, '4');
        mDisplayMap.put(R.id.five, '5');
        mDisplayMap.put(R.id.six, '6');
        mDisplayMap.put(R.id.seven, '7');
        mDisplayMap.put(R.id.eight, '8');
        mDisplayMap.put(R.id.nine, '9');
        mDisplayMap.put(R.id.zero, '0');
        mDisplayMap.put(R.id.pound, '#');
        mDisplayMap.put(R.id.star, '*');
        
        mToneMap.put('1', ToneGenerator.TONE_DTMF_1);
        mToneMap.put('2', ToneGenerator.TONE_DTMF_2);
        mToneMap.put('3', ToneGenerator.TONE_DTMF_3);
        mToneMap.put('4', ToneGenerator.TONE_DTMF_4);
        mToneMap.put('5', ToneGenerator.TONE_DTMF_5);
        mToneMap.put('6', ToneGenerator.TONE_DTMF_6);
        mToneMap.put('7', ToneGenerator.TONE_DTMF_7);
        mToneMap.put('8', ToneGenerator.TONE_DTMF_8);
        mToneMap.put('9', ToneGenerator.TONE_DTMF_9);
        mToneMap.put('0', ToneGenerator.TONE_DTMF_0);
        mToneMap.put('#', ToneGenerator.TONE_DTMF_P);
        mToneMap.put('*', ToneGenerator.TONE_DTMF_S);

        View button;
        for (int viewId : mDisplayMap.keySet()) {
            button = findViewById(viewId);
            button.setOnClickListener(this);
        }
    }
    
	Thread t;
	EditText mDigits;
	boolean running;
	public static boolean started;
    private static final HashMap<Integer, Character> mDisplayMap =
        new HashMap<Integer, Character>();
    private static final HashMap<Character, Integer> mToneMap =
        new HashMap<Character, Integer>();
    
	@Override
	public void onClick(View v) {
        int viewId = v.getId();

        // if the button is recognized
        if (mDisplayMap.containsKey(viewId)) {
                    appendDigit(mDisplayMap.get(viewId));
        }
    }

    void appendDigit(final char c) {
       // mDigits.getText().append(c);
    }

    @Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call_phone_ui);
		start();//
		//initInCallScreen();
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if(!android.os.Build.BRAND.equalsIgnoreCase("archos"))
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }
    
    
    ////////////////
    ///*******************************************************************************************
	
	  private void updateDisplayForPerson(CallerInfo info,
            boolean isPrivateNumber,
            boolean isTemporary,
            Call call) {
		    String n;
	        String displayNumber = null;
	        String label = null;
	        Uri personUri = null;
	        
	        if (info != null) {

	            if (TextUtils.isEmpty(info.name)) {
	                if (TextUtils.isEmpty(info.phoneNumber)) {
	                	{
	                        n = this.getString(R.string.unknown);
	                    }
	                } else {
	                    n = info.phoneNumber;
	                }
	                displayNumber = info.phoneNumber;
	            } else {
	                n = info.name;
	                displayNumber = info.phoneNumber;
	                label = info.phoneLabel;
	            }
	            personUri = ContentUris.withAppendedId(People.CONTENT_URI, info.person_id);
	        } else {
	        	{
	                n = this.getString(R.string.unknown);
	            }
	        }
	        
	        if(n!="" && n!="null" && n!=null && !n.contains("<"))
	        {
	        	 name.setText(n);
	        }
	        else
	        {
	        	name.setText(displayNumber);
	        }
	        
	        if(displayNumber != null && displayNumber!="" && (displayNumber.substring(0,2).equals("86") || displayNumber.substring(0,2).equals("01") || displayNumber.substring(0,2).equals("80")))
	        {
	        	callphonetips.setVisibility(View.VISIBLE);//��ʾ�շ���ʾ
	        }
	        else
	        {
	        	callphonetips.setVisibility(View.GONE);//��ʾ�շ���ʾ
	        }
	}
	  
    private ContactsAsyncHelper.ImageTracker mPhotoTracker;
    
    void updatePhoneNum(Phone phone, Call call)
    {
    	  Connection conn = call.getEarliestConnection();
    	  boolean isPrivateNumber = false; 
    	  boolean runQuery = true;
    	  
    	                 Object o = conn.getUserData();
    	                 try
    	                 {
    	                 if (o instanceof PhoneUtils.CallerInfoToken) {
    	                     runQuery = mPhotoTracker.isDifferentImageRequest(
    	                             ((PhoneUtils.CallerInfoToken) o).currentInfo);
    	                 } else {
    	                     runQuery = mPhotoTracker.isDifferentImageRequest(conn);
    	                 }
    	                 } catch (Exception e) {
    	         		}

    	              if (runQuery) {
    	                     PhoneUtils.CallerInfoToken info = PhoneUtils.startGetCallerInfo(this, conn, this, call);
    	                     updateDisplayForPerson(info.currentInfo, isPrivateNumber, !info.isFinal, call);
    	                     
    	                 } else {

    	                     if (o instanceof CallerInfo) {
    	                         CallerInfo ci = (CallerInfo) o;
    	                        updateDisplayForPerson(ci, false, false, call);
    	                     } else if (o instanceof PhoneUtils.CallerInfoToken){
    	                         CallerInfo ci = ((PhoneUtils.CallerInfoToken) o).currentInfo;
    	                         updateDisplayForPerson(ci, false, true, call);
    	                     } else {
    	                     }
    	 }
    }
    
    
    void start(){ 
    	
    	mApp = new WeakReference<Context>(this);
		mIntent = new WeakReference<Intent>(intent);
		
        	answer = (ImageButton)findViewById(R.id.answer);
	        endcall = (Button)findViewById(R.id.endcall);
	        decline = (ImageButton)findViewById(R.id.decline);
	        speaker = (ImageButton)findViewById(R.id.speaker);
	        name = (TextView)findViewById(R.id.name);
	        status = (TextView)findViewById(R.id.statu);
	        slide = (TextView)findViewById(R.id.slide);
	        time = (Chronometer)findViewById(R.id.time);
	        lin1 = (LinearLayout)findViewById(R.id.lin1);
	        lin2 = (LinearLayout)findViewById(R.id.lin2);
	        tools = (RelativeLayout)findViewById(R.id.tools);
	        callphonetips= (TextView)findViewById(R.id.callphonetip2);
	        
	        onClick = new itemShort();
        	onTouch = new itemTouch();
        	
        	speaker.setOnClickListener(onClick);
        	endcall.setOnClickListener(onClick);
        	answer.setOnTouchListener(onTouch);
        	decline.setOnTouchListener(onTouch);
 }
    

    void income()
    {
    	 status.setText("来电");
        lin1.setVisibility(View.VISIBLE);
	    lin2.setVisibility(View.GONE);
	    status.setVisibility(View.VISIBLE);
	    time.setVisibility(View.GONE);
		tools.setVisibility(View.GONE);
    }
    void income2()
    {
        status.setVisibility(View.GONE);
        lin1.setVisibility(View.GONE);
        lin2.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
        tools.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
    }
    void callout()
    {
        status.setText("拨号中");
          status.setVisibility(View.VISIBLE);
          lin1.setVisibility(View.GONE);
          lin2.setVisibility(View.VISIBLE);
          tools.setVisibility(View.VISIBLE);
          time.setVisibility(View.GONE);
    }
    
    
    void switchspeaker(){
    	
        try
        {

     	 if(RtpStreamReceiver.speakermode != AudioManager.MODE_NORMAL)
     	 {
        	  speaker.setImageResource(R.drawable.ic_in_call_touch_speaker_on);
     	 }
     	 else
     	 {
        	  speaker.setImageResource(R.drawable.ic_in_call_touch_speaker_off);
     	 }
     	 
   	     Receiver.engine(this).speaker(RtpStreamReceiver.speakermode == AudioManager.MODE_NORMAL?AudioManager.MODE_IN_CALL:AudioManager.MODE_NORMAL);
     	 
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
     class itemShort implements OnClickListener{
 		 public void onClick(View v) {
 				// TODO 自动生成的方法存根
 			 switch(v.getId())
 			 {
 			 case R.id.endcall:toEnd();break;
 			 case R.id.speaker:switchspeaker();break;
 			 }
 		 }
 	 }
 	 class itemTouch implements OnTouchListener{
 	    	private int lastX;
 	    	private int screenWidth;
 	        private int screenHeight;
 	        private ViewGroup.LayoutParams aparam;
 	        private ViewGroup.LayoutParams dparam;
 	        private DisplayMetrics dm;
 	        itemTouch(){
 	        	dm = mApp.get().getResources().getDisplayMetrics();
 		        screenWidth=dm.widthPixels;
 		        screenHeight=dm.heightPixels;
 		        aparam=answer.getLayoutParams(); 
 		        dparam=decline.getLayoutParams();
 	        }
 			public boolean onTouch(View v, MotionEvent event) {
 				// TODO 自动生成的方法存根
 				
 				if(v.getId() == R.id.answer && decline.getVisibility() == View.VISIBLE)
 				{
 					decline.setVisibility(View.INVISIBLE);
 					slide.setText("右滑接听");
 				}
 				if(v.getId() == R.id.decline && answer.getVisibility() == View.VISIBLE)
 				{
 					answer.setVisibility(View.INVISIBLE);
 					slide.setText("左滑拒接");
 				}
 				int ea=event.getAction();
 		          switch(ea){
 		          case MotionEvent.ACTION_DOWN:           
 		           
 		           lastX=(int)event.getRawX();
 		           //lastY=(int)event.getRawY();           
 		           break;
 		          case MotionEvent.ACTION_MOVE:
 		           int dx=(int)event.getRawX()-lastX;
 		           int dy=0;//(int)event.getRawY()-lastY;           
 		           
 		           int l=v.getLeft()+dx; 
 		           int b=v.getBottom()+dy;
 		           int r=v.getRight()+dx;
 		           int t=v.getTop()+dy;
 		           if(l<0){
 		            l=0;    
 		            r=l+v.getWidth();
 		           }
 		           
 		           if(t<0){
 		            t=0;
 		            b=t+v.getHeight();
 		           }
 		           
 		           if(r>screenWidth){
 		            r=screenWidth;
 		            l=r-v.getWidth();
 		           }
 		          
 		           if(b>screenHeight){
 		            b=screenHeight;
 		            t=b-v.getHeight();
 		           }
 		           v.layout(l, t, r, b);
 		           
 		           lastX=(int)event.getRawX();
 		           //lastY=(int)event.getRawY();
 		           
 		           v.postInvalidate();  
 		           
 		           break;
 		          case MotionEvent.ACTION_UP:
 		        	  if(v.getId() == R.id.answer)
 		        		  v.setLayoutParams(aparam);
 		        	  else
 		        		  v.setLayoutParams(dparam);
 		        	  if(v.getId() == R.id.answer)
 						{
 							decline.setVisibility(View.VISIBLE);
 							if(lastX > screenWidth-backpx(60))
 							{
 								toAnswer();
 								lin1.setVisibility(View.GONE);
 						         lin2.setVisibility(View.VISIBLE);
 							}
 						}
 						if(v.getId() == R.id.decline)
 						{
 							answer.setVisibility(View.VISIBLE);
 							if(lastX < backpx(60))
 							{
 								isanswer = true;
 								toEnd();
 							    lin1.setVisibility(View.GONE);
 							}
 						}
 						slide.setText("移动滑块");
 		           break;          
 		          }
 		          return false;
 			}
 			 int backpx(float dpValue) {  
 			        final float scale = dm.density;  
 			        return (int) (dpValue * scale + 0.5f);  
 			 }
 	    }
 	void toAnswer(){
 		try {    
 			answer();
 	        } catch (Exception e) {    
 	            e.printStackTrace();    
 	        }    
 	}
 	void toEnd(){
 		running=false;
 		timeNum=0;
 		try {    
 			endcall.setVisibility(View.GONE);
 			reject();
      } catch (Exception e) {       
      } 
 	 }	
 	
 	
 	
     ////////////////
     ///*******************************************************************************************

 	
 	public void reject() {
 		if (Receiver.ccCall != null) {
 			Receiver.stopRingtone();
 			Receiver.ccCall.setState(Call.State.DISCONNECTED);
 		//	mCallCard.displayMainCallStatus(ccPhone,Receiver.ccCall);
 		//	mDialerDrawer.close();
 		//	mDialerDrawer.setVisibility(View.GONE);
 	       // if (mSlidingCardManager != null)
 	        //	mSlidingCardManager.showPopup();
 		}
         (new Thread() {
 			@Override
 			public void run() {
         		Receiver.engine(mContext).rejectcall();
 			}
 		}).start();
         
     }
 	
 	public void answer() {
         (new Thread() {
 			@Override
 			public void run() {
 				Receiver.engine(mContext).answercall();
 			}
 		}).start();   
 		if (Receiver.ccCall != null) {
 			Receiver.ccCall.setState(Call.State.ACTIVE);
 			Receiver.ccCall.base = SystemClock.elapsedRealtime();
 			//mCallCard.displayMainCallStatus(ccPhone,Receiver.ccCall);
 			//mDialerDrawer.setVisibility(View.VISIBLE);
 	       // if (mSlidingCardManager != null)
 	       // 	mSlidingCardManager.showPopup();
 		}
 		Log.v("电话", "电话  answer()===========");
 	
 	}
 	
 	@Override
 	public boolean onKeyDown(int keyCode, KeyEvent event) {
         switch (keyCode) {
         case KeyEvent.KEYCODE_MENU:
         	if (Receiver.call_state == UserAgent.UA_STATE_INCOMING_CALL && mSlidingCardManager == null) {
         		answer();
         		
         		Log.v("电话", "电话  KeyEvent.KEYCODE_MENU=====");
 				return true;
         	}
         	break;
         
         case KeyEvent.KEYCODE_CALL:
         	switch (Receiver.call_state) {
         	case UserAgent.UA_STATE_INCOMING_CALL:
         		
         		Log.v("电话", "电话  UA_STATE_INCOMING_CALL");
         		answer();
         		break;
         	case UserAgent.UA_STATE_INCALL:
         	case UserAgent.UA_STATE_HOLD:
        			Receiver.engine(this).togglehold();
        			
        			Log.v("电话", "电话 UserAgent.UA_STATE_INCALL");
        			break;
         	}
             // consume KEYCODE_CALL so PhoneWindow doesn't do anything with it
             return true;

         case KeyEvent.KEYCODE_BACK:
         	//if (mDialerDrawer.isOpened())
         	//	mDialerDrawer.animateClose();
         	if (Receiver.call_state == UserAgent.UA_STATE_INCOMING_CALL)
         		reject();      
             return true;

         case KeyEvent.KEYCODE_CAMERA:
             // Disable the CAMERA button while in-call since it's too
             // easy to press accidentally.
         	return true;
         	
         case KeyEvent.KEYCODE_VOLUME_DOWN:
         case KeyEvent.KEYCODE_VOLUME_UP:
         	if (Receiver.call_state == UserAgent.UA_STATE_INCOMING_CALL) {
         		Receiver.stopRingtone();
         		return true;
         	}
         	RtpStreamReceiver.adjust(keyCode,true);
         	return true;
         }
         
         if (Receiver.call_state == UserAgent.UA_STATE_INCALL) {
         	
 	        char number = event.getNumber();
 	        if (Character.isDigit(number) || number == '*' || number == '#') {
 	        	appendDigit(number);
 	        	return true;
 	        }
 	        
         }
         
         return super.onKeyDown(keyCode, event);
 	}

 	@Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 		boolean result = super.onCreateOptionsMenu(menu);

 		MenuItem m = menu.add(0, DTMF_MENU_ITEM, 0, R.string.menu_dtmf);
 		m.setIcon(R.drawable.ic_menu_dial_pad);
 		return result;
 	}

 	@Override
 	public boolean onPrepareOptionsMenu(Menu menu) {
 		boolean result = super.onPrepareOptionsMenu(menu);

 		menu.findItem(DTMF_MENU_ITEM).setVisible(Receiver.call_state == UserAgent.UA_STATE_INCALL);
 		return result;
 	}
 		
 	@Override
 	public boolean onOptionsItemSelected(MenuItem item) {
 		switch (item.getItemId()) {
 		case DTMF_MENU_ITEM:
 			mDialerDrawer.animateOpen();
 			return true;
 		default:
 			return super.onOptionsItemSelected(item);
 		}
 	}
 	
 	@Override
 	public boolean onKeyUp(int keyCode, KeyEvent event) {
 		switch (keyCode) {
         case KeyEvent.KEYCODE_VOLUME_DOWN:
         case KeyEvent.KEYCODE_VOLUME_UP:
         	RtpStreamReceiver.adjust(keyCode,false);
         	
     		Log.v("电话", "电话 KeyEvent.KEYCODE_VOLUME_DOWN");
     		
         	return true;
         case KeyEvent.KEYCODE_ENDCALL:
         	if (Receiver.pstn_state == null ||
 				(Receiver.pstn_state.equals("IDLE") && (SystemClock.elapsedRealtime()-Receiver.pstn_time) > 3000)) {
         			reject();      
         			return true;		
         	}
         	break;
 		}
 		Receiver.pstn_time = 0;
 		return false;
 	}

 	@Override
 	public void onAccuracyChanged(Sensor sensor, int accuracy) {
 	}

 	void setScreenBacklight(float a) {
         WindowManager.LayoutParams lp = getWindow().getAttributes(); 
         lp.screenBrightness = a; 
         getWindow().setAttributes(lp);		
 	}

 	static final float PROXIMITY_THRESHOLD = 5.0f;
 	public static boolean pactive;
 	public static long pactivetime;
 	
 	@Override
 	public void onSensorChanged(SensorEvent event) {
 		boolean keepon = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(org.sipdroid.sipua.ui.Settings.PREF_KEEPON, org.sipdroid.sipua.ui.Settings.DEFAULT_KEEPON);
 		if (first) {
 			first = false;
 			return;
 		}
 		float distance = event.values[0];
         boolean active = (distance >= 0.0 && distance < PROXIMITY_THRESHOLD && distance < event.sensor.getMaximumRange());
 		if (!(keepon && Receiver.on_wlan) ||
 				(InCallScreen.mSlidingCardManager != null && InCallScreen.mSlidingCardManager.isSlideInProgress()) ||
 				Receiver.call_state == UserAgent.UA_STATE_INCOMING_CALL || Receiver.call_state == UserAgent.UA_STATE_HOLD)
 			active = false;
         pactive = active;
         pactivetime = SystemClock.elapsedRealtime();
         setScreenBacklight((float) (active?0.1:-1));
 	}
 	 /**
      * Implemented for CallerInfoAsyncQuery.OnQueryCompleteListener interface.
      * refreshes the CallCard data when it called.
      */
     @Override
 	public void onQueryComplete(int token, Object cookie, CallerInfo ci) {
         if (cookie instanceof Call) {
             Call call = (Call) cookie;
             updateDisplayForPerson(ci, false, false, call);
            // updatePhotoForCallState(call);
         }
     }

     /**
      * Implemented for ContactsAsyncHelper.OnImageLoadCompleteListener interface.
      * make sure that the call state is reflected after the image is loaded.
      */
     @Override
 	public void onImageLoadComplete(int token, Object cookie, ImageView iView,
             boolean imagePresent){
         if (cookie != null) {
            // updatePhotoForCallState((Call) cookie);
         }
     }
 }

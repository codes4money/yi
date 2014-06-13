package com.zijunlin.Zxing.Demo;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.studio.b56.im.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import com.zijunlin.Zxing.Demo.camera.CameraManager;
import com.zijunlin.Zxing.Demo.decoding.CaptureActivityHandler;
import com.zijunlin.Zxing.Demo.decoding.InactivityTimer;
import com.zijunlin.Zxing.Demo.view.ViewfinderView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.TextView;
public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private TextView txtResult;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private String codes="";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_main);
		//初始化 CameraManager
		CameraManager.init(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		txtResult = (TextView) findViewById(R.id.txtResult);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		re();
		
	} 
	public void re()
	{
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		viewfinderView.drawResultBitmap(barcode);
		 playBeepSoundAndVibrate();
		 codes=obj.getText();
		String st1="二维码内容:"+codes;
		if(codes.toLowerCase().indexOf("http")>=0)
		{
			st1="是否打开URL:"+codes;
		}
		else if(codes.toLowerCase().indexOf("讯聊")>=0)
		{
			String fname=codes.split(":")[1].split(",")[0];
			st1="是否将讯聊用户 "+fname+" 加为好友?";
		}
		else if(codes.toLowerCase().indexOf("card")>=0)
		{
			Pattern p = Pattern.compile("N:(.*?)[;| ]");    
			Matcher m = p.matcher(codes);    
			if(m.find()){    
			    MatchResult mr=m.toMatchResult();    
			    st1="是否添加  "+mr.group(1)+" 的名片到手机?";
			}
			else
			{
				 p = Pattern.compile("[\u4E00-\u9FA5][;| ]");
				 m = p.matcher(codes); 
				 if(m.find())
				 {    
					    MatchResult mr=m.toMatchResult();    
					    st1="是否添加  "+mr.group(0)+" 的名片到手机?";
				  }
				 else
				 {
					 p = Pattern.compile("(((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8})");     
					 m = p.matcher(codes);    
					 if(m.find()){    
						    MatchResult mr=m.toMatchResult();    
						    st1="是将手机联系人  "+mr.group(0)+" 保存到手机?";
					 }
					 else
					 {
						 p = Pattern.compile("((\\d{3}-|\\d{4}-)(\\d{8}|\\d{7}))");      
						 m = p.matcher(codes);    
						 if(m.find()){    
							    MatchResult mr=m.toMatchResult();    
							    st1="是将电话联系人  "+mr.group(0)+" 保存到手机?";
						 }
					 }
				 }
			}
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(st1);
		
		Dialog dialog = new AlertDialog.Builder(this)
				.setTitle("操作")
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("确认",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								if(codes.toLowerCase().indexOf("http")>=0)
								{
									Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(codes));
									it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");  
								    startActivity(it);
								    finish();
								}
							}
						})
						
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								
								finish();
								
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
		
	}

	
	
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}

package org.sipdroid.sipua.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sipdroid.sipua.contact.PhotoColumn;

import com.studio.b56.im.R;

public final class PhotoCacher
{
  static final String LOAD_KEY = "id";
  static final int MSG_LOAD = 1;
  static PhotoCacher mInstance;
  final Map<Long, Bitmap> mCacher = new HashMap();
  Drawable mDefaultContactIcon;
  PhotoHandler mHandler;
  Object mHandlerMux = new Object();
  Listener mListener;
  @SuppressWarnings("unchecked")
  final Set<Long> mLoaded = new HashSet();
  
  final PhotoColumn mPhoto;
  Thread mThread;

  PhotoCacher()
  {
    Context localContext = Receiver.mContext;
    this.mPhoto = PhotoColumn.create(localContext);
    this.mDefaultContactIcon = localContext.getResources().getDrawable(R.drawable.contact_item_default);
    start();
  }

  public static PhotoCacher getInstance()
  {
      PhotoCacher localPhotoCacher = mInstance;
      if (mInstance == null)
      {
        mInstance = new PhotoCacher();
        localPhotoCacher = mInstance;
      }
      return localPhotoCacher;
  }
  
  public Drawable getDefaultContactIcon()
  {
    return this.mDefaultContactIcon;
  }
  public Bitmap getPhotoById(Long paramLong)
  {
    Bitmap localBitmap = null;
      try
      {
        long l = paramLong.longValue();
        if (l <= 0L)
          return localBitmap;
        
        if (loadStarted(paramLong))
        {
          startLoad(paramLong);
        }
      }catch(Exception e){}
      localBitmap = (Bitmap)this.mCacher.get(paramLong);
      
      return localBitmap;
  }

  boolean loadStarted(Long paramLong)
  {
    return this.mLoaded.add(paramLong);
  }

  void notifyListener(Long paramLong, Bitmap paramBitmap)
  {
    try
    {
      if (this.mListener != null)
        this.mListener.onImageLoad(paramLong, paramBitmap);
    }catch(Exception e){}
  }

  public void setListener(Listener paramListener)
  {
    try
    {
      this.mListener = paramListener;
    }catch(Exception e){}
  }

  void setPhotoById(Long paramLong, Bitmap paramBitmap)
  {
    try
    {
      this.mCacher.put(paramLong, paramBitmap);
    }catch(Exception e){}
  }

  void start()
  {
    this.mThread = new Thread()
    {
      public void run()
      {
        Looper.prepare();
        synchronized (PhotoCacher.this.mHandlerMux)
        {
          PhotoCacher.this.mHandler = new PhotoCacher.PhotoHandler(Looper.myLooper());
          Looper.loop();
          return;
        }
      }
    };
    this.mThread.start();
    synchronized (this.mHandlerMux)
    {
        if (this.mHandler != null)
          return;
        try
        {
          Thread.sleep(1L);
        }
        catch (InterruptedException localInterruptedException)
        {
        }
    }
  }

  void startLoad(Long paramLong)
  {
    Message localMessage = new Message();
    localMessage.what = 1;
    Bundle localBundle = new Bundle();
    localBundle.putLong("id", paramLong.longValue());
    localMessage.setData(localBundle);
    this.mHandler.sendMessage(localMessage);
  }

  public static abstract interface Listener
  {
    public abstract void onImageLoad(Long paramLong, Bitmap paramBitmap);
  }

  class PhotoHandler extends Handler
  {
    public PhotoHandler(Looper arg2)
    {
      super();
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
      case 1:
    	  long l = paramMessage.getData().getLong("id");
          Bitmap localBitmap = mPhoto.query(l);
          PhotoCacher.this.setPhotoById(Long.valueOf(l), localBitmap);
          PhotoCacher.this.notifyListener(Long.valueOf(l), localBitmap);
          break;
      }
    }
  }
}

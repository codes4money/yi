package org.sipdroid.sipua.contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

public abstract class PhotoColumn
{
  protected Context ctx;

  PhotoColumn(Context paramContext)
  {
    this.ctx = paramContext;
  }

  public static PhotoColumn create(Context paramContext)
  {
	  
      if (Build.VERSION.SDK_INT >= 5)
      {
    	  return new PhotoColumnNew(paramContext);
      }
      else
      {
    	  return new PhotoColumnOld(paramContext);
      }
  }

  protected abstract Bitmap onQuery(long paramLong);

  public Bitmap query(long paramLong)
  {
    return onQuery(paramLong);
  }
}
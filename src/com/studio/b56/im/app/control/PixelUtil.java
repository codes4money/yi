package com.studio.b56.im.app.control;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;

public class PixelUtil
{
  public static int dip2px(Context paramContext, float paramFloat)
  {
    return (int)(0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().density);
  }

  public static Bitmap getRoundedCornerBitmap(Context paramContext, Bitmap paramBitmap, float paramFloat)
  {
    int i = dip2px(paramContext, paramFloat);
    Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas localCanvas = new Canvas(localBitmap);
    localCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
    Paint localPaint = new Paint();
    Rect localRect = new Rect(0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
    localCanvas.drawRoundRect(new RectF(localRect), i, i, localPaint);
    localPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    localCanvas.drawBitmap(paramBitmap, localRect, localRect, localPaint);
    return localBitmap;
  }

  public static Bitmap setAlpha(Bitmap paramBitmap, int paramInt)
  {
    int[] arrayOfInt = new int[paramBitmap.getWidth() * paramBitmap.getHeight()];
    paramBitmap.getPixels(arrayOfInt, 0, paramBitmap.getWidth(), 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight());
    int i = paramInt * 255 / 100;
    for (int j = 0; ; j++)
    {
      if (j >= arrayOfInt.length)
        return Bitmap.createBitmap(arrayOfInt, paramBitmap.getWidth(), paramBitmap.getHeight(), Bitmap.Config.ARGB_8888);
      arrayOfInt[j] = (i << 24 | 0xFFFFFF & arrayOfInt[j]);
    }
  }

  public static int sp2px(Context paramContext, float paramFloat)
  {
    return (int)(0.5F + paramFloat * paramContext.getResources().getDisplayMetrics().scaledDensity);
  }
}

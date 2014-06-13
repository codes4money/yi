package org.sipdroid.sipua.contact;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
public abstract class IdColumn
{
  protected Context ctx;

  IdColumn(Context paramContext)
  {
    this.ctx = paramContext;
  }

  public static IdColumn create(Context paramContext)
  {
	  int version = 3;
	  try
        {
          version = Integer.parseInt(Build.VERSION.SDK);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          version = 3;
        }
	  
    if (version >= 5)
    {
      return  new IdColumnNew(paramContext);
    }
    else
    {
     return  new IdColumnOld(paramContext);
    }
  }

  protected abstract long onQuery(Cursor paramCursor);

  public long query(Cursor paramCursor)
  {
    return onQuery(paramCursor);
  }
}
package org.sipdroid.sipua.contact;
import android.database.Cursor;
import android.os.Build;
public abstract class NameColumn
{
  public static NameColumn create()
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
      return   new NameColumnNew();
    }
    else
    {
     return   new NameColumnOld();
    }
  }

  protected abstract String onQuery(Cursor paramCursor);

  public String query(Cursor paramCursor)
  {
    return onQuery(paramCursor);
  }
}
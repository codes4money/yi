package org.sipdroid.sipua.contact;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public abstract class SortkeyColumn
{
  public static final String SORT_KEY = "sort_key";
  Map<String, String> cache = new HashMap();

  public static SortkeyColumn create(Cursor paramCursor)
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
    	 if (isSortkeyColumnExist(paramCursor))
         {
           return  new SortkeyColumnNew();
         }
    	 return new SortkeyColumnMid();
    }
    else
    {
     return  new SortkeyColumnOld();
    }
    
  }

  static boolean isSortkeyColumnExist(Cursor paramCursor)
  {
    if (paramCursor.getColumnIndex("sort_key") < 0)
    {
    	return false;
    }
    return true;
  }

  protected abstract String onQuery(Cursor paramCursor, String paramString);

  public String query(Cursor paramCursor, String paramString)
  {
    String str2="";
    if (paramString!=null && paramString!="")
    {
    	String str1 = paramString.substring(0, 1);
        str2 = (String)this.cache.get(str1);
        if (str2 == null)
        {
        str2 = onQuery(paramCursor, str1);
        this.cache.put(str1, str2);
        }
    }
    return str2;
  }
}
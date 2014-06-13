package org.sipdroid.sipua.contact;
import android.content.Context;
import android.os.Build;

import java.util.ArrayList;
public abstract class NumberColumn{
	 protected Context context;

	  NumberColumn(Context paramContext)
	  {
	    this.context = paramContext;
	  }

	  public static NumberColumn create(Context paramContext)
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
	      return new NumberQueryerNew(paramContext);
	    }
	    else
	    {
	     return new NumberColumnOld(paramContext);
	    }
	  }

	  protected abstract ArrayList<String> onQuery(long paramLong);

	  public ArrayList<String> query(long paramLong)
	  {
	    return onQuery(paramLong);
	  }
}

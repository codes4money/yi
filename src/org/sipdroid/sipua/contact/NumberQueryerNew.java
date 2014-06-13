package org.sipdroid.sipua.contact;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class NumberQueryerNew extends NumberColumn{
	 NumberQueryerNew(Context paramContext)
	  {
	    super(paramContext);
	  }

	  protected ArrayList<String> onQuery(long paramLong)
	  {
	    ArrayList localArrayList = new ArrayList();
	    Cursor localCursor = this.context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id = " + paramLong, null, null);
	    if (localCursor != null)
	    {
	    while (localCursor.moveToNext())
	    {
	    	try
	    	{
	    	 String str = localCursor.getString(localCursor.getColumnIndex("data1"));
	          if (str!=null && str!="")
	            localArrayList.add(str);
	    	}
	    	catch(Exception e){}
	    }
        localCursor.close();
	    }
	    return localArrayList;
	  }
}

package org.sipdroid.sipua.contact;

import java.util.ArrayList;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;

public class NumberColumnOld extends NumberColumn{
	  NumberColumnOld(Context paramContext)
	  {
	    super(paramContext);
	  }

	  protected ArrayList<String> onQuery(long paramLong)
	  {
	    ArrayList localArrayList = new ArrayList();
	    ContentResolver localContentResolver = this.context.getContentResolver();
	    Uri localUri = Contacts.Phones.CONTENT_URI;
	    String[] arrayOfString = new String[1];
	    arrayOfString[0] = "number";
	    Cursor localCursor = localContentResolver.query(localUri, arrayOfString, "person = " + paramLong, null, null);
	    
	    if (localCursor != null)
	    {
	    while (localCursor.moveToNext())
	    {
	          String str = localCursor.getString(localCursor.getColumnIndexOrThrow("number"));
	          if (str!=null && str!="")
	            localArrayList.add(str);
	    }
	    localCursor.close();
	    }
	   return localArrayList;
	  }
}

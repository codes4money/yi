package org.sipdroid.sipua.contact;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;

public class PhotoColumnOld extends PhotoColumn{
	PhotoColumnOld(Context paramContext)
	  {
	    super(paramContext);
	  }
	protected Bitmap onQuery(long l)
	{
		return android.provider.Contacts.People.loadContactPhoto(ctx, ContentUris.withAppendedId(android.provider.Contacts.People.CONTENT_URI, l), 0, null);
	}
	
}

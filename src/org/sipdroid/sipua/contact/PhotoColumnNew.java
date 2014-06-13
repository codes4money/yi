package org.sipdroid.sipua.contact;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public  class PhotoColumnNew extends PhotoColumn{
	  PhotoColumnNew(Context paramContext)
	  {
	    super(paramContext);
	  }

	@Override
	protected Bitmap onQuery(long l) {
		InputStream inputstream;
		Bitmap bitmap1 = null;
		android.net.Uri uri = ContentUris.withAppendedId(android.provider.ContactsContract.Contacts.CONTENT_URI, l);
		inputstream = android.provider.ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(), uri);
		if (inputstream != null)
		{
			Bitmap bitmap = BitmapFactory.decodeStream(inputstream);
			bitmap1 = bitmap;
			try
			{
				inputstream.close();
			}
			catch (IOException ioexception1)
			{
			}
		}
		return bitmap1;
	}
}

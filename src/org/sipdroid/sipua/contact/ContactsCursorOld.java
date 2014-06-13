package org.sipdroid.sipua.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.Contacts.People;
public class ContactsCursorOld extends ContactsCursor
{
  public ContactsCursorOld(Context paramContext)
  {
    super(paramContext);
  }

  protected Cursor onGetContacts()
  {
    Object localObject = null;
    try
    {
      String[] arrayOfString = new String[2];
      arrayOfString[0] = "_id";
      arrayOfString[1] = "name";
      Cursor localCursor = this.context.getContentResolver().query(Contacts.People.CONTENT_URI, arrayOfString, null, null, "name COLLATE LOCALIZED asc");
      localObject = localCursor;
      return localCursor ;
    }
    catch (Exception localException)
    {
    }
    return null;
  }
}
package org.sipdroid.sipua.contact;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class ContactsCursorNew extends ContactsCursor
{
  static final String[] projection_with_sortkey;
  static final String[] projection_without_sortkey;
  static final String sort_order_with_name = "display_name COLLATE LOCALIZED asc";
  static final String sort_order_with_sortkey = "sort_key COLLATE LOCALIZED asc";
  String[] projection = projection_with_sortkey;
  String sortOrder = "sort_key COLLATE LOCALIZED asc";

  static
  {
    String[] arrayOfString1 = new String[2];
    arrayOfString1[0] = "_id";
    arrayOfString1[1] = "display_name";
    projection_without_sortkey = arrayOfString1;
    String[] arrayOfString2 = new String[3];
    arrayOfString2[0] = "_id";
    arrayOfString2[1] = "display_name";
    arrayOfString2[2] = "sort_key";
    projection_with_sortkey = arrayOfString2;
  }

  ContactsCursorNew(Context paramContext)
  {
    super(paramContext);
  }

  protected Cursor onGetContacts()
  {
    Object localObject = null;
    try
    {
      Cursor localCursor2 = this.context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, this.projection, null, null, this.sortOrder);
      localObject = localCursor2;
      return localCursor2 ;
    }
    catch (RuntimeException localRuntimeException1)
    {
        useOldWay();
        try
        {
          Cursor localCursor1 = this.context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, this.projection, null, null, this.sortOrder);
          return localCursor1;
        }
        catch (RuntimeException localRuntimeException2)
        {
        }
    }
    return null;
  }

  void useOldWay()
  {
    this.projection = projection_without_sortkey;
    this.sortOrder = "display_name COLLATE LOCALIZED asc";
  }
}
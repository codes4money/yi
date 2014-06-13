package org.sipdroid.sipua.contact;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
public abstract class ContactsCursor
{
  protected Context context;

  public ContactsCursor(Context paramContext)
  {
    this.context = paramContext;
  }

  public static ContactsCursor create(Context paramContext)
  {
    if (Integer.parseInt(Build.VERSION.SDK)>5)
    {
    	return new ContactsCursorNew(paramContext);
    }
    else
    {
    	return new ContactsCursorOld(paramContext);
    }
  }

  public Cursor getContacts()
  {
    return onGetContacts();
  }

  protected abstract Cursor onGetContacts();
}
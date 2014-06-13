package org.sipdroid.sipua.contact;
import android.database.Cursor;

class NameColumnOld extends NameColumn
{
  protected String onQuery(Cursor paramCursor)
  {
    String str = paramCursor.getString(paramCursor.getColumnIndexOrThrow("name"));
    if (str!=null && str!="")
    {
    	return str;
    }
    return "";
  }
}
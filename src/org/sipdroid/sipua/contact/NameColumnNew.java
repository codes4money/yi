package org.sipdroid.sipua.contact;
import android.database.Cursor;

class NameColumnNew extends NameColumn
{
  protected String onQuery(Cursor paramCursor)
  {
    String str = paramCursor.getString(paramCursor.getColumnIndexOrThrow("display_name"));
    if (str!=null && str!="")
    {
      return str;
    }
    else
    {
      return "";
    }
    
  }
}
package org.sipdroid.sipua.contact;
import android.database.Cursor;

class SortkeyColumnNew extends SortkeyColumn
{
  protected String onQuery(Cursor paramCursor, String paramString)
  {
    String str1 = paramCursor.getString(paramCursor.getColumnIndexOrThrow("sort_key"));
    if (str1!=null && str1!="")
    {
    	return str1.substring(0, 1).toUpperCase();
    }
    return "#";
  }
}
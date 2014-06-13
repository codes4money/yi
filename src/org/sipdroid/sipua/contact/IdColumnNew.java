package org.sipdroid.sipua.contact;
import android.content.Context;
import android.database.Cursor;

class IdColumnNew extends IdColumn
{
  IdColumnNew(Context paramContext)
  {
    super(paramContext);
  }

  protected long onQuery(Cursor paramCursor)
  {
    return paramCursor.getLong(paramCursor.getColumnIndexOrThrow("_id"));
  }
}
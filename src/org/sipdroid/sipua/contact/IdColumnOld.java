package org.sipdroid.sipua.contact;
import android.content.Context;
import android.database.Cursor;

class IdColumnOld extends IdColumn
{
  IdColumnOld(Context paramContext)
  {
    super(paramContext);
  }

  protected long onQuery(Cursor paramCursor)
  {
    return paramCursor.getLong(paramCursor.getColumnIndexOrThrow("_id"));
  }
}
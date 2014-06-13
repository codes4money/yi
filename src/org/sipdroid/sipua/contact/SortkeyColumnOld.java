package org.sipdroid.sipua.contact;
import android.database.Cursor;
import java.util.ArrayList;

import com.studio.b56.im.CommFun;

class SortkeyColumnOld extends SortkeyColumn
{
  protected String onQuery(Cursor paramCursor, String paramString)
  {
	 return  CommFun.converterToFirstSpell(paramString).substring(0, 1).toUpperCase();
  }
}

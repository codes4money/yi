package com.studio.b56.im.app.control;

import android.app.Activity;
import android.content.res.Resources;

public final class AlertDialogUtils
{
  public static void mAlertDialog(Activity paramActivity,  String param1, String param2)
  {
    if (paramActivity.isFinishing());
    
      new BaseDialog.Builder(paramActivity).setTitle(param1).setMessage(param2).setYesListener(new BaseDialog.YesListener()
      {
        public void doYes()
        {
        }
      }).setNoCancel(true).show();
  }
}

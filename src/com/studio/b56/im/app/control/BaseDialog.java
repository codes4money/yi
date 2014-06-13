package com.studio.b56.im.app.control;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.PrintStream;
import com.studio.b56.im.R;

public class BaseDialog extends Dialog
{
  boolean dismissDiolog = true;
  int mBlack = 0;
  Button mCancelButton = null;
  CancelListener mCancelListener = null;
  LinearLayout mContentLayout = null;
  LinearLayout mContentSelect = null;
  View mDividerView = null;
  TextView mMessTextView = null;
  TextView mTitleTextView = null;
  Button mYesButton = null;
  YesListener mYesListener = null;
  LinearLayout mlayout = null;

  public BaseDialog(Context paramContext)
  {
    super(paramContext,R.style.IpcallDialog);
    show();
    setContentView(R.layout.dialog);
    this.mBlack = paramContext.getResources().getColor(R.color.black);
    this.mTitleTextView = ((TextView)findViewById(R.id.dialog_title_text));
    this.mMessTextView = ((TextView)findViewById(R.id.dialog_message_tv));
    this.mlayout = ((LinearLayout)findViewById(R.id.dialog_layout));
    this.mContentLayout = ((LinearLayout)findViewById(R.id.dialog_content));
    this.mContentSelect = ((LinearLayout)findViewById(R.id.dialog_select));
    this.mYesButton = ((Button)findViewById(R.id.dialog_yes));
    this.mCancelButton = ((Button)findViewById(R.id.dialog_cancel));
    this.mDividerView = findViewById(R.id.dialog_divider);
    this.mMessTextView.setVisibility(8);
    this.mYesButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (BaseDialog.this.mYesListener != null)
          BaseDialog.this.mYesListener.doYes();
        if (BaseDialog.this.dismissDiolog)
          BaseDialog.this.dismiss();
      }
    });
    this.mYesButton.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        if (paramMotionEvent.getAction() == 0)
        {
          BaseDialog.this.mYesButton.setTextColor(-1);
          if (BaseDialog.this.mCancelButton.getVisibility() == 0)
          {
            BaseDialog.this.mYesButton.setBackgroundResource(R.drawable.dialog_left_button);
          }
          else
          {
          BaseDialog.this.mYesButton.setBackgroundResource(R.drawable.dialog_all_button);
          }
        }
        else if (paramMotionEvent.getAction() != 1)
        {
        	 BaseDialog.this.mYesButton.setTextColor(BaseDialog.this.mBlack);
             BaseDialog.this.mYesButton.setBackgroundResource(R.drawable.apha);
        }
        return false;
      }
    });
    this.mCancelButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (BaseDialog.this.mCancelListener != null)
          BaseDialog.this.mCancelListener.doCancel();
        BaseDialog.this.dismiss();
      }
    });
    this.mCancelButton.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
       // MainActivity.cancelEventTimer();
        System.out.println("event.x = " + paramMotionEvent.getX());
        if (paramMotionEvent.getAction() == 0)
        {
          BaseDialog.this.mCancelButton.setTextColor(-1);
          if (BaseDialog.this.mYesButton.getVisibility() == 0)
          {
            BaseDialog.this.mCancelButton.setBackgroundResource(R.drawable.dialog_right_button);
          }
          else
          {
              BaseDialog.this.mCancelButton.setBackgroundResource(R.drawable.dialog_all_button);
          }
          
        }
        else  if (paramMotionEvent.getAction() != 1)
        {
          BaseDialog.this.mCancelButton.setTextColor(BaseDialog.this.mBlack);
          BaseDialog.this.mCancelButton.setBackgroundResource(R.drawable.apha);
        }
        return false;
      }
    });
  }

  public void addView(View paramView)
  {
    this.mContentLayout.addView(paramView);
  }

  public void dismissDialog(boolean paramBoolean)
  {
    this.dismissDiolog = paramBoolean;
  }

  public LinearLayout getLayout()
  {
    return this.mlayout;
  }

  public static class Builder
  {
    BaseDialog build;

    public Builder(Context paramContext)
    {
      this.build = new BaseDialog(paramContext);
    }

    private void checkVisiState()
    {
    	if ((this.build.mCancelButton.getVisibility() == 8) && (this.build.mYesButton.getVisibility() == 8))
        {
          this.build.mContentSelect.setVisibility(8);
          this.build.mDividerView.setVisibility(0);
        }
    	else if ((this.build.mCancelButton.getVisibility() != 0) || (this.build.mYesButton.getVisibility() != 0))
    	{
      	  this.build.mDividerView.setVisibility(8);
          this.build.mContentSelect.setVisibility(0);
    	}
    }

    public Builder addView(View paramView)
    {
      this.build.mContentLayout.addView(paramView);
      return this;
    }

    public BaseDialog getbuild()
    {
      return this.build;
    }

    public Builder setCancelListener(BaseDialog.CancelListener paramCancelListener)
    {
      this.build.mCancelListener = paramCancelListener;
      return this;
    }

    public Builder setCancelListener(String paramString, BaseDialog.CancelListener paramCancelListener)
    {
      if (paramString != null)
        this.build.mCancelButton.setText(paramString);
      this.build.mCancelListener = paramCancelListener;
      return this;
    }

    public Builder setMessage(String paramString)
    {
      this.build.mMessTextView.setText(paramString);
      this.build.mMessTextView.setVisibility(0);
      return this;
    }

    public Builder setNoCancel(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.build.mCancelButton.setVisibility(8);
      }
      else
      {
    	  this.build.mCancelButton.setVisibility(0);
      }
      checkVisiState();
      return this;
    }

    public Builder setNoYes(boolean paramBoolean)
    {
    	 if (paramBoolean)
         {
           this.build.mYesButton.setVisibility(8);
         }
         else
         {
       	  this.build.mYesButton.setVisibility(0);
         }
         checkVisiState();
         return this;
    }

    public Builder setTitle(String paramString)
    {
      this.build.mTitleTextView.setText(paramString);
      return this;
    }

    public Builder setYesListener(BaseDialog.YesListener paramYesListener)
    {
      this.build.mYesListener = paramYesListener;
      return this;
    }

    public Builder setYesListener(String paramString, BaseDialog.YesListener paramYesListener)
    {
      if (paramString != null)
        this.build.mYesButton.setText(paramString);
      this.build.mYesListener = paramYesListener;
      return this;
    }

    public BaseDialog show()
    {
      this.build.setCanceledOnTouchOutside(false);
      WindowManager.LayoutParams localLayoutParams = this.build.getWindow().getAttributes();
      localLayoutParams.dimAmount = 0.5F;
      this.build.getWindow().setAttributes(localLayoutParams);
      this.build.getWindow().addFlags(2);
      this.build.show();
      return this.build;
    }
  }

  public static abstract interface CancelListener
  {
    public abstract void doCancel();
  }

  public static abstract interface YesListener
  {
    public abstract void doYes();
  }
}

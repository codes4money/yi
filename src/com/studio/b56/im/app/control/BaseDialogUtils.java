package com.studio.b56.im.app.control;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.studio.b56.im.R;

public class BaseDialogUtils {
	String inputMsg;
	  int inputMsgColor = -1;
	  BaseDialog.Builder mBuilder;
	  String mCancel = "ȡ��";
	  Context mContext;
	  String mOk = "ȷ��";
	  OnItemClickListen listen;
	  onItemCheckedCHangeListener listen2;
	  InputListener listen3;
	  String str;
	  EditText editV;
	  public BaseDialogUtils(Context paramContext)
	  {
	    this.mBuilder = new BaseDialog.Builder(paramContext);
	    this.mContext = paramContext;
	  }

	  private void setcontentHeight(View paramView, Object[] paramArrayOfObject)
	  {
	    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, 2 * ((Activity)this.mContext).getWindowManager().getDefaultDisplay().getHeight() / 3);
	    if (paramArrayOfObject.length > 6)
	      paramView.setLayoutParams(localLayoutParams);
	    this.mBuilder.show();
	  }

	  public BaseDialog getdialog()
	  {
	    return this.mBuilder.getbuild();
	  }

	  public void setDialogContent(View paramView)
	  {
	    LinearLayout localLinearLayout = (LinearLayout)this.mBuilder.getbuild().findViewById(R.id.dialog_content);
	    localLinearLayout.removeAllViews();
	    localLinearLayout.setGravity(17);
	    localLinearLayout.addView(paramView);
	  }

	  public BaseDialogUtils setInputMsg(int paramInt)
	  {
	    this.inputMsg = this.mContext.getString(paramInt);
	    return this;
	  }

	  public BaseDialogUtils setInputMsg(int paramInt1, int paramInt2)
	  {
	    this.inputMsgColor = paramInt2;
	    return setInputMsg(paramInt1);
	  }

	  public BaseDialogUtils setInputMsg(CharSequence paramCharSequence)
	  {
	    this.inputMsg = paramCharSequence.toString();
	    return this;
	  }

	  public BaseDialogUtils setInputMsg(CharSequence paramCharSequence, int paramInt)
	  {
	    this.inputMsgColor = paramInt;
	    return setInputMsg(paramCharSequence);
	  }

	  public void setListViewItem(String[] paramArrayOfString, OnItemClickListen paramOnItemClickListen)
	  {
	    ListView localListView = new ListView(this.mContext);
	    localListView.setFadingEdgeLength(0);
	    localListView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
	    localListView.setDivider(this.mContext.getResources().getDrawable(R.drawable.horizontal_line));
	    localListView.setScrollBarStyle(0);
	    localListView.setAdapter(new ArrayAdapter(this.mContext, R.layout.phone_dialog_item, paramArrayOfString));
		listen=paramOnItemClickListen;
	    
	    localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
	    {
	      public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
	      {
	    	 listen.onClick(paramInt);
	        BaseDialogUtils.this.mBuilder.getbuild().dismiss();
	      }
	    });
	    
	    setcontentHeight(localListView, paramArrayOfString);
	    this.mBuilder.addView(localListView).setNoYes(true);
	  }

	  public BaseDialogUtils setMessage(String paramString)
	  {
	    this.mBuilder.setMessage(paramString);
	    return this;
	  }

	  public BaseDialogUtils setNoYes(boolean paramBoolean)
	  {
	    this.mBuilder.setNoYes(paramBoolean);
	    return this;
	  }

	  public BaseDialogUtils setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener)
	  {
	    this.mBuilder.getbuild().setOnDismissListener(paramOnDismissListener);
	    return this;
	  }

	  public BaseDialogUtils setTextViewAndChekBoxItem2(String[] paramArrayOfString, final onItemCheckedCHangeListener paramonItemCheckedCHangeListener)
	  {
	    ScrollView localScrollView = new ScrollView(this.mContext);
	    LinearLayout localLinearLayout = new LinearLayout(this.mContext);
	    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, -2);
	    localLinearLayout.setOrientation(1);
	    localLinearLayout.setLayoutParams(localLayoutParams);
	    localScrollView.addView(localLinearLayout);
	    localScrollView.setLayoutParams(localLayoutParams);
	    setDialogContent(localScrollView);
	    int i = paramArrayOfString.length;
	    //listen2=paramonItemCheckedCHangeListener;
	    for (int j = 0; ; j++)
	    {
	      if (j >= i)
	      {
	        setcontentHeight(localScrollView, paramArrayOfString);
	        return this;
	      }
	      str = paramArrayOfString[j];
	      View localView = LayoutInflater.from(this.mContext).inflate(R.layout.contact_sms_dialog_item, null);
	      CheckBox localCheckBox = (CheckBox)localView.findViewById(R.id.contact_sms_checkBox);
	      ((TextView)localView.findViewById(R.id.contact_sms_number_text)).setText(str);
	      localCheckBox.setVisibility(0);
	      localCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
	      { 
	        public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
	        {
	        	paramonItemCheckedCHangeListener.onItemCheckedChange(str, paramBoolean);
	        }
	      });
	      localLinearLayout.addView(localView);
	    }
	  }
	  
		public BaseDialogUtils setTextViewAndChekBoxItem(String as[], final onItemCheckedCHangeListener listener)
		{
			ScrollView scrollview = new ScrollView(mContext);
			LinearLayout linearlayout = new LinearLayout(mContext);
			android.widget.LinearLayout.LayoutParams layoutparams = new android.widget.LinearLayout.LayoutParams(-1, -2);
			linearlayout.setOrientation(1);
			linearlayout.setLayoutParams(layoutparams);
			scrollview.addView(linearlayout);
			scrollview.setLayoutParams(layoutparams);
			setDialogContent(scrollview);
			int i = as.length;
			int j = 0;
			do
			{
				if (j >= i)
				{
					setcontentHeight(scrollview, as);
					return this;
				}
				
				final String number = as[j];
				View view =  LayoutInflater.from(this.mContext).inflate(R.layout.contact_sms_dialog_item, null);
				final CheckBox checkbox = (CheckBox)view.findViewById(R.id.contact_sms_checkBox);
				TextView textv=(TextView)view.findViewById(R.id.contact_sms_number_text);
				((TextView)view.findViewById(R.id.contact_sms_number_text)).setText(number);
				checkbox.setVisibility(0);
				textv.setOnClickListener(new View.OnClickListener()
		        {
			          public void onClick(View paramView)
			          {
			        	  if(checkbox.isChecked())
			        	  {
			        		  checkbox.setChecked(false);
			        	  }
			        	  else
			        	  {
			        		  checkbox.setChecked(true);
			        	  }
			        	 listener.onItemCheckedChange(number,true);
			          }
			        });
				
				checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton compoundbutton, boolean flag)
					{
						listener.onItemCheckedChange(number, flag);
					
					}
					
				});

				linearlayout.addView(view);
				j++;
			} while (true);
		}
		

	  public BaseDialogUtils setTitle(String paramString)
	  {
	    this.mBuilder.setTitle(paramString);
	    return this;
	  }

	  public BaseDialogUtils setYesListener(BaseDialog.YesListener paramYesListener)
	  {
	    this.mBuilder.setYesListener(paramYesListener);
	    return this;
	  }

	  public void show()
	  {
	    this.mBuilder.show();
	  }

	  public void showInputDialog(InputListener paramInputListener)
	  {
	    View localView = View.inflate(this.mContext, R.layout.vip_dialog_item, null);
	    EditText localEditText = (EditText)localView.findViewById(R.id.vip_dialog_Edit);
	   // localEditText.setInputType(paramInputListener.getInputType());
	    TextView localTextView = (TextView)localView.findViewById(R.id.vip_dialog_text);
	    localTextView.setText(this.inputMsg);
	    editV=localEditText;
	    listen3=paramInputListener;
	    localEditText.setFocusable(true);
	    if ((this.inputMsg == null) || (this.inputMsg.equals("")))
	    {
	      localTextView.setVisibility(8);
	    }
	    
	    this.mBuilder.addView(localView).setYesListener(new BaseDialog.YesListener()
	    {
	      public void doYes()
	      {
	    	  
	        String str = editV.getText().toString();
	        if (listen3.isInputTrue(str))
	        {
	          BaseDialogUtils.this.mBuilder.getbuild().dismissDialog(true);
	          listen3.ok(str);
	        }
	        else
	        {
	          BaseDialogUtils.this.mBuilder.getbuild().dismissDialog(false);
	          Toast.makeText(BaseDialogUtils.this.mContext, listen3.getErrMsg(), 1).show();
	        }
	        
	      }
	      
	    }).show();
	    
	  }

	  public static abstract interface ConfirmListener
	  {
	    public abstract void ok();
	  }

	  public static abstract interface InputListener
	  {
	    public abstract String getErrMsg();

	    public abstract int getInputType();

	    public abstract boolean isInputTrue(String paramString);

	    public abstract void ok(String paramString);
	  }

	  public static abstract interface OnItemClickListen
	  {
	    public abstract void onClick(int paramInt);
	  }

	  public static abstract interface onItemCheckedCHangeListener
	  {
	    public abstract void onItemCheckedChange(String paramString, boolean paramBoolean);
	  }
}

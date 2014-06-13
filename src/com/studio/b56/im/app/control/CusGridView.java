package com.studio.b56.im.app.control;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class CusGridView extends GridView{

	public CusGridView(Context context) {  
        super(context);  
        // TODO Auto-generated constructor stub  
    }  
    public CusGridView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // TODO Auto-generated constructor stub  
    }  
    public CusGridView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        // TODO Auto-generated constructor stub  
    }  

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(   
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
          
        super.onMeasure(widthMeasureSpec, expandSpec); 
	}
}

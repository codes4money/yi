package org.sipdroid.sipua.ui;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainSlideHost extends TabHost
{
  boolean isOpenAnimation;
  int mTabCount;
  Animation slideLeftIn;
  Animation slideLeftOut;
  Animation slideRightIn;
  Animation slideRightOut;

  public MainSlideHost(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setupSlide();
  }

  public void addTab(TabHost.TabSpec paramTabSpec)
  {
    this.mTabCount = (1 + this.mTabCount);
    super.addTab(paramTabSpec);
  }

  public int getTabCount()
  {
    return this.mTabCount;
  }
	public void setCurrentTab(int i)
	{
		super.setCurrentTab(i);
		
		int j = getCurrentTab();
		if (getCurrentView() != null && isOpenAnimation)
			if (j == -1 + mTabCount && i == 0)
				getCurrentView().startAnimation(slideLeftOut);
			else
			if (j == 0 && i == -1 + mTabCount)
				getCurrentView().startAnimation(slideRightOut);
			else
			if (i > j)
				getCurrentView().startAnimation(slideLeftOut);
			else
			if (i < j)
				getCurrentView().startAnimation(slideRightOut);
		
		if (isOpenAnimation)
			if (j == -1 + mTabCount && i == 0)
				getCurrentView().startAnimation(slideLeftIn);
			else
			if (j == 0 && i == -1 + mTabCount)
				getCurrentView().startAnimation(slideRightIn);
			else
			if (i > j)
				getCurrentView().startAnimation(slideLeftIn);
			else
			if (i < j)
				getCurrentView().startAnimation(slideRightIn);
	}
	

  public void setOpenAnimation(boolean paramBoolean)
  {
    this.isOpenAnimation = paramBoolean;
  }

  void setupSlide()
  {
    this.isOpenAnimation = false;
  }
}
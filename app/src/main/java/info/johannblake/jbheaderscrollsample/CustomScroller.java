package info.johannblake.jbheaderscrollsample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import info.johannblake.widgets.jbheaderscrolllib.JBHeaderScroll;

/**
 * Created by Johann on 6/16/15.
 */
public class CustomScroller extends RelativeLayout
{
  private JBHeaderScroll jbHeaderScroll;

  public CustomScroller(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public CustomScroller(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev)
  {
    if (this.jbHeaderScroll != null)
      this.jbHeaderScroll.onScrollerDispatchTouchEventListener(this, ev);

    return super.dispatchTouchEvent(ev);
  }

  public void setJBHeaderRef(JBHeaderScroll jbHeaderScroll)
  {
    this.jbHeaderScroll = jbHeaderScroll;
  }
}

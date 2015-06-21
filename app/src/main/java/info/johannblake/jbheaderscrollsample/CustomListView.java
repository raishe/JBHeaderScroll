package info.johannblake.jbheaderscrollsample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import info.johannblake.widgets.jbheaderscrolllib.JBHeaderScroll;

/**
 * Created by Johann on 6/16/15.
 */
public class CustomListView extends ListView
{
  private JBHeaderScroll jbHeaderScroll;

  public CustomListView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  public CustomListView(Context context, AttributeSet attrs, int defStyleAttr)
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

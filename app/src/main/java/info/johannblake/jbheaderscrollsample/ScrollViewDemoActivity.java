package info.johannblake.jbheaderscrollsample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import info.johannblake.widgets.jbheaderscrolllib.JBHeaderScroll;


public class ScrollViewDemoActivity extends Activity
{
  private final String LOG_TAG = "ScrollViewDemoActivity";

  private JBHeaderScroll jbHeaderScroll;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_scrollview_demo);

      // Fill the scrollview with some data.
      final CustomScrollView scrollview = (CustomScrollView) findViewById(R.id.scrollview);

      LinearLayout llItems = new LinearLayout(this);
      llItems.setOrientation(LinearLayout.VERTICAL);

      for (int i = 0; i < 100; i++)
      {
        TextView textview = new TextView(this);
        textview.setText(String.valueOf(i));
        llItems.addView(textview);
      }

      scrollview.addView(llItems);

      // Setup a JBHeaderScroll.
      final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

      toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        @Override
        public void onGlobalLayout()
        {
          try
          {
            scrollview.setY(toolbar.getHeight());
            jbHeaderScroll = new JBHeaderScroll(toolbar, 0);
            jbHeaderScroll.registerScroller(scrollview, new JBHeaderScroll.IJBHeaderScroll()
            {
              @Override
              public void onResize(float top)
              {
                try
                {
                  // The list's view top edge must be adjusted during scrolling.
                  // IMPORTANT: Make sure you use the correct type of LayoutParams which is the type that applies to the parent
                  // container of the listview.
                  RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                  scrollview.setLayoutParams(layoutParams);
                  scrollview.setY(top);
                  toolbar.bringToFront();  // This may be necessary in your app depending on your layout.
                }
                catch (Exception ex)
                {
                  Log.e(LOG_TAG, "onResize: " + ex.toString());
                }
              }

              @Override
              public int onHeaderBeforeAnimation(boolean scrollingUp, float scrollDelta)
              {
                return JBHeaderScroll.ANIMATE_HEADER_USE_DEFAULT;
              }

              @Override
              public void onHeaderAfterAnimation(boolean animatedUp, float scrollDelta)
              {
              }
            });

            scrollview.setJBHeaderRef(jbHeaderScroll);
            toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
          catch (Exception ex)
          {
            Log.e(LOG_TAG, "onGlobalLayout: " + ex.toString());
          }
        }
      });
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "showListViewDemo: " + ex.toString());
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev)
  {
    if (this.jbHeaderScroll != null)
      this.jbHeaderScroll.onRootDispatchTouchEventListener(ev);

    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_list_view_demo, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings)
    {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}

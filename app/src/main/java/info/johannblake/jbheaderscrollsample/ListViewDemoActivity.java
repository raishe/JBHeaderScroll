package info.johannblake.jbheaderscrollsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import info.johannblake.widgets.jbheaderscrolllib.JBHeaderScroll;


public class ListViewDemoActivity extends Activity
{
  private final String LOG_TAG = "ListViewDemoActivity";

  private JBHeaderScroll jbHeaderScroll;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_listview_demo);

      // Fill the listview with some data.
      final CustomListView listview = (CustomListView) findViewById(R.id.listview);
      List<String> items = new ArrayList<>();

      for (int i = 0; i < 100; i++)
        items.add(String.valueOf(i));

      ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
      listview.setAdapter(arrayAdapter);

      // Setup a JBHeaderScroll.
      final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

      toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        @Override
        public void onGlobalLayout()
        {
          try
          {
            listview.setY(toolbar.getHeight());
            jbHeaderScroll = new JBHeaderScroll(toolbar, 0);
            jbHeaderScroll.registerScroller(listview, new JBHeaderScroll.IJBHeaderScroll()
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
                  listview.setLayoutParams(layoutParams);
                  listview.setY(top);
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

            listview.setJBHeaderRef(jbHeaderScroll);
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

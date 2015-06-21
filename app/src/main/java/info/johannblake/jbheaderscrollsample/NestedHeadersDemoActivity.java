package info.johannblake.jbheaderscrollsample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import info.johannblake.widgets.jbheaderscrolllib.JBHeaderScroll;


public class NestedHeadersDemoActivity extends Activity
{
  private final String LOG_TAG = "NestedHeaders";

  private JBHeaderScroll jbHeaderScrollOuter;
  private JBHeaderScroll jbHeaderScrollInner;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_nestedheaders_demo);

      // Fill the listview1 with some data.
      final CustomListView listview1 = (CustomListView) findViewById(R.id.listview1);
      List<String> items1 = new ArrayList<>();

      for (int i = 0; i < 100; i++)
        items1.add(String.valueOf(i));

      ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items1);
      listview1.setAdapter(arrayAdapter1);

      // Fill the listview2 with some data.
      final CustomListView listview2 = (CustomListView) findViewById(R.id.listview2);
      List<String> items2 = new ArrayList<>();

      for (int i = 0; i < 100; i++)
        items2.add(String.valueOf(i));

      ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items2);
      listview2.setAdapter(arrayAdapter2);

      // Setup a JBHeaderScroll.
      final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

      toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        @Override
        public void onGlobalLayout()
        {
          try
          {
            listview1.setY(toolbar.getHeight());
            jbHeaderScrollOuter = new JBHeaderScroll(toolbar, 0);

            jbHeaderScrollOuter.registerScroller(listview1, new JBHeaderScroll.IJBHeaderScroll()
            {
              @Override
              public void onResize(float top)
              {
                try
                {
                  // The list's view top edge must be adjusted during scrolling.
                  // IMPORTANT: Make sure you use the correct type of LayoutParams which is the type that applies to the parent
                  // container of the listview.
                  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                  listview1.setLayoutParams(layoutParams);
                  listview1.setY(top);
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

            listview1.setJBHeaderRef(jbHeaderScrollOuter);


            // Setup the right listview
            final CustomScroller customScroller1 = (CustomScroller) findViewById(R.id.customScroller1);
            customScroller1.setY(toolbar.getHeight());

            jbHeaderScrollOuter.registerScroller(customScroller1, new JBHeaderScroll.IJBHeaderScroll()
            {
              @Override
              public void onResize(float top)
              {
                try
                {
                  // The list's view top edge must be adjusted during scrolling.
                  // IMPORTANT: Make sure you use the correct type of LayoutParams which is the type that applies to the parent
                  // container of the listview.
                  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
                  customScroller1.setLayoutParams(layoutParams);
                  customScroller1.setY(top);
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

            customScroller1.setJBHeaderRef(jbHeaderScrollOuter);


            // Setup the scroller for the nested header.
            final LinearLayout llHeader2 = (LinearLayout) findViewById(R.id.llHeader2);

            listview2.setY(llHeader2.getHeight());
            jbHeaderScrollInner = new JBHeaderScroll(llHeader2, 0);

            jbHeaderScrollInner.registerScroller(listview2, new JBHeaderScroll.IJBHeaderScroll()
            {
              @Override
              public void onResize(float top)
              {
                try
                {
                  // The list's view top edge must be adjusted during scrolling.
                  // IMPORTANT: Make sure you use the correct type of LayoutParams which is the type that applies to the parent
                  // container of the listview.
                  RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                  listview2.setLayoutParams(layoutParams);
                  listview2.setY(top);
                  llHeader2.bringToFront();  // This may be necessary in your app depending on your layout.
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

            listview2.setJBHeaderRef(jbHeaderScrollInner);



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
      Log.e(LOG_TAG, "onCreate: " + ex.toString());
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev)
  {
    if (this.jbHeaderScrollOuter != null)
      this.jbHeaderScrollOuter.onRootDispatchTouchEventListener(ev);

    if (this.jbHeaderScrollInner != null)
      this.jbHeaderScrollInner.onRootDispatchTouchEventListener(ev);

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

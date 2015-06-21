package info.johannblake.jbheaderscrollsample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import info.johannblake.widgets.jbheaderscrolllib.JBHeaderScroll;


public class WebViewDemoActivity extends Activity
{
  private final String LOG_TAG = "WebViewDemoActivity";

  private JBHeaderScroll jbHeaderScroll;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_webview_demo);

      // Fill the scrollview with some data.
      final CustomWebView webview = (CustomWebView) findViewById(R.id.webview);

      setupWebView(webview);

      // Setup a JBHeaderScroll.
      final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

      toolbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
      {
        @Override
        public void onGlobalLayout()
        {
          try
          {
            webview.setY(toolbar.getHeight());
            jbHeaderScroll = new JBHeaderScroll(toolbar, 0);
            jbHeaderScroll.registerScroller(webview, new JBHeaderScroll.IJBHeaderScroll()
            {
              @Override
              public void onReposition(float top, boolean scrollingUp, float scrollDelta)
              {
                try
                {
                  // The list's view top edge must be adjusted during scrolling.
                  // IMPORTANT: Make sure you use the correct type of LayoutParams which is the type that applies to the parent
                  // container of the listview.

                  // When the user releases their finger while scrolling very slowly, the jitter from their finger
                  // may result in a slight amount of scrolling downward. This can result in a side effect of the
                  // header animating down when it might have animated up depending on its current position. To
                  // avoid this, avoid repositioning the scroller for small amounts of scrolling. You may need to
                  // play with this value.

                  if (scrollingUp || (!scrollingUp && (scrollDelta > 5)))
                  {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(0, (int) top, 0, 0);
                    webview.setLayoutParams(layoutParams);
                    toolbar.bringToFront(); // Necessary if your scroller is rendered last.
                  }
                }
                catch (Exception ex)
                {
                  Log.e(LOG_TAG, "onReposition: " + ex.toString());
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

            webview.setJBHeaderRef(jbHeaderScroll);
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


  private void setupWebView(CustomWebView webview)
  {
    try
    {
      // Setup the webview
      WebSettings webSettings = webview.getSettings();
      webSettings.setBuiltInZoomControls(true);
      webSettings.setSupportZoom(true);
      webSettings.setJavaScriptEnabled(true);
      webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

      webview.setWebViewClient(new WebViewClient()
      {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
          view.loadUrl(url);
          return true;
        }
      });

      webview.loadUrl("https://en.wikipedia.org/wiki/Antikythera_mechanism");
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "setupWebView: " + ex.toString());
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

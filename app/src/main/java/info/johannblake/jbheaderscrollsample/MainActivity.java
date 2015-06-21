package info.johannblake.jbheaderscrollsample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity
{
  private final String LOG_TAG = "MainActivity";


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }


  public void showNestedHeadersDemo(View v)
  {
    try
    {
      Intent intent = new Intent(this, NestedHeadersDemoActivity.class);
      startActivity(intent);
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "showNestedHeadersDemo: " + ex.toString());
    }
  }

  public void showListViewDemo(View v)
  {
    try
    {
      Intent intent = new Intent(this, ListViewDemoActivity.class);
      startActivity(intent);
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "showListViewDemo: " + ex.toString());
    }
  }


  public void showScrollViewDemo(View v)
  {
    try
    {
      Intent intent = new Intent(this, ScrollViewDemoActivity.class);
      startActivity(intent);
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "showScrollViewDemo: " + ex.toString());
    }
  }


  public void showWebViewDemo(View v)
  {
    try
    {
      Intent intent = new Intent(this, WebViewDemoActivity.class);
      startActivity(intent);
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "showWebViewDemo: " + ex.toString());
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
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

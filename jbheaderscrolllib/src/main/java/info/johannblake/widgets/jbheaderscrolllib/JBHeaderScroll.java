/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Johann Blake
 *
 * https://www.linkedin.com/in/johannblake
 * https://plus.google.com/+JohannBlake
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package info.johannblake.widgets.jbheaderscrolllib;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;

import java.util.HashMap;

/**
 * Provides synchronization between scrolling content and headers that are moved in and out of view as content is scrolled up or down.
 * <p/>
 * A scroller, as referred to in this code, can be any control that is capable of scrolling content such as a ScrollView, ListView and
 * even a WebView.
 * <p/>
 * A header, as referred to in this code, can be anything that needs to be scrolled into or out of view as the user scrolls content in
 * a scroller. In a typical application, a header could be a toolbar at the top of the app or above a scroller. Android Actionbars are
 * not supported.
 * <p/>
 * The purpose of scrolling the header out of view when scrolling up is to allow the user the ability to see as much content as
 * possible without the header taking up unnecessary space on the screen. While this is generally not a big issue on smartphones
 * or tablets with large screens, it does become an issue under certain conditions. If the app is scrolling images and you want
 * to let the user see the image in the maximum size possible while scrolling, a fixed height header would reduce the size of the
 * image. Another situation is when the size of rows in a listview are fairly large, you will see fewer rows, so maximizing the
 * height of the listview to show as many rows as possible is preferrable. Finally, even if all you are showing is text in the
 * scroll area, some users who are badly visually impaired may need to set the textsize to be very large. Scrolling the header out
 * of view increases the amount of space for more text.
 * <p/>
 * <p/>
 * When the user scrolls up, the header will move up or down in sync with the scrolling. When moving up, the header becomes increasingly
 * less visible. When scrolling down, it becomes increasingly visible until it is completely scrolled into its full position.
 * <p/>
 * If the user scrolls slowly and then releases their finger before the header has been either scrolled fully up or fully down, the header
 * will then automatically scroll fully up if the less than half the header's height is visible or scroll down if more than half is visible.
 * This automatic scrolling up / down is animated.
 * <p/>
 * There is support for multiple scrollers. Consider an application like Gmail. On a tablet, if Gmail is shown in landscape mode, you
 * can see 3 separate scroll areas layed out next to each other horizontally (the folders list for the current account, the list of
 * emails in the inbox, and the details of the currently selected email). Gmail has a single header at the top of the app and all
 * three scroll areas are beneath this header. The Gmail header is fixed and does not scroll up or down as the user scrolls content
 * in the scroll areas.
 * <p/>
 * With JBHeaderScroll, if you had an app similar to Gmail and scrolled on any of the three scrollable areas, the header would scroll
 * up or down while managing the top position of those scrollers that are not currently being scrolled. Without this management, scrolling
 * up on one list may cause the header to scroll out of view and leave a gap at the top of those scrollers that were not scrolled
 * directly. JBHeaderScroll will make sure that no gaps remain when scrolling up.
 */
public class JBHeaderScroll
{
  private final String LOG_TAG = "JBHeaderScroll";

  public View vHeader;
  private boolean initialized;
  private HashMap<View, ScrollableContent> hmScrollableViews = new HashMap<View, ScrollableContent>();

  private boolean fingerUp = true;
  private boolean scrollingUp;
  private float scrollDelta;
  private float scrollerMinTopY;
  private float scrollerMaxTopY;
  private ScrollableContent scrollableContent;
  private float motionEventPrevY;

  private float headerInitialY;
  private boolean headerAnimating;
  private ObjectAnimator animatorHeader;
  private boolean cancelHeaderAnimation;


  public final static int ANIMATE_HEADER_USE_DEFAULT = 0;
  public final static int ANIMATE_HEADER_UP = 1;
  public final static int ANIMATE_HEADER_DOWN = 2;


  /**
   * Constructor for JBHeaderScroll
   *
   * @param viewHeader The view that will be scrolled into or out of view.
   * @param yOffset    The amount of space between the top of the header and the upper edge where the scroller will no
   *                   longer be visible as the user scrolls up. In a typical app, the header will appear at the top
   *                   of the screen with no spacing between its top edge and the parent container holding the header, so
   *                   in this case yOffset should be set to zero. But in cases where you have padding or margins around
   *                   the header, you need to provide the amount of offset in order to prevent the scroller from scrolling
   *                   beyond the upper edge of the header. So if your header has a margin or padding at the top edge of
   *                   10dp, you would set yOffset to 10.
   */
  public JBHeaderScroll(final View viewHeader, final int yOffset)
  {
    this.vHeader = viewHeader;

    viewHeader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
    {
      @Override
      public void onGlobalLayout()
      {
        try
        {
          if (!initialized)
          {
            initialized = true;

            // Determine the upper and lower absolute Y positions that scrollers
            // can scroll between. During scrolling, the scroller's top position (Y)
            // is adjusted to be within this range.
            scrollerMinTopY = yOffset;
            scrollerMaxTopY = scrollerMinTopY + vHeader.getHeight();

            vHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
          }
        }
        catch (Exception ex)
        {
          Log.e(LOG_TAG, "onGlobalLayout: " + ex.toString());
        }
      }
    });
  }

  /**
   * Registers a scroller. This is required in order to have a header scroll into or out of view while the user
   * scrolls a scroller.
   *
   * @param vScroller       The control that is performing scrolling such as a scrollview, listview, webview, etc.
   * @param iJBHeaderScroll A reference to the interface that JBHeaderScroll needs in order to communicate
   *                        with the scroller during scrolling.
   */
  public void registerScroller(View vScroller, IJBHeaderScroll iJBHeaderScroll)
  {
    try
    {
      if (this.hmScrollableViews.get(vScroller) != null)
        return;

      ScrollableContent scrollableContent = new ScrollableContent(vScroller, iJBHeaderScroll);
      this.hmScrollableViews.put(vScroller, scrollableContent);
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "registerScroller: " + ex.toString());
    }
  }


  /**
   * Receives motion events from the scroller. Scrollers must implement the dispatchTouchEvent method and call this
   * method from there.
   *
   * @param v     Indicates the scroller that sent the motion event.
   * @param event The motion event that was sent.
   */

  public void onScrollerDispatchTouchEventListener(View v, MotionEvent event)
  {
    try
    {
      if (!this.initialized)
        return;

      if (event.getAction() == MotionEvent.ACTION_DOWN)
        this.scrollableContent = this.hmScrollableViews.get(v);
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "onScrollerDispatchTouchEventListener: " + ex.toString());
    }
  }


  /**
   * Receives all the motion events to indicate finger up/down and scrolling. Motion events
   * are received from the client who must send the events from some level within their
   * activity or fragment's view hierarchy but at a view that is a parent of the scroller.
   * The scroller itself must never send motion events to this method.
   *
   * @param event
   */
  public void onRootDispatchTouchEventListener(MotionEvent event)
  {
    try
    {
      if (!this.initialized)
        return;

      if (event.getAction() == MotionEvent.ACTION_UP)
      {
        // Reposition the header if necessary.
        this.fingerUp = true;

        if (((this.vHeader.getY() != 0) && (this.vHeader.getY() != -this.vHeader.getHeight())) || (scrollDelta != 0))
          onScrollSyncTouch();

        this.scrollableContent = null;
      }
      else if (event.getAction() == MotionEvent.ACTION_DOWN)
      {
        this.fingerUp = false;
        this.motionEventPrevY = event.getY();

        // Switch to the selected scroller.
        //this.scrollableContent = this.hmScrollableViews.get(this.scrollableContent);
      }

      if ((event.getAction() == MotionEvent.ACTION_MOVE) && (this.scrollableContent != null))
      {
        // Adjust the position of the scroller and header.
        scrollingUp = event.getY() < this.motionEventPrevY;
        scrollDelta = Math.abs(event.getY() - this.motionEventPrevY);
        this.motionEventPrevY = event.getY();

        if (this.scrollDelta != 0)
          onScrollSyncMotion();
      }
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "onRootDispatchTouchEventListener: " + ex.toString());
    }
  }


  /**
   * Gets called when any motion is detected on the scrollview. It's responsible for scrolling the header and
   * repositioning the scroller.
   */
  private void onScrollSyncMotion()
  {
    try
    {
      // Display the header if the user was flinging the scrollview down.
      if (!scrollingUp && (this.scrollDelta > 50) && fingerUp)
      {
        animateHeaderDown();
        return;
      }

      // Hide the header if the user was flinging the scrollview up.
      if (scrollingUp && (this.scrollDelta > 50) && fingerUp)
      {
        animateHeaderUp();
        return;
      }

      if (headerAnimating || fingerUp)
        return;

      // The header's top value will be either fully up or fully down after scrolling has completed with a finger
      // up. During the next scroll, the top is shifted either up or down depending on whether the header
      // was initially visible or hidden and depending on whether the user scrolls up or down.
      if (headerInitialY == 0)
      {
        if (scrollingUp)
        {
          float y = this.vHeader.getY() - this.scrollDelta;

          if (y < -this.vHeader.getHeight())
            y = -this.vHeader.getHeight();

          this.vHeader.setY(y);

          // Move the scroller's top position upward which effectively
          // makes the scroller larger - provided the client creates a
          // new layout and pins the scroller to its container's bottom.
          // IMPORTANT: Originally, the height of the scroller was adjusted here
          // but Android will readjust the height of ListViews based on internal
          // factors, making it impossible to have a predictable height.

          float delta = this.scrollDelta;

          // Tell all other scrollers to move up.
          for (ScrollableContent scrollerContent : this.hmScrollableViews.values())
          {
            float newTop = scrollerContent.vContentArea.getY() - delta;

            if (newTop < this.scrollerMinTopY)
              newTop = this.scrollerMinTopY;

            scrollerContent.iJBHeaderScroll.onResize(newTop);
          }
        }
        else
        {
          // Bring the header into view.
          float y = this.vHeader.getY() + this.scrollDelta;

          if (y > 0)
            y = 0;

          this.vHeader.setY(y);

          // Move the scroller's top position downward which effectively
          // makes the scroller smaller - provided the client creates a
          // new layout and pins the scroller to its container's bottom.

          float newTop = this.scrollableContent.vContentArea.getY() + this.scrollDelta;

          if (newTop > this.scrollerMaxTopY)
            newTop = this.scrollerMaxTopY;

          if (this.scrollableContent.iJBHeaderScroll != null)
            this.scrollableContent.iJBHeaderScroll.onResize(newTop);
        }
      }
      else
      {
        if (scrollingUp)
        {
          float y = this.vHeader.getY() - this.scrollDelta;

          if (y < -this.vHeader.getHeight())
            y = -this.vHeader.getHeight();

          this.vHeader.setY(y);

          float delta = this.scrollDelta;
          float newTop = this.scrollableContent.vContentArea.getY() - delta;

          if (newTop < this.scrollerMinTopY)
            newTop = this.scrollerMinTopY;

          if (this.scrollableContent.iJBHeaderScroll != null)
            this.scrollableContent.iJBHeaderScroll.onResize(newTop);
        }
        else
        {
          // Bring the header into view.
          float y = this.vHeader.getY() + this.scrollDelta;

          if (y > 0)
            y = 0;

          this.vHeader.setY(y);

          // Bring the scroller into view.

          float newTop = this.scrollableContent.vContentArea.getY() + this.scrollDelta;

          if (newTop > this.scrollerMaxTopY)
            newTop = this.scrollerMaxTopY;

          if (this.scrollableContent.iJBHeaderScroll != null)
            this.scrollableContent.iJBHeaderScroll.onResize(newTop);
        }
      }
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "onScrollSyncMotion: " + ex.toString());
    }
  }


  /**
   * Gets called whenever a touch event on the scrollview occurs.
   */
  private void onScrollSyncTouch()
  {
    try
    {
      processHeaderScrollPosition();
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "onScrollSyncTouch: " + ex.toString());
    }
  }


  /**
   * This is where the decision is made to either display or hide the header.
   */
  private void processHeaderScrollPosition()
  {
    try
    {
      if (this.fingerUp)
      {
        if (this.scrollableContent == null)
          return;
        
        // Animate the header up or down if the client has requested it.
        if (this.scrollableContent.iJBHeaderScroll != null)
        {
          int animateDirection = this.scrollableContent.iJBHeaderScroll.onHeaderBeforeAnimation(this.scrollingUp, this.scrollDelta);

          if (animateDirection == ANIMATE_HEADER_UP)
          {
            animateHeaderUp();
            return;
          }
          else if (animateDirection == ANIMATE_HEADER_DOWN)
          {
            animateHeaderDown();
            return;
          }
        }

        //The scroller's top is within the range of header's height.
        if ((this.scrollableContent.vContentArea.getY() > this.scrollerMinTopY) && (this.scrollableContent.vContentArea.getY() < this.scrollerMaxTopY))
        {
          animateHeaderDown();
          return;
        }

        // Scrolling a large delta up.
        if (this.scrollingUp && (this.scrollDelta > this.vHeader.getHeight() / 2))
        {
          animateHeaderUp();
          return;
        }

        // Header's top has moved up more than half its height while scrolling up.
        if (this.scrollingUp && (this.vHeader.getY() < -(this.vHeader.getHeight() / 2)))
        {
          animateHeaderUp();
          return;
        }

        // Header's top has moved up less than half its height while scrolling up.
        if (this.scrollingUp && (this.vHeader.getY() > -(this.vHeader.getHeight() / 2)))
        {
          animateHeaderDown();
          return;
        }

        // Scrolling down and the header is initially not visible at all.
        if (!this.scrollingUp && (this.vHeader.getY() <= -this.vHeader.getHeight()))
        {
          animateHeaderDown();
          return;
        }

        // Scrolling a large delta down.
        if (!this.scrollingUp && (this.scrollDelta > this.vHeader.getHeight() / 2))
        {
          animateHeaderDown();
          return;
        }

        // Header's top has moved down more than half its height while scrolling down.
        if (!this.scrollingUp && (this.vHeader.getY() < -(this.vHeader.getHeight() / 2)))
        {
          animateHeaderUp();
          return;
        }

        // Header's top has moved down less than half its height while scrolling down.
        if (!this.scrollingUp && (this.vHeader.getY() > -(this.vHeader.getHeight() / 2)))
        {
          animateHeaderDown();
          return;
        }

      }
      else
      {
        if (this.animatorHeader != null)
          this.animatorHeader.cancel();
      }
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "processHeaderScrollPosition: " + ex.toString());
    }
  }


  /**
   * Animates the header up to be hidden.
   */
  public void animateHeaderUp()
  {
    try
    {
      if (this.animatorHeader != null)
        this.animatorHeader.cancel();

      if (this.vHeader.getY() == this.scrollerMinTopY)
        return;

      headerAnimating = true;
      this.headerInitialY = -this.vHeader.getHeight();
      PropertyValuesHolder pvhYBar = PropertyValuesHolder.ofFloat("y", this.vHeader.getY(), -this.vHeader.getHeight());
      this.animatorHeader = ObjectAnimator.ofPropertyValuesHolder(this.vHeader, pvhYBar);
      this.animatorHeader.setInterpolator(new LinearInterpolator());
      this.animatorHeader.setDuration(200);
      this.animatorHeader.addListener(animListenerHeader);
      this.cancelHeaderAnimation = false;
      this.animatorHeader.start();

      if (this.scrollableContent != null)
        this.scrollableContent.iJBHeaderScroll.onHeaderAfterAnimation(true, this.scrollDelta);
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "animateHeaderUp: " + ex.toString());
    }
  }


  /**
   * Animates the header down to be visible.
   */
  public void animateHeaderDown()
  {
    try
    {
      if (this.animatorHeader != null)
        this.animatorHeader.cancel();

      if (this.vHeader.getY() == this.scrollerMaxTopY)
        return;

      headerAnimating = true;
      this.headerInitialY = 0;
      PropertyValuesHolder pvhYBar = PropertyValuesHolder.ofFloat("y", this.vHeader.getY(), 0);
      this.animatorHeader = ObjectAnimator.ofPropertyValuesHolder(this.vHeader, pvhYBar);
      this.animatorHeader.setInterpolator(new LinearInterpolator());
      this.animatorHeader.setDuration(200);
      this.animatorHeader.addListener(animListenerHeader);
      this.cancelHeaderAnimation = false;
      this.animatorHeader.start();

      if (this.scrollableContent != null)
        this.scrollableContent.iJBHeaderScroll.onHeaderAfterAnimation(false, this.scrollDelta);
    }
    catch (Exception ex)
    {
      Log.e(LOG_TAG, "animateHeaderDown: " + ex.toString());
    }
  }


  /**
   * The animation listener. Needed to know when the animation should be canceled.
   */
  private Animator.AnimatorListener animListenerHeader = new Animator.AnimatorListener()
  {
    @Override
    public void onAnimationStart(Animator animation)
    {
      if (cancelHeaderAnimation)
        animatorHeader.cancel();
    }

    @Override
    public void onAnimationEnd(Animator animation)
    {
      headerAnimating = false;
      cancelHeaderAnimation = false;
    }

    @Override
    public void onAnimationCancel(Animator animation)
    {
      cancelHeaderAnimation = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation)
    {
    }
  };


  public interface IJBHeaderScroll
  {
    void onResize(float top);

    int onHeaderBeforeAnimation(boolean scrollingUp, float scrollDelta);

    void onHeaderAfterAnimation(boolean animatedUp, float scrollDelta);
  }


  private class ScrollableContent
  {
    public View vContentArea;
    public IJBHeaderScroll iJBHeaderScroll;
    public float originalHeight;

    public ScrollableContent(View vContentArea, IJBHeaderScroll iJBHeaderScroll)
    {
      this.vContentArea = vContentArea;
      this.iJBHeaderScroll = iJBHeaderScroll;
      this.originalHeight = vContentArea.getHeight();
    }
  }
}

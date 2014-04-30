package jianshu.io.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

public class ObservableWebView extends WebView
{
  private OnScrollChangedCallback mOnScrollChangedCallback;
  private int threshold;

  public ObservableWebView(final Context context)
  {
    super(context);
  }

  public ObservableWebView(final Context context, final AttributeSet attrs)
  {
    super(context, attrs);
  }

  public ObservableWebView(final Context context, final AttributeSet attrs, final int defStyle)
  {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt)
  {
    super.onScrollChanged(l, t, oldl, oldt);
    int height = getHeight();
    int range = computeVerticalScrollRange();
    if(threshold == 0) {
      threshold = (int) (range - height * 1.2);
    }
    boolean isAtTheEnd = t >= threshold;
    Log.d("onscroll", String.format("t: %d, h:%d, r: %d", t, height, range));
    if(mOnScrollChangedCallback != null){
        mOnScrollChangedCallback.onScrollChanged(isAtTheEnd);
    }
  }

  public OnScrollChangedCallback getOnScrollChangedCallback()
  {
    return mOnScrollChangedCallback;
  }

  public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback)
  {
    mOnScrollChangedCallback = onScrollChangedCallback;
  }

  /**
   * Impliment in the activity/fragment/view that you want to listen to the webview
   */
  public interface OnScrollChangedCallback
  {
    void onScrollChanged(boolean isAtThenEnd);
  }
}

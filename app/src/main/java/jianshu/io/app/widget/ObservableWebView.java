package jianshu.io.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ObservableWebView extends WebView
{
  private OnScrollChangedCallback mOnScrollChangedCallback;
  private int[] realSize = new int[2];

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
    if(mOnScrollChangedCallback != null){
        mOnScrollChangedCallback.onScrollChanged(l, t, oldl, oldt);
    }
  }

  public int[] getRealSize() {
    this.realSize[0] = computeHorizontalScrollRange();
    this.realSize[1] = computeVerticalScrollRange();
    return this.realSize;
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
    void onScrollChanged(final int l, final int t, final int oldl, final int oldt);
//    void onScrollChanged(boolean isAtThenEnd);
  }
}

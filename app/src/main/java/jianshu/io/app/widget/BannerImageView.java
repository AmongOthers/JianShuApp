package jianshu.io.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2014/8/10.
 */
public class BannerImageView extends ImageView {

  public BannerImageView(Context context) {
    super(context);
  }

  public BannerImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public BannerImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int mode = MeasureSpec.getMode(widthMeasureSpec);
    if (mode == MeasureSpec.EXACTLY) {
      int width = MeasureSpec.getSize(widthMeasureSpec);
      int height = width * 9 / 16;
      setMeasuredDimension(width, height);
    }
  }
}

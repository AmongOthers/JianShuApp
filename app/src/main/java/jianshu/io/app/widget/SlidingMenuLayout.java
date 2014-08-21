package jianshu.io.app.widget;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/8/3.
 */
public class SlidingMenuLayout extends RelativeLayout {
  static final float OPEN_THRESHOLD = 0.4f;

  View mContent;
  View mMenu;
  MenuLayout mMenuContainer;

  public SlidingMenuLayout(Context context) {
    super(context);
  }

  public SlidingMenuLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SlidingMenuLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void init(final Activity activity, View menu) {
    TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
    int background = a.getResourceId(0, 0);
    a.recycle();
    ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
    ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
    decorChild.setBackgroundResource(background);
    decor.removeView(decorChild);
    mContent = decorChild;
    mMenu = menu;
    LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    addView(mContent, params);
    mMenuContainer = new MenuLayout(activity, mMenu);
    mMenuContainer.addView(mMenu, params);
    addView(mMenuContainer, params);
    decor.addView(this);
  }

  public void open() {
    mMenuContainer.open();
  }

  public void close() {
    mMenuContainer.close();
  }

  public boolean isOpen() {
    return mMenuContainer.mOpenPercent == 1.0f;
  }

  public void setMenuListener(MenuListener listener) {
    mMenuContainer.setMenuListener(listener);
  }

  public void toggle() {
    if (isOpen()) {
      close();
    } else {
      open();
    }
  }

  public interface MenuListener {
    void onPercentChanged(final float percent);
  }

  @Override
  protected boolean fitSystemWindows(Rect insets) {
    int leftPadding = insets.left;
    int rightPadding = insets.right;
    int topPadding = insets.top;
    int bottomPadding = insets.bottom;
    setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
    return true;
  }

  class MenuLayout extends RelativeLayout implements OnDragListener {
    int mWidthOffset;
    int mWidth;
    int mHeight;
    int mMenuWidth;
    int mRight;
    int mTouchSlop;
    View mMenu;
    MenuListener mListener;
    float mOpenPercent = 0f;
    float mLastMotionX;
    float mLastMotionY;
    float mLastDragX;
    boolean mIsBeingDragged;
    Paint mFadePaint = new Paint();
    Drawable mShadowRight;

    public MenuLayout(Context context, View menu) {
      super(context);
      mMenu = menu;
      mWidthOffset = (int) getResources().getDimension(R.dimen.menu_offset);
      //a touch event on the edge should be considered to open the menu
      mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 2;
      //引用了SwipeBackLayout的阴影
      mShadowRight = getResources().getDrawable(R.drawable.shadow_right);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
      final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
      if (action == MotionEvent.ACTION_DOWN) {
        float x = ev.getX();
        if (mMenuContainer.isShowing() && !mMenuContainer.hit(x)) {
          return true;
        } else if (!mMenuContainer.isShowing() && x <= mTouchSlop) {
          setOpenPercent(0.1f);
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
      if (action == MotionEvent.ACTION_DOWN) {
        Log.d("drag", "action_down");
        //有可能mIsBeingDragged没有被重置，那么在一个新的ACTION_DOWN触发的时候，可以重置它
        mIsBeingDragged = false;
        mLastMotionX = MotionEventCompat.getX(event, 0);
        mLastMotionY = MotionEventCompat.getY(event, 0);
        if (!mMenuContainer.isShowing()) {
          if (mLastMotionX > mTouchSlop) {
            return false;
          }
        }
        return true;
      } else if (action == MotionEvent.ACTION_MOVE) {
        Log.d("drag", "action_move");
        if (!mIsBeingDragged) {
          determineDrag(event);
        }
        return true;
      } else if (action == MotionEvent.ACTION_UP) {
        Log.d("drag", "action_up");
        if (mMenuContainer.isShowing() && mMenuContainer.mOpenPercent <= 0.5f) {
          mMenuContainer.close();
        }
      }
      return false;
    }

    void determineDrag(MotionEvent event) {
      final float x = MotionEventCompat.getX(event, 0);
      final float dx = x - mLastMotionX;
      if (mOpenPercent == 1f && dx > 0) {
        return;
      }
      final float xDiff = Math.abs(dx);
      final float y = MotionEventCompat.getY(event, 0);
      final float dy = y - mLastMotionY;
      final float yDiff = Math.abs(dy);
      if (xDiff > mTouchSlop && xDiff > yDiff) {
        mIsBeingDragged = true;
        this.setOnDragListener(mMenuContainer);
        this.startDrag(null, new DragShadowBuilder(), null, 0);
        mLastDragX = x;
//        mLastMotionX = x;
//        mLastMotionY = y;
      }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
      if (mWidth == 0) {
        mWidth = r - 1;
        mHeight = b - t;
        mMenuWidth = mWidth - mWidthOffset;
      }
      int left = (int) (0 - (mMenuWidth) * (1 - mOpenPercent));
      mRight = left + mMenuWidth;
      mMenu.layout(left, 0, mRight, b - t);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
      super.dispatchDraw(canvas);
      mShadowRight.setBounds(mRight, 0,
          mRight + mShadowRight.getIntrinsicWidth(), mHeight);
      mShadowRight.setAlpha((int) (mOpenPercent * 255));
      mShadowRight.draw(canvas);
      int alpha = (int) (0.5 * 255 * Math.abs(mOpenPercent));
      mFadePaint.setColor(Color.argb(alpha, 0, 0, 0));
      //避免边缘有白边
      canvas.drawRect(mRight, 0, mWidth + 1, mHeight, mFadePaint);
    }

    public void setOpenPercent(float percent) {
      mOpenPercent = percent;
      requestLayout();
      invalidate();
    }

    public void setMenuListener(MenuListener listener) {
      mListener = listener;
    }

    public boolean isShowing() {
      return mOpenPercent > 0f;
    }

    public boolean hit(float x) {
      return x <= mRight;
    }

    public void open() {
      animOpen();
    }

    public void close() {
      animClose();
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
      final int action = event.getAction();
      switch (action) {
        case DragEvent.ACTION_DRAG_STARTED:
          return true;
        case DragEvent.ACTION_DRAG_LOCATION:
          float percent = 0f;
          float x = event.getX();
          float xDiff = x - mLastDragX;
          mLastDragX = x;
          percent = mOpenPercent + xDiff / mMenuWidth;
          if (percent > 1f) {
            percent = 1f;
          }
          setOpenPercent(percent);
          if (mListener != null) {
            mListener.onPercentChanged(percent);
          }
          return true;
        case DragEvent.ACTION_DRAG_ENDED:
          mIsBeingDragged = false;
          if (mOpenPercent >= OPEN_THRESHOLD) {
            open();
          } else {
            close();
          }
          return true;
        default:
          return true;
      }
    }

    void animOpen() {
      ObjectAnimator anim = ObjectAnimator.ofFloat(this, "openPercent", mOpenPercent, 1f);
      anim.setDuration(getResources().getInteger(R.integer.menu_anim_ms));
      anim.start();
    }

    void animClose() {
      ObjectAnimator anim = ObjectAnimator.ofFloat(this, "openPercent", mOpenPercent, 0f);
      anim.setDuration(getResources().getInteger(R.integer.menu_anim_ms));
      anim.start();
    }
  }
}

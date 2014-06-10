package jianshu.io.app.card;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;
import jianshu.io.app.model.UpdateItem;

/**
 * Created by Administrator on 2014/6/3.
 */
public abstract class TimeStreamCard extends Card {
  protected FinalBitmap mFb;
  protected UpdateItem mItem;
  protected ImageView mAvatar;
  protected TextView mTime;
  private AnimatorSet rightFlip;
  private AnimatorSet leftFlip;
  private GestureDetector mGestureDetector;
  private GestureDetector.SimpleOnGestureListener mGestureListener;

  public TimeStreamCard(Context context, UpdateItem item, FinalBitmap fb, int innerLayout) {
    super(context, innerLayout);
    mItem = item;
    mFb = fb;
    rightFlip = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.anim.card_flip_right);
    leftFlip = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.anim.card_flip_left);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
    View temp = view.findViewById(R.id.timestream_avatar);
    if (temp != null) {
      mAvatar = (ImageView) temp;
      if (mItem.getAvatarUrl() != null) {
        mFb.display(mAvatar, mItem.getAvatarUrl());
      }
    }
    mTime = (TextView) view.findViewById(R.id.timestream_time);
    mTime.setText(mItem.getTime());
    final View parentView = (View) parent.getParent();
    rightFlip.setTarget(parentView);
    leftFlip.setTarget(parentView);

    mGestureListener = new GestureDetector.SimpleOnGestureListener() {

      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        int width = parentView.getWidth();
        float x = e.getX();
        boolean isPressingRight = x >= width / 2;
        if (isPressingRight) {
          leftFlip.start();
        } else {
          rightFlip.start();
        }
        return false;
      }
    };
    mGestureDetector = new GestureDetector(getContext(), mGestureListener);
    //注意不是绑定到parentView上
    parent.setOnTouchListener(new View.OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
      }
    });
  }

  public void onClick(Activity activity) {

  }
}

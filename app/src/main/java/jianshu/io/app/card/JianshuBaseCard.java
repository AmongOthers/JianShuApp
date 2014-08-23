package jianshu.io.app.card;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.shamanland.fonticon.FontIconDrawable;

import net.tsz.afinal.FinalBitmap;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;
import jianshu.io.app.model.ArticleItem;

/**
 * Created by Administrator on 2014/5/11.
 */
public class JianshuBaseCard extends Card {

  protected FinalBitmap fb;
  protected ArticleItem item;

  protected static Resources resources;
  protected static Drawable heartDrawable;
  protected static Drawable heartEmptyDrawable;
  protected static int GrayColor = -1;
  protected static int JianshuColor = -1;
  private AnimatorSet rightFlip;
  private AnimatorSet leftFlip;
  private GestureDetector mGestureDetector;
  private GestureDetector.SimpleOnGestureListener mGestureListener;

  public JianshuBaseCard(final Context context, ArticleItem item, FinalBitmap fb, int layoutId) {
    super(context, layoutId);

    rightFlip = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.anim.card_flip_right);
    leftFlip = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.anim.card_flip_left);

    this.item = item;
    this.fb = fb;
    if(JianshuBaseCard.resources == null) {
      JianshuBaseCard.resources = context.getResources();
    }
    if(JianshuBaseCard.heartDrawable == null) {
      JianshuBaseCard.heartDrawable = FontIconDrawable.inflate(JianshuBaseCard.resources, R.xml.fa_heart);
    }
    if(JianshuBaseCard.heartEmptyDrawable == null) {
      JianshuBaseCard.heartEmptyDrawable = FontIconDrawable.inflate(JianshuBaseCard.resources, R.xml.fa_heart_o);
    }
    if(JianshuBaseCard.GrayColor == -1) {
      JianshuBaseCard.GrayColor = JianshuBaseCard.resources.getColor(R.color.textGray);
    }
    if(JianshuBaseCard.JianshuColor == -1) {
      JianshuBaseCard.JianshuColor = JianshuBaseCard.resources.getColor(R.color.jianshu);
    }
  }

  public ArticleItem getItem() {
    return this.item;
  }

//  @Override
//  public void setupInnerViewElements(ViewGroup parent, View view) {
//    super.setupInnerViewElements(parent, view);
//
//    final View parentView = (View) parent.getParent();
//    rightFlip.setTarget(parentView);
//    leftFlip.setTarget(parentView);
//
//    mGestureListener = new GestureDetector.SimpleOnGestureListener() {
//
//      @Override
//      public boolean onSingleTapUp(MotionEvent e) {
//        int width = parentView.getWidth();
//        float x = e.getX();
//        boolean isPressingRight = x >= width / 2;
//        if(isPressingRight) {
//          leftFlip.start();
//        } else {
//          rightFlip.start();
//        }
//        return false;
//      }
//    };
//    mGestureDetector = new GestureDetector(getContext(), mGestureListener);
//    //注意不是绑定到parentView上
//    parent.setOnTouchListener(new View.OnTouchListener() {
//
//      @Override
//      public boolean onTouch(View v, MotionEvent event) {
//        return mGestureDetector.onTouchEvent(event);
//      }
//    });
//  }
}
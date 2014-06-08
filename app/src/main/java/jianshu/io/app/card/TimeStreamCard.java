package jianshu.io.app.card;

import android.content.Context;
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
public abstract class TimeStreamCard extends Card{
  protected FinalBitmap mFb;
  protected UpdateItem mItem;
  protected ImageView mAvatar;
  protected TextView mTime;

  public TimeStreamCard(Context context, UpdateItem item, FinalBitmap fb, int innerLayout) {
    super(context, innerLayout);
    mItem = item;
    mFb = fb;
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
    View temp = view.findViewById(R.id.timestream_avatar);
    if (temp != null) {
      mAvatar = (ImageView)temp;
      if (mItem.getAvatarUrl() != null) {
        mFb.display(mAvatar, mItem.getAvatarUrl());
      }
    }
    mTime = (TextView)view.findViewById(R.id.timestream_time);
    mTime.setText(mItem.getTime());
  }
}

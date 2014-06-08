package jianshu.io.app.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.R;
import jianshu.io.app.model.UserUpdateItem;

;

/**
 * Created by Administrator on 2014/6/7.
 */
public class UserUpdateCard extends TimeStreamCard {

  TextView mUser;
  TextView mTarget;

  public UserUpdateCard(Context context, UserUpdateItem item, FinalBitmap fb, int innerLayout) {
    super(context, item, fb, innerLayout);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
    UserUpdateItem item = (UserUpdateItem)mItem;
    mUser = (TextView)view.findViewById(R.id.timestream_user);
    mUser.setText(item.getUser());
    String action = item.getAction();
    mTarget = (TextView)view.findViewById(R.id.timestream_target);
    mTarget.setText(action);
  }
}

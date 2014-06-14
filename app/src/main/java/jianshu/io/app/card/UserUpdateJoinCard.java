package jianshu.io.app.card;

import android.content.Context;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.R;
import jianshu.io.app.model.UserUpdateItem;

/**
 * Created by Administrator on 2014/6/13.
 */
public class UserUpdateJoinCard extends UserUpdateCard{
  public UserUpdateJoinCard(Context context, UserUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.user_update_join_content);
  }
}

package jianshu.io.app.card;

import android.content.Context;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.R;
import jianshu.io.app.model.UserUpdateFollowUpdateItem;

/**
 * Created by Administrator on 2014/6/7.
 */
public class UserUpdateFollowCard extends UserUpdateCard {
  public UserUpdateFollowCard(Context context, UserUpdateFollowUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.user_update_follow_card_content);
  }
}

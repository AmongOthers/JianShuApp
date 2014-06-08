package jianshu.io.app.card;

import android.content.Context;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.R;
import jianshu.io.app.model.UserUpdateLikeUpdateItem;

/**
 * Created by Administrator on 2014/6/7.
 */
public class UserUpdateLikeCard extends UserUpdateCard {
  public UserUpdateLikeCard(Context context, UserUpdateLikeUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.user_update_like_card_content);
  }
}

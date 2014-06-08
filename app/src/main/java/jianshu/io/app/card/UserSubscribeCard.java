package jianshu.io.app.card;

import android.content.Context;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.R;
import jianshu.io.app.model.UserSubscribeUpdateItem;

/**
 * Created by Administrator on 2014/6/8.
 */
public class UserSubscribeCard extends UserUpdateCard{
  public UserSubscribeCard(Context context, UserSubscribeUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.user_update_subscribe_card_content);
  }
}

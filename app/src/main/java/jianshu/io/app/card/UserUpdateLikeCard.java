package jianshu.io.app.card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.ArticleActivity;
import jianshu.io.app.R;
import jianshu.io.app.model.UserUpdateLikeUpdateItem;

/**
 * Created by Administrator on 2014/6/7.
 */
public class UserUpdateLikeCard extends UserUpdateCard {
  public UserUpdateLikeCard(Context context, UserUpdateLikeUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.user_update_like_card_content);
  }

  @Override
  public void onClick(Activity activity) {
    UserUpdateLikeUpdateItem item = (UserUpdateLikeUpdateItem)mItem;
    Intent intent = new Intent(activity, ArticleActivity.class);
    intent.putExtra("url", item.getUrl());
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.slide_in_left, 0);
  }
}

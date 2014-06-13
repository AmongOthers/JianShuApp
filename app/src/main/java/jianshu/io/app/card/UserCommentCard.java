package jianshu.io.app.card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.ArticleActivity;
import jianshu.io.app.R;
import jianshu.io.app.model.UserCommentUpdateItem;

/**
 * Created by Administrator on 2014/6/7.
 */
public class UserCommentCard extends UserUpdateCard {

  TextView mContent;

  public UserCommentCard(Context context, UserCommentUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.comment_card_content);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
    mContent = (TextView)view.findViewById(R.id.timestream_comment);
    mContent.setText(((UserCommentUpdateItem)mItem).getContent());
  }

  @Override
  public void onClick(Activity activity) {
    UserCommentUpdateItem item = (UserCommentUpdateItem)mItem;
    Intent intent = new Intent(activity, ArticleActivity.class);
    intent.putExtra("url", item.getUrl());
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.slide_in_left, 0);
  }
}

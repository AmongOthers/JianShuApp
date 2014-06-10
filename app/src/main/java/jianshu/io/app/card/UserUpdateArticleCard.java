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
import jianshu.io.app.model.UserUpdateArticleUpdateItem;

/**
 * Created by Administrator on 2014/6/7.
 */
public class UserUpdateArticleCard extends TimeStreamCard {
  TextView mTitle;
  TextView mSummary;

  public UserUpdateArticleCard(Context context, UserUpdateArticleUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.user_update_article_card_content);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
    mTitle = (TextView)view.findViewById(R.id.timestream_title);
    mTitle.setText(((UserUpdateArticleUpdateItem)mItem).getTitle());
    mSummary = (TextView)view.findViewById(R.id.timestream_summary);
    mSummary.setText(((UserUpdateArticleUpdateItem) mItem).getSummary());
  }

  @Override
  public void onClick(Activity activity) {
    UserUpdateArticleUpdateItem item = (UserUpdateArticleUpdateItem)mItem;
    Intent intent = new Intent(activity, ArticleActivity.class);
    intent.putExtra("url", item.getUrl());
    intent.putExtra("title", item.getTitle());
    intent.putExtra("summary", item.getSummary());
    intent.putExtra("author", "");
    activity.startActivity(intent);
    activity.overridePendingTransition(R.anim.slide_in_left, 0);
  }
}

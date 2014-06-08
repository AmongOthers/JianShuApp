package jianshu.io.app.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

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
    mSummary.setText(((UserUpdateArticleUpdateItem)mItem).getSummary());
  }
}

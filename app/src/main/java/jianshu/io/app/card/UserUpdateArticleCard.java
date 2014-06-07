package jianshu.io.app.card;

import android.content.Context;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/6/7.
 */
public class UserUpdateArticleCard extends Card {
  public UserUpdateArticleCard(Context context) {
    super(context, R.layout.user_update_article_card_content);
  }

  public UserUpdateArticleCard(Context context, int innerLayout) {
    super(context, innerLayout);
  }
}

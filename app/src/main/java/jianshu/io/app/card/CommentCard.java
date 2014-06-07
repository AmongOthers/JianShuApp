package jianshu.io.app.card;

import android.content.Context;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/6/7.
 */
public class CommentCard extends Card {
  public CommentCard(Context context) {
    super(context, R.layout.comment_card_content);
  }

  public CommentCard(Context context, int innerLayout) {
    super(context, innerLayout);
  }
}

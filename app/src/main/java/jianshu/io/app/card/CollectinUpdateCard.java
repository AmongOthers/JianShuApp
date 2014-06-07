package jianshu.io.app.card;

import android.content.Context;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/6/7.
 */
public class CollectinUpdateCard extends Card {
  public CollectinUpdateCard(Context context) {
    super(context, R.layout.collection_update_card_content);
  }

  public CollectinUpdateCard(Context context, int innerLayout) {
    super(context, innerLayout);
  }
}

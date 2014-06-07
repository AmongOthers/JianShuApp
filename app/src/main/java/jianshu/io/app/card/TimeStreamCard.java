package jianshu.io.app.card;

import android.content.Context;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/6/3.
 */
public class TimeStreamCard extends Card{
  public TimeStreamCard(Context context) {
    super(context, R.layout.staggered_content);
  }

  public TimeStreamCard(Context context, int innerLayout) {
    super(context, innerLayout);
  }
}

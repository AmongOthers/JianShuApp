package jianshu.io.app.adapter;

import android.content.Context;

import java.util.List;

import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapter;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2014/6/7.
 */
public class TimeStreamCardArrayAdapter extends CardGridStaggeredArrayAdapter {
  /**
   * Constructor
   *
   * @param context The current context.
   * @param cards   The cards to represent in the ListView.
   */
  public TimeStreamCardArrayAdapter(Context context, List<Card> cards) {
    super(context, cards);
  }
}

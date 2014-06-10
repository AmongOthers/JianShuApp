package jianshu.io.app.adapter;

import android.content.Context;

import java.util.List;

import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapter;
import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Administrator on 2014/6/7.
 */
public class TimeStreamCardArrayAdapter extends CardGridStaggeredArrayAdapter {

  private CardClickListener mCardClickListener;
  private String mSession;

  public TimeStreamCardArrayAdapter(Context context, List<Card> cards) {
    super(context, cards);
  }

  public void onClick(Card card) {
    if (mCardClickListener != null) {
      mCardClickListener.onClick(card);
    }
  }

  public interface CardClickListener {
    void onClick(Card card);
  }

  public void setCardClickListener(CardClickListener cardClickListener) {
    mCardClickListener = cardClickListener;
  }

  public String getSession() {
    return mSession;
  }

  public void setSession(String session) {
    mSession = session;
  }
}

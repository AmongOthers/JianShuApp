package jianshu.io.app.adapter;

import android.content.Context;

import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

/**
 * Created by Administrator on 2014/5/16.
 */
public class JianshuCardArrayAdapter extends CardArrayAdapter {

  private CardClickListener mCardClickListener;
  private String mUserToken;

  public JianshuCardArrayAdapter(Context context, List<Card> cards) {
    super(context, cards);
  }

  public void onClick(Card card) {
    if(mCardClickListener != null) {
      mCardClickListener.onClick(card);
    }
  }

  public interface CardClickListener {
    void onClick(Card card);
  }

  public void setCardClickListener(CardClickListener cardClickListener) {
    mCardClickListener = cardClickListener;
  }

  public String getUserToken() {
    return mUserToken;
  }

  public void setUserToken(String userToken) {
    mUserToken = userToken;
  }
}

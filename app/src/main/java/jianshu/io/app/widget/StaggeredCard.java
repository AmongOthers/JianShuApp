package jianshu.io.app.widget;

import android.content.Context;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/6/3.
 */
public class StaggeredCard extends Card{
  public StaggeredCard(Context context) {
    super(context, R.layout.staggered_content);
  }

  public StaggeredCard(Context context, int innerLayout) {
    super(context, innerLayout);
  }
}

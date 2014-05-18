package jianshu.io.app.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.cardslib.library.internal.CardExpand;
import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/5/16.
 */
public class HotCardExpand extends CardExpand {
  public HotCardExpand(Context context) {
    super(context, R.layout.hotcard_expand);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
  }
}

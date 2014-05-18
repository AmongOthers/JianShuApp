package jianshu.io.app.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import it.gmariotti.cardslib.library.internal.CardHeader;
import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/5/18.
 */
public class HotCardHeader extends CardHeader {
  public HotCardHeader(Context context) {
    super(context, R.layout.hotcard_header_inner);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
  }
}

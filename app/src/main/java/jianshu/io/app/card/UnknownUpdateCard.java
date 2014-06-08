package jianshu.io.app.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.R;
import jianshu.io.app.model.UnknownUpdateItem;

/**
 * Created by Administrator on 2014/6/8.
 */

public class UnknownUpdateCard extends TimeStreamCard {

  private TextView mText;

  public UnknownUpdateCard(Context context, UnknownUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.unknown_update_card_content);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
    mText = (TextView)view.findViewById(R.id.timestream_unknown);
    mText.setText(((UnknownUpdateItem)mItem).getText());
  }
}




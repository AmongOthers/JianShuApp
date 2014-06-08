package jianshu.io.app.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import jianshu.io.app.R;
import jianshu.io.app.model.CollectionUpdateItem;

/**
 * Created by Administrator on 2014/6/7.
 */
public class CollectinUpdateCard extends TimeStreamCard {

  TextView mCollection;
  TextView mTarget;

  public CollectinUpdateCard(Context context, CollectionUpdateItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.collection_update_card_content);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);
    CollectionUpdateItem item = (CollectionUpdateItem)mItem;
    mCollection = (TextView)view.findViewById(R.id.timestream_collection);
    mCollection.setText(item.getCollection());
    String action = item.getAction();
    mTarget = (TextView)view.findViewById(R.id.timestream_target);
    mTarget.setText(action);
  }
}

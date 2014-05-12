package jianshu.io.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by Administrator on 2014/5/11.
 */
public class EndlessCardListView extends CardListView implements AbsListView.OnScrollListener{

  private boolean isLoading;
  private EndlessListener listener;
  private View footer;

  public EndlessCardListView(Context context) {
    super(context);
    init();
  }

  public EndlessCardListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public EndlessCardListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    this.setOnScrollListener(this);
  }

  @Override
  public void onScrollStateChanged(AbsListView absListView, int i) {

  }

  @Override
  public void onScroll(AbsListView absListView, int firstVisibleItem,
                       int visibleItemCount, int totalItemCount) {
    if(getAdapter() == null) {
      return;
    }

    if(getAdapter().getCount() == 0) {
      return;
    }

    int l = visibleItemCount + firstVisibleItem;
    if(l >= totalItemCount && !isLoading && !this.listener.isAtTheEnd()) {
      this.addFooterView(this.footer);
      this.isLoading = true;
      this.listener.onScrollEnd();
    }
  }

  public void setFooter(View view) {
    this.footer = view;
  }

  public void setListener(EndlessListener listener) {
    this.listener = listener;
  }

  public void notifyNewDataLoaded() {
    this.removeFooterView(this.footer);
    this.isLoading = false;
  }

  @Override
  public void setAdapter(CardArrayAdapter adapter) {
    this.addFooterView(this.footer);
    super.setAdapter(adapter);
    this.removeFooterView(this.footer);
  }
}

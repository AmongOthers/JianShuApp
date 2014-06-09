package jianshu.io.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import it.gmariotti.cardslib.library.extra.staggeredgrid.internal.CardGridStaggeredArrayAdapter;
import it.gmariotti.cardslib.library.extra.staggeredgrid.view.CardGridStaggeredView;

/**
 * Created by Administrator on 2014/6/9.
 */
public class EndlessStaggeredGridView extends CardGridStaggeredView implements AbsListView.OnScrollListener {

  private boolean isLoading;
  private EndlessListener listener;
  private View footer;

  public EndlessStaggeredGridView(Context context) {
    super(context);
    init();
  }

  public EndlessStaggeredGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public EndlessStaggeredGridView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    this.setOnScrollListener(this);
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {

  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    if (getAdapter() == null) {
      return;
    }

    if (getAdapter().getCount() == 0) {
      return;
    }

    int l = visibleItemCount + firstVisibleItem;
    if (l >= totalItemCount && !isLoading && !this.listener.isAtTheEnd()) {
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
  public void setAdapter(CardGridStaggeredArrayAdapter adapter) {
    this.addFooterView(this.footer);
    super.setAdapter(adapter);
    this.removeFooterView(this.footer);
  }
}

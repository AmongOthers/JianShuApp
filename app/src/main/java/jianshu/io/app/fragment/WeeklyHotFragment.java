package jianshu.io.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import jianshu.io.app.ArticleActivity;
import jianshu.io.app.R;
import jianshu.io.app.model.RecommendationItem;
import jianshu.io.app.model.datapool.DataPool;
import jianshu.io.app.model.datapool.WeeklyHotPageDataPool;
import jianshu.io.app.util.RecommendationAsyncTask;
import jianshu.io.app.widget.EndlessCardListView;
import jianshu.io.app.widget.EndlessListener;
import jianshu.io.app.widget.HotCard;
import jianshu.io.app.widget.LoadingTextView;

/**
 * Created by Administrator on 2014/5/7.
 */
public class WeeklyHotFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, EndlessListener {

  public static WeeklyHotFragment newInstance() {
    return new WeeklyHotFragment();
  }

  FinalBitmap fb;
  EndlessCardListView mListView;
  SwipeRefreshLayout mRefreshLayout;
  CardArrayAdapter mAdapter;
  LoadingTextView mFooter;
  DataPool mPool;
  View mEmptyView;
  boolean mIsEmpty;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.hot, null);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final Activity activity = getActivity();

    this.fb = FinalBitmap.create(activity);

    mPool = new WeeklyHotPageDataPool();

    mEmptyView = getActivity().getLayoutInflater().inflate(R.layout.empty_pull, null);
    mListView = (EndlessCardListView) (activity.findViewById(R.id.hotlist));
    mListView.setListener(this);
    mFooter = (LoadingTextView) activity.getLayoutInflater().inflate(R.layout.footer, null);
    mListView.setFooter(mFooter);
    mAdapter = new CardArrayAdapter(getActivity(), new ArrayList<Card>());
    mListView.setAdapter(mAdapter);

    mRefreshLayout = (SwipeRefreshLayout) (activity.findViewById(R.id.ptr_layout));
    mRefreshLayout.setColorScheme(R.color.jianshu, R.color.card_list_gray, R.color.jianshu, R.color.card_list_gray);
    mRefreshLayout.setOnRefreshListener(this);
    mRefreshLayout.setRefreshing(true);
    onRefresh();
  }

  private Card[] initCard(RecommendationItem[] data) {
    Card[] result = new Card[data.length];
    int i = 0;
    for(RecommendationItem item : data) {
      HotCard card = new HotCard(this.getActivity(), item, this.fb);
      result[i++] = card;
      card.addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, new Card.OnCardClickListener() {
        @Override
        public void onClick(Card card, View view) {
          RecommendationItem item = ((HotCard)card).getItem();
          Intent intent = new Intent(getActivity(), ArticleActivity.class);
          intent.putExtra("url", item.getUrl());
          intent.putExtra("title", item.getTitle());
          intent.putExtra("summary", item.getSummary());
          intent.putExtra("author", item.getAuthor());
          startActivity(intent);
          getActivity().overridePendingTransition(R.anim.slide_in_left, 0);
        }
      });
    }
    return result;
  }

  @Override
  public void onRefresh() {
    new RecommendationAsyncTask(true, this.mPool, new RecommendationAsyncTask.OnPostExecuteTask() {
      @Override
      public void run(RecommendationItem[] data) {
        mRefreshLayout.setRefreshing(false);
        if(data != null) {
          if(mIsEmpty) {
            mIsEmpty = false;
            mRefreshLayout.removeView(mEmptyView);
            mRefreshLayout.addView(mListView);
          } else {
            mAdapter.clear();
          }
          mAdapter.addAll(initCard(data));
        } else {
          Context context = WeeklyHotFragment.this.getActivity();
          if(context != null) {
            Toast.makeText(context, ":( 加载失败，请重试", Toast.LENGTH_LONG).show();
          }
          if(mAdapter.getCount() == 0 && !mIsEmpty) {
            mIsEmpty = true;
            mRefreshLayout.removeView(mListView);
            mRefreshLayout.addView(mEmptyView);
          }
        }
      }
    }).execute();
  }

  @Override
  public boolean isAtTheEnd() {
    return mPool.isAtTheEnd();
  }

  @Override
  public void onScrollEnd() {
    mFooter.startAnimation();
    new RecommendationAsyncTask(false, this.mPool, new RecommendationAsyncTask.OnPostExecuteTask() {
      @Override
      public void run(RecommendationItem[] data) {
        mFooter.endAnimation();
        mListView.notifyNewDataLoaded();
        if (data != null) {
          mAdapter.addAll(initCard(data));
        } else {
          Context context = WeeklyHotFragment.this.getActivity();
          if(context != null) {
            Toast.makeText(context, ":( 加载失败，请重试", Toast.LENGTH_LONG).show();
          }
        }
      }
    }).execute();
  }
}

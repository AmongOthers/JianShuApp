package jianshu.io.app.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import jianshu.io.app.ArticleActivity;
import jianshu.io.app.R;
import jianshu.io.app.adapter.RecommendationAdapter;
import jianshu.io.app.model.RecommendationItem;
import jianshu.io.app.model.datapool.DataPool;
import jianshu.io.app.model.datapool.HomePageDataPool;
import jianshu.io.app.util.RecommendationAsyncTask;
import jianshu.io.app.widget.EndlessListView;
import jianshu.io.app.widget.EndlessListener;
import jianshu.io.app.widget.LoadingTextView;

/**
 * Created by Administrator on 14-3-8.
 */
public class RecommendationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, EndlessListener {

  public static RecommendationFragment newInstance() {
    return new RecommendationFragment();
  }

  EndlessListView mListView;
  SwipeRefreshLayout mRefreshLayout;
  RecommendationAdapter mAdapter;
  LoadingTextView mFooter;
  DataPool mPool;
  View mEmptyView;
  boolean mIsEmpty;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.recommendation, null);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final Activity activity = getActivity();

    mPool = new HomePageDataPool();

    mEmptyView = getActivity().getLayoutInflater().inflate(R.layout.empty_pull, null);
    mListView = (EndlessListView) (activity.findViewById(R.id.list));
    mListView.setListener(this);
    mFooter = (LoadingTextView) activity.getLayoutInflater().inflate(R.layout.footer, null);
    mListView.setFooter(mFooter);
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        RecommendationItem item = mAdapter.getItem((int)l);
        Intent intent = new Intent(activity, ArticleActivity.class);
        intent.putExtra("url", item.getUrl());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("summary", item.getSummary());
        intent.putExtra("author", item.getAuthor());
        startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_left, 0);
      }
    });
    mAdapter = new RecommendationAdapter(getActivity(),
        R.layout.article_list_item);
    mListView.setAdapter(mAdapter);

    mRefreshLayout = (SwipeRefreshLayout) (activity.findViewById(R.id.ptr_layout));
    mRefreshLayout.setColorScheme(R.color.jianshu, R.color.white, R.color.jianshu, R.color.white);
    mRefreshLayout.setOnRefreshListener(this);
    mRefreshLayout.setRefreshing(true);
    onRefresh();
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
          mAdapter.addAll(data);
        } else {
          Context context = RecommendationFragment.this.getActivity();
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
          mAdapter.addAll(data);
        } else {
          Context context = RecommendationFragment.this.getActivity();
          if(context != null) {
            Toast.makeText(context, ":( 加载失败，请重试", Toast.LENGTH_LONG).show();
          }
        }
      }
    }).execute();
  }
}

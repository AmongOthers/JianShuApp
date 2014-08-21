package jianshu.io.app.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.tsz.afinal.FinalBitmap;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;
import jianshu.io.app.adapter.TimeStreamCardArrayAdapter;
import jianshu.io.app.card.CollectinUpdateCard;
import jianshu.io.app.card.TimeStreamCard;
import jianshu.io.app.card.UnknownUpdateCard;
import jianshu.io.app.card.UserCommentCard;
import jianshu.io.app.card.UserSubscribeCard;
import jianshu.io.app.card.UserUpdateArticleCard;
import jianshu.io.app.card.UserUpdateFollowCard;
import jianshu.io.app.card.UserUpdateJoinCard;
import jianshu.io.app.card.UserUpdateLikeCard;
import jianshu.io.app.model.CollectionUpdateItem;
import jianshu.io.app.model.JianshuSession;
import jianshu.io.app.model.StatePool;
import jianshu.io.app.model.UnknownUpdateItem;
import jianshu.io.app.model.UpdateItem;
import jianshu.io.app.model.UserCommentUpdateItem;
import jianshu.io.app.model.UserSubscribeUpdateItem;
import jianshu.io.app.model.UserUpdateArticleUpdateItem;
import jianshu.io.app.model.UserUpdateFollowUpdateItem;
import jianshu.io.app.model.UserUpdateJoinUpdateItem;
import jianshu.io.app.model.UserUpdateLikeUpdateItem;
import jianshu.io.app.model.datapool.DataPool;
import jianshu.io.app.model.datapool.TimeStreamDataPool;
import jianshu.io.app.util.DataPoolAsyncTask;
import jianshu.io.app.widget.EndlessListener;
import jianshu.io.app.widget.EndlessStaggeredGridView;
import jianshu.io.app.widget.LoadingTextView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimeStreamFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimeStreamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeStreamFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, EndlessListener, TimeStreamCardArrayAdapter.CardClickListener {

  OnFragmentInteractionListener mListener;
  TimeStreamCardArrayAdapter mAdapter;
  EndlessStaggeredGridView mContentView;
  FinalBitmap mFb;
  boolean mIsEmpty;
  SwipeRefreshLayout mRefreshLayout;
  View mHeader;
  LoadingTextView mFooter;
  TimeStreamDataPool mPool;
  View mEmptyView;
  private static final String LIST_STATE = "listState";
  Parcelable mListState;

  public static TimeStreamFragment newInstance() {
    TimeStreamFragment fragment = new TimeStreamFragment();
    return fragment;
  }

  public TimeStreamFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_timestream, container, false);
    Activity activity = getActivity();
    mFb = FinalBitmap.create(activity);
    Object[] states = StatePool.getInstance().getFragmentState(getActivity(), TimeStreamDataPool.TIMELINE_URL);
    mPool = (TimeStreamDataPool) states[0];
    mAdapter = (TimeStreamCardArrayAdapter) states[1];
    mAdapter.setCardClickListener(this);
    if (states[2] != null) {
      mListState = (Parcelable) states[2];
    }

    mEmptyView = getActivity().getLayoutInflater().inflate(R.layout.empty_pull, null);
    mContentView = (EndlessStaggeredGridView) view.findViewById(R.id.timestream_grid);
    mContentView.setListener(this);
    mHeader = activity.getLayoutInflater().inflate(R.layout.header, null);
    mFooter = (LoadingTextView) activity.getLayoutInflater().inflate(R.layout.footer, null);
    mContentView.addHeaderView(mHeader);
    mContentView.setFooter(mFooter);
    mContentView.setAdapter(mAdapter);

    mRefreshLayout = (SwipeRefreshLayout) (view.findViewById(R.id.ptr_layout));
    mRefreshLayout.setColorScheme(R.color.accent, R.color.accent, R.color.accent, R.color.accent);
    mRefreshLayout.setOnRefreshListener(this);

    return view;
  }

  @Override
  public void onViewStateRestored(Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (mListState != null) {
      mContentView.onRestoreInstanceState(mListState);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    //判断userInfo是否有变化
    boolean isUserInfoChanged = true;
    String cachedSession = mAdapter.getSession();
    String currentSession = JianshuSession.getsInstance().getSession();
    if ((currentSession == null && cachedSession == null) ||
        ((currentSession != null && cachedSession != null) && currentSession.equals(cachedSession))) {
      isUserInfoChanged = false;
    }
    mAdapter.setSession(currentSession);

    if (isUserInfoChanged || mAdapter.getCount() == 0) {
      mRefreshLayout.setRefreshing(true);
      onRefresh();
    } else {
      mAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    //如果第一个项目可见，那么恢复的时候将会跳到顶部，于是增加了一个1px的header解决这个BUG
    mListState = mContentView.onSaveInstanceState();
    StatePool.getInstance().putListViewState(TimeStreamDataPool.TIMELINE_URL, mListState);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onRefresh() {
    new DataPoolAsyncTask(true, this.mPool, new DataPoolAsyncTask.OnPostExecuteTask() {
      @Override
      public void run(Exception exception, Object[] data) {
        mRefreshLayout.setRefreshing(false);
        Context context = getActivity();
        if (context == null) {
          return;
        }
        if (exception != null) {
          if (exception instanceof DataPool.LoginRequiredException) {
            mListener.onLoginRequired();
            Toast.makeText(context, "请重新登陆", Toast.LENGTH_LONG).show();
            return;
          }
        }
        if (data != null) {
          if (mIsEmpty) {
            mIsEmpty = false;
            mRefreshLayout.removeView(mEmptyView);
            mRefreshLayout.addView(mContentView);
          } else {
            mAdapter.clear();
          }
          mAdapter.addAll(initCard(context, data));
        } else {
          if (context != null) {
            Toast.makeText(context, ":( 加载失败，请重试", Toast.LENGTH_LONG).show();
          }
          if (mAdapter.getCount() == 0 && !mIsEmpty) {
            mIsEmpty = true;
            mRefreshLayout.removeView(mContentView);
            mRefreshLayout.addView(mEmptyView);
          }
        }
      }
    }).execute();
  }

  private Card[] initCard(Context context, Object[] data) {
    int i = 0;
    Card[] result = new Card[data.length];
    Card card;
    for (Object temp : data) {
      UpdateItem item = (UpdateItem) temp;
      if (item instanceof UserUpdateArticleUpdateItem) {
        card = new UserUpdateArticleCard(context, (UserUpdateArticleUpdateItem) item, mFb);
      } else if (item instanceof UserCommentUpdateItem) {
        card = new UserCommentCard(context, (UserCommentUpdateItem) item, mFb);
      } else if (item instanceof UserUpdateLikeUpdateItem) {
        card = new UserUpdateLikeCard(context, (UserUpdateLikeUpdateItem) item, mFb);
      } else if (item instanceof UserUpdateFollowUpdateItem) {
        card = new UserUpdateFollowCard(context, (UserUpdateFollowUpdateItem) item, mFb);
      } else if (item instanceof UserSubscribeUpdateItem) {
        card = new UserSubscribeCard(context, (UserSubscribeUpdateItem) item, mFb);
      } else if (item instanceof CollectionUpdateItem) {
        card = new CollectinUpdateCard(context, (CollectionUpdateItem) item, mFb);
      } else if(item instanceof UserUpdateJoinUpdateItem) {
        card = new UserUpdateJoinCard(context, (UserUpdateJoinUpdateItem) item, mFb);
      } else {
        card = new UnknownUpdateCard(context, (UnknownUpdateItem) item, mFb);
      }
      card.addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, new Card.OnCardClickListener() {
        @Override
        public void onClick(Card card, View view) {
          mAdapter.onClick(card);
        }
      });
      result[i++] = card;
    }
    return result;
  }

  @Override
  public boolean isAtTheEnd() {
    return mPool.isAtTheEnd();
  }

  @Override
  public void onScrollEnd() {
    mFooter.startAnimation();
    new DataPoolAsyncTask(false, this.mPool, new DataPoolAsyncTask.OnPostExecuteTask() {
      @Override
      public void run(Exception exception, Object[] data) {
        mFooter.endAnimation();
        mContentView.notifyNewDataLoaded();
        Context context = getActivity();
        if (context == null) {
          return;
        }
        if (exception != null) {
          if (exception instanceof DataPool.LoginRequiredException) {
            mListener.onLoginRequired();
          }
        }
        if (data != null) {
          mAdapter.addAll(initCard(context, data));
        } else {
          Toast.makeText(context, ":( 加载失败，请重试", Toast.LENGTH_LONG).show();
        }
      }
    }).execute();
  }

  @Override
  public void onClick(Card card) {
    ((TimeStreamCard) card).onClick(getActivity());
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p/>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    public void onLoginRequired();
  }

}

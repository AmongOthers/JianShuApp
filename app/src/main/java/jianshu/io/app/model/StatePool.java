package jianshu.io.app.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.adapter.JianshuCardArrayAdapter;
import jianshu.io.app.adapter.TimeStreamCardArrayAdapter;
import jianshu.io.app.model.datapool.DataPool;
import jianshu.io.app.model.datapool.HomePageDataPool;
import jianshu.io.app.model.datapool.HotPageDataPool;
import jianshu.io.app.model.datapool.TimeStreamDataPool;

/**
 * Created by Administrator on 2014/5/15.
 */
public class StatePool {

  private static StatePool instance;

  private Map<String, Object[]> mStateMap = new HashMap<String, Object[]>();

  private StatePool() {

  }

  public static StatePool getInstance() {
    if(instance == null) {
      instance = new StatePool();
    }
    return instance;
  }

  public void putListViewState(String url, Object state) {
    mStateMap.get(url)[2] = state;
  }

  public Object[] getFragmentState(Context context, String url) {
    if(mStateMap.containsKey(url)) {
      return mStateMap.get(url);
    } else {
      Object[] result = new Object[3];
      DataPool pool;
      if(url.equals(HomePageDataPool.HOME_PAGE_URL)) {
        pool = new HomePageDataPool();
      } else if(url.equals(TimeStreamDataPool.TIMELINE_URL)) {
        pool = new TimeStreamDataPool();
      } else {
        pool = new HotPageDataPool(url);
      }
      result[0] = pool;
      if(url.equals(TimeStreamDataPool.TIMELINE_URL)) {
        TimeStreamCardArrayAdapter adapter = new TimeStreamCardArrayAdapter(context, new ArrayList<Card>());
        result[1] = adapter;
      } else {
        JianshuCardArrayAdapter adapter = new JianshuCardArrayAdapter(context, new ArrayList<Card>());
        result[1] = adapter;
      }
      mStateMap.put(url, result);
      return result;
    }
  }

  public Object[] getState(String tag) {
    if(mStateMap.containsKey(tag)) {
      return mStateMap.get(tag);
    }
    return null;
  }

  public void putState(String tag, Object[] state) {
    mStateMap.put(tag, state);
  }

}

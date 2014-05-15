package jianshu.io.app.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import jianshu.io.app.model.datapool.DataPool;
import jianshu.io.app.model.datapool.HomePageDataPool;
import jianshu.io.app.model.datapool.HotPageDataPool;

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

  public void putListViewState(String url, int[] state) {
    mStateMap.get(url)[2] = state;
  }

  public Object[] getState(Context context, String url) {
    if(mStateMap.containsKey(url)) {
      return mStateMap.get(url);
    } else {
      Object[] result = new Object[3];
      DataPool pool;
      if(url.equals(HomePageDataPool.HOME_PAGE_URL)) {
        pool = new HomePageDataPool();
      } else {
        pool = new HotPageDataPool(url);
      }
      result[0] = pool;
      CardArrayAdapter adapter = new CardArrayAdapter(context, new ArrayList<Card>());
      result[1] = adapter;
      mStateMap.put(url, result);
      return result;
    }
  }
}

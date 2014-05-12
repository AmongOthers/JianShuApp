package jianshu.io.app.util;

import android.os.AsyncTask;

import java.io.IOException;

import jianshu.io.app.model.RecommendationItem;
import jianshu.io.app.model.datapool.DataPool;

/**
 * Created by Administrator on 2014/5/11.
 */
public class RecommendationAsyncTask extends AsyncTask<Void, Void, RecommendationItem[]> {

  private boolean isRefresh;
  private DataPool pool;
  private OnPostExecuteTask task;

  public RecommendationAsyncTask(boolean isRefresh, DataPool pool, OnPostExecuteTask task) {
    this.isRefresh = isRefresh;
    this.pool = pool;
    this.task = task;
  }

  @Override
  protected RecommendationItem[] doInBackground(Void... params) {
    try {
      if(this.isRefresh) {
        return this.pool.refresh();
      } else {
        return this.pool.pull();
      }
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  protected void onPostExecute(RecommendationItem[] data) {
    this.task.run(data);
  }

  public interface OnPostExecuteTask {
    void run(RecommendationItem[] data);
  }
}

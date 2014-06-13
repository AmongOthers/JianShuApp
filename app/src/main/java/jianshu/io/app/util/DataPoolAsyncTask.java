package jianshu.io.app.util;

import android.os.AsyncTask;

import java.io.IOException;

import jianshu.io.app.model.datapool.DataPool;

/**
 * Created by Administrator on 2014/5/11.
 */
public class DataPoolAsyncTask extends AsyncTask<Void, Void, Object[]> {

  private boolean isRefresh;
  private DataPool pool;
  private OnPostExecuteTask task;
  private Exception exception;

  public DataPoolAsyncTask(boolean isRefresh, DataPool pool, OnPostExecuteTask task) {
    this.isRefresh = isRefresh;
    this.pool = pool;
    this.task = task;
  }

  @Override
  protected Object[] doInBackground(Void... params) {
    try {
      if(this.isRefresh) {
        return this.pool.refresh();
      } else {
        return this.pool.pull();
      }
    } catch (IOException e) {
      this.exception = e;
      return null;
    } catch (DataPool.LoginRequiredException e) {
      this.exception = e;
      return null;
    }
  }

  @Override
  protected void onPostExecute(Object[] data) {
    this.task.run(this.exception, data);
  }

  public interface OnPostExecuteTask {
    void run(Exception exception, Object[] data);
  }
}

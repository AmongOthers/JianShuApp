package jianshu.io.app;

import android.app.Application;

import model.JianshuSession;

/**
 * Created by Administrator on 2014/4/11.
 */
public class JianshuApplication extends Application {

  private JianshuSession mSession;

  @Override
  public void onCreate() {
    super.onCreate();
    CrashHandler.getInstance().init(this);

    mSession = new JianshuSession(this);
  }

  public JianshuSession getSession() {
    return mSession;
  }

}

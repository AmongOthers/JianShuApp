package jianshu.io.app;

import android.app.Application;

/**
 * Created by Administrator on 2014/4/11.
 */
public class CrashApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    CrashHandler.getInstance().init(this);
  }
}

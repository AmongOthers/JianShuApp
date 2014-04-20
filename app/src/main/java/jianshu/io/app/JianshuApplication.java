package jianshu.io.app;

import android.app.Application;

import model.JianshuSession;
import model.UserInfoManager;

/**
 * Created by Administrator on 2014/4/11.
 */
public class JianshuApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    CrashHandler.getInstance().init(this);

    JianshuSession.init(this);
    UserInfoManager.init(this);
  }

}

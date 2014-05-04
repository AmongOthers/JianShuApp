package jianshu.io.app;

import android.app.Application;

import com.shamanland.fonticon.FontIconTypefaceHolder;

import jianshu.io.app.util.CrashHandler;
import jianshu.io.app.model.JianshuSession;
import jianshu.io.app.model.UserInfoManager;

/**
 * Created by Administrator on 2014/4/11.
 */
public class JianshuApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    FontIconTypefaceHolder.init(getAssets(), "fontawesome.ttf");
    CrashHandler.getInstance().init(this);
    JianshuSession.init(this);
    UserInfoManager.init(this);
  }

}

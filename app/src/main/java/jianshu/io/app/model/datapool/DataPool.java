package jianshu.io.app.model.datapool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import jianshu.io.app.model.JianshuSession;
import jianshu.io.app.model.UserInfoManager;

/**
 * Created by Administrator on 2014/5/5.
 */
public abstract class DataPool {

  protected final static String CURRENT_USER_SLUG = "meta#current_user_slug";

  protected String mStartUrl;
  protected boolean mIsAtTheEnd;
  protected String mLoadMoreUrl;

  public DataPool(String startUrl) {
    mStartUrl = startUrl;
  }

  public Object[] refresh() throws IOException, LoginRequiredException {
    onRefresh();
    mIsAtTheEnd = false;
    return load(mStartUrl);
  }

  protected abstract void onRefresh();

  private Object[] load(String url) throws IOException, LoginRequiredException {
    Object httpResult = JianshuSession.getsInstance().getSync(url, true);
    if (httpResult instanceof String) {
      Document doc = Jsoup.parse((String) httpResult);
      if(doc.select("div.login-page").size() > 0) {
        JianshuSession.getsInstance().validate();
        if(JianshuSession.getsInstance().getState() instanceof JianshuSession.LogoutState) {
          throw new LoginRequiredException();
        }
      }
      parsePageUserInfo(doc);
      return this.getItems(doc);
    } else {
      JianshuSession.getsInstance().validate();
      if(JianshuSession.getsInstance().getState() instanceof JianshuSession.LogoutState) {
        throw new LoginRequiredException();
      }
    }
    return null;
  }

  protected abstract Object[] getItems(Document doc) throws IOException, LoginRequiredException;

  private void parsePageUserInfo(Document doc) {
    //页面中如果包含用户信息，说明是已登录的
    String userId = null;
    Elements userElements = doc.select(CURRENT_USER_SLUG);
    if (userElements.size() > 0) {
      Element userEl = userElements.get(0);
      userId = userEl.attr("value");
    }
    if(userId != null) {
      JianshuSession.getsInstance().notifyUserLogin();
    } else {
      JianshuSession.getsInstance().notifyUserLogout();
    }
    UserInfoManager.getsInstance().setUserId(userId);
  }

  public Object[] pull() throws IOException, LoginRequiredException {
    if (!mIsAtTheEnd) {
      return load(mLoadMoreUrl);
    } else {
      return null;
    }
  }

  public boolean isAtTheEnd() {
    return mIsAtTheEnd;
  }

  //清先登录
  public class LoginRequiredException extends Exception {

  }

}

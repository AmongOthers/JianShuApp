package model;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import net.tsz.afinal.FinalHttp;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;

import java.util.List;

import jianshu.io.app.util.WebViewCookieParser;

/**
 * Created by Administrator on 2014/4/12.
 */
public class JianshuSession {

  static final String DOMAIN = "jianshu.io";
  private JianshuSessionState mSessionState;
  private FinalHttp mFinalHttpForHtml;
  private FinalHttp mFinalHttpForJavaScript;
  private CookieManager mCookieManager;
  private List<BasicClientCookie> mCookieList;

  public JianshuSession(Context context) {
    mFinalHttpForJavaScript = new FinalHttp();
    mFinalHttpForJavaScript.addHeader("Accept", "*/*;q=0.5, text/javascript, application/javascript, application/ecmascript, application/x-ecmascript");
    mFinalHttpForJavaScript.addHeader("Accept-Language", "zh-CN,zh;q=0.8");

    mFinalHttpForHtml = new FinalHttp();
    mFinalHttpForHtml.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    mFinalHttpForHtml.addHeader("Accept-Language", "zh-CN,zh;q=0.8");

    CookieSyncManager.createInstance(context);
    mCookieManager = CookieManager.getInstance();

    String cookieStr = mCookieManager.getCookie(DOMAIN);
    mCookieList = new WebViewCookieParser().parse(cookieStr, DOMAIN);
    if(isCookiesContainUserToken()) {
      mSessionState = new LoginState();
    } else {
      mSessionState = new LogoutState();
    }
  }

  public boolean isUserLogin() {
    return getState() instanceof LoginState;
  }

  private boolean isCookiesContainUserToken() {
    if(mCookieList != null && mCookieList.size() > 0) {
      for(BasicClientCookie cookie : mCookieList) {
        if(cookie.getName().trim().equals("remember_user_token")) {
          return true;
        }
      }
    }
    return false;
  }

  public synchronized JianshuSessionState getState() {
    return mSessionState;
  }

  public synchronized void notifyUserLogin() {
    mSessionState.notifyUserLogin(this);
  }

  public synchronized void notifyUserLogout() {
    mSessionState.notifyUserLogout(this);
  }

  public synchronized Object getSync(String url, boolean isHtml) {
    return mSessionState.getSync(this, url, isHtml);
  }

  public synchronized Object postSync(String url, boolean isHtml) {
    return mSessionState.postSync(this, url, isHtml);
  }

  abstract class JianshuSessionState {

    public abstract void notifyUserLogin(JianshuSession session);

    public abstract void notifyUserLogout(JianshuSession session);

    public abstract Object getSync(JianshuSession session, String url, boolean isHtml);

    public abstract Object postSync(JianshuSession session, String url, boolean isHtml);

  }

  public class LoginState extends JianshuSessionState {

    public LoginState() {
      BasicCookieStore cookieStore = new BasicCookieStore();
      for(BasicClientCookie cookie : mCookieList) {
        cookieStore.addCookie(cookie);
      }
      HttpContext httpContext;
      httpContext = mFinalHttpForJavaScript.getHttpContext();
      httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
      httpContext = mFinalHttpForHtml.getHttpContext();
      httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    @Override
    public void notifyUserLogin(JianshuSession session) {
    }

    @Override
    public void notifyUserLogout(JianshuSession session) {
      session.mSessionState = new LogoutState();
    }

    @Override
    public Object getSync(JianshuSession session, String url, boolean isHtml) {
      FinalHttp finalHttp = isHtml ? mFinalHttpForHtml : mFinalHttpForJavaScript;
      return finalHttp.getSync(url);
    }

    @Override
    public Object postSync(JianshuSession session, String url, boolean isHtml) {
      FinalHttp finalHttp = isHtml ? mFinalHttpForHtml : mFinalHttpForJavaScript;
      return finalHttp.postSync(url);
    }
  }

  public class LogoutState extends JianshuSessionState {

    @Override
    public void notifyUserLogin(JianshuSession session) {
      String cookieStr = mCookieManager.getCookie(DOMAIN);
      mCookieList = new WebViewCookieParser().parse(cookieStr, DOMAIN);
      if (isCookiesContainUserToken()) {
        session.mSessionState = new LoginState();
      }
    }

    @Override
    public void notifyUserLogout(JianshuSession session) {
    }


    @Override
    public Object getSync(JianshuSession session, String url, boolean isHtml) {
      FinalHttp finalHttp = isHtml ? mFinalHttpForHtml : mFinalHttpForJavaScript;
      return finalHttp.getSync(url);
    }

    @Override
    public Object postSync(JianshuSession session, String url, boolean isHtml) {
      FinalHttp finalHttp = isHtml ? mFinalHttpForHtml : mFinalHttpForJavaScript;
      return finalHttp.postSync(url);
    }
  }
}


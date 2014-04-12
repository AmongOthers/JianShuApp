package jianshu.io.app.util;

import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/4/12.
 */
public class WebViewCookieParser {
  public List<BasicClientCookie> parse(String cookieStr, String domain) {
    List<BasicClientCookie> cookies = null;
    if (cookieStr != null && !cookieStr.equals("") ) {
      String[] cookieStrs = cookieStr.split(";");
      cookies = new ArrayList<BasicClientCookie>(cookieStrs.length);
      for (int i = 0; i < cookieStrs.length; i++) {
        String[] nvp = cookieStrs[i].split("=");
        BasicClientCookie c = new BasicClientCookie(nvp[0], nvp[1]);
        //c.setVersion(1);
        c.setDomain(domain);
        cookies.add(c);
      }
    }
    return cookies;
  }
}

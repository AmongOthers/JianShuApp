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
        String temp = cookieStrs[i];
        int firstIndex = temp.indexOf("=");
        if(firstIndex > 0) {
          String name = temp.substring(0, firstIndex);
          String value = temp.substring(firstIndex + 1);
          BasicClientCookie c = new BasicClientCookie(name, value);
          c.setDomain(domain);
          cookies.add(c);
        }
      }
    }
    return cookies;
  }
}

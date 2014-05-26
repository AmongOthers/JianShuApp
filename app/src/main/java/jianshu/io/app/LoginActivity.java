package jianshu.io.app;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jianshu.io.app.model.JianshuSession;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class LoginActivity extends SwipeBackActivity {

  private WebView mWebView;
  private boolean mIsLoginSuccessful;
  private static final Pattern LOGIN_SUCCESSFUL =
      Pattern.compile("http://jianshu.io/users/auth/.*/callback");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mWebView = (WebView) findViewById(R.id.login_webview);
    WebSettings settings = mWebView.getSettings();
    settings.setJavaScriptEnabled(true);
    mWebView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Matcher matcher = LOGIN_SUCCESSFUL.matcher(url);
        if(matcher.find()) {
          mIsLoginSuccessful = true;
        }
        String cookie = CookieManager.getInstance().getCookie(url);
        Log.d("jianshu", String.format("url: %s, cookie: ", url, cookie));
        if(cookie != null && cookie.contains("_session_id") && cookie.contains("remember_user_token")) {
          JianshuSession.getsInstance().validate();
          setResult(RESULT_OK);
          LoginActivity.this.finish();
          overridePendingTransition(0, R.anim.slide_out_right);
        } else if(mIsLoginSuccessful && url.equals("http://jianshu.io/")) {
          //cookie设置成功以remember_user_token为准，在少数情况下，可能登陆成功了，但是
          //检查的时候cookie还没有设置好
          JianshuSession.getsInstance().validate();
          setResult(RESULT_OK);
          LoginActivity.this.finish();
          overridePendingTransition(0, R.anim.slide_out_right);
        }
        return false;
      }
    });
    mWebView.loadUrl("http://jianshu.io/sign_in");
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
//    getMenuInflater().inflate(R.menu.login, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    finish();
    overridePendingTransition(0, R.anim.slide_out_right);
  }

}

package jianshu.io.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jianshu.io.app.widget.ObservableWebView;
import model.JianshuSession;


public class LikeActivity extends Activity implements ObservableWebView.OnScrollChangedCallback {

  private ObservableWebView mWebView;
  private View mLikeView;
  private TextView mLikeTextView;
  private boolean isLiking;
  private int likingCount = 12;
  private Animation fadeIn;
  private Animation fadeOut;

  static final String LIKE_SYMBOL = "♥";
  static final String UNLIKE_SYMBOL = "♡";
  static final String LIKE_URL = "http://jianshu.io/notes/137026/like";
  //static final Pattern LIKE_COUNT_PATTERN = Pattern.compile("text\\(\"([0-9].*)个喜欢\"\\)", Pattern.DOTALL);
  static final Pattern LIKE_COUNT_PATTERN = Pattern.compile("\"([0-9]+)个喜欢\"", Pattern.DOTALL);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_like);

    mWebView = (ObservableWebView)findViewById(R.id.like_webview);
    mWebView.loadUrl("http://jianshu.io/p/4e6f1efdcb39");
    mWebView.setOnScrollChangedCallback(this);
    mLikeView = findViewById(R.id.like);
    mLikeTextView = (TextView) findViewById(R.id.like_text);
    mLikeView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        (new AsyncTask<Void, Void, Boolean>() {

          @Override
          protected Boolean doInBackground(Void... params) {
            Object httpResult = JianshuSession.getsInstance().postSync(LIKE_URL, false);
            if(httpResult instanceof String) {
              String str = (String)httpResult;
              if (str.startsWith("$")) {
                if (str.contains("addClass('note-liked')")) {
                  LikeActivity.this.isLiking = true;
                } else if (str.contains("removeClass('note-liked')")) {
                  LikeActivity.this.isLiking = false;
                }
                Matcher matcher = LIKE_COUNT_PATTERN.matcher(str);
                if (matcher.find()) {
                  LikeActivity.this.likingCount = Integer.parseInt(matcher.group(1));
                }
                return true;
              }
            }
            return false;
          }

          @Override
          protected void onPostExecute(Boolean succeed) {
            if (succeed) {
              updateLike();
            }
          }
        }).execute();
      }
    });

    updateLike();
//    initAnimation();

    if(!JianshuSession.getsInstance().isUserLogin()) {
      mLikeTextView.setText("请先登录");
    }
  }

//  private void initAnimation() {
//    this.fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
//    this.fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
//    this.fadeOut.setAnimationListener(new Animation.AnimationListener() {
//      @Override
//      public void onAnimationStart(Animation animation) {
//
//      }
//
//      @Override
//      public void onAnimationEnd(Animation animation) {
//        mLikeView.setVisibility(View.GONE);
//      }
//
//      @Override
//      public void onAnimationRepeat(Animation animation) {
//
//      }
//    });
//  }

  private void updateLike() {
    String text = (isLiking ? LIKE_SYMBOL : UNLIKE_SYMBOL) + " " + this.likingCount;
    mLikeTextView.setText(text);
    mLikeTextView.setTextColor(getResources().getColor(isLiking ? R.color.white_trans : R.color.jianshu_trans));
    mLikeView.setBackgroundResource(isLiking ? R.drawable.border_fill : R.drawable.border);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.like, menu);
    return true;
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
  public void onScrollChanged(boolean isAtTheEnd) {
//    if(isAtTheEnd) {
//      mLikeView.setVisibility(View.VISIBLE);
//      mLikeView.startAnimation(this.fadeIn);
//    } else {
//      mLikeView.startAnimation(this.fadeOut);
//    }
  }
}

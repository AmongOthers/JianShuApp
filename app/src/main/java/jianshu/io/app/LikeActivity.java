package jianshu.io.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jianshu.io.app.widget.ObservableWebView;
import jianshu.io.app.model.JianshuSession;


public class LikeActivity extends Activity implements ObservableWebView.OnScrollChangedCallback {

  private ObservableWebView mWebView;
  private View mLikeView;
  private TextView mLikeTextView;
  private ProgressBar mLikeProgress;
  private boolean isLikingProgressing;
  private ObjectAnimator likingAnim;
  private ObjectAnimator unlikingAnim;
  private ObjectAnimator currentAnim;
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

    this.fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
    this.fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
    this.fadeOut.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        LikeActivity.this.mLikeView.setVisibility(View.GONE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });

    mWebView = (ObservableWebView)findViewById(R.id.like_webview);
    mWebView.loadUrl("http://jianshu.io/p/4e6f1efdcb39");
    mWebView.setOnScrollChangedCallback(this);
    mLikeProgress = (ProgressBar)findViewById(R.id.like_progress);
    this.likingAnim = ObjectAnimator.ofInt(this.mLikeProgress, "progress", 2, mLikeProgress.getMax() - 1);
    this.likingAnim.setDuration(2000);
    this.unlikingAnim = ObjectAnimator.ofInt(this.mLikeProgress, "progress", mLikeProgress.getMax() - 1, 2);
    this.unlikingAnim.setDuration(2000);

    mLikeView = findViewById(R.id.like);
    mLikeTextView = (TextView) findViewById(R.id.like_text);
    mLikeView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        final LikeActivity that = LikeActivity.this;
        final int max = mLikeProgress.getMax();
        if(that.isLikingProgressing) {
          return;
        }
        that.isLikingProgressing = true;

        that.currentAnim = that.isLiking ? that.unlikingAnim : that.likingAnim;
        that.currentAnim.start();

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
            LikeActivity.this.isLikingProgressing = false;
            //that.currentAnim.end();
            if (succeed) {
              updateLike();
            } else {
              Toast.makeText(that, "网络问题，请重试", Toast.LENGTH_LONG).show();
            }
          }
        }).execute();
      }
    });

    updateLike();
//    initAnimation()

    if(!JianshuSession.getsInstance().isUserLogin()) {
      mLikeTextView.setText("请先登录");
    }
  }

  private void updateLike() {
    if(this.currentAnim != null) {
      this.currentAnim.end();
    }
    mLikeProgress.setProgress(isLiking ? mLikeProgress.getMax() : 0);
    String text = (isLiking ? LIKE_SYMBOL : UNLIKE_SYMBOL) + " " + this.likingCount;
    mLikeTextView.setText(text);
    mLikeTextView.setTextColor(getResources().getColor(isLiking ? R.color.white_trans : R.color.jianshu_trans));
    //mLikeView.setBackgroundResource(isLiking ? R.drawable.border_fill : R.drawable.border);
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
    if(isAtTheEnd) {
      if(mLikeView.getVisibility() == View.GONE) {
        mLikeView.setVisibility(View.VISIBLE);
        mLikeView.startAnimation(this.fadeIn);
      }
    } else {
      if(mLikeView.getVisibility() == View.VISIBLE) {
        mLikeView.startAnimation(this.fadeOut);
      }
    }
  }
}

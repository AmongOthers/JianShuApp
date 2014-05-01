package jianshu.io.app;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jianshu.io.app.dialog.ScanFinishedDialogFragment;
import jianshu.io.app.widget.LoadingTextView;
import jianshu.io.app.widget.ObservableWebView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import model.JianshuSession;


public class ArticleActivity extends SwipeBackActivity implements ScanFinishedDialogFragment.OnFragmentInteractionListener, ObservableWebView.OnScrollChangedCallback {

  static final String LIKE_SYMBOL = "♥";
  static final String UNLIKE_SYMBOL = "♡";
  static final Pattern LIKE_COUNT_PATTERN = Pattern.compile("\"([0-9]+)个喜欢\"", Pattern.DOTALL);

  private LoadingTextView mLoadingArticle;
  private String mUrl;
  private String mTitle;
  private String mSummary;
  private String mAuthor;
  private String avatarUrl;
  private View mLikeView;
  private TextView mLikeTextView;
  private boolean isLiking;
  private int likingCount = 0;
  private String likeUrl;
  private ProgressBar mLikeProgress;
  private boolean isLikingProgressing;
  private ObjectAnimator likingAnim;
  private ObjectAnimator unlikingAnim;
  private ObjectAnimator currentAnim;

  private Handler handler;
  private ObservableWebView mWebView;
  private Button mRetryButton;
  private View scanLight;
  private SwipeBackLayout mSwipeBackLayout;
  private Animation scanAnim;
  private Animation fadeIn;
  private Animation fadeOut;
  private ScanFinishedDialogFragment scanFinishedDialogFragment;

  private String imagePath;
  private FinalHttp mFinalHttp;
  private ShareActionProvider mShareActionProvider;
  private DownloadManager mDownloadManager;
  private Bitmap scanBitmap;
  private String content;


  protected static String Css;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article);

    this.handler = new Handler();

    Intent intent = getIntent();
    mUrl = intent.getStringExtra("url");
    mTitle = intent.getStringExtra("title");
    mSummary = intent.getStringExtra("summary");
    mAuthor = intent.getStringExtra("author");
    mLoadingArticle = (LoadingTextView) findViewById(R.id.loading_article);
    mWebView = (ObservableWebView) findViewById(R.id.web);
    mWebView.setOnScrollChangedCallback(this);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.addJavascriptInterface(this, "article");
    mRetryButton = (Button) findViewById(R.id.retry);
    this.scanLight = (View) findViewById(R.id.scan_light);

    final ArticleActivity that = this;

    this.fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
    this.fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
    this.fadeOut.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        ArticleActivity.this.mLikeView.setVisibility(View.GONE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });

    this.scanAnim = AnimationUtils.loadAnimation(this, R.anim.scan);
    this.scanAnim.setAnimationListener(new Animation.AnimationListener() {

      @Override
      public void onAnimationStart(Animation animation) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        that.scanFinishedDialogFragment = ScanFinishedDialogFragment.newInstance();
        that.scanFinishedDialogFragment.show(ft, "scan");
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        ArticleActivity.this.scanLight.setVisibility(View.GONE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });

    mLikeProgress = (ProgressBar) findViewById(R.id.like_progress);
    this.likingAnim = ObjectAnimator.ofInt(this.mLikeProgress, "progress", 2, mLikeProgress.getMax() - 1);
    this.likingAnim.setDuration(2000);
    this.unlikingAnim = ObjectAnimator.ofInt(this.mLikeProgress, "progress", mLikeProgress.getMax() - 1, 2);
    this.unlikingAnim.setDuration(2000);

    mLikeView = findViewById(R.id.like);
    mLikeTextView = (TextView) findViewById(R.id.like_text);
    mLikeView.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        final ArticleActivity that = ArticleActivity.this;
        final int max = mLikeProgress.getMax();
        if (that.isLikingProgressing) {
          return;
        }
        that.isLikingProgressing = true;

        that.currentAnim = that.isLiking ? that.unlikingAnim : that.likingAnim;
        that.currentAnim.start();

        (new AsyncTask<Void, Void, Boolean>() {

          @Override
          protected Boolean doInBackground(Void... params) {
            Object httpResult = JianshuSession.getsInstance().postSync(that.likeUrl, false);
            if (httpResult instanceof String) {
              String str = (String) httpResult;
              if (str.startsWith("$")) {
                if (str.contains("addClass('note-liked')")) {
                  that.isLiking = true;
                } else if (str.contains("removeClass('note-liked')")) {
                  that.isLiking = false;
                }
                Matcher matcher = LIKE_COUNT_PATTERN.matcher(str);
                if (matcher.find()) {
                  that.likingCount = Integer.parseInt(matcher.group(1));
                }
                return true;
              }
            }
            return false;
          }

          @Override
          protected void onPostExecute(Boolean succeed) {
            that.isLikingProgressing = false;
            //即使是网络问题失败了，也要重置进度条状态
            updateLike();
            if (!succeed) {
              Toast.makeText(that, "网络似乎不给力", Toast.LENGTH_LONG).show();
            }
          }
        }).execute();
      }
    });

    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    //滑动返回
    mSwipeBackLayout = getSwipeBackLayout();
    mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

    mRetryButton.setOnClickListener(new Button.OnClickListener() {

      @Override
      public void onClick(View v) {
        mRetryButton.setVisibility(View.INVISIBLE);
        mLoadingArticle.setVisibility(View.VISIBLE);
        loadArticle();
      }
    });

    mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

    mFinalHttp = new FinalHttp();

    loadArticle();
  }

  private void updateLike() {
    if (this.currentAnim != null) {
      this.currentAnim.end();
    }
//    if (this.mLikeView.getVisibility() == View.GONE) {
//      this.mLikeView.setVisibility(View.VISIBLE);
//    }
    mLikeProgress.setProgress(isLiking ? mLikeProgress.getMax() : 0);
    String text = (isLiking ? LIKE_SYMBOL : UNLIKE_SYMBOL) + " " + this.likingCount;
    mLikeTextView.setText(text);
    mLikeTextView.setTextColor(getResources().getColor(isLiking ? R.color.white_trans : R.color.jianshu_trans));
  }

  private void loadArticle() {
    final ArticleActivity that = this;
    mLoadingArticle.startAnimation();
    (new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... params) {
        Object httpResult = JianshuSession.getsInstance().getSync(mUrl, true);
        if (httpResult instanceof String) {
          Document doc = Jsoup.parse((String) httpResult);
          //mImageUrl = doc.select("div.meta-bottom").get(0).attr("data-image");
          Element likeBtnEl = doc.select(".like > .btn").get(0);
          String likeUrlAttr = likeBtnEl.attr("href");
          //判断登录状态
          if (!likeUrlAttr.equals("#login-model")) {
            that.likeUrl = "http://jianshu.io" + likeBtnEl.attr("href");
            that.isLiking = likeBtnEl.hasClass("note-liked");
            Elements functionEls = doc.select("div.comment li a");
            Element likeCountEl = null;
            for (Element el : functionEls) {
              if (el.attr("href").equals("#like")) {
                likeCountEl = el;
                break;
              }
            }
            String countStr = likeCountEl.text().replace("个喜欢", "").trim();
            that.likingCount = Integer.parseInt(countStr);
          }
          Element article = doc.select("div.preview").get(0);
          Element title = article.select("h1.title").get(0);
          Element authorInfo = article.select("div.meta-top").get(0);
          Element content = article.select("div.show-content").get(0);
          String extractedDocStr = String.format("<html lang=\"zh-CN\">" +
                  "<head>" +
                  "<meta charset=\"utf-8\">" +
                  "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                  "<style type=\"text/css\">%s</style>" +
                  "</head>" +
                  "<body class=\"post output zh cn reader-day-mode reader-font1  win\">" +
                  "<div class=\"post-bg\">" +
                  "<div class=\"container\">" +
                  "<div class=\"article\">" +
                  "<div class=\"preview\">" +
                  "%s" + "%s" + "%s" +
                  "</div>" +
                  "</div>" +
                  "</div>" +
                  "</div>" +
                  getJianshuBar() +
                  "</body>" +
                  "</html>",
              getCss(),
              title.toString(), authorInfo.toString(), content.toString()
          );
          return extractedDocStr;
        } else {
          return null;
        }
      }

      @Override
      protected void onPostExecute(String s) {
        mLoadingArticle.endAnimation();
        mLoadingArticle.setVisibility(View.INVISIBLE);
        if (s != null) {
          that.content = s;
          mWebView.setVisibility(View.VISIBLE);
          mWebView.loadData(s, "text/html; charset=UTF-8", null);
        } else {
          mWebView.setVisibility(View.INVISIBLE);
          mRetryButton.setVisibility(View.VISIBLE);
        }
        updateLike();
      }
    }).execute();
  }

  protected String getCss() {
    if (Css == null) {
      try {
        InputStream stream = getAssets().open("jianshu.css");
        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();
        Css = new String(buffer);
      } catch (IOException e) {
        // Handle exceptions here
      }
    }
    return Css;
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mLoadingArticle.getVisibility() == View.VISIBLE) {
      mLoadingArticle.endAnimation();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.article, menu);
    setShareIntent(menu);
    return true;
  }

  private void setShareIntent(Menu menu) {
    MenuItem item = menu.findItem(R.id.menu_item_share);
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_TITLE, mTitle);
    shareIntent.putExtra(Intent.EXTRA_TEXT, getSharedContent());
    shareIntent.putExtra(Intent.EXTRA_ORIGINATING_URI, mUrl);
    shareIntent.setType("text/plain");
    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(shareIntent);
    }
  }

  private String getSharedContent() {
    return String.format("《%s》 by %s %s (%s)", mTitle, mAuthor, mUrl, "分享自简书");
  }

  private String getJianshuBar() {
    return "<div class='jianshu_bar'><h1>简书</h1><h3>最好的写作和阅读平台</h3><p><a href='http://jianshu.io'>http://jianshu.io</a></p></div> ";
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.  }   int id = item.getItemId();
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, R.anim.slide_out_right);
        return true;
      case R.id.menu_item_picture:
        mWebView.loadUrl("javascript:document.getElementsByClassName('jianshu_bar')[0].style.display = 'block'");
        this.handler.postDelayed(new Runnable() {
          @Override
          public void run() {
            scanContent();
          }
        }, 1000);
        this.scanLight.setVisibility(View.VISIBLE);
        this.scanLight.startAnimation(this.scanAnim);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void scanContent() {
    int[] size = this.mWebView.getRealSize();
    int width = size[0];
    int height = size[1];
    try {
      this.scanBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(this.scanBitmap);
      this.mWebView.draw(canvas);
    } catch (OutOfMemoryError e) {
      this.scanBitmap = null;
      mWebView.loadUrl("javascript:document.getElementsByClassName('jianshu_bar')[0].style.display = 'none'");
      this.scanFinishedDialogFragment.onScanError("篇幅过长，不能扫描");
      return;
    }
    final ArticleActivity that = ArticleActivity.this;
    (new AsyncTask<Void, Void, Boolean>() {

      @Override
      protected Boolean doInBackground(Void... params) {
        try {
          File jianshuImageFile = new File(Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_PICTURES) + "/jianshu");
          if (!jianshuImageFile.exists()) {
            jianshuImageFile.mkdirs();
          }
          that.imagePath = Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_PICTURES) + "/jianshu/" + getImageFileName(mTitle);
          FileOutputStream fos = new FileOutputStream(that.imagePath);
          that.scanBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
          that.scanBitmap.recycle();
          that.scanBitmap = null;
          fos.close();
          return true;
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return false;
      }

      @Override
      protected void onPostExecute(Boolean succeed) {
        mWebView.loadUrl("javascript:document.getElementsByClassName('jianshu_bar')[0].style.display = 'none'");
        if (succeed) {
          that.scanFinishedDialogFragment.onScanFinished();
        } else {
          that.scanFinishedDialogFragment.onScanError("保存图片时遇到错误");
        }
      }

    }).execute();
  }

  static final char[] RESERVED_CHARS = new char[]{'|', '\\', '?', '*', '<', '\"', ':', '>', '+', '[', ']', '/', '\''};

  private String getImageFileName(String title) {
    String imageFileName = title;
    for (char ch : RESERVED_CHARS) {
      imageFileName = imageFileName.replace(ch, '%');
    }
    return imageFileName + ".jpeg";
  }

  @Override
  public void onBackPressed() {
    finish();
    overridePendingTransition(0, R.anim.slide_out_right);
  }

  @Override
  public void onViewButtonPressed() {
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    Fragment f = getFragmentManager().findFragmentByTag("scan");
    if (f != null) {
      ft.remove(f);
      ft.commit();
    }
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(new File(this.imagePath)), "image/jpeg");
    startActivity(intent);
  }

  @Override
  public void onScrollChanged(boolean isAtTheEnd) {
    if (isAtTheEnd) {
      if (mLikeView.getVisibility() == View.GONE) {
        mLikeView.setVisibility(View.VISIBLE);
        mLikeView.startAnimation(this.fadeIn);
      }
    } else {
      if (mLikeView.getVisibility() == View.VISIBLE) {
        mLikeView.startAnimation(this.fadeOut);
      }
    }
  }
}

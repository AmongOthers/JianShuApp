package jianshu.io.app;

import android.app.ActionBar;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;

import jianshu.io.app.widget.LoadingTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ArticleActivity extends SwipeBackActivity {

  private LoadingTextView mLoadingArticle;
  private String mUrl;
  private String mTitle;
  private String mSummary;
  private String mAuthor;
  private WebView mWebView;
  private Button mRetryButton;
  private SwipeBackLayout mSwipeBackLayout;
  private FinalHttp mFinalHttp;
  private ShareActionProvider mShareActionProvider;
  private DownloadManager mDownloadManager;
  private String mImageUrl;

  protected static String Css;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article);

    Intent intent = getIntent();
    mUrl = intent.getStringExtra("url");
    mTitle = intent.getStringExtra("title");
    mSummary = intent.getStringExtra("summary");
    mAuthor = intent.getStringExtra("author");
    mLoadingArticle = (LoadingTextView)findViewById(R.id.loading_article);
    mWebView = (WebView)findViewById(R.id.web);
    mRetryButton = (Button)findViewById(R.id.retry);

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

  private void loadArticle() {
    mLoadingArticle.startAnimation();
    (new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... params) {
        Object httpResult = mFinalHttp.getSync(mUrl);
        if(httpResult instanceof String) {
          Document doc = Jsoup.parse((String) httpResult);
          mImageUrl = doc.select("div.meta-bottom").get(0).attr("data-image");
          Element article = doc.select("div.preview").get(0);
          Element title = article.select("h1.title").get(0);
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
              "%s" + "%s" +
              "</div>" +
              "</div>" +
              "</div>" +
              "</div>" +
              "</body>" +
              "</html>",
              getCss(),
              title.toString(), content.toString());
          return extractedDocStr;
        } else {
          return null;
        }
      }

      @Override
      protected void onPostExecute(String s) {
        mLoadingArticle.endAnimation();
        mLoadingArticle.setVisibility(View.INVISIBLE);
        if(s != null) {
          mWebView.setVisibility(View.VISIBLE);
          mWebView.loadData(s, "text/html; charset=UTF-8", null);
        } else {
          mWebView.setVisibility(View.INVISIBLE);
          mRetryButton.setVisibility(View.VISIBLE);
        }
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
    if(mLoadingArticle.getVisibility() == View.VISIBLE) {
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
        Toast.makeText(this, "开始下载长微博图片", Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse(mImageUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(mTitle);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + "/jianshu", getImageFileName(mTitle));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        mDownloadManager.enqueue(request);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  static final char[] RESERVED_CHARS = new char[]{'|', '\\', '?', '*', '<', '\"', ':', '>', '+', '[', ']', '/', '\''};
  private String getImageFileName(String title) {
    String imageFileName = mTitle;
    for(char ch : RESERVED_CHARS) {
      imageFileName = imageFileName.replace(ch, '%');
    }
    return imageFileName + ".jpeg";
  }

  @Override
  public void onBackPressed() {
    finish();
    overridePendingTransition(0, R.anim.slide_out_right);
  }
}

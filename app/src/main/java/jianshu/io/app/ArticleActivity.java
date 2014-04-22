package jianshu.io.app;

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
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jianshu.io.app.dialog.ScanFinishedDialogFragment;
import jianshu.io.app.widget.LoadingTextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class ArticleActivity extends SwipeBackActivity implements ScanFinishedDialogFragment.OnFragmentInteractionListener {

  private LoadingTextView mLoadingArticle;
  private String mUrl;
  private String mTitle;
  private String mSummary;
  private String mAuthor;
  private String avatarUrl;

  private WebView mWebView;
  private Button mRetryButton;
  private View scanLight;
  private SwipeBackLayout mSwipeBackLayout;
  private Animation scanAnim;

  private String imagePath;
  private FinalHttp mFinalHttp;
  private ShareActionProvider mShareActionProvider;
  private DownloadManager mDownloadManager;
  private Bitmap scanBitmap;

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
    this.scanLight = (View)findViewById(R.id.scan_light);

    this.scanAnim = AnimationUtils.loadAnimation(this, R.anim.scan);
    this.scanAnim.setAnimationListener(new Animation.AnimationListener() {

      ScanFinishedDialogFragment scanFinishedDialogFragment = null;

      @Override
      public void onAnimationStart(Animation animation) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        scanFinishedDialogFragment = ScanFinishedDialogFragment.newInstance();
        scanFinishedDialogFragment.show(ft, "scan");
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        ArticleActivity.this.scanLight.setVisibility(View.GONE);
        scanFinishedDialogFragment.onScanFinished();
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

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

  private void loadArticle() {
    mLoadingArticle.startAnimation();
    (new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... params) {
        Object httpResult = mFinalHttp.getSync(mUrl);
        if(httpResult instanceof String) {
          Document doc = Jsoup.parse((String) httpResult);
          //mImageUrl = doc.select("div.meta-bottom").get(0).attr("data-image");
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
              "</body>" +
              "</html>",
              getCss(),
              title.toString(), authorInfo.toString(), content.toString());
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
        int[] size = getRealSize(mWebView);
        int width = size[0];
        int height = size[1];
        try {
          this.scanBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
          Canvas canvas = new Canvas(this.scanBitmap);
          mWebView.draw(canvas);
        } catch (OutOfMemoryError e) {
          this.scanBitmap = null;
          Toast.makeText(this, "扫描图像失败，请重试", Toast.LENGTH_LONG).show();
          return true;
        }
        final ArticleActivity that = ArticleActivity.this;
        (new AsyncTask<Void, Void, Boolean>(){

          @Override
          protected Boolean doInBackground(Void... params) {
            try {
              File jianshuImageFile = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES) + "/jianshu");
              if(!jianshuImageFile.exists()) {
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
            if(succeed) {
              that.scanLight.setVisibility(View.VISIBLE);
              that.scanLight.startAnimation(that.scanAnim);
            } else {
              Toast.makeText(that, "扫描时遇到错误", Toast.LENGTH_LONG).show();
            }
          }

        }).execute();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private int[] getRealSize(WebView webView) {
    try {
      Method widthMethod = webView.getClass().getDeclaredMethod("computeHorizontalScrollRange");
      widthMethod.setAccessible(true);
      int width = (Integer) widthMethod.invoke(webView);
      Method heightMethod = webView.getClass().getDeclaredMethod("computeVerticalScrollRange");
      heightMethod.setAccessible(true);
      int height = (Integer) heightMethod.invoke(webView);
      return new int[]{ width, height };
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  static final char[] RESERVED_CHARS = new char[]{'|', '\\', '?', '*', '<', '\"', ':', '>', '+', '[', ']', '/', '\''};
  private String getImageFileName(String title) {
    String imageFileName = title;
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
    @Override
  public void onViewButtonPressed() {
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    Fragment f = getFragmentManager().findFragmentByTag("scan");
    if(f != null) {
      ft.remove(f);
      ft.commit();
    }
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.fromFile(new File(this.imagePath)), "image/jpeg");
    startActivity(intent);
  }
}

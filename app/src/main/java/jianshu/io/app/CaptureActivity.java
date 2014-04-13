package jianshu.io.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jianshu.io.app.widget.LoadingTextView;


public class CaptureActivity extends Activity {
  private String mTitle;
  private String mContent;

  private LoadingTextView mLoadingArticle;
  private WebView mWebView;
  private Button mRetryButton;
  private ShareActionProvider mShareActionProvider;

  protected static String Css;
  protected static String Content;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article);

    mTitle  = "<h1>测试长微博图片</h1>";
    mContent = getContent();

    mLoadingArticle = (LoadingTextView)findViewById(R.id.loading_article);
    mWebView = (WebView)findViewById(R.id.web);
    mRetryButton = (Button)findViewById(R.id.retry);

    mLoadingArticle.setVisibility(View.INVISIBLE);
    mRetryButton.setVisibility(View.INVISIBLE);
    mWebView.setVisibility(View.VISIBLE);

    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    loadArticle();
  }

  private void loadArticle() {
    final String extractedDocStr = String.format("<html lang=\"zh-CN\">" +
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
        mTitle.toString(), mContent.toString()
    );
    mWebView.loadData(extractedDocStr, "text/html; charset=UTF-8", null);
//    mWebView.loadDataWithBaseURL("data://", extractedDocStr, "text/html; charset=UTF-8", null, null);
//    (new AsyncTask<Void, Void, Void>(){
//      @Override
//      protected Void doInBackground(Void... params) {
//        try {
//          String path = getCacheDir() + "/temp.html";
//          OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path));
//          writer.write(extractedDocStr);
//          writer.close();
//        } catch (FileNotFoundException e) {
//          e.printStackTrace();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//        return null;
//      }
//
//      @Override
//      protected void onPostExecute(Void aVoid) {
//        String path = getCacheDir() + "/temp.html";
//        mWebView.loadUrl("file://" + path);
//      }
//    }).execute();
  }

  private String getContent() {
    if (Content == null) {
      try {
        InputStream stream = getAssets().open("content.txt");
        int size = stream.available();
        byte[] buffer = new byte[size];
        stream.read(buffer);
        stream.close();
        Content = new String(buffer);
      } catch (IOException e) {
        // Handle exceptions here
      }
    }
    return Content;
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
    getMenuInflater().inflate(R.menu.capture, menu);
    return true;
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
      case R.id.cap_item_picture:
        int[] size = getRealSize(mWebView);
        int width = size[0];
        int height = size[1];
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mWebView.draw(canvas);
        (new AsyncTask<Void, Void, Void>(){

          @Override
          protected Void doInBackground(Void... params) {
            try {
              FileOutputStream fos = new FileOutputStream(
                  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) +
                      "/jianshu/" + getImageFileName("测试长微博"));
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
              fos.close();
            } catch (FileNotFoundException e) {
              e.printStackTrace();
            } catch (IOException e) {
              e.printStackTrace();
            }
            return null;
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
}

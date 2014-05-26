package jianshu.io.app.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jianshu.io.app.stackblur.StackBlurManager;
import jianshu.io.app.util.FileUtils;

/**
 * Created by Administrator on 2014/5/25.
 */
public class CoverDownloader {

  public static final Pattern CoverPattern = Pattern.compile("\\(http(.*)\\)");
  private static CoverDownloader sInstance;
  StackBlurManager mStackBlurManager;
  private static final int[] STEPS = new int[]{205, 155, 105, 55, 5, 0};
  private File mCoverFile;

  public static CoverDownloader getInstance() {
    if (sInstance == null) {
      sInstance = new CoverDownloader();
    }
    return sInstance;
  }

  private CoverDownloader() {

  }

  public void fetchCover(Context context) throws IOException {
    String imagePath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES) + "/jianshu/cover";
    File file = new File(imagePath);
    if (!file.exists()) {
      file.mkdirs();
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String todayDirName = imagePath + "/" + dateFormat.format(new Date());
    File todayDir = new File(todayDirName);
    FileUtils.deleteDirectory(todayDir);
    todayDir.mkdirs();
    mCoverFile = new File(todayDirName + "/" + "cover.jpg");
    download();
    process();
    context.getSharedPreferences("jianshu", Context.MODE_PRIVATE).edit().
        putString("cover", mCoverFile.getAbsolutePath()).commit();
  }

  public Bitmap[] loadCover(Context context) {
    String coverFileName = context.getSharedPreferences("jianshu", Context.MODE_PRIVATE).getString("cover", null);
    if(coverFileName != null) {
      Bitmap[] bitmaps = new Bitmap[STEPS.length];
      for(int i = 0; i < STEPS.length; i++) {
        String temp = coverFileName + STEPS[i];
        bitmaps[i] = getBitmapFromFile(new File(temp));
      }
      return bitmaps;
    } else {
      return null;
    }
  }

  public void download() throws IOException {
    Object httpResult = JianshuSession.getsInstance().getSync("http://jianshu.io", true);
    if (httpResult instanceof String) {
      Document doc = Jsoup.parse((String) httpResult);
      Element coverEl = doc.select("div.cover-img").get(0);
      String style = coverEl.attr("style");
      Matcher matcher = CoverPattern.matcher(style);
      if (matcher.find()) {
        InputStream in = null;
        OutputStream out = null;
        out = new FileOutputStream(mCoverFile);

        String coverUrl = "http" + matcher.group(1);
        URL url = new URL(coverUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
          in = connection.getInputStream();
          byte[] buffer = new byte[1024 * 10];
          int length;
          while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
          }
          out.flush();
        } finally {
          if (in != null) {
            in.close();
          }
          if (out != null) {
            out.close();
          }
          connection.disconnect();
        }
      }
    }
  }

  public void process() throws FileNotFoundException {
    String coverFileName = mCoverFile.getAbsolutePath();
    mStackBlurManager = new StackBlurManager(getBitmapFromFile(mCoverFile));
    Bitmap[] bitmaps = new Bitmap[STEPS.length];
    int i = 0;
    for (int step : STEPS) {
      bitmaps[i] = mStackBlurManager.process(STEPS[i]);
      String temp = coverFileName + STEPS[i];
      FileOutputStream fos = new FileOutputStream(temp);
      bitmaps[i].compress(Bitmap.CompressFormat.JPEG, 100, fos);
      bitmaps[i].recycle();
      i++;
    }
  }

  private Bitmap getBitmapFromFile(File coverFile) {
    Bitmap bitmap = null;
    try {
      InputStream in = new FileInputStream(coverFile);
      bitmap = BitmapFactory.decodeStream(in);
    } catch (IOException e) {
      return null;
    }
    return bitmap;
  }
}

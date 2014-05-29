package jianshu.io.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import jianshu.io.app.model.CoverDownloader;
import jianshu.io.app.stackblur.StackBlurManager;

public class SplashActivity extends Activity {

  ImageView mSplash;
  View mIntroView;
  Bitmap[] mBitmaps;
  Handler mHandler;
  int mIndex;
  StackBlurManager mStackBlurManager;
  private static final int[] STEPS = new int[]{205, 155, 105, 55, 5, 0};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    mHandler = new Handler();
    mSplash = (ImageView) findViewById(R.id.splash);
    mIntroView = findViewById(R.id.intro);

    boolean isSplashToShow = false;
    SharedPreferences preferences = getSharedPreferences("jianshu", MODE_PRIVATE);
    String cover = preferences.getString("cover", null);
    if (cover != null) {
      String coverShown = preferences.getString("shown", null);
      if (coverShown == null || !coverShown.equals(cover)) {
        isSplashToShow = true;
        preferences.edit().putString("shown", cover).commit();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            if (mBitmaps == null) {
              mBitmaps = CoverDownloader.getInstance().loadCover(SplashActivity.this);
              if (mBitmaps == null) {
                timer.cancel();
                timer.purge();
                SplashActivity.this.finish();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
              }
            }
            mHandler.post(new Runnable() {
              @Override
              public void run() {
                if (mIndex < mBitmaps.length) {
                  Bitmap bitmap = mBitmaps[mIndex];
                  mSplash.setImageBitmap(bitmap);
                } else if (mIndex == mBitmaps.length) {
                  mIntroView.setVisibility(View.VISIBLE);
                } else if (mIndex == mBitmaps.length + 10) {
                  timer.cancel();
                  timer.purge();
                  SplashActivity.this.finish();
                  startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                mIndex++;
              }
            });
          }
        }, 0, 200);
      }
    }
    if(!isSplashToShow) {
      SplashActivity.this.finish();
      startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }
  }

  @Override
  protected void onPause() {
    super.onDestroy();
    if (mBitmaps != null) {
      for (int i = 0; i < mBitmaps.length; i++) {
        if (mBitmaps[i] != null) {
          mBitmaps[i].recycle();
        }
      }
    }
  }
}

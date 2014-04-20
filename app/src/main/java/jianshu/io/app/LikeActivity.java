package jianshu.io.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import model.JianshuSession;


public class LikeActivity extends Activity {

  private Button mLikeButton;

  static final String LIKE_URL = "http://jianshu.io/notes/130176/like";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_like);

    mLikeButton = (Button) findViewById(R.id.like);
    mLikeButton.setOnClickListener(new Button.OnClickListener(){

      @Override
      public void onClick(View v) {
        (new AsyncTask<Void, Void, Object>() {

          @Override
          protected Object doInBackground(Void... params) {
            return JianshuSession.getsInstance().postSync(LIKE_URL, false);
          }

          @Override
          protected void onPostExecute(Object s) {
            if(s instanceof String) {
              Log.d("jianshu", "I like it");
            }
          }
        }).execute();
      }
    });

    if(!JianshuSession.getsInstance().isUserLogin()) {
      mLikeButton.setText("请先登录");
    }
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

}

package jianshu.io.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/5/25.
 */
public class StartAt7Receiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Toast.makeText(context, "Start At 7 broadcast", Toast.LENGTH_LONG).show();
  }
}

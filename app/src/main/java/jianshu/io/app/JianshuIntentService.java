package jianshu.io.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;

import jianshu.io.app.model.CoverDownloader;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class JianshuIntentService extends IntentService {
  // TODO: Rename actions, choose action names that describe tasks that this
  // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
  public static final String ACTION_START_AT_7 = "jianshu.io.app.action.StartAt7";
  public static final String ACTION_EVERY_HOUR = "jianshu.io.app.action.EveryHour";

  /**
   * Starts this service to perform action Foo with the given parameters. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void StartActionStartAt7(Context context) {
    context.startService(getStartAt7Intent(context));
  }

  public static Intent getStartAt7Intent(Context context) {
    Intent intent = new Intent(context, JianshuIntentService.class);
    intent.setAction(ACTION_START_AT_7);
    return intent;
  }


  /**
   * Starts this service to perform action Baz with the given parameters. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void startActionEveryHour(Context context, String param1, String param2) {
    context.startService(getEveryHourIntent(context));
  }

  public static Intent getEveryHourIntent(Context context) {
    Intent intent = new Intent(context, JianshuIntentService.class);
    intent.setAction(ACTION_EVERY_HOUR);
    return intent;
  }

  public JianshuIntentService() {
    super("JianshuIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_START_AT_7.equals(action)) {
        handleActionStartAt7();
      } else if (ACTION_EVERY_HOUR.equals(action)) {
        handleActionEveryHour();
      }
    }
  }

  /**
   * Handle action Foo in the provided background thread with the provided
   * parameters.
   */
  private void handleActionStartAt7() {
    try {
      Log.d("jianshu", "try to download cover at 7");
      CoverDownloader.getInstance().fetchCover(this);
      Log.d("jianshu", "download cover ok at 7");
    } catch (IOException e) {
      Log.e("jianshu", "download cover failed at 7: " + e.toString());
      //7点钟的时候获取失败，那么每隔1小时重试一次
      Intent intent = JianshuIntentService.getEveryHourIntent(this);
      boolean isAlarmSet = (PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_NO_CREATE) != null);
      if (!isAlarmSet) {
        PendingIntent pendingInent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingInent);
      }
    }
  }

  /**
   * Handle action Baz in the provided background thread with the provided
   * parameters.
   */
  private void handleActionEveryHour() {
    try {
      Log.d("jianshu", "try to download cover at every hour");
      CoverDownloader.getInstance().fetchCover(this);
      Log.d("jianshu", "download cover ok at every hour");
      Intent intent = JianshuIntentService.getEveryHourIntent(this);
      PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_NO_CREATE);
      AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
      alarmManager.cancel(pendingIntent);
    } catch (IOException e) {
      Log.e("jianshu", "download cover failed at every hour: " + e.toString());
    }
  }
}

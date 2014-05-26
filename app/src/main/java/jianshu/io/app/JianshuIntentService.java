package jianshu.io.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

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
  public static final String ACTION_BAZ = "jianshu.io.app.action.BAZ";

  // TODO: Rename parameters
  private static final String EXTRA_PARAM1 = "jianshu.io.app.extra.PARAM1";
  private static final String EXTRA_PARAM2 = "jianshu.io.app.extra.PARAM2";

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
  public static void startActionBaz(Context context, String param1, String param2) {
    Intent intent = new Intent(context, JianshuIntentService.class);
    intent.setAction(ACTION_BAZ);
    intent.putExtra(EXTRA_PARAM1, param1);
    intent.putExtra(EXTRA_PARAM2, param2);
    context.startService(intent);
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
      } else if (ACTION_BAZ.equals(action)) {
        final String param1 = intent.getStringExtra(EXTRA_PARAM1);
        final String param2 = intent.getStringExtra(EXTRA_PARAM2);
        handleActionBaz(param1, param2);
      }
    }
  }

  /**
   * Handle action Foo in the provided background thread with the provided
   * parameters.
   */
  private void handleActionStartAt7() {
    try {
      CoverDownloader.getInstance().fetchCover(this);
    } catch (IOException e) {
      Log.e("jianshu", "Download cover failed: " + e.toString());
    }
  }

  /**
   * Handle action Baz in the provided background thread with the provided
   * parameters.
   */
  private void handleActionBaz(String param1, String param2) {
    // TODO: Handle action Baz
    throw new UnsupportedOperationException("Not yet implemented");
  }
}

package jianshu.io.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import net.tsz.afinal.FinalBitmap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import jianshu.io.app.dialog.NotReadyFragment;
import jianshu.io.app.fragment.CardFragment;
import jianshu.io.app.fragment.HotPagerFragment;
import jianshu.io.app.fragment.TimeStreamFragment;
import jianshu.io.app.model.JianshuSession;
import jianshu.io.app.model.StatePool;
import jianshu.io.app.model.UserInfo;
import jianshu.io.app.model.UserInfoManager;
import jianshu.io.app.model.datapool.HomePageDataPool;
import jianshu.io.app.widget.ActionBarDecor;
import jianshu.io.app.widget.SlidingMenuLayout;

public class MainActivity extends ActionBarActivity
    implements AdapterView.OnItemClickListener,
    UserInfoManager.UserInfoManagerListener, JianshuSession.JianshuSessionListener, TimeStreamFragment.OnFragmentInteractionListener {

  private static final int LOGIN_FROM_START = 0;
  private static final int LOGIN_FROM_BUTTON = 1;

  static final Map<Integer, String> mTitles = new HashMap<Integer, String>(){
  };

  static {
    mTitles.put(R.id.home, "首页");
    mTitles.put(R.id.hot, "热门");
    mTitles.put(R.id.timeline, "动态");
  }

  private CharSequence mTitle;
  private CharSequence mDrawerTitle;
  private FinalBitmap finalBitmap;
  private Handler handler;
  private boolean backFromLogin;
  private int mSelectedMenuId;
  private SlidingMenuLayout mSlidingMenuLayout;
  private ActionBarDecor mActionBarDecor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    boolean isTranslucent = false;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window w = getWindow();
      w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      isTranslucent = true;
    }
    setContentView(R.layout.activity_main);
    if(isTranslucent) {
      SystemBarTintManager tintManager = new SystemBarTintManager(this);
      tintManager.setTintColor(getResources().getColor(R.color.primary_dark));
      tintManager.setStatusBarTintEnabled(true);
      tintManager.setNavigationBarTintEnabled(true);
    }

    mSlidingMenuLayout = new SlidingMenuLayout(this);
    View menu = this.getLayoutInflater().inflate(R.layout.drawer_menu, null);
    mSlidingMenuLayout.init(this, menu);

    mActionBarDecor = new ActionBarDecor(this);
    mActionBarDecor.setIconClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mSlidingMenuLayout.toggle();
      }
    });

    initDrawer(menu);

    Object[] state = StatePool.getInstance().getState("main");
    if (state != null) {
      mSelectedMenuId = (Integer) state[0];
    } else {
      mSelectedMenuId = R.id.home;
    }

    this.finalBitmap = FinalBitmap.create(this);
    this.handler = new Handler();

    UserInfoManager.getsInstance().setListener(this);

    setUiAccordingIfLogin();

    initAlarm();
  }

  private void initDrawer(View menu) {
    ArrayList<View> items = new ArrayList<View>();
    menu.findViewsWithText(items, "menu", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
    for(View item : items) {
      item.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          selectItem(v.getId());
          mSlidingMenuLayout.toggle();
        }
      });
    }
  }

  private void initAlarm() {
    Intent intent = JianshuIntentService.getStartAt7Intent(this);
    boolean isAlarmSet = (PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_NO_CREATE) != null);
    if (!isAlarmSet) {
      //马上刷新
      startService(intent);

      PendingIntent pendingInent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
      AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.set(Calendar.HOUR_OF_DAY, 7);
      calendar.set(Calendar.MINUTE, 0);
      alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
          calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingInent);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    JianshuSession.getsInstance().addListener(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    JianshuSession.getsInstance().removeListener(this);
    StatePool.getInstance().putState("main", new Object[]{mSelectedMenuId});
  }

  @Override
  protected void onResumeFragments() {
    super.onResumeFragments();
    if (this.backFromLogin) {
      this.backFromLogin = false;
      setUiAccordingIfLogin();
    }
  }

  private void setUiAccordingIfLogin() {
    selectItem(mSelectedMenuId);
//    JianshuSession.getsInstance().validate();
//    if (JianshuSession.getsInstance().isUserLogin()) {
//      showUserInfo(UserInfoManager.getsInstance().getUserInfo());
//      selectItem(mPosition);
//    } else {
//      onLoginRequired();
//    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    backFromLogin = true;
  }

  private void showUserInfo(final UserInfo userInfo) {
//    final MainActivity that = MainActivity.this;
//    this.handler.post(new Runnable() {
//      @Override
//      public void run() {
//        if (userInfo != null) {
//          that.userLoginView.setVisibility(View.GONE);
//          that.finalBitmap.display(that.userAvatar, userInfo.getAvatarUrl());
//          that.userName.setText(userInfo.getName());
//          that.userIntroduce.setText(userInfo.getIntroduce());
//          that.mUserView.setVisibility(View.VISIBLE);
//        } else {
//          that.userLoginView.setVisibility(View.VISIBLE);
//          that.mUserView.setVisibility(View.GONE);
//        }
//      }
//    });

  }

  private void selectItem(int id) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = getRelatedFragment(id);
    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "main").commit();
    mActionBarDecor.setTitle(mTitles.get(id));
  }


  private Fragment getRelatedFragment(int id) {
    Fragment fragment;
    switch (id) {
      case R.id.home:
        fragment = CardFragment.newInstance(HomePageDataPool.HOME_PAGE_URL);
        break;
      case R.id.hot:
        fragment = HotPagerFragment.newInstance();
        break;
      case R.id.timeline:
        fragment = TimeStreamFragment.newInstance();
        break;
      default:
        fragment = NotReadyFragment.newInstance();
    }
    return fragment;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    selectItem(position);
  }

  @Override
  public void onUserInfoChanged(UserInfo userInfo) {
    showUserInfo(userInfo);
  }

  @Override
  public void onLogin() {

  }

  @Override
  public void onLogout() {
    Intent intent = new Intent(this, LoginActivity.class);
    startActivityForResult(intent, LOGIN_FROM_START);
    MainActivity.this.overridePendingTransition(R.anim.slide_in_left, 0);
  }

  @Override
  public void onLoginRequired() {
    Intent intent = new Intent(this, LoginActivity.class);
    startActivityForResult(intent, LOGIN_FROM_START);
    MainActivity.this.overridePendingTransition(R.anim.slide_in_left, 0);
  }
}

package jianshu.io.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import java.util.Calendar;

import jianshu.io.app.dialog.NotReadyFragment;
import jianshu.io.app.fragment.CardFragment;
import jianshu.io.app.fragment.HotPagerFragment;
import jianshu.io.app.model.JianshuSession;
import jianshu.io.app.model.StatePool;
import jianshu.io.app.model.UserInfo;
import jianshu.io.app.model.UserInfoManager;
import jianshu.io.app.model.datapool.DataPool;
import jianshu.io.app.model.datapool.HomePageDataPool;
import jianshu.io.app.widget.AfinalRoundedImageView;

public class MainActivity extends ActionBarActivity
    implements AdapterView.OnItemClickListener,
    UserInfoManager.UserInfoManagerListener, JianshuSession.JianshuSessionListener {

  private static final int LOGIN_FROM_START = 0;
  private static final int LOGIN_FROM_BUTTON = 1;

  LinearLayout mMenus;
  DrawerLayout mDrawerLayout;
  View mDrawerContianer;
  View mUserView;
  AfinalRoundedImageView userAvatar;
  TextView userName;
  TextView userIntroduce;
  View userLoginView;
  Button loginBtn;

  private CharSequence mTitle;
  private CharSequence mDrawerTitle;
  private ActionBarDrawerToggle mDrawerToggle;
  private String[] mTitles;
  private FinalBitmap finalBitmap;
  private Handler handler;
  private boolean backFromLogin;
  private View selectedView;
  private DataPool mHomeDataPool = new HomePageDataPool();
  private int mPosition;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initAlarm();

    Object[] state = StatePool.getInstance().getState("main");
    if (state != null) {
      mPosition = (Integer) state[0];
    }

    initViews();

    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);

    mTitles = getResources().getStringArray(R.array.drawer_titles);
    mTitle = mDrawerTitle = getTitle();

    initDrawer();

    this.finalBitmap = FinalBitmap.create(this);

    this.handler = new Handler();

    this.loginBtn.setOnClickListener(new Button.OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_FROM_BUTTON);
        MainActivity.this.overridePendingTransition(R.anim.slide_in_left, 0);
      }
    });

    UserInfoManager.getsInstance().setListener(this);

    setUiAccordingIfLogin();
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

  private void initViews() {
    mMenus = (LinearLayout) findViewById(R.id.left_drawer);
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerContianer = findViewById(R.id.drawer_container);
    mUserView = findViewById(R.id.user);
    userAvatar = (AfinalRoundedImageView) findViewById(R.id.userAvatar);
    userName = (TextView) findViewById(R.id.userName);
    userIntroduce = (TextView) findViewById(R.id.userIntroduce);
    userLoginView = findViewById(R.id.user_login);
    loginBtn = (Button) findViewById(R.id.login_btn);
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
    StatePool.getInstance().putState("main", new Object[]{mPosition});
  }

    @Override
    protected void onResumeFragments() {
      super.onResumeFragments();
      if (this.backFromLogin) {
        this.backFromLogin = false;
        JianshuSession.getsInstance().validate();
        setUiAccordingIfLogin();
      }
  }

  private void setUiAccordingIfLogin() {
    JianshuSession.getsInstance().validate();
    if (JianshuSession.getsInstance().isUserLogin()) {
      showUserInfo(UserInfoManager.getsInstance().getUserInfo());
      selectItem(mPosition);
    } else {
      Intent intent = new Intent(this, LoginActivity.class);
      startActivityForResult(intent, LOGIN_FROM_START);
      MainActivity.this.overridePendingTransition(R.anim.slide_in_left, 0);
    }
  }

  private void initDrawer() {
    int count = mMenus.getChildCount();
    for (int i = 0; i < count; i++) {
      View view = mMenus.getChildAt(i);
      view.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          int position = 0;
          switch (v.getId()) {
            case R.id.home:
              position = 0;
              break;
            case R.id.hot:
              position = 1;
              break;
//            case R.id.th:
//              position = 2;
//              break;
            default:
              break;
          }
          selectItem(position);
        }
      });
    }

    mDrawerToggle = new ActionBarDrawerToggle(
        this,                  /* host Activity */
        mDrawerLayout,         /* DrawerLayout object */
        R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
        R.string.drawer_open,  /* "open drawer" description for accessibility */
        R.string.drawer_close  /* "close drawer" description for accessibility */
    ) {
      public void onDrawerClosed(View view) {
        getActionBar().setTitle(mTitle);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }

      public void onDrawerOpened(View drawerView) {
        getActionBar().setTitle(mDrawerTitle);
        invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
      }
    };
    mDrawerLayout.setDrawerListener(mDrawerToggle);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    backFromLogin = true;
  }

  private void showUserInfo(final UserInfo userInfo) {
    final MainActivity that = MainActivity.this;
    this.handler.post(new Runnable() {
      @Override
      public void run() {
        if (userInfo != null) {
          that.userLoginView.setVisibility(View.GONE);
          that.finalBitmap.display(that.userAvatar, userInfo.getAvatarUrl());
          that.userName.setText(userInfo.getName());
          that.userIntroduce.setText(userInfo.getIntroduce());
          that.mUserView.setVisibility(View.VISIBLE);
        } else {
          that.userLoginView.setVisibility(View.VISIBLE);
          that.mUserView.setVisibility(View.GONE);
        }
      }
    });

  }

  private void selectItem(int position) {
    mPosition = position;
    View v = mMenus.getChildAt(position);
    if (this.selectedView == v) {
      return;
    }
    if (this.selectedView != null) {
      this.selectedView.setSelected(false);
    }
    v.setSelected(true);
    this.selectedView = v;
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment = getRelatedFragment(position);
    if (fragment instanceof DialogFragment) {
      ((DialogFragment) fragment).show(fragmentManager, "not ready");
    } else {
      fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "home").commit();
    }

    setTitle(mTitles[position]);
    mDrawerLayout.closeDrawer(mDrawerContianer);
  }


  private Fragment getRelatedFragment(int position) {
    Fragment fragment;
    switch (position) {
      case 0:
        fragment = CardFragment.newInstance(HomePageDataPool.HOME_PAGE_URL);
        break;
      case 1:
        fragment = HotPagerFragment.newInstance();
        break;
      default:
        fragment = NotReadyFragment.newInstance();
    }
    return fragment;
  }

  @Override
  public void setTitle(CharSequence title) {
    mTitle = title;
    getActionBar().setTitle(mTitle);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    // If the nav drawer is open, hide action items related to the content view
    boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerContianer);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // The action bar home/up action should open or close the drawer.
    // ActionBarDrawerToggle will take care of this.
    if (mDrawerToggle.onOptionsItemSelected(item)) {
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggls
    mDrawerToggle.onConfigurationChanged(newConfig);
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
}

package jianshu.io.app;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.annotation.view.ViewInject;

import jianshu.io.app.dialog.NotReadyFragment;
import jianshu.io.app.widget.AfinalRoundedImageView;
import model.JianshuSession;
import model.UserInfo;
import model.UserInfoManager;

public class MainActivity extends FinalActivity
  implements AdapterView.OnItemClickListener,
  UserInfoManager.UserInfoManagerListener
{

  private static final int LOGIN_FROM_START = 0;
  private static final int LOGIN_FROM_BUTTON = 1;

  @ViewInject(id = R.id.left_drawer)
  ListView mDrawerList;
  @ViewInject(id = R.id.drawer_layout)
  DrawerLayout mDrawerLayout;
  @ViewInject(id = R.id.drawer_container)
  View mDrawerContianer;
  @ViewInject(id = R.id.user)
  View mUserView;
  @ViewInject(id = R.id.userAvatar)
  AfinalRoundedImageView userAvatar;
  @ViewInject(id = R.id.userName)
  TextView userName;
  @ViewInject(id = R.id.userIntroduce)
  TextView userIntroduce;
  @ViewInject(id = R.id.user_login)
  View userLoginView;
  @ViewInject(id = R.id.login_btn)
  Button loginBtn;

  private CharSequence mTitle;
  private CharSequence mDrawerTitle;
  private ActionBarDrawerToggle mDrawerToggle;
  private String[] mTitles;
  private FinalBitmap finalBitmap;
  private Handler handler;
  private boolean backFromLogin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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

  @Override
  protected void onResume() {
    super.onResume();
    if(this.backFromLogin) {
      this.backFromLogin = false;
      JianshuSession.getsInstance().validate();
      setUiAccordingIfLogin();
    }
  }

  private void setUiAccordingIfLogin() {
    if(JianshuSession.getsInstance().isUserLogin()) {
      showUserInfo(UserInfoManager.getsInstance().getUserInfo());
      selectItem(0);
    } else {
      Intent intent = new Intent(this, LoginActivity.class);
      startActivityForResult(intent, LOGIN_FROM_START);
      MainActivity.this.overridePendingTransition(R.anim.slide_in_left, 0);
    }
  }

  private void initDrawer() {
    mDrawerList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        R.layout.drawer_item,
        mTitles));
    mDrawerList.setOnItemClickListener(this);
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
    Fragment fragment = getRelatedFragment(position);
    FragmentManager fragmentManager = getFragmentManager();
    if (fragment instanceof DialogFragment) {
      ((DialogFragment) fragment).show(fragmentManager, "not ready");
    } else {
      fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    mDrawerList.setItemChecked(position, true);
    setTitle(mTitles[position]);
    mDrawerLayout.closeDrawer(mDrawerContianer);
  }


  private Fragment getRelatedFragment(int position) {
    switch (position) {
      case 0:
        return RecommendationFragment.newInstance();
      default:
        return NotReadyFragment.newInstance();
    }
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

}

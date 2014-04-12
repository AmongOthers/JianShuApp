package jianshu.io.app;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.annotation.view.ViewInject;

import jianshu.io.app.widget.AfinalRoundedImageView;
import model.JianshuSession;
import model.UserInfo;

public class MainActivity extends FinalActivity {

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

  private CharSequence mTitle;
  private CharSequence mDrawerTitle;
  private ActionBarDrawerToggle mDrawerToggle;
  private String[] mTitles;
  private String usereId;
  private JianshuSession session;
  private FinalBitmap finalBitmap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    this.finalBitmap = FinalBitmap.create(this);

    mTitles = getResources().getStringArray(R.array.drawer_titles);
    mTitle = mDrawerTitle = getTitle();
    mDrawerList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
        R.layout.drawer_item,
        mTitles));
    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);

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

    if (savedInstanceState == null) {
      selectItem(0);
    }

    this.session = ((JianshuApplication) this.getApplication()).getSession();
    if(this.session.isUserLogin()) {
      final MainActivity that = this;
      (new AsyncTask<Void, Void, UserInfo>(){
        @Override
        protected UserInfo doInBackground(Void... params) {
          return UserInfo.load(that);
        }

        @Override
        protected void onPostExecute(UserInfo userInfo) {
          if(userInfo != null) {
            that.finalBitmap.display(that.userAvatar, userInfo.getAvatarUrl());
            that.userName.setText(userInfo.getName());
            that.userIntroduce.setText(userInfo.getIntroduce());
            mUserView.setVisibility(View.VISIBLE);
          }
        }
      }).execute();
    }
  }

  public void showUserInfo(String userId) {
    this.usereId = userId;
    final MainActivity that = MainActivity.this;
    if(usereId != null) {
      (new AsyncTask<Void, Void, UserInfo>(){

        @Override
        protected UserInfo doInBackground(Void... params) {
          return UserInfo.load(that, that.session, that.usereId);
        }

        @Override
        protected void onPostExecute(UserInfo userInfo) {
          that.finalBitmap.display(that.userAvatar, userInfo.getAvatarUrl());
          that.userName.setText(userInfo.getName());
          that.userIntroduce.setText(userInfo.getIntroduce());
          mUserView.setVisibility(View.VISIBLE);
        }

      }).execute();
    } else {
      mUserView.setVisibility(View.GONE);
    }
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
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
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

  class DrawerItemClickListener implements AdapterView.OnItemClickListener {

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
      selectItem(i);
    }
  }
}

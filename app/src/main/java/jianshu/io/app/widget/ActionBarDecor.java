package jianshu.io.app.widget;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shamanland.fonticon.FontIconView;

import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/8/10.
 */
public class ActionBarDecor {
  FontIconView mIcon;
  TextView mTextView;
  ActionBar mActionBar;
  android.support.v7.app.ActionBar mSupportActionBar;

  public ActionBarDecor(Context context, android.app.ActionBar actionBar, boolean isBackMode) {
    mActionBar = actionBar;
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View actionBarDeco;
    if(isBackMode) {
      actionBarDeco = li.inflate(R.layout.material_action_bar_back, null);
    } else {
      actionBarDeco = li.inflate(R.layout.material_action_bar, null);
    }
    actionBar.setCustomView(actionBarDeco, new ActionBar.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    ));
    mIcon = (FontIconView) actionBarDeco.findViewById(R.id.icon);
    mTextView = (TextView) actionBarDeco.findViewById(R.id.title);
  }

  public ActionBarDecor(Context context, android.support.v7.app.ActionBar actionBar, boolean isBackMode) {
    mSupportActionBar = actionBar;
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
    LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View actionBarDeco;
    if(isBackMode) {
      actionBarDeco = li.inflate(R.layout.material_action_bar_back, null);
    } else {
      actionBarDeco = li.inflate(R.layout.material_action_bar, null);
    }
    actionBar.setCustomView(actionBarDeco, new android.support.v7.app.ActionBar.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    ));
    mIcon = (FontIconView) actionBarDeco.findViewById(R.id.icon);
    mTextView = (TextView) actionBarDeco.findViewById(R.id.title);
  }

  public void setIconClickListener(View.OnClickListener listener) {
    mIcon.setOnClickListener(listener);
  }

  public void setTitle(String mTitle) {
    mTextView.setText(mTitle);
  }

  public void show() {
    if(mActionBar != null) {
      mActionBar.show();
    } else {
      mSupportActionBar.show();
    }
  }

  public void hide() {
    if(mActionBar != null) {
      mActionBar.hide();
    } else {
      mSupportActionBar.hide();
    }
  }
}

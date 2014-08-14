package jianshu.io.app.widget;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jianshu.io.app.R;

/**
 * Created by Administrator on 2014/8/10.
 */
public class ActionBarDecor {
  View mIcon;
  TextView mTextView;

  public ActionBarDecor(ActionBarActivity activity) {
    ActionBar actionBar = activity.getSupportActionBar();
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
    LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View actionBarDeco = li.inflate(R.layout.material_action_bar, null);
    actionBar.setCustomView(actionBarDeco, new ActionBar.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    ));
    mIcon = actionBarDeco.findViewById(R.id.icon);
    mTextView = (TextView) actionBarDeco.findViewById(R.id.title);
  }

  public void setIconClickListener(View.OnClickListener listener) {
    mIcon.setOnClickListener(listener);
  }

  public void setTitle(String mTitle) {
    mTextView.setText(mTitle);
  }
}

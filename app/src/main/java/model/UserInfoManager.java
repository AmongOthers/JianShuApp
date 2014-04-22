package model;

import android.content.Context;

/**
 * Created by Administrator on 2014/4/19.
 */
public class UserInfoManager implements JianshuSession.JianshuSessionListener {

  private static UserInfoManager sInstance;

  private Context context;
  private UserInfo cachedUserInfo;
  private String userId;
  private UserInfo userInfo;
  private UserInfoManagerListener listener;

  public static synchronized UserInfoManager init(Context context) {
    if(sInstance == null) {
      sInstance = new UserInfoManager(context);
    }
    return sInstance;
  }

  public static UserInfoManager getsInstance() {
    return sInstance;
  }

  private UserInfoManager(Context context) {
    this.context = context;
    this.cachedUserInfo = UserInfo.loadFromFileCache(this.context);
    JianshuSession.getsInstance().setListener(this);
    if(JianshuSession.getsInstance().isUserLogin()) {
      setUserInfo(this.cachedUserInfo);
    } else {
      setUserInfo(null);
    }
  }

  public void setListener(UserInfoManagerListener listener) {
    this.listener = listener;
  }

  public UserInfo getUserInfo() {
    return this.userInfo;
  }

  private void setUserInfo(UserInfo userInfo) {
    this.userInfo = userInfo;
    if(this.listener != null) {
      this.listener.onUserInfoChanged(this.userInfo);
    }
  }

  public void refresh() {
    if (this.userId != null) {
      UserInfo userInfo = UserInfo.load(this.context, this.userId);
      if (userInfo != null) {
        if (this.userInfo == null || !isSame(userInfo)) {
          setUserInfo(userInfo);
        }
      }
    }
  }

  private boolean isSame(UserInfo userInfo) {
    return this.userInfo.getUserId().equals(userInfo.getUserId()) &&
        this.userInfo.getName().equals(userInfo.getName()) &&
        this.userInfo.getAvatarUrl().equals(userInfo.getAvatarUrl()) &&
        this.userInfo.getIntroduce().equals(userInfo.getIntroduce());
  }

  public void setUserId(String userId) {
    String oldUserId = this.userId;
    this.userId = userId;
    JianshuSession.getsInstance().validate();
    if(oldUserId == null || !oldUserId.equals(userId)) {
      if(this.cachedUserInfo != null) {
        if(this.cachedUserInfo.getUserId().equals(userId)) {
          setUserInfo(this.cachedUserInfo);
        }
      }
      refresh();
    }
  }

  @Override
  public void onLogin() {
  }

  @Override
  public void onLogout() {
    setUserInfo(null);
    this.userId = null;
  }

  public interface UserInfoManagerListener {
    void onUserInfoChanged(UserInfo userInfo);
  }
}

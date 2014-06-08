package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class UserUpdateItem extends UpdateItem{

  String mUser;
  String mTarget;
  String mAction;

  public UserUpdateItem(String user, String action, String target, String avatarUrl, String time) {
    super(avatarUrl, time);
    mUser = user;
    mAction = action;
    mTarget = target;
  }

  public String getUser() {
    return mUser;
  }

  public String getTarget() {
    return mTarget;
  }

  public String getAction() {
    return mAction;
  }
}

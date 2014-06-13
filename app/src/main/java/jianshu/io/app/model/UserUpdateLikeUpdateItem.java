package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class UserUpdateLikeUpdateItem extends UserUpdateItem{

  String mUrl;

  public UserUpdateLikeUpdateItem(String url, String user, String action, String target, String avatarUrl, String time) {
    super(user, action, target, avatarUrl, time);
    mUrl = url;
  }

  public String getUrl() {
    return mUrl;
  }
}

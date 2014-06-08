package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public abstract class UpdateItem {
  private String mAvatarUrl;
  private String mTime;

  public UpdateItem(String avatarUrl, String time) {
    mAvatarUrl = avatarUrl;
    mTime = time;
  }

  public String getAvatarUrl() {
    return mAvatarUrl;
  }

  public String getTime() {
    return mTime;
  }

}

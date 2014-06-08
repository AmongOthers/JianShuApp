package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class UserUpdateArticleUpdateItem extends UpdateItem{
  private String mTitle;
  private String mSummary;

  public UserUpdateArticleUpdateItem(String title, String content, String avatarUrl, String time) {
    super(avatarUrl, time);
    mTitle = title;
    mSummary = content;
  }

  public String getTitle() {
    return mTitle;
  }

  public String getSummary() {
    return mSummary;
  }
}

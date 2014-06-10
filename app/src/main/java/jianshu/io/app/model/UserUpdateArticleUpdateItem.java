package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class UserUpdateArticleUpdateItem extends UpdateItem{

  private String mTitle;
  private String mSummary;
  private String mUrl;

  public UserUpdateArticleUpdateItem(String title, String summary, String url, String avatarUrl, String time) {
    super(avatarUrl, time);
    mTitle = title;
    mSummary = summary;
    mUrl = url;
  }

  public String getTitle() {
    return mTitle;
  }

  public String getSummary() {
    return mSummary;
  }

  public String getUrl() {
    return mUrl;
  }
}

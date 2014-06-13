package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class UserCommentUpdateItem extends UserUpdateItem{

  String mContent;
  String mUrl;

  public UserCommentUpdateItem(String url, String content, String user, String article, String avatarUrl, String time) {
    super(user, user + " 评论了文章 " + article, article, avatarUrl, time);
    mContent = content;
    mUrl = url;
  }

  public String getContent() {
    return mContent;
  }

  public String getUrl() {
    return mUrl;
  }
}

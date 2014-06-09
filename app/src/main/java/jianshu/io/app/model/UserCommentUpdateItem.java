package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class UserCommentUpdateItem extends UserUpdateItem{

  String mContent;

  public UserCommentUpdateItem(String content, String user, String article, String avatarUrl, String time) {
    super(user, user + " 评论了文章 " + article, article, avatarUrl, time);
    mContent = content;
  }

  public String getContent() {
    return mContent;
  }

}

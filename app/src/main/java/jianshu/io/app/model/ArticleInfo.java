package jianshu.io.app.model;

import java.util.List;

/**
 * Created by Administrator on 2014/5/11.
 */
public class ArticleInfo {
  private String notebook;
  private List<String> topics;
  private int commentCount;
  private int likeCount;
  private boolean _isLiking;


  public ArticleInfo(String notebook, List<String> topics, int commentCount,
                     int likeCount, boolean _isLiking) {
    this.notebook = notebook;
    this.topics = topics;
    this.commentCount = commentCount;
    this.likeCount = likeCount;
    this._isLiking = _isLiking;
  }

  public String getNotebook() {
    return notebook;
  }

  public List<String> getTopics() {
    return topics;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public boolean isLiking() {
    return this._isLiking;
  }

}

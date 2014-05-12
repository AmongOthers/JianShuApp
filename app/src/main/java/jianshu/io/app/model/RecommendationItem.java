package jianshu.io.app.model;

import java.util.List;

/**
 * Created by Administrator on 14-3-21.
 */
public class RecommendationItem {

  private String title;
  private String avatar;
  private String summary;
  private String url;
  private String author;
  private ArticleInfo articleInfo;

  public RecommendationItem(String title,
                            String avatar,
                            String summary,
                            String url,
                            String author,
                            ArticleInfo articleInfo
                            ) {
    this.title = title;
    this.avatar = avatar;;
    this.summary = summary;
    this.url = url;
    this.author = author;
    this.articleInfo = articleInfo;
  }

  public String getTitle() {
    return title;
  }

  public String getSummary() {
    return summary;
  }

  public String getAvatar() {
    return avatar;
  }

  public String getUrl() {
    return url;
  }

  public String getAuthor() {
    return author;
  }

  public String getNotebook() {
    return this.articleInfo.getNotebook();
  }

  public List<String> getTopics() {
    return this.articleInfo.getTopics();
  }

  public int getCommentCount() {
    return this.articleInfo.getCommentCount();
  }

  public int getLikeCount() {
    return this.articleInfo.getLikeCount();
  }

  public boolean isLiking() {
    return this.articleInfo.isLiking();
  }

}

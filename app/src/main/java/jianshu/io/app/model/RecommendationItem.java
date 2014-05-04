package jianshu.io.app.model;

/**
 * Created by Administrator on 14-3-21.
 */
public class RecommendationItem {
  public RecommendationItem(String title, String avatar, String summary, String url, String author) {
    this.title = title;
    this.avatar = avatar;;
    this.summary = summary;
    this.url = url;
    this.author = author;
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

  private String title;
  private String avatar;
  private String summary;
  private String url;
  private String author;
}

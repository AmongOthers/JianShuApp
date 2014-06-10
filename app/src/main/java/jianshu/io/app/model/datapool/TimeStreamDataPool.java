package jianshu.io.app.model.datapool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jianshu.io.app.model.CollectionUpdateItem;
import jianshu.io.app.model.UserCommentUpdateItem;
import jianshu.io.app.model.UnknownUpdateItem;
import jianshu.io.app.model.UpdateItem;
import jianshu.io.app.model.UserSubscribeUpdateItem;
import jianshu.io.app.model.UserUpdateArticleUpdateItem;
import jianshu.io.app.model.UserUpdateFollowUpdateItem;
import jianshu.io.app.model.UserUpdateLikeUpdateItem;

/**
 * Created by Administrator on 2014/6/7.
 */
public class TimeStreamDataPool extends DataPool {

  public static final String TIMELINE_URL = "http://jianshu.io/timeline";

  protected final static String LOAD_MORE_SELECTOR = "div.load-more button";
  protected final static String CURRENT_USER_SLUG = "meta#current_user_slug";

  public TimeStreamDataPool() {
    super(TIMELINE_URL);
  }

  protected void onRefresh() {

  }

  @Override
  protected Object[] getItems(Document doc) {
    return getUpdateItems(doc);
  }

  private UpdateItem[] getUpdateItems(Document doc) {
    Elements loadMoreElements = doc.select(LOAD_MORE_SELECTOR);
    if (loadMoreElements.size() > 0) {
      mLoadMoreUrl = getHtmlUrl(loadMoreElements.get(0).attr("data-url"));
    } else {
      mLoadMoreUrl = null;
      mIsAtTheEnd = true;}


    UpdateItem[] result = null;
    Elements timelineEls = doc.select("ul.timeline-content li");
    result = new UpdateItem[timelineEls.size()];
    for (int i = 0; i < timelineEls.size(); i++) {
      Element timelineEl = timelineEls.get(i);
      String avatarUrl = parseAvatarUrl(timelineEl);
      String time = parseMetaTime(timelineEl);
      if (timelineEl.hasClass("user-update")) {
        if (timelineEl.hasClass("article")) {
          result[i] = parseUserUpdateArticle(timelineEl, avatarUrl, time);
        } else {
          Element span = timelineEl.select("span").get(0);
          String action = span.text();
          Elements metaEls = span.select("a");
          Element userMetaEl = metaEls.get(0);
          String user = userMetaEl.text();
          Element targetMetaEl = metaEls.get(1);
          String target = targetMetaEl.text();
          if(action.contains(user + " 喜欢")) {
            result[i] = new UserUpdateLikeUpdateItem(user, action, target, avatarUrl, time);
          } else if(action.contains(user + " 关注")) {
            result[i] = new UserUpdateFollowUpdateItem(user, action, target, avatarUrl, time);
          } else if(action.contains(user + " 订阅")) {
            result[i] = new UserSubscribeUpdateItem(user, action, target, avatarUrl, time);
          }
        }
      } else if (timelineEl.hasClass("comment")) {
        Element metaSpan = timelineEl.select("div.meta span").get(0);
        Elements metaEls = metaSpan.select("a");
        Element userMetaEl = metaEls.get(0);
        String user = userMetaEl.text();
        Element targetMetaEl = metaEls.get(1);
        String target = targetMetaEl.text();
        result[i] = parseComment(timelineEl, avatarUrl, time, user, target);
      } else if(timelineEl.hasClass("collection-update")) {
        Element span = timelineEl.select("span").get(0);
        String action = span.text();
        Elements metaEls = span.select("a");
        Element userMetaEl = metaEls.get(0);
        String user = userMetaEl.text();
        Element targetMetaEl = metaEls.get(1);
        String target = targetMetaEl.text();
        result[i] = new CollectionUpdateItem(user, action, target, time);
      } else {
        result[i] = parseUnknownUpdateItem(timelineEl, avatarUrl, time);
      }
    }
    return result;
  }

  private UpdateItem parseComment(Element timelineEl, String avatarUrl, String time, String user, String target) {
    Element commentContentEl = timelineEl.select("p.comment-content").get(0);
    String commentContent = extracContent(commentContentEl.text());
    return new UserCommentUpdateItem(commentContent, user, target, avatarUrl, time);
  }

  private String extracContent(String text) {
    return text.replace("\"", "").trim();
  }

  private UpdateItem parseUnknownUpdateItem(Element timelineEl, String avatarUrl, String time) {
    return new UnknownUpdateItem(timelineEl.text(), avatarUrl, time);
  }

  private UpdateItem parseUserUpdateArticle(Element timelineEl, String avatarUrl, String time) {
    Element articleContentEl = timelineEl.select("div.article-content").get(0);
    Element titleEl = articleContentEl.select("a.title").get(0);
    String title = titleEl.text();
    String url = "http://jianshu.io" + titleEl.attr("href");
    Element contentEl = articleContentEl.select("p").get(0);
    String content = contentEl.text();
    return new UserUpdateArticleUpdateItem(title, content, url, avatarUrl, time);
  }

  private String getHtmlUrl(String url) {
    String[] fragments = url.split("\\?");
    return TIMELINE_URL + ".html?" + fragments[1];
  }

  private String parseMetaTime(Element timelineEl) {
    Elements timeEls = timelineEl.select("div.meta time");
    if(timeEls.size() > 0) {
      return timeEls.get(0).text();
    }
    return null;
  }

  private String parseAvatarUrl(Element timelineEl) {
    Elements imgEls = timelineEl.select("div.avatar img");
    if(imgEls.size() > 0) {
      return imgEls.get(0).attr("src");
    } else {
      return null;
    }
  }
}

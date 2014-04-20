package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Administrator on 2014/3/29.
 */
public class HomePageDataPool {

  protected final static String HOME_PAGE_URL = "http://jianshu.io";
  protected final static String ARTICLE_SELECTOR = "div.article";
  protected final static String TITLE_SELECTOR = "a.title";
  protected final static String AVATAR_SELECTOR = "a.avatar > img";
  protected final static String CONTENT_SELECTOR = "a.content";
  protected final static String LOAD_MORE_SELECTOR = "div.load-more button";
  protected final static String URL_SELECTOR = "a.content";
  protected final static String AUTHOR_SELECTOR = "a.author";
  protected final static String CURRENT_USER_SLUG = "meta#current_user_slug";

  private boolean mIsAtTheEnd;
  private String mLoadMoreUrl;

  public HomePageDataPool() {
  }

  public RecommendationItem[] refresh() throws IOException{
    mIsAtTheEnd = false;
    String url = HOME_PAGE_URL;
    return load(url);
  }

  private RecommendationItem[] load(String url) throws IOException {
    Object httpResult = JianshuSession.getsInstance().getSync(url, true);
    if (httpResult instanceof String) {
      Document doc = Jsoup.parse((String) httpResult);

      //页面中如果包含用户信息，说明是已登录的
      String userId = null;
      Elements userElements = doc.select(CURRENT_USER_SLUG);
      if(userElements.size() > 0) {
        Element userEl = userElements.get(0);
        userId = userEl.attr("value");
      }
      UserInfoManager.getsInstance().setUserId(userId);

      Elements loadMoreElements = doc.select(LOAD_MORE_SELECTOR);
      if (loadMoreElements.size() > 0) {
//        mLoadMoreUrl = HOME_PAGE_URL + loadMoreElements.get(0).attr("data-url");
        mLoadMoreUrl = getHtmlUrl(loadMoreElements.get(0).attr("data-url"));
      } else {
        mLoadMoreUrl = null;
        mIsAtTheEnd = true;
      }
      Elements articleElements = doc.select(ARTICLE_SELECTOR);
      if (articleElements != null) {
        int i = 0;
        RecommendationItem[] result = new RecommendationItem[articleElements.size()];
        for (Element el : articleElements) {
          result[i++] = parseElement(el);
        }
        return result;
      } else {
        return null;
      }
    } else {
      return null;
    }
//    Connection connection = Jsoup.connect(url)
//        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//        .header("Cache-Control", "max-age=0");
//    Document doc = connection.get();

  }

  private String getHtmlUrl(String url) {
    String[] fragments = url.split("\\?");
    return HOME_PAGE_URL + fragments[0] + ".html?" + fragments[1];
  }

  public RecommendationItem[] pull() throws IOException {
    if(!mIsAtTheEnd) {
      return load(mLoadMoreUrl);
    } else {
      return null;
    }
  }

  public boolean isAtTheEnd() {
    return mIsAtTheEnd;
  }

  public RecommendationItem parseElement(Element el) {
    Element titleEl = el.select(TITLE_SELECTOR).get(0);
    String title = titleEl.text();
    Element avatarEl = el.select(AVATAR_SELECTOR).get(0);
    String avatarUrl = avatarEl.attr("src");
    Element contentEl = el.select(CONTENT_SELECTOR).get(0);
    String content = contentEl.text();
    Element urlEl = el.select(URL_SELECTOR).get(0);
    String url = HOME_PAGE_URL + urlEl.attr("href");
    Element authorEl = el.select(AUTHOR_SELECTOR).get(0);
    String author = authorEl.text();
    return new RecommendationItem(title, avatarUrl, content, url, author);
  }
}

package jianshu.io.app.model.datapool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jianshu.io.app.model.ArticleInfo;
import jianshu.io.app.model.RecommendationItem;

/**
 * Created by Administrator on 2014/3/29.
 */
public class HomePageDataPool extends DataPool {

  public final static String HOME_PAGE_URL = "http://jianshu.io";

  protected final static String ARTICLE_SELECTOR = "div.article";
  protected final static String TITLE_SELECTOR = "a.title";
  protected final static String AVATAR_SELECTOR = "a.avatar > img";
  protected final static String CONTENT_SELECTOR = "a.content";
  protected final static String LOAD_MORE_SELECTOR = "div.load-more button";
  protected final static String URL_SELECTOR = "a.content";
  protected final static String AUTHOR_SELECTOR = "a.author";
  protected final static String CURRENT_USER_SLUG = "meta#current_user_slug";

  public HomePageDataPool() {
    super(HOME_PAGE_URL);
  }

  @Override
  protected void onRefresh() {
    //nothing to do
  }

  @Override
  protected RecommendationItem[] getRecommendationItems(Document doc) {
    Elements loadMoreElements = doc.select(LOAD_MORE_SELECTOR);
    if (loadMoreElements.size() > 0) {
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
  }

  private String getHtmlUrl(String url) {
    String[] fragments = url.split("\\?");
    return HOME_PAGE_URL + fragments[0] + ".html?" + fragments[1];
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
        Element articleInfoEl = el.select("div.article-info").get(0);
    ArticleInfo articleInfo = parseArticleInfo(articleInfoEl);
    return new RecommendationItem(title, avatarUrl, content, url, author, articleInfo);
  }
}

package jianshu.io.app.model.datapool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jianshu.io.app.model.ArticleInfo;
import jianshu.io.app.model.RecommendationItem;

/**
 * Created by Administrator on 2014/5/5.
 */
public class HotPageDataPool extends DataPool{

  private int pageIndex = 1;

  public HotPageDataPool(String url) {
    super(url);
  }

  @Override
  protected void onRefresh() {
    pageIndex = 1;
  }

  @Override
  protected RecommendationItem[] getRecommendationItems(Document doc) {
    pageIndex++;
    mLoadMoreUrl = mStartUrl + "?_=" + System.currentTimeMillis() +"&page=" + pageIndex;

    Elements articleElements = doc.select("ul.top-notes li");
    if(articleElements != null) {
      int i = 0;
      RecommendationItem[] result = new RecommendationItem[articleElements.size()];
      for(Element el : articleElements) {
        result[i++] = parseElement(el);
      }
      return result;
    } else {
      return null;
    }
  }

  private RecommendationItem parseElement(Element el) {
    Element titleEl = el.select("h4 > a").get(0);
    String title = titleEl.text();
    String url = "http://jianshu.io" + titleEl.attr("href");
    Element avatarEl = el.select("a.avatar > img").get(0);
    String avatarUrl = avatarEl.attr("src");
    Element articleInfoEl = el.select("div.article-info").get(0);
    ArticleInfo articleInfo = parseArticleInfo(articleInfoEl);
    return new RecommendationItem(title, avatarUrl, "", url, "", articleInfo);
  }

}

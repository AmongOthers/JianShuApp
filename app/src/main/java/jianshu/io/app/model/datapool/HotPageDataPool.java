package jianshu.io.app.model.datapool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jianshu.io.app.model.ArticleInfo;
import jianshu.io.app.model.ArticleItem;

/**
 * Created by Administrator on 2014/5/5.
 */
public class HotPageDataPool extends ArticleDataPool{

  private int pageIndex = 1;

  public HotPageDataPool(String url) {
    super(url);
  }

  @Override
  protected void onRefresh() {
    pageIndex = 1;
  }

  @Override
  protected ArticleItem[] getArticleItems(Document doc) {
    pageIndex++;
    mLoadMoreUrl = mStartUrl + "?_=" + System.currentTimeMillis() +"&page=" + pageIndex;

    Elements articleElements = doc.select("ul.top-notes li");
    if(articleElements != null) {
      int i = 0;
      ArticleItem[] result = new ArticleItem[articleElements.size()];
      for(Element el : articleElements) {
        result[i++] = parseElement(el);
      }
      return result;
    } else {
      return null;
    }
  }

  private ArticleItem parseElement(Element el) {
    Element titleEl = el.select("h4 > a").get(0);
    String title = titleEl.text();
    String url = "http://jianshu.io" + titleEl.attr("href");
    Element avatarEl = el.select("a.avatar > img").get(0);
    String avatarUrl = avatarEl.attr("src");
    Element articleInfoEl = el.select("div.article-info").get(0);
    ArticleInfo articleInfo = parseArticleInfo(articleInfoEl);
    return new ArticleItem(title, avatarUrl, "", url, "", articleInfo);
  }

}

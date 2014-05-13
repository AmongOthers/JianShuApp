package jianshu.io.app.model.datapool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import jianshu.io.app.model.ArticleInfo;
import jianshu.io.app.model.RecommendationItem;

/**
 * Created by Administrator on 2014/5/5.
 */
public class WeeklyHotPageDataPool extends DataPool{

  private static final String HOT_URL = "http://jianshu.io/top/weekly.html";

  private int pageIndex = 1;

  public WeeklyHotPageDataPool() {
    super(HOT_URL);
  }

  @Override
  protected void onRefresh() {
    pageIndex = 1;
  }

  @Override
  protected RecommendationItem[] getRecommendationItems(Document doc) {
    pageIndex++;
    mLoadMoreUrl = HOT_URL + "?_=" + System.currentTimeMillis() +"&page=" + pageIndex;

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

  private ArticleInfo parseArticleInfo(Element el) {
    Element notebookEl = el.select("a.notebook").get(0);
    String notebook = notebookEl.text();

    Elements topicEls = el.select("a.collections");
    List<String> topics = new ArrayList<String>(topicEls.size());
    for(Element topicEl : topicEls) {
      topics.add(topicEl.text());
    }

    int commentCount = 0;
    Elements blankTargetEls = el.select("a[target=_blank]");
    for(Element blankTargetEl : blankTargetEls) {
      if(blankTargetEl.attr("href").contains("#comments")) {
        commentCount = string2int(blankTargetEl.text());
        break;
      }
    }

    boolean isLiking = false;
    Elements likeStateEls = el.select(".icon-heart");
    if(likeStateEls.size() > 0) {
      isLiking = true;
    } else {
      isLiking = false;
    }
    Element likeEl = el.select("a.like-icon-button").get(0);
    int likeCount = string2int(likeEl.text());

    return new ArticleInfo(notebook, topics, commentCount, likeCount, isLiking);
  }

  private int string2int(String str) {
    str = str.replace("\"", "").trim();
    return Integer.parseInt(str);
  }

}

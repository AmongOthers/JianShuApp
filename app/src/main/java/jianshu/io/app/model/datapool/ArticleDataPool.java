package jianshu.io.app.model.datapool;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import jianshu.io.app.model.ArticleInfo;
import jianshu.io.app.model.ArticleItem;

/**
 * Created by Administrator on 2014/6/8.
 */
public abstract class ArticleDataPool extends DataPool {

  public ArticleDataPool(String startUrl) {
    super(startUrl);
  }

  @Override
  protected Object[] getItems(Document doc) {
    return getArticleItems(doc);
  }

  protected ArticleInfo parseArticleInfo(Element el) {
    Element notebookEl = el.select("a.notebook").get(0);
    String notebook = notebookEl.text();

    Elements topicEls = el.select("a.collections");
    List<String> topics = new ArrayList<String>(topicEls.size());
    for (Element topicEl : topicEls) {
      topics.add(topicEl.text());
    }

    int commentCount = 0;
    Elements blankTargetEls = el.select("a[target=_blank]");
    for (Element blankTargetEl : blankTargetEls) {
      if (blankTargetEl.attr("href").contains("#comments")) {
        commentCount = string2int(blankTargetEl.text());
        break;
      }
    }

    boolean isLiking = false;
    Elements likeStateEls = el.select(".icon-heart");
    if (likeStateEls.size() > 0) {
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

  protected abstract ArticleItem[] getArticleItems(Document doc);
}

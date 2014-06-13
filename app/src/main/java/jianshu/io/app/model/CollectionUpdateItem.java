package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class CollectionUpdateItem extends UpdateItem{

  String mCollection;
  String mTarget;
  String mAction;
  String mUrl;

  public CollectionUpdateItem(String url, String collection, String action, String target, String time) {
    super(null, time);
    mCollection = collection;
    mAction = action;
    mTarget = target;
    mUrl = url;
  }

  public String getCollection() {
    return mCollection;
  }

  public String getAction() {
    return mAction;
  }

  public String getUrl() {
    return mUrl;
  }
}

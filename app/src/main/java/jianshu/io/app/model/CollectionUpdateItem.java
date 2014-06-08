package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class CollectionUpdateItem extends UpdateItem{

  String mCollection;
  String mTarget;
  String mAction;

  public CollectionUpdateItem(String collection, String action, String target, String time) {
    super(null, time);
    mCollection = collection;
    mAction = action;
    mTarget = target;
  }

  public String getCollection() {
    return mCollection;
  }

  public String getAction() {
    return mAction;
  }

}

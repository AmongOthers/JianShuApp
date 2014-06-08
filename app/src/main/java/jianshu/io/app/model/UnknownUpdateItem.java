package jianshu.io.app.model;

/**
 * Created by Administrator on 2014/6/8.
 */
public class UnknownUpdateItem extends UpdateItem{

  private String mText;

  public UnknownUpdateItem(String text, String avatar, String time) {
    super(avatar, time);
  }

  public String getText() {
    return mText;
  }
}

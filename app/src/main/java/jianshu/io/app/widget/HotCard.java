package jianshu.io.app.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;

import org.jsoup.helper.StringUtil;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;
import jianshu.io.app.model.RecommendationItem;

/**
 * Created by Administrator on 2014/5/11.
 */
public class HotCard extends Card {

  private FinalBitmap fb;
  private RecommendationItem item;
  private TextView title;
  private ImageView avatar;
  private TextView noteBook;
  private TextView topic;
  private TextView comment;
  private TextView like;

  public HotCard(final Context context, RecommendationItem item, FinalBitmap fb) {
    super(context, R.layout.hotcard_content);
    this.item = item;
    this.fb = fb;
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);

    this.title = (TextView)parent.findViewById(R.id.hot_title);
    this.avatar = (ImageView)parent.findViewById(R.id.hot_avatar);
    this.noteBook = (TextView)parent.findViewById(R.id.hot_notebook);
    this.topic = (TextView)parent.findViewById(R.id.hot_th);
    this.comment = (TextView)parent.findViewById(R.id.hot_comment);
    this.like = (TextView)parent.findViewById(R.id.hot_like);


    this.fb.display(this.avatar, this.item.getAvatar());
    this.title.setText(this.item.getTitle());
    this.noteBook.setText(this.item.getNotebook());
    String topicsStr = StringUtil.join(this.item.getTopics(), "");
    this.topic.setText(topicsStr);
    this.comment.setText(String.valueOf(this.item.getCommentCount()));
    this.like.setText(String.valueOf(this.item.getLikeCount()));
  }

  public RecommendationItem getItem() {
    return this.item;
  }
}

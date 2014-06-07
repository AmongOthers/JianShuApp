package jianshu.io.app.card;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shamanland.fonticon.FontIconTextView;

import net.tsz.afinal.FinalBitmap;

import org.jsoup.helper.StringUtil;

import jianshu.io.app.R;
import jianshu.io.app.model.RecommendationItem;

/**
 * Created by Administrator on 2014/5/11.
 */
public class HomeCard extends JianshuBaseCard {

  private TextView title;
  private TextView author;
  private ImageView avatar;
  private FontIconTextView noteBook;
  private FontIconTextView topic;
  private FontIconTextView comment;
  private FontIconTextView like;
  private TextView summary;

  public HomeCard(final Context context, RecommendationItem item, FinalBitmap fb) {
    super(context, item, fb, R.layout.homecard_content);
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);

    this.title = (TextView) parent.findViewById(R.id.hot_title);
    this.summary = (TextView) parent.findViewById(R.id.hot_summary);
    this.author = (TextView) parent.findViewById(R.id.hot_author);
    this.avatar = (ImageView) parent.findViewById(R.id.hot_avatar);
    this.noteBook = (FontIconTextView) parent.findViewById(R.id.hot_notebook);
    this.topic = (FontIconTextView) parent.findViewById(R.id.hot_th);
    this.comment = (FontIconTextView) parent.findViewById(R.id.hot_comment);
    this.like = (FontIconTextView) parent.findViewById(R.id.hot_like);


    this.fb.display(this.avatar, this.item.getAvatar());
    this.title.setText(this.item.getTitle());
    this.summary.setText(this.getItem().getSummary());
    this.author.setText(this.item.getAuthor());
    this.noteBook.setText(this.item.getNotebook());
    String topicsStr = StringUtil.join(this.item.getTopics(), "");
    this.topic.setText(topicsStr);
    this.comment.setText(String.valueOf(this.item.getCommentCount()));
    this.like.setText(String.valueOf(this.item.getLikeCount()));
    if (this.getItem().isLiking()) {
      this.like.setCompoundDrawables(HotCard.heartDrawable, null, null, null);
      this.like.setTextColor(HotCard.JianshuColor);
    } else {
      this.like.setCompoundDrawables(HotCard.heartEmptyDrawable, null, null, null);
      this.like.setTextColor(HotCard.GrayColor);
    }
  }

}
package jianshu.io.app.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shamanland.fonticon.FontIconDrawable;
import com.shamanland.fonticon.FontIconTextView;

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
  private FontIconTextView noteBook;
  private FontIconTextView topic;
  private FontIconTextView comment;
  private FontIconTextView like;
  private static Resources resources;
  private static Drawable heartDrawable;
  private static Drawable heartEmptyDrawable;
  private static int GrayColor = -1;
  private static int JianshuColor = -1;

  public HotCard(final Context context, RecommendationItem item, FinalBitmap fb) {
    super(context, R.layout.hotcard_content);
    this.item = item;
    this.fb = fb;
    if(HotCard.resources == null) {
      HotCard.resources = context.getResources();
    }
    if(HotCard.heartDrawable == null) {
      HotCard.heartDrawable = FontIconDrawable.inflate(HotCard.resources, R.xml.fa_heart);
    }
    if(HotCard.heartEmptyDrawable == null) {
      HotCard.heartEmptyDrawable = FontIconDrawable.inflate(HotCard.resources, R.xml.fa_heart_o);
    }
    if(HotCard.GrayColor == -1) {
      HotCard.GrayColor = HotCard.resources.getColor(R.color.textGray);
    }
    if(HotCard.JianshuColor == -1) {
      HotCard.JianshuColor = HotCard.resources.getColor(R.color.jianshu);
    }
  }

  @Override
  public void setupInnerViewElements(ViewGroup parent, View view) {
    super.setupInnerViewElements(parent, view);


    this.title = (TextView)parent.findViewById(R.id.hot_title);
    this.avatar = (ImageView)parent.findViewById(R.id.hot_avatar);
    this.noteBook = (FontIconTextView)parent.findViewById(R.id.hot_notebook);
    this.topic = (FontIconTextView)parent.findViewById(R.id.hot_th);
    this.comment = (FontIconTextView)parent.findViewById(R.id.hot_comment);
    this.like = (FontIconTextView)parent.findViewById(R.id.hot_like);


    this.fb.display(this.avatar, this.item.getAvatar());
    this.title.setText(this.item.getTitle());
    this.noteBook.setText(this.item.getNotebook());
    String topicsStr = StringUtil.join(this.item.getTopics(), "");
    this.topic.setText(topicsStr);
    this.comment.setText(String.valueOf(this.item.getCommentCount()));
    this.like.setText(String.valueOf(this.item.getLikeCount()));
    if(this.getItem().isLiking()) {
      this.like.setCompoundDrawables(HotCard.heartDrawable, null, null, null);
      this.like.setTextColor(HotCard.JianshuColor);
    } else {
      this.like.setCompoundDrawables(HotCard.heartEmptyDrawable, null, null, null);
      this.like.setTextColor(HotCard.GrayColor);
    }
  }

  public RecommendationItem getItem() {
    return this.item;
  }
}

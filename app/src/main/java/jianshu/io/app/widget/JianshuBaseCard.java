
package jianshu.io.app.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.shamanland.fonticon.FontIconDrawable;

import net.tsz.afinal.FinalBitmap;

import it.gmariotti.cardslib.library.internal.Card;
import jianshu.io.app.R;
import jianshu.io.app.model.RecommendationItem;

/**
 * Created by Administrator on 2014/5/11.
 */
public class JianshuBaseCard extends Card {

  protected FinalBitmap fb;
  protected RecommendationItem item;

  protected static Resources resources;
  protected static Drawable heartDrawable;
  protected static Drawable heartEmptyDrawable;
  protected static int GrayColor = -1;
  protected static int JianshuColor = -1;

  public JianshuBaseCard(final Context context, RecommendationItem item, FinalBitmap fb, int layoutId) {
    super(context, layoutId);
    this.item = item;
    this.fb = fb;
    if(JianshuBaseCard.resources == null) {
      JianshuBaseCard.resources = context.getResources();
    }
    if(JianshuBaseCard.heartDrawable == null) {
      JianshuBaseCard.heartDrawable = FontIconDrawable.inflate(JianshuBaseCard.resources, R.xml.fa_heart);
    }
    if(JianshuBaseCard.heartEmptyDrawable == null) {
      JianshuBaseCard.heartEmptyDrawable = FontIconDrawable.inflate(JianshuBaseCard.resources, R.xml.fa_heart_o);
    }
    if(JianshuBaseCard.GrayColor == -1) {
      JianshuBaseCard.GrayColor = JianshuBaseCard.resources.getColor(R.color.textGray);
    }
    if(JianshuBaseCard.JianshuColor == -1) {
      JianshuBaseCard.JianshuColor = JianshuBaseCard.resources.getColor(R.color.jianshu);
    }
  }

  public RecommendationItem getItem() {
    return this.item;
  }

}
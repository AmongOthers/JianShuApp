package jianshu.io.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.core.BitmapDecorator;

import jianshu.io.app.model.RecommendationItem;

/**
 * Created by Administrator on 14-3-21.
 */
public class RecommendationAdapter extends ArrayAdapter<RecommendationItem> {

  static class ViewHolder {
    TextView title;
    ImageView avatar;
    TextView summary;
    TextView author;
  }

  Activity context;
  int resource;
  FinalBitmap fb;

  public RecommendationAdapter(Activity context, int resource) {
    super(context, resource);
    init(context, resource);
  }

  public RecommendationAdapter(Activity context, int resource, RecommendationItem[] items) {
    super(context, resource);
    init(context, resource);
    this.addAll(items);
  }

  private void init(Activity context, int resource) {
    this.context = context;
    this.resource = resource;
    this.fb = FinalBitmap.create(context);
    this.fb.configBitmapDecorator(new BitmapDecorator() {
      @Override
      public Bitmap decorate(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        if(width != height) {
          int base = width <= height ? width : height;
          int startX = (width - base) / 2;
          int startY = (height - base) / 2;
          bm = bm.createBitmap(bm, startX, startY, base, base);
        }
        return bm;
      }
    });
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView;
    ViewHolder holder;
    if(rowView == null) {
      LayoutInflater inflater = this.context.getLayoutInflater();
      rowView = inflater.inflate(this.resource, null);
      holder = new ViewHolder();
      holder.title = (TextView)rowView.findViewById(R.id.title);
      holder.avatar = (ImageView)rowView.findViewById(R.id.avatar);
      holder.summary = (TextView)rowView.findViewById(R.id.summary);
      holder.author = (TextView)rowView.findViewById(R.id.author);
      rowView.setTag(holder);
    }
    else {
      holder = (ViewHolder)rowView.getTag();
    }
    RecommendationItem item = this.getItem(position);
    holder.title.setText(item.getTitle());
    this.fb.display(holder.avatar, item.getAvatar());
    holder.summary.setText(item.getSummary());
    holder.author.setText(item.getAuthor());
    return rowView;
  }

}

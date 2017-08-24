package com.example.imageselector.custom;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.imageselector.R;
import java.io.File;
import java.util.List;

public class PictureListAdapter extends BaseAdapter {

  private LayoutInflater mInflater;
  private final Activity mActivity;
  private final List<Picture> mPictures;

  private RequestOptions options;

  public PictureListAdapter(Activity activity, List<Picture> pictures) {
    this.mPictures = pictures;
    this.mActivity = activity;
    this.mInflater = activity.getLayoutInflater();
    options = RequestOptions.centerCropTransform();
  }

  @Override public int getCount() {
    return mPictures.size();
  }

  @Override public Picture getItem(int i) {
    return mPictures.get(i);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.item_image, parent, false);
      holder = new ViewHolder(convertView);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    Picture picture = getItem(position);
    if (holder != null && picture != null) {
      File file = new File(picture.getFilePath());
      if (file.exists() && file.isFile()) {
        Glide.with(mActivity).load(picture.getFilePath()).apply(options).into(holder.image);
        holder.name.setVisibility(View.GONE);
      }

      if (picture.isSelected()) {//选中
        holder.mask.setVisibility(View.VISIBLE);
        holder.indicator.setVisibility(View.VISIBLE);
        holder.indicator.setImageResource(R.drawable.ic_checkbox_red_checked);
      } else {//未选中
        holder.mask.setVisibility(View.GONE);
        holder.indicator.setVisibility(View.VISIBLE);
        holder.indicator.setImageResource(R.drawable.ic_checkbox_white_unchecked);
      }
    }
    return convertView;
  }

  class ViewHolder {
    ImageView image;
    View mask;
    TextView name;
    ImageView indicator;

    ViewHolder(View view) {
      name = (TextView) view.findViewById(R.id.name);
      image = (ImageView) view.findViewById(R.id.image);
      mask = view.findViewById(R.id.mask);
      indicator = (ImageView) view.findViewById(R.id.checkmark);
      view.setTag(this);
    }
  }
}

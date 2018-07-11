package com.example.databinding;

import android.databinding.BindingAdapter;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * 自定义绑定
 */
public class CustomBinding {
  @BindingAdapter("android:paddingLeft")
  public static void setPaddingLeft(View view, int padding) {
    view.setPadding(padding, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
  }

  /**
   * 自定义绑定 注意 bind:imageUrl 只是名称可以随意命名 如 imageUrl
   *
   * @param view 绑定到那个View
   * @param url 图片链接地址
   * @param error 加载出错时候的图片 如: R.drawable.ic_launcher_background
   */
  @BindingAdapter({ "bind:imageUrl", "bind:error" })
  public static void loadImage(ImageView view, String url, @IdRes int error) {
    Log.e(url + "ddd", "error:" + error);
    // Glide.with(view).load(url).placeholder(R.drawable.ic_default_image).error(error).into(view);
  }
}

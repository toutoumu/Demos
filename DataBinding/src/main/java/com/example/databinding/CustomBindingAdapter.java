package com.example.databinding;

import android.annotation.TargetApi;
import android.databinding.BindingAdapter;
import android.databinding.adapters.ListenerUtil;
import android.databinding.adapters.ViewBindingAdapter;
import android.os.Build;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import timber.log.Timber;

/**
 * 自定义绑定具体怎么写可以参考 {@link ViewBindingAdapter}
 * https://blog.csdn.net/qq_22703355/article/details/80804660
 */
public class CustomBindingAdapter {

  /**
   * 自定义绑定 注意 bind:imageUrl 只是名称可以随意命名 如 imageUrl
   *
   * @param view 绑定到那个View
   * @param url 图片链接地址
   * @param error 加载出错时候的图片 如: R.drawable.ic_launcher_background
   */
  @BindingAdapter({ "bind:imageUrl", "bind:error" })
  public static void loadImage(ImageView view, String url, @IdRes int error) {
    Timber.e("自定义绑定传递参数为: url: %s ,error: %s", url, error);
    // Glide.with(view).load(url).placeholder(R.drawable.ic_default_image).error(error).into(view);
  }
}

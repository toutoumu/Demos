package com.example.glidev4;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.glidev4.glide.ConcealUtil;
import com.example.glidev4.glide.GlideApp;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.image) ImageView mImage;
  @BindView(R.id.btn1) TextView mBtn1;
  @BindView(R.id.btn2) TextView mBtn2;
  @BindView(R.id.btn3) TextView mBtn3;
  private String file;
  private RequestOptions mOptions;

  private String url =
      "http://upload-images.jianshu.io/upload_images/1980684-2266bd1e30bd65fe.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    //初始化
    ConcealUtil.init(this, "123456");

    mOptions = RequestOptions.centerCropTransform()
        .placeholder(R.drawable.ic_default_image)
        .error(R.drawable.ic_default_image);
    //加载图片
    Glide.with(this).load(url).apply(mOptions).into(mImage);
    //加密图片
    InputStream inputStream = getResources().openRawResource(R.raw.abc);
    file = getFilesDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
    ConcealUtil.saveFile(inputStream, file);
  }

  @OnClick({ R.id.btn1, R.id.btn2, R.id.btn3 }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btn1: {//加载正常数据
        Glide.with(this).load(url).apply(mOptions).into(mImage);
        break;
      }
      case R.id.btn2: {//加载加密图片,图片是加密的
        Glide.with(this).load(file).apply(mOptions).into(mImage);
        break;
      }
      case R.id.btn3: {//自定义GlideModel加载图片
        GlideApp.with(this).load(new Picture(file)).apply(mOptions).into(mImage);
        break;
      }
    }
  }
}

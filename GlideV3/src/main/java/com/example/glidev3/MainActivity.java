package com.example.glidev3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.example.glidev3.glide.ConcealUtil;
import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.image) ImageView mImage;
  @BindView(R.id.btn1) TextView mBtn1;
  @BindView(R.id.btn2) TextView mBtn2;
  @BindView(R.id.btn3) TextView mBtn3;
  private String file;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    //加载图片
    Glide.with(this)
        .load("http://upload-images.jianshu.io/upload_images/1980684-2266bd1e30bd65fe.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240")
        .placeholder(R.drawable.ic_default_image)
        .error(R.drawable.ic_default_image)
        .into(mImage);
    //初始化
    ConcealUtil.init(this, "123456");
    //加密图片
    InputStream inputStream = getResources().openRawResource(R.raw.abc);
    file =
        getFilesDir().getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
    ConcealUtil.saveFile(inputStream, file);
  }

  @OnClick({ R.id.btn1, R.id.btn2, R.id.btn3 }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btn1: {//加载正常数据
        Glide.with(this)
            .load("http://upload-images.jianshu.io/upload_images/1980684-2266bd1e30bd65fe.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240")
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
      case R.id.btn2: {//加载加密图片
        Glide.with(this)
            .load(file)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
      case R.id.btn3: {//自定义GlideModel加载图片
        Glide.with(this)
            .from(Picture.class)
            .load(new Picture(file))
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
    }
  }
}

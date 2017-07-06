package com.example.glidev4;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.glidev4.glide.ConcealUtil;
import com.example.glidev4.glide.GlideApp;
import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.image) ImageView mImage;
  private String file;
  private RequestOptions mOptions;

  private static final String url =
      "http://upload-images.jianshu.io/upload_images/1980684-2266bd1e30bd65fe.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
  private static final String gif =
      "https://raw.githubusercontent.com/Krupen/FabulousFilter/master/newDemo2.gif";

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

  @OnClick({
      R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
      R.id.btn8
  }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btn0: {
        Glide.get(MainActivity.this).clearMemory();//清理内存缓存  需要在UI主线程中进行
        new Thread(new Runnable() {
          @Override public void run() {
            Glide.get(MainActivity.this).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
          }
        }).start();
        break;
      }
      case R.id.btn1: {//加载正常数据
        Glide.with(this).load(url).thumbnail(0.1f)//加载1/10尺寸的缩略图，然后加载全图
            .apply(mOptions).into(mImage);
        break;
      }
      case R.id.btn2: {//加载加密图片,图片是加密的
        Glide.with(this).load(file).apply(mOptions).into(mImage);
        break;
      }
      case R.id.btn3: {//自定义GlideModel加载图片,图片是加密的
        GlideApp.with(this)
            .load(new Picture(file))
            .apply(new RequestOptions().placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image))
            .into(mImage);
        break;
      }
      case R.id.btn4: {// 下载图片
        Glide.with(this).downloadOnly().load(url).into(new SimpleTarget<File>() {
          @Override
          public void onResourceReady(File resource, Transition<? super File> transition) {
            Toast.makeText(MainActivity.this, "图片下载成功", Toast.LENGTH_SHORT).show();
          }
        });
        //同步下载,Glide同步下载文件(优先从缓存加载)
        //File file = Glide.with(this).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        //同步下载,Glide同步下载bitmap(每次都会解码为bitmap效率低)
        //Bitmap theBitmap = Glide.with(this).asBitmap().load(url).into(-1, -1).get();

        break;
      }
      case R.id.btn5: {// 加载gif
        Glide.with(this)//.asGif()//如果你希望加载的只是gif,如果不是gif就显示错误图片,那么只用加上asGif方法即可
            .load(gif).apply(mOptions).into(mImage);
        break;
      }
      case R.id.btn6: {// 加载gif为静态图片
        Glide.with(this).asBitmap().apply(mOptions).load(gif).into(mImage);
        break;
      }
      case R.id.btn7: {// 图片转换
        Glide.with(this)//.asFile()//转为文件
            .asBitmap()//转为图片
            .load(url).apply(mOptions).into(new SimpleTarget<Bitmap>(250, 250) {//可以指定图片大小,默认为原图
          @Override
          public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
            Toast.makeText(MainActivity.this, "转换成功" + resource.getByteCount(), Toast.LENGTH_SHORT)
                .show();
          }
        });
        break;
      }
      case R.id.btn8: {// todo 使用mask
        Toast.makeText(MainActivity.this, "正在进行中....", Toast.LENGTH_SHORT).show();
        break;
      }
    }
  }
}

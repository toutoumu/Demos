package com.example.glidev3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.glidev3.glide.ConcealUtil;
import java.io.File;
import java.io.InputStream;
import jp.wasabeef.glide.transformations.MaskTransformation;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.image) ImageView mImage;
  private String file;
  private static final String url =
      "http://upload-images.jianshu.io/upload_images/1980684-2266bd1e30bd65fe.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
  private static final String gif =
      "https://raw.githubusercontent.com/Krupen/FabulousFilter/master/newDemo2.gif";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    //加载图片
    Glide.with(this).load(url)
        .placeholder(R.drawable.ic_default_image)
        .error(R.drawable.ic_default_image)
        .into(mImage);
    //初始化
    ConcealUtil.init(this, "123456");
    //加密图片并保存到本地
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
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
      case R.id.btn2: {//加载加密图片,图片是加密的
        Glide.with(this)
            .load(file)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
      case R.id.btn3: {//自定义GlideModel加载图片,图片是加密的
        Glide.with(this)
            .from(Picture.class)
            .load(new Picture(file))
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
      case R.id.btn4: {// 下载图片
        //异步下载
        Glide.with(this).load(url).downloadOnly(new SimpleTarget<File>() {
          @Override
          public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
            Toast.makeText(MainActivity.this, "图片下载成功", Toast.LENGTH_SHORT).show();
          }
        });
        //同步下载,Glide同步下载文件(优先从缓存加载)
        //File file = Glide.with(this).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        //同步下载,Glide同步下载bitmap(每次都会解码为bitmap效率低)
        //Bitmap theBitmap = Glide.with(this).load(url).asBitmap().into(-1, -1).get();
        break;
      }
      case R.id.btn5: {// 加载gif
        Glide.with(this)
            .load(gif)
            //.asGif()//如果你希望加载的只是gif,如果不是gif就显示错误图片,那么只用加上asGif方法即可
            .fitCenter()
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
      case R.id.btn6: {// 加载gif为静态图片
        Glide.with(this)
            .load(gif)
            .asBitmap()
            .fitCenter()
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
      case R.id.btn7: {// 图片转换
        Glide.with(this)
            .load(url)
            .asBitmap()
            .toBytes()
            .centerCrop()
            .into(new SimpleTarget<byte[]>(250, 250) {//可以指定图片大小,默认为原图
              @Override public void onResourceReady(byte[] data, GlideAnimation anim) {
                Toast.makeText(MainActivity.this, "转换成功" + data.length, Toast.LENGTH_SHORT).show();
              }
            });
        break;
      }
      case R.id.btn8: {// 使用mask
        Glide.with(this)
            .load(url)
            .bitmapTransform(new CenterCrop(this), new MaskTransformation(this, R.drawable.ic_mask))
            //.override(size.x, size.y)//如果是在列表中使用且图片大小不一定是需要每次使用时设置图片大小
            .placeholder(R.drawable.ic_default_image)
            .into(mImage);
        break;
      }
    }
  }
}

package com.example.imageselector;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.imageselector.custom.FileSelectorActivity;
import com.example.imageselector.custom.Picture;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions public class MainActivity extends AppCompatActivity {

  private static final int REQUEST_CODE_OPEN_CAMERA = 101;//打开相机获取图片
  private static final int REQUEST_CODE_OPEN_GALLEY = 102;//打开相册获取图片
  private static final int REQUEST_CODE_CROP = 103;//剪切图片
  public static final int REQUEST_CODE_CUSTOM = 104;

  private static final String AUTHORITY = "com.example.imageselector";

  private File mCropFile;//剪切后的图片
  private File mCameraFile;//拍摄的图片

  private ImageView mImageView;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mImageView = (ImageView) findViewById(R.id.image);
    findViewById(R.id.select).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {//openAlbum();
        MainActivityPermissionsDispatcher.openAlbumWithPermissionCheck(MainActivity.this);
      }
    });
    findViewById(R.id.takepicture).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {//openCamera();
        MainActivityPermissionsDispatcher.openCameraWithPermissionCheck(MainActivity.this);
      }
    });
    findViewById(R.id.custom).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {//openCamera();//showCustom();
        MainActivityPermissionsDispatcher.showCustomWithPermissionCheck(MainActivity.this);
      }
    });
  }

  @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE) void showCustom() {
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putInt(FileSelectorActivity.COUNG, 2);
    intent.putExtras(bundle);
    intent.setClass(MainActivity.this, FileSelectorActivity.class);
    startActivityForResult(intent, REQUEST_CODE_CUSTOM);
  }

  /**
   * 打开相册选取图片
   */
  @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE) void openAlbum() {
    //ACTION_PICK ：直接打开图库，优点是不会出现类型混乱的情况，缺点仅能打开图库而不能从其他路径中获取资源且界面较单一。
    //ACTION_GET_CONTENT：4.4以下的版本打开的是缩略图的图库，4.4以上是可选择的页面(图库/图像选择器)。
    //ACTION_OPEN_DOCUMENT：直接打开图像选择器，不可以在4.4以下使用。(官方建议4.4+使用此Action)。
    Intent intent = new Intent();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4+
      intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
    } else {
      intent.setAction(Intent.ACTION_GET_CONTENT);
    }
    intent.setType("image/*");
    startActivityForResult(intent, REQUEST_CODE_OPEN_GALLEY);
  }

  /**
   * 打开相机拍摄照片
   */
  @NeedsPermission(Manifest.permission.CAMERA) void openCamera() {
    try {
      mCameraFile = createOriImageFile();
    } catch (IOException e) {
    }

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0及以上
      Uri uriForFile = FileProvider.getUriForFile(this, AUTHORITY, mCameraFile);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    } else {
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCameraFile));
    }
    startActivityForResult(intent, REQUEST_CODE_OPEN_CAMERA);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK) {
      return;
    }
    switch (requestCode) {
      case REQUEST_CODE_OPEN_CAMERA: {//照相后返回
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          //通过FileProvider创建一个content类型的Uri
          Uri inputUri = FileProvider.getUriForFile(this, AUTHORITY, mCameraFile);
          startCrop(inputUri);//设置输入类型
        } else {
          Uri inputUri = Uri.fromFile(mCameraFile);
          startCrop(inputUri);
        }
        break;
      }
      case REQUEST_CODE_OPEN_GALLEY: {// 图库后返回
        if (data == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          //相册会返回一个由相册安全策略定义的Uri，app使用这个Uri直接放入裁剪程序会不识别，抛出[暂不支持此类型：华为7.0]
          //GetImagePath.getPath 会返回根据Uri解析出的真实路径
          File imgUri = new File(GetImagePath.getPath(this, data.getData()));
          //根据真实路径转成File,然后通过应用程序重新安全化，再放入裁剪程序中才可以识别
          Uri dataUri = FileProvider.getUriForFile(this, AUTHORITY, imgUri);
          startCrop(dataUri);
        } else {
          startCrop(data.getData());
        }
        break;
      }
      case REQUEST_CODE_CUSTOM: {//自定义文件选择
        if (data != null) {
          ArrayList<Picture> list =
              (ArrayList<Picture>) data.getSerializableExtra(FileSelectorActivity.DATA);
          if (list != null && list.size() > 0) {
            File file = new File(list.get(0).getFilePath());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
              Uri inputUri = FileProvider.getUriForFile(this, AUTHORITY, file);
              startCrop(inputUri);//设置输入类型
            } else {
              Uri inputUri = Uri.fromFile(file);
              startCrop(inputUri);
            }
          }
        }
        break;
      }
      case REQUEST_CODE_CROP: {//裁剪后的回调
        if (mCropFile.exists()) {
          Glide.with(this).load(mCropFile).into(mImageView);
        }
        break;
      }
    }
  }

  /**
   * 裁剪图片方法实现
   *
   * @param inputUri
   */
  public void startCrop(Uri inputUri) {
    if (inputUri == null) {
      return;
    }

    try {
      mCropFile = createCropImageFile();
    } catch (IOException e) {
    }
    Intent intent = new Intent("com.android.camera.action.CROP");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      //7.0 安全机制下不允许保存裁剪后的图片
      //所以仅仅将File Uri传入MediaStore.EXTRA_OUTPUT来保存裁剪后的图像
      Uri outPutUri = Uri.fromFile(mCropFile);
      intent.setDataAndType(inputUri, "image/*");
      intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
      intent.putExtra("noFaceDetection", false);//去除默认的人脸识别，否则和剪裁匡重叠
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    } else {
      Uri outPutUri = Uri.fromFile(mCropFile);
      if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        String url = GetImagePath.getPath(this, inputUri);//这个方法是处理4.4以上图片返回的Uri对象不同的处理方法
        intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
      } else {
        intent.setDataAndType(inputUri, "image/*");
      }
      intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
    }

    // 设置裁剪
    intent.putExtra("crop", "true");
    // aspectX aspectY 是宽高的比例
    intent.putExtra("aspectX", 1);
    intent.putExtra("aspectY", 1);
    // outputX outputY 是裁剪图片宽高
    intent.putExtra("outputX", 200);
    intent.putExtra("outputY", 200);
    intent.putExtra("return-data", false);
    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
    startActivityForResult(intent, REQUEST_CODE_CROP);//这里就将裁剪后的图片的Uri返回了
  }

  /**
   * 创建原图像保存的文件
   *
   * @return {@link File}
   * @throws IOException 异常
   */
  private File createOriImageFile() throws IOException {
    String prefix =
        "HomePic_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
    File directory = new File(
        getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/OriPicture");
    if (!directory.exists()) {
      directory.mkdirs();
    }
    return File.createTempFile(prefix, ".jpg", directory);
  }

  /**
   * 创建裁剪图像保存的文件
   *
   * @return {@link File}
   * @throws IOException 异常
   */
  private File createCropImageFile() throws IOException {
    String prefix =
        "HomePic_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
    File directory = new File(
        getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/CropPicture");
    if (!directory.exists()) {
      directory.mkdirs();
    }
    return File.createTempFile(prefix, ".jpg", directory);
  }

  //***************************权限相关处理*******************

  @OnShowRationale(Manifest.permission.CAMERA) void showRationaleForCamera(
      final PermissionRequest request) {
    new AlertDialog.Builder(this).setMessage("授予权限")
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            request.proceed();
          }
        })
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            request.cancel();
          }
        })
        .show();
  }

  @OnPermissionDenied(Manifest.permission.CAMERA) void showDeniedForCamera() {
    Toast.makeText(this, "拒绝", Toast.LENGTH_SHORT).show();
  }

  @OnNeverAskAgain(Manifest.permission.CAMERA) void showNeverAskForCamera() {
    Toast.makeText(this, "不在询问,需要自己设置", Toast.LENGTH_SHORT).show();
  }

  /**
   * [系统请求存储权限询问对话框] ,选择了禁止(拒绝),下次再调用需要存储权限的方法时候就会调用此方法,
   * 此方法点击确定之后弹出 [系统请求存储权限询问对话框]
   * 点击拒绝之后,将调用 {@link #showDeniedForStorage}
   *
   * @param request {@link PermissionRequest}
   */
  @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE) void showRationaleForStorage(
      final PermissionRequest request) {
    new AlertDialog.Builder(this).setMessage("是否授予权限")
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            request.proceed();
          }
        })
        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            request.cancel();
          }
        })
        .show();
  }

  /**
   * 拒绝之后的提示信息
   */
  @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE) void showDeniedForStorage() {
    Toast.makeText(this, "您已拒绝授予该权限", Toast.LENGTH_SHORT).show();
  }

  /**
   * [系统请求存储权限询问对话框] ,勾选不在提示, 点击禁止.后弹出次提示,再次请求权限时候也会弹出
   */
  @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE) void showNeverAskForStorage() {
    Toast.makeText(this, "您选择了不在询问,需要进入设置页面开启该权限", Toast.LENGTH_SHORT).show();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
  }
}

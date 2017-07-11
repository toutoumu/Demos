package com.example.rxjava;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class MainActivity extends AppCompatActivity {

  private SystemBarTintManager mTintManager;
  private View mParentView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    final TextView textView = (TextView) findViewById(R.id.text);
    textView.setOnClickListener(new View.OnClickListener() {
      public boolean flag;

      @Override public void onClick(View v) {

        if (flag) {
          textView.setText("透明");
          setTransparentForWindow();
        } else {
          textView.setText("不透明");
          setUnTransparentForWindow();
        }
        flag = !flag;
      }
    });
  }

  /**
   * 设置内容显示到状态栏下层,并使状态栏透明
   */
  private void setTransparentForWindow() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      // 设置内容视图与顶部的距离为0,占据状态栏位置
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      //透明状态栏 <item name="android:windowTranslucentStatus">true</item>
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      //透明导航栏 <item name="android:windowTranslucentNavigation">true</item>
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
      getWindow().getDecorView()
          .setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
              | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

      getWindow().setStatusBarColor(Color.TRANSPARENT);
      getWindow().setNavigationBarColor(Color.TRANSPARENT);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
      //透明状态栏 <item name="android:windowTranslucentStatus">true</item>
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      //透明导航栏 <item name="android:windowTranslucentNavigation">true</item>
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

      if (mTintManager == null) {
        mTintManager = new SystemBarTintManager(this);
        ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        mParentView = contentFrameLayout.getChildAt(0);
      }

      mTintManager.setStatusBarTintEnabled(false);
      mTintManager.setNavigationBarTintEnabled(false);
      mParentView.setPadding(0, 0, 0, 0);
    }
  }

  private void setUnTransparentForWindow() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      //透明状态栏 <item name="android:windowTranslucentStatus">true</item>
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      //透明导航栏 <item name="android:windowTranslucentNavigation">true</item>
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      getWindow().getDecorView()
          .setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
              & ~(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN));

      getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      //透明状态栏 <item name="android:windowTranslucentStatus">true</item>
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      //透明导航栏 <item name="android:windowTranslucentNavigation">true</item>
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

      if (mTintManager == null) {
        mTintManager = new SystemBarTintManager(this);
        ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        mParentView = contentFrameLayout.getChildAt(0);
      }

      mTintManager.setStatusBarTintEnabled(true);
      mTintManager.setNavigationBarTintEnabled(true);
      mTintManager.setTintColor(getResources().getColor(R.color.colorPrimary));
      //mParentView.setPadding(0, mTintManager.getConfig().getStatusBarHeight(), 0, 0);
    }
  }
}

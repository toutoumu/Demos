package com.example.uiautomator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  TextView runBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    runBtn = findViewById(R.id.runBtn);
    runBtn.setOnClickListener(v -> {
      runBtn.postDelayed(() -> {
        List<String> list = new ArrayList<>();
        list.add(
          "adb shell am instrument -w -r   -e debug false -e class 'com.example.uiautomator.AppTest#quTouTiaoTest' com.example.uiautomator.debug.test/android.support.test.runner.AndroidJUnitRunner");
        try {
          doCmds(list);
        } catch (Exception e) {
          Log.e(TAG, "出错了", e);
        }
      }, 1000);
    });
  }

  public static void doCmds(List<String> cmds) throws Exception {
    Process process = Runtime.getRuntime().exec("su");
    DataOutputStream os = new DataOutputStream(process.getOutputStream());

    for (String tmpCmd : cmds) {
      os.writeBytes(tmpCmd + "\n");
    }

    os.writeBytes("exit\n");
    os.flush();
    os.close();

    process.waitFor();
  }
}

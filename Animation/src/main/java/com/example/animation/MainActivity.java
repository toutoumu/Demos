package com.example.animation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * http://blog.csdn.net/cdzz11/article/details/52197732
 */
public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, VelocityCheckActivity.class);
        startActivity(intent);
      }
    });
  }
}

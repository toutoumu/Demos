package com.example.uiautomator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

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
      }, 1000);
    });
  }
}

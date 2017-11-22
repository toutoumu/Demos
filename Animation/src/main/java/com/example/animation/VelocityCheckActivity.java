package com.example.animation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;

/**
 * 手势速度检测
 */
public class VelocityCheckActivity extends AppCompatActivity {

  private VelocityTracker mVelocityTracker;
  TextView mTextView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_velocity_check);
    mTextView = findViewById(R.id.text);
  }

  @SuppressLint("SetTextI18n") @Override public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        addMovement(event);
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        addMovement(event);//注意移动的时候也需要添加
        break;
      }
      case MotionEvent.ACTION_UP: {
        // 计算1000毫秒内的评价速度
        mVelocityTracker.computeCurrentVelocity(1000);
        mTextView.setText("X方向速度:"
            + mVelocityTracker.getXVelocity()
            + "\nY方向速度:"
            + mVelocityTracker.getYVelocity());
        recycleVelocityTracker();
        break;
      }
    }
    return super.onTouchEvent(event);
  }

  private void resetVelocityTracker() {
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    } else {
      this.mVelocityTracker.clear();
    }
  }

  private void addMovement(MotionEvent ev) {
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(ev);
  }

  private void recycleVelocityTracker() {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
}

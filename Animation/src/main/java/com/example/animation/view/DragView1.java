package com.example.animation.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 跟随手势移动的View, 通过改变自身layout
 */
public class DragView1 extends View {

  private int lastX;
  private int lastY;

  public DragView1(Context context) {
    this(context, null, 0);
  }

  public DragView1(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DragView1(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
  }

  private void initView() {
    setClickable(true);
    setBackgroundColor(Color.GRAY);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    int x = (int) event.getX();
    int y = (int) event.getY();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        // 记录触摸点坐标
        lastX = x;
        lastY = y;
        break;
      case MotionEvent.ACTION_MOVE:
        // 如果使用的是当前View为参照(参考系)event.getX,event.getY
        // 那么按下时候位置为A 第一次移动后的位置为B 第二次移动后的位置为C
        // 第一次移动后重新布局此时手指按下的位置任然为A 因此不需要重新设置坐标

        // 如果将上面代码
        // int x = (int) event.getX();
        // int y = (int) event.getY();
        // 直接改成getRawX()和getRawY()的话
        // 如果以window边框为参照 按下的时候位置为A 第一次移动后的位置为B 第二次移动后的位置为C
        // 那么第一次移动的偏移参考点为A ,第二次移动的参考点为B(由于View已经被重新布局因此B点为第二次移动的起始点)
        // 所以在以window为参照的时候需要重置坐标点

        int offsetX = x - lastX;
        int offsetY = y - lastY;
        // 在当前left、top、right、bottom的基础上加上偏移量
        layout(getLeft() + offsetX, getTop() + offsetY, getRight() + offsetX,
            getBottom() + offsetY);
        // offsetLeftAndRight(offsetX);
        // offsetTopAndBottom(offsetY);
        break;
    }
    return true;
  }
}
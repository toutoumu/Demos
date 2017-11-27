package com.example.animation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import com.example.animation.R;
import timber.log.Timber;

/**
 * 卷闸门效果
 */
public class DragDoorView2 extends RelativeLayout {

  /** onFling 操作的阈值,绝对值越大速度越大 */
  private static final int VELOCITY_Y = -5000;
  /** 默认动画时长 */
  private static final int MAX_DURATION = 500;
  /** 默认动画时长 */
  private static final int MIN_DURATION = 100;

  private float mStartY;//按下位置
  private boolean mHide;//滑动结束后是否隐藏
  private int mScreenHeight;//屏幕高度

  private ImageView imgView;
  private Scroller mScroller;
  private VelocityTracker mVelocityTracker; //滑动速度监听

  public DragDoorView2(Context context) {
    this(context, null);
  }

  public DragDoorView2(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DragDoorView2(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
    mScroller = new Scroller(context);
  }

  private void initView() {
    imgView = new ImageView(getContext());
    imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    imgView.setScaleType(ImageView.ScaleType.FIT_XY);// 填充整个屏幕
    imgView.setImageResource(R.drawable.bg1); // 默认背景
    this.addView(imgView);

    this.setClickable(true);

    // 获取屏幕分辨率
    WindowManager wm = (WindowManager) (getContext().getSystemService(Context.WINDOW_SERVICE));
    DisplayMetrics dm = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(dm);
    mScreenHeight = dm.heightPixels;
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    addMovement(event);
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        mStartY = event.getRawY();
        mScroller.forceFinished(true);// 手指按下结束滚动
        Timber.e("onDown---手指按下结束滚动");
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        // offsetY > 0 向下滑动  // offset > 0 向右滑动
        float rawY = event.getRawY();
        int offsetY = (int) (rawY - mStartY);
        mStartY = rawY;
        // 禁止向下滑动
        if (offsetY > 0) {//offsetY > 0 是向下滑动
          if (getScrollY() < 0) {//getScrollY() < 0 此时View 有向下滑动
            scrollTo(0, 0);
            return true;
          }
          // 如果此时View已经向上滑动了, 这次将要下滑的距离大于上滑的距离,调整下滑距离等于上滑距离使得不会向下滑动
          if (offsetY > getScrollY()) {
            offsetY = getScrollY();
          }
        }

        scrollBy(0, -offsetY);// 跟随手指上下滚动
        Timber.e("onScroll---跟随手指上下滚动");
        break;
      }
      case MotionEvent.ACTION_UP: {
        // 计算1000毫秒内移动速度
        mVelocityTracker.computeCurrentVelocity(1000);
        float yVelocity = mVelocityTracker.getYVelocity();
        recycleVelocityTracker();
        //根据滑动速度计算动画时间,速度越快时间越短
        int duration = (int) Math.abs((1000 * VELOCITY_Y / yVelocity));
        duration = Math.min(duration, MAX_DURATION);
        duration = Math.max(duration, MIN_DURATION);
        if (yVelocity < VELOCITY_Y) {//如果向上滑动速度超过阈值
          mHide = true;
          mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - getScrollY(), duration);
          invalidate();
          return true;
        }

        if (getScrollY() > mScreenHeight / 2) {//向上滑动超过一半
          Timber.e("onUp---向上滑动超过一半");
          mHide = true;
          mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - getScrollY(), duration);
        } else {
          Timber.e("onUp---向上滑动没有超过一半");
          mHide = false;
          mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), duration);
        }
        // 注意一定要调用这一句,否则界面有可能不会滚动
        invalidate();
        break;
      }
    }
    return true;
  }

  @Override public void computeScroll() {
    if (mScroller.computeScrollOffset()) {
      scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
      postInvalidate();
    } else {
      if (mHide) {
        this.setVisibility(View.GONE);
      }
    }
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
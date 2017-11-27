package com.example.animation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import com.example.animation.R;
import timber.log.Timber;

/**
 * 卷闸门效果 - 使用手势(GestureDetector)
 */
public class DragDoorView extends RelativeLayout {

  /** onFling 操作的阈值,绝对值越大速度越大 */
  private static final int VELOCITY_Y = -5000;

  private boolean mHide;//滑动结束后是否隐藏
  private boolean onFling;//是否触发了onFling
  private int mScreenHeight;//屏幕高度
  private Scroller mScroller;
  private GestureDetector mDetector;
  private MyGestureListener myGestureListener;

  public DragDoorView(Context context) {
    this(context, null);
  }

  public DragDoorView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DragDoorView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView();
    mScroller = new Scroller(context);
    myGestureListener = new MyGestureListener();
    mDetector = new GestureDetector(getContext(), myGestureListener);
  }

  private void initView() {
    ImageView imgView = new ImageView(getContext());
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

  private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override public boolean onDown(MotionEvent e) {
      // 手指按下结束滚动
      mScroller.forceFinished(true);
      Timber.e("onDown---手指按下结束滚动");
      return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      // distanceX distanceY 表示上一次事件(MotionEvent)到这一次触摸事件(MotionEvent)之间的偏移量,两个值不一定来自同一个事件(MotionEvent)
      // distanceX < 0 向右 distanceY > 0 向左 distanceY < 0 向下 distanceY > 0 向上
      scrollBy(0, (int) distanceY);// 跟随手指上下滚动
      Timber.e("onScroll---跟随手指上下滚动");
      return super.onScroll(e1, e2, distanceX, distanceY);
    }

    /**
     * 手指抬起
     *
     * @param event
     */
    private void onUp(MotionEvent event) {
      if (onFling) {
        onFling = false;
        return;
      }

      if (getScrollY() > mScreenHeight / 2) {//向上滑动超过一半
        Timber.e("onUp---向上滑动超过一半");
        mHide = true;
        mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - getScrollY(), 1000);
      } else {
        Timber.e("onUp---向上滑动没有超过一半");
        mHide = false;
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 1000);
      }
      // 注意一定要调用这一句,否则界面有可能不会滚动
      invalidate();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      // velocityY < 0 向上  velocityX < 0 向左
      onFling = true;
      if (velocityY < VELOCITY_Y) {//向上滑动速度超过阈值
        Timber.e("onFling---向上滑动速度超过阈值");
        mHide = true;
        int duration = (int) Math.abs((1000 * VELOCITY_Y / velocityY));//根据速度计算
        mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - getScrollY(), duration);
      } else {// 向上滑动速度不够
        Timber.e("onFling---向上滑动速度不够");
        mHide = false;
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 1000);
      }
      invalidate();
      return super.onFling(e1, e2, velocityX, velocityY);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_UP && !mDetector.onTouchEvent(event)) {
      myGestureListener.onUp(event);
      return false;
    }
    return mDetector.onTouchEvent(event);
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
}
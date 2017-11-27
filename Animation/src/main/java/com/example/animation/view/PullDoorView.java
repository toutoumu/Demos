package com.example.animation.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import com.example.animation.R;

public class PullDoorView extends RelativeLayout implements GestureDetector.OnGestureListener {

  private GestureDetectorCompat mDetector;

  private boolean flay = false;

  private Context mContext;

  private Scroller mScroller;

  private int mScreenWidth = 0;

  private int mScreenHeight = 0;

  private int mLastDownY = 0;

  private int mCurryY;

  private int mDelY;

  private boolean mCloseFlag = false;

  private ImageView mImgView;

  public PullDoorView(Context context) {
    super(context);
    mContext = context;
    setupView();
  }

  public PullDoorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    setupView();
    mDetector = new GestureDetectorCompat(context, this);
  }

  private void setupView() {
    // 这个 Interpolator 你可以设置别的 我这里选择的是有弹跳效果的 BounceInterpolator
    Interpolator polator = new BounceInterpolator();
    mScroller = new Scroller(mContext, polator);

    // 获取屏幕分辨率
    WindowManager wm = (WindowManager) (mContext.getSystemService(Context.WINDOW_SERVICE));
    DisplayMetrics dm = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(dm);
    mScreenHeight = dm.heightPixels;
    mScreenWidth = dm.widthPixels;

    // 这里你一定要设置成透明背景,不然会影响你看到底层布局
    this.setBackgroundColor(Color.TRANSPARENT);
    mImgView = new ImageView(mContext);
    mImgView.setLayoutParams(
        new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    mImgView.setScaleType(ImageView.ScaleType.FIT_XY);// 填充整个屏幕
    mImgView.setImageResource(R.drawable.bg1); // 默认背景
    addView(mImgView);
  }

  /**
   * 设置推动门背景
   *
   * @param id
   */
  public void setBgImage(@DrawableRes int id) {
    mImgView.setImageResource(id);
  }

  /**
   * 设置推动门背景
   *
   * @param drawable
   */
  public void setBgImage(Drawable drawable) {
    mImgView.setImageDrawable(drawable);
  }

  /**
   * 推动门的动画
   *
   * @param startY
   * @param dy
   * @param duration
   */
  public void startBounceAnim(int startY, int dy, int duration) {
    mScroller.startScroll(0, startY, 0, dy, duration);
    invalidate();
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    this.mDetector.onTouchEvent(event);
    int action = event.getAction();
    if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
      return true;
    }
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mLastDownY = (int) event.getY();
        System.err.println("ACTION_DOWN=" + mLastDownY);
        return true;
      case MotionEvent.ACTION_MOVE:

        mCurryY = (int) event.getY();
        System.err.println("ACTION_MOVE=" + mCurryY);
        mDelY = mCurryY - mLastDownY;
        // 只准上滑有效
        if (mDelY < 0) {
          scrollTo(0, -mDelY);
        }
        System.err.println("-------------  " + mDelY);

        break;
      case MotionEvent.ACTION_UP:
        if (flay) {
          flay = false;
          return true;
        }
        mCurryY = (int) event.getY();
        mDelY = mCurryY - mLastDownY;
        if (mDelY < 0) {

          if (Math.abs(mDelY) > mScreenHeight / 2) {

            // 向上滑动超过半个屏幕高的时候 开启向上消失动画
            startBounceAnim(this.getScrollY(), mScreenHeight, 450);
            mCloseFlag = true;
          } else {
            // 向上滑动未超过半个屏幕高的时候 开启向下弹动动画
            startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);
          }
        } else {
          // 如果只是按下松开没有滑动那么将触发这里
          startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);
        }

        break;
    }
    return super.onTouchEvent(event);
  }

  @Override public void computeScroll() {

    if (mScroller.computeScrollOffset()) {
      scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
      Log.i("scroller", "getCurrX()= "
          + mScroller.getCurrX()
          + "     getCurrY()="
          + mScroller.getCurrY()
          + "  getFinalY() =  "
          + mScroller.getFinalY());
      // 不要忘记更新界面
      postInvalidate();
    } else {
      if (mCloseFlag) {
        this.setVisibility(View.GONE);
      }
    }
  }

  @Override public boolean onDown(MotionEvent e) {
    mScroller.forceFinished(true);
    mLastDownY = (int) e.getY();
    System.err.println("ACTION_DOWN=" + mLastDownY);
    return true;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    mCurryY = (int) e2.getY();
    System.err.println("ACTION_MOVE=" + mCurryY);
    mDelY = mCurryY - mLastDownY;
    // 只准上滑有效
    if (mDelY < 0) {
      scrollTo(0, -mDelY);
    }
    System.err.println("-------------  " + mDelY);

    return false;
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    Log.e("velocityX", "" + velocityX);
    Log.e("velocityY", "" + velocityY);
    if (velocityY < -4000) {
      flay = true;// 如果触发了fling 那么up将不再被触发
      startBounceAnim(this.getScrollY(), mScreenHeight, 450);
      mCloseFlag = true;
    }
    return true;
  }

  @Override public void onShowPress(MotionEvent e) {
    Log.e("onShowPress", "onShowPress");
  }

  @Override public boolean onSingleTapUp(MotionEvent e) {
    return false;
  }

  @Override public void onLongPress(MotionEvent e) {

  }
}
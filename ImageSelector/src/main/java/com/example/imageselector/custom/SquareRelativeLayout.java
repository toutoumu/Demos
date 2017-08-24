package com.example.imageselector.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.example.imageselector.R;

/**
 * 自定义View,可以指定比例的方形View,默认为正方形
 */
public class SquareRelativeLayout extends RelativeLayout {
  public static final int HORIZONTAL = 0;
  public static final int VERTICAL = 1;

  private static final float DEFAULT_RATIO = 1;
  private float mRatio;
  private int mOrientation;

  public SquareRelativeLayout(Context context) {
    this(context, null, 0);
  }

  public SquareRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SquareRelativeLayout);
    mRatio = a.getFloat(R.styleable.SquareRelativeLayout_ratio, DEFAULT_RATIO);
    mOrientation = a.getInt(R.styleable.SquareRelativeLayout_orientation, HORIZONTAL);
    a.recycle();
  }

  /**
   * Compare to: {@link android.view.View#getDefaultSize(int, int)}
   * If mode is AT_MOST, return the child size instead of the parent size
   * (unless it is too big).
   */
  private static int getDefaultSize2(int size, int measureSpec) {
    int result = size;
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    switch (specMode) {
      case MeasureSpec.UNSPECIFIED:
        result = size;
        break;
      case MeasureSpec.AT_MOST:
        result = Math.min(size, specSize);
        break;
      case MeasureSpec.EXACTLY:
        result = specSize;
        break;
    }
    return result;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // For simple implementation, or internal size is always 0.
    // We depend on the container to specify the layout size of
    // our view. We can't really know what it is since we will be
    // adding and removing different arbitrary views and do not
    // want the layout to change as this happens.
    setMeasuredDimension(getDefaultSize2(getSuggestedMinimumWidth(), widthMeasureSpec),
        getDefaultSize2(getSuggestedMinimumHeight(), heightMeasureSpec));
    // Children are just made to fill our space.
    if (mOrientation == 0) {
      int childWidthSize = getMeasuredWidth();
      widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
      heightMeasureSpec =
          MeasureSpec.makeMeasureSpec((int) (childWidthSize * mRatio), MeasureSpec.EXACTLY);
    } else {
      int childHeightSize = getMeasuredHeight();
      widthMeasureSpec =
          MeasureSpec.makeMeasureSpec((int) (childHeightSize * mRatio), MeasureSpec.EXACTLY);
      heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeightSize, MeasureSpec.EXACTLY);
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  public float getRatio() {
    return mRatio;
  }

  public void setRatio(float ratio) {
    if (ratio != mRatio) {
      this.mRatio = ratio;
      requestLayout();
    }
  }

  public int getSquareOrientation() {
    return mOrientation;
  }

  /**
   * 宽高以哪个值作为基准
   * <pre>
   * {@link #HORIZONTAL}以宽度作为基准,高度 =  {@link #HORIZONTAL} * {@link #mRatio}
   * {@link #VERTICAL}以高度作为基准,宽度 = {@link #VERTICAL} * {@link #mRatio}
   * </pre>
   *
   * @param orientation {@link #HORIZONTAL}以横向宽度,作为基准,计算高度,{@link #VERTICAL}以纵向高度,作为基准,计算宽度
   */
  public void setSquareOrientation(int orientation) {
    if (mOrientation != orientation) {
      mOrientation = orientation;
      requestLayout();
    }
  }
}
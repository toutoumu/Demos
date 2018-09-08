package uiautomatorcase.example.apple.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public abstract class BaseView extends View {
  Paint mPaint;

  public BaseView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    mPaint = new Paint();
  }

  public void drawText(Canvas canvas, String text, int textSizeDP, int x, int y) {
    mPaint.setColor(Color.BLACK); //绘制画笔颜色
    mPaint.setTextSize(dp2px(textSizeDP));
    canvas.drawText(text, x, y, mPaint);

    /*Rect mRect = new Rect();
    mPaint.getTextBounds(text, 0, text.length(), mRect);
    float weidth = mRect.width();//得到宽度
    float height = mRect.height();//得到高度*/
  }

  public void drawRect(Canvas canvas, float left, float top, float right, float bottom) {
    canvas.drawRect(left, top, right, bottom, mPaint);//画矩形
  }

  /**
   * 绘制椭圆
   *
   * @param canvas
   * @param left
   * @param top
   * @param right
   * @param bottom
   */
  public void drawOval(Canvas canvas, int left, int top, int right, int bottom) {
    mPaint.setColor(Color.BLACK);
    RectF dst = new RectF(left, top, right, bottom);
    canvas.drawOval(dst, mPaint);//绘制区域
  }

  /**
   * 绘制点
   *
   * @param canvas
   */
  public void drawPoint(Canvas canvas) {
    mPaint.setColor(Color.GREEN);
    mPaint.setStrokeWidth(20.0f);//设置点的大小
    canvas.drawPoint(100, 80, mPaint);//参数一水平x轴，参数二垂直y轴，第三个参数为Paint对象。
  }

  /**
   * 绘制线
   *
   * @param canvas
   * @param startX
   * @param startY
   * @param stopX
   * @param stopY
   */
  public void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {
    canvas.drawLine(startX, //参数一起始点的x轴位置，
      startY,//参数二起始点的y轴位置，
      stopX, //参数三终点的x轴水平位置，
      stopY, //参数四y轴垂直位置，
      mPaint);//参数为Paint 画刷对象。
  }

  /**
   * 绘制弧线
   *
   * @param canvas
   * @param left
   * @param top
   * @param right
   * @param bottom
   */
  public void drawArc(Canvas canvas, int left, int top, int right, int bottom) {
    // 在第四个参数中，当为true时，是一个密闭的空间，反之为false
    RectF rect = new RectF(left, top, right, bottom);
    mPaint.reset();
    mPaint.setColor(Color.MAGENTA);
    canvas.drawArc(rect, //参数一是RectF对象，一个矩形区域椭圆形的限用于定义在形状、大小、电弧
      0, //参数二是起始角(度) 在电弧的开始
      90,// 参数三扫描角(度) 开始顺时针测量的
      false,// 参数四是如果这是真的话, 包括椭圆中心的电弧, 并关闭它, 如果它是假这将是一个弧线, 参数五是Paint对象；
      mPaint);
    //mPaint.setColor(Color.GRAY);
    //canvas.drawArc(rect, 0, 90, true, mPaint);
  }

  /**
   * 绘制圆
   *
   * @param canvas
   * @param centerX
   * @param centerY
   * @param radius
   */
  public void drawCircle(Canvas canvas, float centerX, float centerY, float radius) {
    canvas.drawCircle(centerX, centerX, radius, mPaint);
  }

  /**
   * dip转pix
   *
   * @param dp
   * @return
   */
  public int dp2px(float dp) {
    final float scale = getContext().getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }
}
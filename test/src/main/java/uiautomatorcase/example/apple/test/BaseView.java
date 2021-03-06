package uiautomatorcase.example.apple.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
    mPaint.setStrokeCap(Paint.Cap.ROUND);//圆的
  }

  /**
   * 绘制旋转文字
   *
   * @param canvas
   * @param paint
   * @param text
   * @param x
   * @param y
   * @param angle
   */
  public void drawTextAngel(Canvas canvas, String text, int textSizeDP, float x, float y, float angle) {
    mPaint.reset();
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setColor(Color.BLACK);
    mPaint.setTextSize(dp2px(textSizeDP));
    mPaint.setStrokeWidth(10);
    if (angle != 0) {
      canvas.rotate(angle, x, y);
    }
    canvas.drawText(text, x, y, mPaint);
    if (angle != 0) {
      canvas.rotate(-angle, x, y);
    }
  }

  public void drawText(Canvas canvas, String text, int textSizeDP, int x, int y) {
    mPaint.reset();
    mPaint.setStrokeCap(Paint.Cap.ROUND);//圆的
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setColor(Color.BLACK);
    mPaint.setStrokeWidth(10);
    mPaint.setTextSize(dp2px(textSizeDP));
    canvas.drawText(text, x, y, mPaint);

    /*Rect mRect = new Rect();
    mPaint.getTextBounds(text, 0, text.length(), mRect);
    float weidth = mRect.width();//得到宽度
    float height = mRect.height();//得到高度*/
  }

  /**
   * 绘制矩形
   *
   * @param canvas
   * @param left
   * @param top
   * @param right
   * @param bottom
   */
  public void drawRect(Canvas canvas, float left, float top, float right, float bottom) {
    mPaint.reset();
    mPaint.setStrokeCap(Paint.Cap.ROUND);//圆的
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setColor(Color.BLACK);
    mPaint.setStrokeWidth(10);
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
    mPaint.reset();
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setColor(Color.BLACK);
    mPaint.setStrokeWidth(20);
    RectF dst = new RectF(left, top, right, bottom);
    canvas.drawOval(dst, mPaint);//绘制区域
  }

  /**
   * 绘制点
   *
   * @param canvas
   */
  public void drawPoint(Canvas canvas) {
    mPaint.setColor(Color.BLACK);
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
    mPaint.reset();
    mPaint.setStrokeCap(Paint.Cap.ROUND);//圆的
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setColor(Color.BLACK);
    mPaint.setStrokeWidth(10);
    canvas.drawLine(startX, //参数一起始点的x轴位置，
      startY,//参数二起始点的y轴位置，
      stopX, //参数三终点的x轴水平位置，
      stopY, //参数四y轴垂直位置，
      mPaint);//参数为Paint 画刷对象。
  }

  /**
   * 绘制弧线,扇形
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
    mPaint.setStyle(Paint.Style.STROKE);//设置画圆弧的画笔的属性为描边(空心)，个人喜欢叫它描边，叫空心有点会引起歧义
    mPaint.setColor(Color.BLACK);
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
    mPaint.reset();
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setColor(Color.BLACK);
    mPaint.setStrokeWidth(10);
    canvas.drawCircle(centerX, centerY, radius, mPaint);
  }

  /**
   * 绘制五角星
   *
   * @param canvas
   * @param xA 起始点位置A的x轴绝对位置
   * @param yA 起始点位置A的y轴绝对位置
   * @param rFive 五角星边的边长
   */
  public void drawStar(Canvas canvas, float xA, float yA, int rFive) {
    Path mPath = new Path();
    mPaint.setStyle(Paint.Style.FILL);
    float[] floats = fivePoints(xA, yA, rFive);
    for (int i = 0; i < floats.length - 1; i++) {
      mPath.lineTo(floats[i], floats[i += 1]);
    }
    canvas.drawPath(mPath, mPaint);
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

  /**
   * @param xA 起始点位置A的x轴绝对位置
   * @param yA 起始点位置A的y轴绝对位置
   * @param rFive 五角星边的边长
   */
  private float[] fivePoints(float xA, float yA, int rFive) {
    float xB = 0;
    float xC = 0;
    float xD = 0;
    float xE = 0;
    float yB = 0;
    float yC = 0;
    float yD = 0;
    float yE = 0;
    xD = (float) (xA - rFive * Math.sin(Math.toRadians(18)));
    xC = (float) (xA + rFive * Math.sin(Math.toRadians(18)));
    yD = yC = (float) (yA + Math.cos(Math.toRadians(18)) * rFive);
    yB = yE = (float) (yA + Math.sqrt(Math.pow((xC - xD), 2) - Math.pow((rFive / 2), 2)));
    xB = xA + (rFive / 2);
    xE = xA - (rFive / 2);
    float[] floats = new float[] { xA, yA, xD, yD, xB, yB, xE, yE, xC, yC, xA, yA };
    return floats;
  }
}
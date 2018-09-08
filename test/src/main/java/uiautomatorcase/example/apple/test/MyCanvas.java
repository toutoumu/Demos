package uiautomatorcase.example.apple.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class MyCanvas extends BaseView {

  public MyCanvas(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    // canvas.drawColor(Color.MAGENTA);//绘制背景色
    // 左上角是左边原点
    drawText(canvas, "我看看", 25, 100, 100);
    drawOval(canvas, 10, 10, 200, 100);
    drawPoint(canvas);
    drawLine(canvas, 0, 0, 300, 100);
    drawArc(canvas, 20, 30, 400, 500);
  }
}

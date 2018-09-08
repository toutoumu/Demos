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
    canvas.drawColor(0xffe5e5e5);

    // 平行四边形
    drawLine(canvas, 700, 120, 1200, 100);
    drawLine(canvas, 700, 120, 500, 500);
    drawLine(canvas, 500, 500, 1000, 480);
    drawLine(canvas, 1200, 100, 1000, 480);

    // 反斜线
    drawLine(canvas, 1200, 100, 1450, 450);
    drawLine(canvas, 1190, 120, 1430, 450);
    // 小短线
    drawLine(canvas, 1430, 450, 1450, 450);

    // 圆形
    drawCircle(canvas, 1200, 350, 60);

    // 三根竖线
    drawLine(canvas, 550, 500, 550, 800);
    drawLine(canvas, 1030, 420, 1030, 800);
    drawLine(canvas, 1430, 450, 1430, 800);

    //窗户
    drawRect(canvas, 600, 550, 800, 700);

    // 门
    drawRect(canvas, 850, 570, 980, 800);

    // 最底部线条
    drawLine(canvas, 550, 800, 1430, 800);



    //drawOval(canvas,1150,200,1250,300);

    // canvas.drawColor(Color.MAGENTA);//绘制背景色
    // 左上角是左边原点
    // drawText(canvas, "我看看", 25, 100, 100);
    // drawOval(canvas, 10, 10, 200, 100);
    // drawPoint(canvas);
    // drawLine(canvas, 0, 0, 300, 100);
    // drawArc(canvas, 20, 20, 400, 400);
    //
    // drawStar(canvas, 200, 200, 200);
  }
}

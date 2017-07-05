package com.example.customattributes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CustomView extends View {
  private final int color;
  private final ColorStateList textColor;
  private final Drawable drawable;
  private final int height;
  private final int width;
  private final String text;
  private final int enumValue;
  private final float fracton;
  private final boolean mBoolean;
  int resource;

  public CustomView(Context context) {
    this(context, null);
  }

  public CustomView(Context context, AttributeSet attrs) {
    super(context, attrs);
    final Resources res = getResources();
    // 这里能读取的属性是在<declare-styleable name="CustomView">定义的属性
    // 这一行获取 [样式文件]和[控件属性] 里面定义的 <declare-styleable name="CustomView"> 标签中的属性集合
    TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.CustomView, R.attr.CustomStyle, 0);
    // 读取资源文件类型的属性
    resource = a.getResourceId(R.styleable.CustomView_CustomResource, 0);
    // 读取color类型的属性(#333333)
    color = a.getColor(R.styleable.CustomView_CustomColor, res.getColor(R.color.colorPrimary));
    // 读取文字颜色属性(#333333或者selector类型的drawable资源)
    textColor = a.getColorStateList(R.styleable.CustomView_CustomTextColor);
    // 读取背景(#333333或者drawable类型资源)注意与文字颜色的区别
    drawable = a.getDrawable(R.styleable.CustomView_CustomBackground);
    // 读取int类型属性
    height = a.getInteger(R.styleable.CustomView_CustomHeight, 0);
    // 读取dp类型的属性,读出来的值已经转换为px
    width = a.getDimensionPixelSize(R.styleable.CustomView_CustomWidth, 0);
    // 读取字符串类型的属性
    text = a.getString(R.styleable.CustomView_CustomString);
    // 读取枚举类型的属性
    enumValue = a.getInt(R.styleable.CustomView_CustomEnum, 0);
    // 读取百分数类型的属性
    fracton = a.getFraction(R.styleable.CustomView_CustomFraction, 0, 1, 0.0f);
    // 读取bool类型的属性
    mBoolean = a.getBoolean(R.styleable.CustomView_CustomPrivate, false);
    a.recycle();
    // 这里打印的是style.xml中定义的样式中的高度34
    Log.e("CustomView", "高度" + height);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawColor(color);
    Paint paint = new Paint();
    paint.setColor(color);
    canvas.drawText(text, 100, 100, paint);
  }
}

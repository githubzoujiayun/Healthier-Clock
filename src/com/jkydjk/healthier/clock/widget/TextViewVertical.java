package com.jkydjk.healthier.clock.widget;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.StringUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewVertical extends TextView {

  private TypedArray typedArray;
  private Typeface fontFamily;

  private String[] textGroup;

  private float fontWidth, fontHeight;
  private int rows, cols;

  private int lineWidth;

  public TextViewVertical(Context context, AttributeSet attrs) {
    super(context, attrs);
    typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewVertical);

    CharSequence font = typedArray.getText(R.styleable.TextViewVertical_font);

    fontFamily = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/" + font);

    lineWidth = typedArray.getDimensionPixelSize(R.styleable.TextViewVertical_lineWidth, 0);

    setTypeface(fontFamily);

    String text = "测试文字";
    Rect bounds = new Rect();
    getPaint().getTextBounds(text, 0, text.length(), bounds);

    fontWidth = bounds.width() / 4;
    fontHeight = (getLineHeight() + bounds.height()) / 2;

    textGroup = getText().toString().split("\n");

    rows = StringUtil.maxLength(textGroup);
    cols = textGroup.length;

  }

  // 设置行宽
  public void setLineWidth(int lineWidth) {
    this.lineWidth = lineWidth;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    Paint paint = getPaint();
    paint.setColor(getCurrentTextColor());

    float ascent = Math.abs(paint.ascent());
    float drawPosx = 0;
    float drawPosy = ascent;

    for (int i = textGroup.length; i > 0; i--) {
      String colsText = textGroup[i - 1];
      for (int j = 0; j < colsText.length(); j++) {
        char item = colsText.charAt(j);
        canvas.drawText(String.valueOf(item), drawPosx, drawPosy, paint);
        drawPosy += fontHeight;
      }
      drawPosy = ascent;
      drawPosx += fontWidth + lineWidth;
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
  }

  private int measureWidth(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if (specMode == MeasureSpec.EXACTLY) {
      return specSize;
    } else {
      return (int) (fontWidth * cols + getPaddingLeft() + getPaddingRight() + lineWidth * (cols - 1));
    }
  }

  private int measureHeight(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);

    if (specMode == MeasureSpec.EXACTLY) {
      return specSize;
    } else {
      return (int) (getLineHeight() * rows + getPaddingTop() + getPaddingBottom());
    }
  }

}
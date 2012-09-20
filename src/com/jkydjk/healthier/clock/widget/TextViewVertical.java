package com.jkydjk.healthier.clock.widget;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewVertical extends TextView {

  private TypedArray typedArray;
  private Typeface fontFamily;
  private int lineWidth;

  private int mTextPosx = 0;// x坐标
  private int mTextPosy = 0;// y坐标
  private int mTextWidth = 0;// 绘制宽度
  private int mTextHeight = 0;// 绘制高度
  private int mFontHeight = 0;// 绘制字体高度
  private int mRealLine = 0;// 字符串真实的行数
  private int mLineWidth = 0;// 列宽度
  private int TextLength = 0;// 字符串长度
  private int oldwidth = 0;// 存储久的width

  public TextViewVertical(Context context, AttributeSet attrs) {
    super(context, attrs);
    typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewVertical);

    CharSequence font = typedArray.getText(R.styleable.TextViewVertical_font);

    fontFamily = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/" + font);

    lineWidth = typedArray.getDimensionPixelSize(R.styleable.TextViewVertical_lineWidth, 0);

    setTypeface(fontFamily);

  }

  // 设置行宽
  public void setLineWidth(int LineWidth) {
    mLineWidth = LineWidth;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Log.v("onDraw");

    getTextInfo();

    String thetext = getText().toString();

    char ch;
    mTextPosy = 0;// 初始化y坐标
    mTextPosx = mTextWidth - mLineWidth;// 初始化x坐标
    for (int i = 0; i < this.TextLength; i++) {
      ch = thetext.charAt(i);
      if (ch == '\n') {
        mTextPosx -= mLineWidth;// 换列
        mTextPosy = 0;
      } else {
        mTextPosy += mFontHeight;
        if (mTextPosy > this.mTextHeight) {
          mTextPosx -= mLineWidth;// 换列
          i--;
          mTextPosy = 0;
        } else {
          canvas.drawText(String.valueOf(ch), mTextPosx, mTextPosy, getPaint());
        }
      }
    }

    canvas.drawText("xxx", 100f, 100f, getPaint());
    canvas.drawText("xxx", 100f, 200f, getPaint());
    canvas.drawText("xxx", 200f, 200f, getPaint());
    canvas.drawText("xxx", 300f, 300f, getPaint());

  }

  // 计算文字行数和总宽
  private void getTextInfo() {
    Log.v("GetTextInfo");

    String text = "测试文字";
    Rect bounds = new Rect();
    Paint textPaint = getPaint();
    textPaint.getTextBounds(text, 0, text.length(), bounds);
    int height = bounds.height();
    int width = bounds.width();

    Log.v("get font width & height: " + width / 4 + "|" + height);

    char ch;
    int h = 0;

    getPaint().setTextSize(getTextSize());

    // 获得字宽
    if (mLineWidth == 0) {
      float[] widths = new float[1];
      getPaint().getTextWidths("正", widths);// 获取单个汉字的宽度
      mLineWidth = (int) Math.ceil(widths[0] * 1.1 + 2);
    }

    FontMetrics fm = getPaint().getFontMetrics();
    mFontHeight = (int) (Math.ceil(fm.descent - fm.top) * 0.9);// 获得字体高度

    // 计算文字行数
    mRealLine = 0;
    for (int i = 0; i < getText().length(); i++) {
      ch = getText().charAt(i);

      if (ch == '\n') {
        mRealLine++;// 真实的行数加一
        h = 0;
      } else {
        h += mFontHeight;
        if (h > this.mTextHeight) {
          mRealLine++;// 真实的行数加一
          i--;
          h = 0;
        } else {
          if (i == this.TextLength - 1) {
            mRealLine++;// 真实的行数加一
          }
        }
      }
    }
    mRealLine++;// 额外增加一行
    mTextWidth = mLineWidth * mRealLine;// 计算文字总宽度
    measure(mTextWidth, getHeight());// 重新调整大小
    layout(getLeft(), getTop(), getLeft() + mTextWidth, getBottom());// 重新绘制容器
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    Log.v("onMeasure");
    int measuredHeight = measureHeight(heightMeasureSpec);
    // int measuredWidth = measureWidth(widthMeasureSpec);
    if (mTextWidth == 0)
      getTextInfo();
    setMeasuredDimension(mTextWidth, measuredHeight);
    if (oldwidth != getWidth()) {
      oldwidth = getWidth();
    }
  }

  private int measureHeight(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    int result = 500;
    if (specMode == MeasureSpec.AT_MOST) {
      result = specSize;
    } else if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    }
    mTextHeight = result;// 设置文本高度
    return result;
  }

  /*
   * private int measureWidth(int measureSpec) { int specMode =
   * MeasureSpec.getMode(measureSpec); int specSize =
   * MeasureSpec.getSize(measureSpec); int result = 500; if (specMode ==
   * MeasureSpec.AT_MOST){ result = specSize; }else if (specMode ==
   * MeasureSpec.EXACTLY){ result = specSize; } return result; }
   */
}
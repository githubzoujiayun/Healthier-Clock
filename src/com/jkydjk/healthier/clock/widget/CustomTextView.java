package com.jkydjk.healthier.clock.widget;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.Log;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

  private TypedArray typedArray;

  private Typeface fontFamily;

  public CustomTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    typedArray = context.obtainStyledAttributes(attrs, R.styleable.CoustomTextView);
    init();
  }

  public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    typedArray = context.obtainStyledAttributes(attrs, R.styleable.CoustomTextView);
    init();
  }

  private void init() {

    CharSequence font = typedArray.getText(R.styleable.CoustomTextView_font);

    if (fontFamily == null)
      fontFamily = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/" + font);

    this.setTypeface(fontFamily);

  }

}

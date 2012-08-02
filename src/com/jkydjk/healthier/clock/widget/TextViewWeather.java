package com.jkydjk.healthier.clock.widget;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class TextViewWeather extends TextView {

	public static Typeface FONT_NAME;

	public TextViewWeather(Context context) {
		super(context);
		setCustomFont(context);
	}

	public TextViewWeather(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context);
	}

	public TextViewWeather(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context);
	}

	private void setCustomFont(Context context) {
		if (FONT_NAME == null)
			FONT_NAME = Typeface.createFromAsset(context.getAssets(), "fonts/Climacons.ttf");
		this.setTypeface(FONT_NAME);
	}
}

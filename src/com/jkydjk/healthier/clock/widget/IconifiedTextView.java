package com.jkydjk.healthier.clock.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconifiedTextView extends LinearLayout {

	private TextView mText = null;
	private ImageView mIcon = null;

	public IconifiedTextView(Context context, IconifiedText aIconifiedText) {
		super(context);

		this.setOrientation(HORIZONTAL);
		mIcon = new ImageView(context);
		mIcon.setImageDrawable(aIconifiedText.getIcon());
		mIcon.setPadding(8, 12, 6, 12);
		addView(mIcon, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		mText = new TextView(context);
		mText.setText(aIconifiedText.getText());
		mText.setPadding(8, 6, 6, 10);
		mText.setTextSize(26);
		addView(mText, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	public void setText(String words) {
		mText.setText(words);
	}

	public void setIcon(Drawable bullet) {
		mIcon.setImageDrawable(bullet);
	}
}

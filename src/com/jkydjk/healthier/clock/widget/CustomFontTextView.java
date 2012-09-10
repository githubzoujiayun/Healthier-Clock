package com.jkydjk.healthier.clock.widget;

import com.jkydjk.healthier.clock.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontTextView extends TextView {

    private Typeface fontFamily;

    public CustomFontTextView(Context context) {
        super(context);
        setCustomFont(context, null, 0);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs, 0);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs, defStyle);
    }

    private void setCustomFont(Context context, AttributeSet attrs, int defStyle) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoustomFontTextView);
        CharSequence font = a.getText(R.styleable.CoustomFontTextView_font);
        
        if (fontFamily == null)
            fontFamily = Typeface.createFromAsset(context.getAssets(), "fonts/"+font);
        this.setTypeface(fontFamily);

    }
}

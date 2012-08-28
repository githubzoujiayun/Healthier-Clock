package com.jkydjk.healthier.clock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkydjk.healthier.clock.R;

public class FileItem extends RelativeLayout {

    private ImageView icon;
    private TextView fileName;
    private ImageView status;

    public FileItem(Context context) {
        this(context, null);
    }

    public FileItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setIcon(int resId){
        icon.setImageResource(resId);
    }
    
    public void setFileName(String name){
        fileName.setText(name);
    }
    
    public void setStatusVisibility(boolean isSelected){
        if (isSelected) {
            status.setVisibility(View.VISIBLE);
        } else {
            status.setVisibility(View.GONE);
        }
    }
    
    
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        icon = (ImageView) findViewById(R.id.icon);
        fileName = (TextView) findViewById(R.id.file_name);
        status = (ImageView) findViewById(R.id.status);
    }
}
package com.jkydjk.healthier.clock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.FileExtension;

public class FilePage extends RelativeLayout {
    
    private FileItem selectedFileItem;
    
    private RelativeLayout shadow;

    private FileExtension fileExtension;

    public FilePage(Context context) {
        this(context, null);
    }

    public FilePage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilePage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FileExtension getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(FileExtension fileExtension) {
        this.fileExtension = fileExtension;
        if (fileExtension != null) {
            this.setId(fileExtension.getLevel());
        }
    }

    public void hideShadow() {
        if (shadow != null)
            shadow.setVisibility(View.GONE);
    }

    public void showShadow() {
        if (shadow != null)
            shadow.setVisibility(View.VISIBLE);
    }

    public void setShadowDrawable(int resId) {
        if (shadow != null)
            shadow.setBackgroundResource(resId);
    }

    public void setShadowWidthRes(int resId) {
        setShadowWidth((int) getResources().getDimension(resId));
    }

    public void setShadowWidth(int pixels) {
        if (shadow != null) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pixels, ViewGroup.LayoutParams.FILL_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            shadow.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        shadow = new RelativeLayout(this.getContext());
        shadow.setBackgroundResource(R.drawable.layout_shadow);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        shadow.setLayoutParams(layoutParams);
        this.addView(shadow);
    }

    public FileItem getSelectedFileItem() {
        return selectedFileItem;
    }

    public void setSelectedFileItem(FileItem getSelectedFileItem) {
        this.selectedFileItem = getSelectedFileItem;
    }
    
}
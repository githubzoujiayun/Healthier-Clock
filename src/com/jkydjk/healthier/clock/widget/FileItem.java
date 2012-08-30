package com.jkydjk.healthier.clock.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.FileExtension;

public class FileItem extends RelativeLayout {
    private FilePage filePage;
    private FileExtension fileExtension;
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

    public FilePage getFilePage() {
        return filePage;
    }

    public void setFilePage(FilePage filePage) {
        this.filePage = filePage;
    }

    public FileExtension getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(FileExtension fileExtension) {
        this.fileExtension = fileExtension;
        setFileName(fileExtension.getCustomName());
        setIcon(fileExtension.getIcon());
    }

    public void setIcon(int resId) {
        icon.setImageResource(resId);
    }

    public void setFileName(String name) {
        fileName.setText(name);
    }

    public void setIsSelected(boolean selected) {
        if (selected) {
            FilePage page = getFilePage();
            FileItem fileItem = page.getSelectedFileItem();
            if (fileItem != null) {
                fileItem.fileExtension.setSelected(false);
                fileItem.setBackgroundResource(R.drawable.list_blockbg_middle_line_selector);
                fileItem.status.setVisibility(View.GONE);
            }
            page.setSelectedFileItem(this);
        }

        if (fileExtension.isDirectory()) {
            setBackgroundResource(selected ? R.drawable.list_blockbg_middle_line_pressed : R.drawable.list_blockbg_middle_line_selector);
        } else {
            status.setVisibility(selected ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * @return the status
     */
    public ImageView getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(ImageView status) {
        this.status = status;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        icon = (ImageView) findViewById(R.id.icon);
        fileName = (TextView) findViewById(R.id.file_name);
        status = (ImageView) findViewById(R.id.status);
    }
}
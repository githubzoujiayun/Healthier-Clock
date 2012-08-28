package com.jkydjk.healthier.clock.entity;

import java.io.File;
import java.net.URI;

import android.view.View;

import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.FileUtil;

public class FileExtension extends File {

    private static final long serialVersionUID = 1L;

    private String customName;

    private int icon = 0;

    private boolean selected = false;

    private boolean selectable = true;

    private View view;

    public FileExtension(File dir, String name) {
        super(dir, name);
    }

    public FileExtension(String dirPath, String name) {
        super(dirPath, name);
    }

    public FileExtension(String path) {
        super(path);
    }

    public FileExtension(URI uri) {
        super(uri);
    }

    public FileExtension(File file) {
        super(file, "");
    }

    public FileExtension(File file, String customName, int icon) {
        super(file, "");
        this.customName = customName;
        this.icon = icon;
    }

    public String getCustomName() {
        return customName == null ? super.getName() : customName;
    }

    public void setName(String name) {
        this.customName = name;
    }

    public int getIcon() {
        if (icon != 0) {
            return icon;
        }
        if (isDirectory()) {
            return R.drawable.icon_folder;
        }

        switch (FileUtil.fileType(this)) {
        case FileUtil.IMAGE:
            return R.drawable.image;

        case FileUtil.WEBTEXT:
            return R.drawable.webtext;

        case FileUtil.PACKAGE:
            return R.drawable.packed;

        case FileUtil.AUDIO:
            return R.drawable.icon_music;

        case FileUtil.VIDEO:
            return R.drawable.video;

        default:
            return R.drawable.text;
        }
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * 是否可以选中
     * 
     * @return
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * 设置是否可以被选中
     * 
     * @param selectable
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    /**
     * 比较文件名是否相同
     */
//    public int compareTo(FileExtension other) {
//        if (getName() != null)
//            return getName().toLowerCase().compareTo(other.getName().toLowerCase());
//        else
//            throw new IllegalArgumentException();
//    }

}

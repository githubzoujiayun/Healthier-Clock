package com.jkydjk.healthier.clock.entity;

import java.io.File;
import java.net.URI;

import android.view.View;

import com.jkydjk.healthier.clock.BaseActivity;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.util.FileUtil;
import com.jkydjk.healthier.clock.widget.FileItem;

public class FileExtension implements Comparable<FileExtension> {

    public static FileExtension SDCARD_FILE_EXTENSION = new FileExtension(BaseActivity.SDCARD);
    
    public static final int FLODER_IS_EMPTY = 0x0;
    public static final int NO_FILES_IN_THE_FLODER = 0x1;

    private FileItem fileItem;

    private File file;

    private String customName;

    private int icon = 0;

    private boolean selected = false;

    private boolean selectable = true;

    private View view;

    public FileExtension(File dir, String name) {
        this.file = new File(dir, name);
    }

    public FileExtension(String dirPath, String name) {
        this.file = new File(dirPath, name);
    }

    public FileExtension(String path) {
        this.file = new File(path);
    }

    public FileExtension(URI uri) {
        this.file = new File(uri);
    }

    public FileExtension(File file) {
        this.file = file;
    }

    public FileExtension(File file, String customName, int icon) {
        this.file = file;
        this.customName = customName;
        this.icon = icon;
    }

    public FileItem getFileItem() {
        return fileItem;
    }

    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    public String getCustomName() {
        return customName == null ? file.getName() : customName;
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

        switch (FileUtil.fileType(file)) {
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
     * Return file level
     * 
     * @return
     */
    public int getLevel() {
        return file.toString().split("/").length - SDCARD_FILE_EXTENSION.getFile().toString().split("/").length;
    }

    /**
     * 比较文件名是否相同 用于排序 Collections.sort(fileExtensionEntries);
     */
    public int compareTo(FileExtension other) {
        if (file.getName() != null) {
            return getCustomName().toLowerCase().compareTo(other.getCustomName().toLowerCase());
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public File[] listFiles() {
        return file.listFiles();
    }

    public File getFile() {
        return file;
    }

}

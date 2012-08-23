package com.jkydjk.healthier.clock.entity;

import android.graphics.drawable.Drawable;

public class FileText implements Comparable<FileText> {
	// 文件名
	private String mText = "";

	// 文件的图标ICON
	private Drawable icon = null;

	// 能否选中
	private boolean mSelectable = true;

	public FileText(String text, Drawable bullet) {
		icon = bullet;
		mText = text;
	}

	/**
	 *  是否可以选中
	 * @return
	 */
	public boolean isSelectable() {
		return mSelectable;
	}

	/**
	 *  设置是否可以被选中
	 * @param selectable
	 */
	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}

	public String getText() {
		return mText;
	}

	/**
	 * 设置文件名
	 * @param text
	 */
	public void setText(String text) {
		mText = text;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public Drawable getIcon() {
		return icon;
	}
	
	/**
	 * 比较文件名是否相同
	 */
	public int compareTo(FileText other) {
		if (mText != null)
			return mText.toLowerCase().compareTo(other.getText().toLowerCase());
		else
			throw new IllegalArgumentException();
	}
}

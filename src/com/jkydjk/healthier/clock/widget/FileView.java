package com.jkydjk.healthier.clock.widget;

import java.io.File;

import android.content.Context;
import android.widget.CheckedTextView;

public class FileView extends CheckedTextView {

	private File file;

	public FileView(Context context, File file) {
		super(context);
		this.file = file;
		setText(file.getName());
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}

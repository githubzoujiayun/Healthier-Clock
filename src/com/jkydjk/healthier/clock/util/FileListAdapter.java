package com.jkydjk.healthier.clock.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.jkydjk.healthier.clock.widget.FileView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FileListAdapter extends BaseAdapter {

	private Context context;

	private List<File> items = new ArrayList<File>();
	
	public FileListAdapter(Context context){
		this.context = context;
	}

	public int getCount() {
		return items.size();
	}

	public Object getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		FileView fileView = null;
		if (convertView == null) {
			fileView = new FileView(context, items.get(position));
		} else {
			fileView = (FileView) convertView;
			fileView.setFile(items.get(position));
		}
		return fileView;
	}

}

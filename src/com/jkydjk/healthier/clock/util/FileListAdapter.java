package com.jkydjk.healthier.clock.util;

import java.util.ArrayList;
import java.util.List;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.FileExtension;
import com.jkydjk.healthier.clock.widget.FileItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileListAdapter extends BaseAdapter {

	private LayoutInflater mFactory;

	private Context mContext = null;

	private List<FileExtension> mItems = new ArrayList<FileExtension>();

	public FileListAdapter(Context context) {
		mContext = context;
		mFactory = LayoutInflater.from(context);
	}
	
	public FileListAdapter(Context context, List<FileExtension> list) {
        mContext = context;
        mFactory = LayoutInflater.from(context);
        mItems = list;
    }

	public void addItem(FileExtension it) {
		mItems.add(it);
	}

	public void setListItems(List<FileExtension> lit) {
		mItems = lit;
	}

	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int position) {
		return mItems.get(position);
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isSelectable(int position) {
		return mItems.get(position).isSelectable();
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
	    FileItem fileItem = (FileItem) convertView;
		
		if (fileItem == null) {
			fileItem = (FileItem) mFactory.inflate(R.layout.file_item, parent, false);
		}
		
		FileExtension fileExtension = mItems.get(position);
		
		fileItem.setIcon(fileExtension.getIcon());
		fileItem.setFileName(fileExtension.getCustomName());
		fileItem.setStatusVisibility(fileExtension.isSelected());

		return fileItem;
	}
}

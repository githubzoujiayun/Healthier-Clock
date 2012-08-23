package com.jkydjk.healthier.clock.util;

import java.util.ArrayList;
import java.util.List;
import com.jkydjk.healthier.clock.R;
import com.jkydjk.healthier.clock.entity.FileText;
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

	private List<FileText> mItems = new ArrayList<FileText>();

	public FileListAdapter(Context context) {
		mContext = context;
		mFactory = LayoutInflater.from(context);
	}

	public void addItem(FileText it) {
		mItems.add(it);
	}

	public void setListItems(List<FileText> lit) {
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
		View view = convertView;
		
		if (view == null) {
			view = mFactory.inflate(R.layout.file_item, parent, false);
		}

		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		icon.setImageDrawable(mItems.get(position).getIcon());

		TextView fileName = (TextView) view.findViewById(R.id.file_name);
		fileName.setText(mItems.get(position).getText());

		return view;
	}
}

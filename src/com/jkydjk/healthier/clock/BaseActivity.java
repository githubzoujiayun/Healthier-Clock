package com.jkydjk.healthier.clock;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class BaseActivity extends Activity {

	/**
	 * Android动态获取图片资源
	 * 
	 * @param context
	 * @param name
	 * @return Resource ID: R.drawable.xxxxx
	 */
	public static int getImageResourceID(Context context, String name) {
		return context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + name, null, null);
	}

	/**
	 * Android动态获取文本资源
	 * 
	 * @param context
	 * @param name
	 * @return Resource ID: R.string.xxxxx
	 */
	public static int getStringResourceID(Context context, String name) {
		return context.getResources().getIdentifier(context.getPackageName() + ":string/" + name, null, null);
	}
	
	/**
	 * 调整ListView高度
	 * @param view
	 */
	public static void adjustmentListViewHeightBasedOnChildren(AdapterView view) {
		ListAdapter listAdapter = (ListAdapter) view.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, view);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.height = totalHeight + (view.getHeight() * (listAdapter.getCount() - 1));
		view.setLayoutParams(params);
		view.requestLayout();
	}
}

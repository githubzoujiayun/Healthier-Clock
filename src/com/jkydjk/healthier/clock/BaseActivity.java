package com.jkydjk.healthier.clock;

import android.app.Activity;
import android.content.Context;

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
	
}

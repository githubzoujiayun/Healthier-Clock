package com.jkydjk.healthier.clock.util;

public class StringUtil {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 *            字符串
	 * @return 是否为空
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}
}

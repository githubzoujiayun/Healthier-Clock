package com.jkydjk.healthier.clock.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {

	public static final int FILE = 0x01;
	public static final int AUDIO = 0x02;
	public static final int IMAGE = 0x03;
	public static final int PACKAGE = 0x04;
	public static final int WEBTEXT = 0x05;
	public static final int VIDEO = 0x06;

	public static final String[] AUDIO_SUFFIXES = { "mp3", "wav", "ogg", "midi", "wma" };
	public static final String[] IMAGE_SUFFIXES = { "png", "gif", "jpg", "jpeg", "bmp" };
	public static final String[] PACKAGE_SUFFIXES = { "jar", "zip", "rar", "gz" };
	public static final String[] WEBTEXT_SUFFIXES = { "htm", "html", "php" };
	public static final String[] VIDEO_SUFFIXES = { "mp4", "rm", "mpg", "avi", "mpeg" };

	public static Map<String, Integer> SUFFIXES;

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static int fileType(File file) {
		if (SUFFIXES == null) {
			SUFFIXES = new HashMap<String, Integer>();
			putSuffixesKeyValue(AUDIO_SUFFIXES, AUDIO);
			putSuffixesKeyValue(IMAGE_SUFFIXES, IMAGE);
			putSuffixesKeyValue(PACKAGE_SUFFIXES, PACKAGE);
			putSuffixesKeyValue(WEBTEXT_SUFFIXES, WEBTEXT);
			putSuffixesKeyValue(VIDEO_SUFFIXES, VIDEO);
		}

		Integer type = SUFFIXES.get(getFileSuffix(file));

		return type != null ? type : FILE;
	}

	private static void putSuffixesKeyValue(String[] suffixes, int value) {
		for (String suffix : suffixes) {
			SUFFIXES.put(suffix, value);
		}
	}

	/**
	 * Return the suffix of the passed file.
	 * 
	 * @param file
	 *            File to retrieve suffix for.
	 * @return Suffix for file or an empty string if unable to get the suffix.
	 */
	public static String getFileSuffix(File file) {
		if (file == null) {
			return null;
		}
		int pos = file.getName().lastIndexOf('.');
		if (pos > 0 && pos < file.getName().length() - 1) {
			return file.getName().substring(pos + 1);
		}
		return "";
	}

}

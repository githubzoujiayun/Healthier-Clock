package com.jkydjk.healthier.clock.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

  /**
   * 判断字符串是否为空
   * 
   * @param str
   *          字符串
   * @return 是否为空
   */
  public static boolean isEmpty(String str) {
    return str == null || "".equals(str.trim());
  }

  public static int maxLength(String[] array) {
    int maxLength = 0;
    int tempLength = 0;

    for (int i = 0; i < array.length; i++) {
      tempLength = array[i].length();
      if (tempLength > maxLength) {
        maxLength = tempLength;
      }
    }

    return maxLength;
  }

  /**
   * 检查是否为E-mail地址
   * 
   * @param email
   * @return
   */
  public static boolean isEmail(String email) {
    String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(email);
    return matcher.find();
  }

}

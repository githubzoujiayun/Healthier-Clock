package com.jkydjk.healthier.clock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Lunar {

  private int year;
  private int month;
  private int day;
  private boolean leap;
  private Calendar calendar;

  public static String chineseNumber[] = { "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊" };

  /**
   * 六十甲子
   */
  public static final String[] JiaZhi = { "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉", "甲戌", "乙亥", "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未", "甲申", "乙酉", "丙戌", "丁亥", "戊子", "己丑",
      "庚寅", "辛卯", "壬辰", "癸巳", "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥", "庚子", "辛丑", "壬寅", "癸卯", "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥", "壬子", "癸丑", "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉",
      "壬戌", "癸亥" };

  public final static String[] Gan = { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
  public final static String[] Zhi = { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };

  public final static String[] LUNAR_TIME_NAMES = { "子时", "丑时", "丑时", "寅时", "寅时", "卯时", "卯时", "辰时", "辰时", "巳时", "巳时", "午时", "午时", "未时", "未时", "申时", "申时", "酉时", "酉时", "戌时", "戌时", "亥时", "亥时", "子时" };

  public final static String[] ANIMALS = { "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };

  public static final SimpleDateFormat CHINESE_DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

  public static final SimpleDateFormat MONTH_DAY_FORMAT = new SimpleDateFormat("MM/dd");

  public static final SimpleDateFormat WEEK_DAY_FORMAT = new SimpleDateFormat("E");

  private static long[] lunarInfo = { 0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2,
      0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7,
      0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
      0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0,
      0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954,
      0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50,
      0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0,
      0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

  private static final String[] SolarTerm = { "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至" };

  private final static long[] SolarTermInfo = { 0, 21208, 42467, 63836, 85337, 107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758 };

  // -----------------------------------节气-------------------------------------

  /**
   * 获得当前节气
   *
   * @return
   */
  public static int getCurrentSolarTermIntervalIndex() {
    Calendar cal = Calendar.getInstance();

    int index = getSoralTermIndex(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    if (index == -1) {
      do {
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1); // 得到前一天
        index = getSoralTermIndex(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
      } while (index == -1);
    }
    return index;
  }

  /**
   * 获得该日期在哪个节气区间内
   *
   * @param cal
   * @return
   */
  public static String getSolarTermInterval(Calendar cal) {
    String solarTerms = Lunar.getSolarTerm(cal);
    if (solarTerms == null) {
      do {
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1); // 得到前一天
        solarTerms = Lunar.getSolarTerm(cal);
      } while (solarTerms == null);
    }
    return solarTerms;
  }

  /**
   * 根据日期得到节气
   *
   * @param cal
   * @return
   */
  public static String getSolarTerm(Calendar cal) {
    return getSoralTerm(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * 根据日期(y年m月d日)得到节气
   *
   * @param y
   * @param m
   * @param d
   * @return
   */
  public static String getSoralTerm(int y, int m, int d) {
    int index = getSoralTermIndex(y, m, d);
    return index == -1 ? null : SolarTerm[index];
  }

  /**
   * 根据日期(y年m月d日)得到节气Index
   *
   * @param y
   * @param m
   * @param d
   * @return
   */
  public static int getSoralTermIndex(int y, int m, int d) {
    int n = (m - 1) * 2;
    if (d == sTerm(y, n))
      return n;
    else if (d == sTerm(y, n + 1))
      return n + 1;
    else {
      return -1; // 到这里说明非节气时间
    }
  }

  /**
   * y年的第n个节气为几日（从0小寒起算）
   *
   * @param y
   * @param n
   * @return
   */
  private static int sTerm(int y, int n) {
    Calendar cal = Calendar.getInstance();
    cal.set(1900, 0, 6, 2, 5, 0);
    long temp = cal.getTime().getTime();
    cal.setTime(new Date((long) ((31556925974.7 * (y - 1900) + SolarTermInfo[n] * 60000L) + temp)));
    return cal.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * 获取农历日期 如： 六月十一
   *
   * @param cal
   * @return
   */
  public static String getLunar(Calendar cal) {
    Map<String, Object> cl = calculateLunar(cal);
    int month = (Integer) cl.get("month");
    int day = (Integer) cl.get("day");
    boolean leap = (Boolean) cl.get("leap");
    return (leap ? "闰" : "") + chineseNumber[month - 1] + "月" + getChinaDayString(day);
  }

  /**
   * 给定日期计算农历的年、月、日以及是否闰年
   *
   * @param cal
   * @return
   */
  private static Map<String, Object> calculateLunar(Calendar cal) {
    int year, month, day, leapMonth = 0;
    boolean leap = false;

    Date baseDate = null;
    try {
      baseDate = CHINESE_DATE_FORMAT.parse("1900年1月31日");
    } catch (ParseException e) {
      e.printStackTrace();
    }

    // 求出和1900年1月31日相差的天数
    int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);

    // 用offset减去每农历年的天数 计算当天是农历第几天 i最终结果是农历的年份 offset是当年的第几天
    int iYear, daysOfYear = 0;
    for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
      daysOfYear = yearDays(iYear);
      offset -= daysOfYear;
    }
    if (offset < 0) {
      offset += daysOfYear;
      iYear--;
    }
    // 农历年份
    year = iYear;

    leapMonth = leapMonth(iYear); // 闰哪个月,1-12

    // 用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
    int iMonth, daysOfMonth = 0;
    for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
      // 闰月
      if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
        --iMonth;
        leap = true;
        daysOfMonth = leapDays(year);
      } else
        daysOfMonth = monthDays(year, iMonth);

      offset -= daysOfMonth;
      // 解除闰月
      if (leap && iMonth == (leapMonth + 1))
        leap = false;
    }
    // offset为0时，并且刚才计算的月份是闰月，要校正
    if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
      if (leap) {
        leap = false;
      } else {
        leap = true;
        --iMonth;
      }
    }
    // offset小于0时，也要校正
    if (offset < 0) {
      offset += daysOfMonth;
      --iMonth;
    }
    month = iMonth;
    day = offset + 1;

    Map<String, Object> lunar = new HashMap<String, Object>();

    lunar.put("year", year);
    lunar.put("month", month);
    lunar.put("day", day);
    lunar.put("leap", leap);

    return lunar;
  }

  // --------------------获取农历日期结束---------------------

  // 传回农历 y年的总天数
  private static int yearDays(int y) {
    int i, sum = 348;
    for (i = 0x8000; i > 0x8; i >>= 1) {
      if ((lunarInfo[y - 1900] & i) != 0)
        sum += 1;
    }
    return (sum + leapDays(y));
  }

  // 传回农历 y年闰月的天数
  private static int leapDays(int y) {
    if (leapMonth(y) != 0) {
      return (lunarInfo[y - 1900] & 0x10000) != 0 ? 30 : 29;
    } else
      return 0;
  }

  // 传回农历 y年闰哪个月 1-12 , 没闰传回 0
  private static int leapMonth(int y) {
    return (int) (lunarInfo[y - 1900] & 0xf);
  }

  // 传回农历 y年m月的总天数
  private static int monthDays(int y, int m) {
    return (lunarInfo[y - 1900] & (0x10000 >> m)) == 0 ? 29 : 30;
  }

  // 传入 月日的offset 传回干支, 0=甲子
  private static String cyclicalm(int num) {
    return (Gan[num % 10] + Zhi[num % 12]);
  }

  // 传入 offset 传回干支, 0=甲子
  public String cyclical() {
    return cyclicalm(year - 1900 + 36);
  }

  // 获取农历中日的表示
  public static String getChinaDayString(int day) {
    String chineseTen[] = { "初", "十", "廿", "卅" };
    int n = day % 10 == 0 ? 9 : day % 10 - 1;
    if (day > 30)
      return "";
    if (day == 10)
      return "初十";
    else
      return chineseTen[day / 10] + chineseNumber[n];
  }

  /**
   * 传回农历 y年的生肖
   *
   * @return
   */
  public String animals() {
    return ANIMALS[(year - 4) % 12];
  }

  /**
   * 获取农历时辰
   *
   * @param hour
   * @return Example: 子时
   */
  public static String getLunarTime(int hour) {
    return LUNAR_TIME_NAMES[hour];
  }

  /**
   * 获取农历时辰
   *
   * @param hour
   * @return Example: 子时
   */
  public String getLunarTime() {
    return getLunarTime(calendar.get(Calendar.HOUR_OF_DAY));
  }

  /**
   * 给定日期获得一个农历对象
   *
   * @param cal
   */
  public Lunar(Calendar cal) {
    Map<String, Object> cl = calculateLunar(cal);
    this.calendar = cal;
    this.year = (Integer) cl.get("year");
    this.month = (Integer) cl.get("month");
    this.day = (Integer) cl.get("day");
    this.leap = (Boolean) cl.get("leap");
  }

  /**
   * 转换成公历时间
   *
   * @param sdf
   * @return 转换后的时间字符串
   */
  public String getGregorian() {
    return getGregorian(MONTH_DAY_FORMAT);
  }

  /**
   * 转换成公历时间
   *
   * @param sdf
   * @return 转换后的时间字符串
   */
  public String getGregorian(SimpleDateFormat sdf) {
    return sdf == null ? null : sdf.format(calendar.getTime());
  }

  /**
   * 获得完整的公历表示
   *
   * @return Example: 1980年08月08日(周五) 下午4点
   */
  public String getGregorianFull() {
    return CHINESE_DATE_FORMAT.format(calendar.getTime()) + "(" + getWeekDay() + ") " + getAMPM() + calendar.get(Calendar.HOUR) + "点";
  }

  /**
   * 获取农历日期
   *
   * @return Example: 六月廿八
   */
  public String getLunar() {
    return (leap ? "闰" : "") + chineseNumber[month - 1] + "月" + getChinaDayString(day);
  }

  /**
   * 获得完整的农历表示
   *
   * @return Example: 庚申年六月廿八日 申时
   */
  public String getLunarFull() {
    return cyclical() + "年" + getLunar() + "日 " + getLunarTime();
  }

  /**
   * 获取星期 Week Day
   *
   * @return Example: 周五
   */
  public String getWeekDay() {
    return WEEK_DAY_FORMAT.format(calendar.getTime());
  }

  /**
   * 获取上午/下午
   *
   * @return
   */
  public String getAMPM() {
    return calendar.get(Calendar.AM_PM) == 0 ? "上午" : "下午";
  }

  /**
   * 获得（八字）天干地支
   *
   * @return
   */
  public String getCharacter() {
    return getYearGanZhi() + " " + getMonthGanZhi() + " " + getDayGanZhi();
  }

  /**
   * 获得年的天干地支
   *
   * @return
   */
  public String getYearGanZhi() {

    // int[] hourIndex = { 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9,
    // 9, 10, 10, 11, 11, 12, 12, 1 };

    // int hour = hourIndex[calendar.get(Calendar.HOUR_OF_DAY)];

    int idx = (year - 1864) % 60;

    // 没有过春节的话那么年还算上一年的
    String y = JiaZhi[idx];

    // String m = "";
    // String d = "";
    // String h = "";
    //
    // idx = idx % 5;
    // int idxm = 0;
    //
    // // 年上起月 甲己之年丙作首，乙庚之岁戊为头， 丙辛必定寻庚起，丁壬壬位顺行流， 更有戊癸何方觅，甲寅之上好追求。
    //
    // idxm = (idx + 1) * 2;
    // if (idxm == 10) {
    // idxm = 0;
    // }
    //
    // m = Gan[(idxm + month - 1) % 10] + Zhi[(month + 2 - 1) % 12];
    //
    // Date baseDate = null;
    // try {
    // baseDate = CHINESE_DATE_FORMAT.parse("1900年1月31日");
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    //
    // // 求出和1900年1月31日甲辰日相差的天数 甲辰日是第四十天
    // int offset = (int) ((calendar.getTime().getTime() -
    // baseDate.getTime()) / 86400000L);
    // offset = (offset + 40) % 60;
    //
    // d = JiaZhi[offset];
    //
    // // 日上起时 甲己还生甲，乙庚丙作初， 丙辛从戊起，丁壬庚子居， 戊癸何方发，壬子是真途。
    // offset = (offset % 5) * 2;
    //
    // h = Gan[(offset + hour - 1) % 10] + Zhi[hour - 1];
    //
    // m = getMonthGanZhi();

    return y;// + " " + m + " " + d + " " + h;
  }

  /**
   * 获得月的天干地支
   *
   * @return
   */
  private String getMonthGanZhi() {
    String year = getYearGanZhi().substring(0, 1);
    String[][] table = { { "丙寅", "戊寅", "庚寅", "壬寅", "甲寅" }, { "丁卯", "己卯", "辛卯", "癸卯", "乙卯" }, { "戊辰", "庚辰", "壬辰", "甲辰", "丙辰" }, { "己巳", "辛巳", "癸巳", "乙巳", "丁巳" }, { "庚午", "壬午", "甲午", "丙午", "戊午" },
        { "辛未", "癸未", "乙未", "丁未", "己未" }, { "壬申", "甲申", "丙申", "戊申", "庚申" }, { "癸酉", "乙酉", "丁酉", "己酉", "辛酉" }, { "甲戌", "丙戌", "戊戌", "庚戌", "壬戌" }, { "乙亥", "丁亥", "己亥", "辛亥", "癸亥" },
        { "丙子", "戊子", "庚子", "壬子", "甲子" }, { "丁丑", "己丑", "辛丑", "癸丑", "乙丑" }, };
    String[][] solarTerms = { { "立春", "雨水" }, { "惊蛰", "春分" }, { "清明", "谷雨" }, { "立夏", "小满" }, { "芒种", "夏至" }, { "小暑", "大暑" }, { "立秋", "处暑" }, { "白露", "秋分" }, { "寒露", "霜降" }, { "立冬", "小雪" },
        { "大雪", "冬至" }, { "小寒", "大寒" } };
    String[][] gan = { { "甲", "己" }, { "乙", "庚" }, { "丙", "辛" }, { "丁", "壬" }, { "戊", "癸" } };
    String solarTerm = Lunar.getSolarTermInterval(calendar);
    int x = 0;
    for (int i = 0; i < gan.length; i++) {
      if (year.equals(gan[i][0]) || year.equals(gan[i][1]))
        x = i;
    }
    int y = 0;
    for (int i = 0; i < solarTerms.length; i++) {
      if (solarTerm.equals(solarTerms[i][0]) || solarTerm.equals(solarTerms[i][1]))
        y = i;
    }
    return table[y][x];
  }

  /**
   * 获得天的天干地支
   *
   * @return
   */
  public String getDayGanZhi() {
    int idx = (year - 1864) % 60;

    // 没有过春节的话那么年还算上一年的
    String y = JiaZhi[idx];

    String m = "";
    String d = "";
    String h = "";

    idx = idx % 5;
    int idxm = 0;

    // 年上起月 甲己之年丙作首，乙庚之岁戊为头， 丙辛必定寻庚起，丁壬壬位顺行流， 更有戊癸何方觅，甲寅之上好追求。

    idxm = (idx + 1) * 2;
    if (idxm == 10) {
      idxm = 0;
    }

    m = Gan[(idxm + month - 1) % 10] + Zhi[(month + 2 - 1) % 12];

    Date baseDate = null;
    SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    try {
      baseDate = chineseDateFormat.parse("1900-1-31");
    } catch (ParseException e) {
      e.printStackTrace();
    }

    // 求出和1900年1月31日甲辰日相差的天数 甲辰日是第四十天
    int offset = (int) ((calendar.getTime().getTime() - baseDate.getTime()) / 86400000L);
    offset = (offset + 40) % 60;

    d = JiaZhi[offset];

    // 日上起时 甲己还生甲，乙庚丙作初， 丙辛从戊起，丁壬庚子居， 戊癸何方发，壬子是真途。
    offset = (offset % 5) * 2;

    // h = Gan[(offset + hour - 1) % 10] + Zhi[hour - 1];

    return d + h;
  }

  @Override
  public String toString() {
    return getGregorian(MONTH_DAY_FORMAT) + "  " + getGregorian(WEEK_DAY_FORMAT) + " " + (leap ? "闰" : "") + chineseNumber[month - 1] + "月" + getChinaDayString(day);
  }

}

package com.jkydjk.healthier.clock.entity;

public class Hour {

  /**
   * #根据给定时间(小时)返回对应时辰
   * 
   * @param hour
   * @return
   */
  public static int from_time_hour(int hour) {
    int a = hour % 2;

    if (a == 0) {
      return hour / 2;
    } else {
      return (hour + 1) / 2;
    }
  }

}

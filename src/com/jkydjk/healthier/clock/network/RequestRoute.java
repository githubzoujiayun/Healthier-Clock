package com.jkydjk.healthier.clock.network;

public class RequestRoute {

  /**
   * <pre>
   * API请求地址:
   * http://192.168.2.100:3000/mobile/
   * </pre>
   */
  public static String REQUEST_PATH = "http://192.168.2.100:3000/mobile/";

  public static String IMAGE_REQUEST_PATH = "http://192.168.2.100:3000";

  // public static String REQUEST_PATH = "http://jkydjk.com/mobile/";

  
  public static String getImageURL(String url) {
    return IMAGE_REQUEST_PATH + url;
  }

  /**
   * <pre>
   * 用户登录
   * <strong>Route</strong> : user/singin
   * <strong>Method</strong>: POST
   * </pre>
   */
  public static final String USER_SIGNIN = "user/signin";

  /**
   * <pre>
   * 设置用户真实姓名
   * <strong>Route</strong> : user/realname
   * <strong>Method</strong>: POST
   * </pre>
   */
  public static final String USER_REALNAME = "user/realname";

  /**
   * <pre>
   * 设置用户性别
   * <strong>Route</strong> : user/gender
   * <strong>Method</strong>: POST
   * </pre>
   */
  public static final String USER_GENDER = "user/gender";

  /**
   * <pre>
   * 设置用户生日
   * <strong>Route</strong> : user/birthday
   * <strong>Method</strong>: POST
   * </pre>
   */
  public static final String USER_BIRTHDAY = "user/birthday";

  /**
   * <pre>
   * 设置用户所在城市
   * <strong>Route</strong> : user/city
   * <strong>Method</strong>: POST
   * </pre>
   */
  public static final String USER_CITY = "user/city";

  /**
   * <pre>
   * 用户体质
   * <strong>Route</strong> : user/constitution
   * <strong>Method</strong>: POST
   * </pre>
   */
  public static final String USER_CONSTITUTION = "user/constitution";

  /**
   * <pre>
   * 天气
   * <strong>Route</strong> : weather
   * <strong>Method</strong>: POST
   * </pre>
   */
  public static final String WEATHER = "weather";

  /**
   * <pre>
   * 时辰方案(给定时辰方案内容)
   * <strong>Route</strong> : solution/hour/(:id)
   * <strong>Method</strong>: GET
   * </pre>
   */
  public static final String SOLUTION_HOUR = "solution/hour";

  /**
   * <pre>
   * 节气方案(返回列表)
   * <strong>Route</strong> : solution/solar_term/(:id)
   * <strong>Method</strong>: GET
   * </pre>
   */
  public static final String SOLUTION_SOLAR_TERM = "solution/solar_term";

  /**
   * <pre>
   * 方案列表图片
   * <strong>Route</strong> : solution/list/:id/image
   * <strong>Method</strong>: GET
   * </pre>
   */
  public static String solutionListImage(int id) {
    return REQUEST_PATH + "solution/list/" + id + "/image";
  }

  /**
   * <pre>
   * 方案内容
   * <strong>Route</strong> : solution/:id
   * <strong>Method</strong>: GET
   * </pre>
   */
  public static String solution(int id) {
    return REQUEST_PATH + "solution/" + id;
  }

  /**
   * <pre>
   * 方案图片
   * <strong>Route</strong> : solution/:id/image
   * <strong>Method</strong>: GET
   * </pre>
   */
  public static String solutionImage(int id) {
    return REQUEST_PATH + "solution/" + id + "/image";
  }
  
  /**
   * <pre>
   * 穴位图片
   * <strong>Route</strong> : acupoint/:id/image
   * <strong>Method</strong>: GET
   * </pre>
   */
  public static String acupointImage(int id) {
    return REQUEST_PATH + "acupoint/" + id + "/image";
  }

  /**
   * <pre>
   * 穴位图片地址
   * <strong>Route</strong> : acupoint/:id/images
   * <strong>Method</strong>: GET
   * </pre>
   */
  public static String acupointImages(int id) {
    return REQUEST_PATH + "acupoint/" + id + "/images";
  }
}

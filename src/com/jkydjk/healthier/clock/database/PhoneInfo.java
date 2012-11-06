package com.jkydjk.healthier.clock.database;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 需要加入权限<uses-permission
 * android:name="android.permission.READ_PHONE_STATE"/>
 * @author echowang_2011@163.com
 *
 */
public class PhoneInfo {
	/**
	 * TelephonyManager提供设备上获取通讯服务信息的入口。 应用程序可以使用这个类方法确定的电信服务商和国家 以及某些类型的用户访问信息。
	 * 应用程序也可以注册一个监听器到电话收状态的变化。不需要直接实例化这个类
	 * 使用Context.getSystemService(Context.TELEPHONY_SERVICE)来获取这个类的实例。
	 */
	private TelephonyManager telephonyManager = null;
	
	public PhoneInfo(Context context) {
		// TODO Auto-generated constructor stub
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	/**
	 * 获取手机号
	 * @return
	 */
	public String phoneNumber() {
		return telephonyManager.getLine1Number();
	}
	
	/**
	 * 获取手机的IMEI
	 * @return
	 */
	public String phoneIMEI() {
		return telephonyManager.getDeviceId();
	}

	/**
	 * 获取手机服务商信息
	 * @return
	 */
	public String providersName() {
		String ProvidersName = null;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		String IMSI = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		System.out.println(IMSI);
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		}
		return ProvidersName;
	}
	
	/**
	 * 获取手机SIM卡的序列号
	 * @return
	 */
	public String simSerialNumber(){
		return telephonyManager.getSimSerialNumber();
	}
}

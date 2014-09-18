package com.hct.zc.service;

/**
 * 格式化电话号码.
 * 
 * @time 2014年6月11日 下午3:40:04
 * @author liuzenglong163@gmail.com
 */

public class PhoneNumFormater {

	/**
	 * 
	 * 格式化手机号码格式，有些手机的通讯录里面的联系人的手机号码格式是"***-*****-****".
	 * <p/>
	 * 比如"135-5689-7852"
	 * 
	 * @time 2014年6月11日 下午3:43:05
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @return
	 */
	public static String formatPhoneNum(String phone) {
		String phoneFormated = null;
		phoneFormated = phone.replaceAll("[^0-9]", "");
		return phoneFormated;
	}
}

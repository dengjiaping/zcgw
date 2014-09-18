package com.hct.zc.utils;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.hct.zc.activity.reglogin.LoginActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;

public class ContextUtil {

	public static void pushToActivity(Context from, Class<?> to) {
		Intent intent = new Intent(from, to);
		from.startActivity(intent);
	}

	/**
	 * 如果没有登录即跳到登录页面.
	 * 
	 * @param from
	 * @param to
	 */
	public static void pushToActivityWithLogin(Context from, Class<?> to) {
		Intent intent = null;
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			intent = new Intent(from, LoginActivity.class);
		} else {
			intent = new Intent(from, to);
		}

		from.startActivity(intent);
	}

	public static void pushToActivityWithLogin(Context context, Intent intent) {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			intent = new Intent(context, LoginActivity.class);
		}
		context.startActivity(intent);
	}

	public static String getIMEI(Context context) {
		return ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

}

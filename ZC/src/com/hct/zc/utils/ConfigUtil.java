package com.hct.zc.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 
 * @todo 系统工具类
 * @time 2014-5-12 上午11:05:02
 * @author liuzenglong163@gmail.com
 */
@SuppressLint("SimpleDateFormat")
public class ConfigUtil {

	public static int dip2px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 
	 * @author lzlong@zwmob.com
	 * @time 2014-1-15 下午5:15:36
	 * @todo 获取设备的density
	 * @return
	 */
	public static float getDensity(Activity context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 获取曲线图轴左右边距
	 * 
	 * @param context
	 * @return
	 */
	public static int getDisSize(Activity context, float dis) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (scale * dis);
	}

	/**
	 * 获取imei
	 * 
	 * @return
	 */
	public static String getImei(Context app) {
		TelephonyManager telephonyManager = (TelephonyManager) app
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return imei;
	}

	/**
	 * 获取电话号码
	 * 
	 * @param app
	 * @return
	 */
	public static String getPhoneNumber(Context app) {
		TelephonyManager telephonyManager = (TelephonyManager) app
				.getSystemService(Context.TELEPHONY_SERVICE);
		String NativePhoneNumber = "";
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;

	}

	/**
	 * 获取版本号
	 * 
	 * */
	public static int getVersionCode(Context app) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = app.getPackageManager().getPackageInfo(
					app.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 
	 * 获取版本名称
	 * */
	public static String getVersionName(Context app) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = app.getPackageManager().getPackageInfo(
					app.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 判断当前网络是否已经连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

}

package com.hct.zc.utils;

import android.util.Log;

import com.hct.zc.BuildConfig;

public class LogUtil {

	private static final String COMMON_TAG = "ZC";

	public static boolean DEBUG_STATE = BuildConfig.DEBUG;

	public static void v(Object object, String msg) {
		if (DEBUG_STATE) {
			Log.v(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] "
					+ msg);
		}
	}

	public static void d(Object object, String msg) {
		if (DEBUG_STATE) {
			Log.d(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] "
					+ msg);
		}
	}

	public static void i(Object object, String msg) {
		if (DEBUG_STATE) {
			Log.i(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] "
					+ msg);
		}
	}

	public static void w(Object object, String msg) {
		Log.w(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] " + msg);
	}

	public static void e(Object object, String msg) {
		Log.e(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] " + msg);
	}

	public static void v(Object object, String msg, Throwable tr) {
		if (DEBUG_STATE) {
			Log.v(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] "
					+ msg, tr);
		}
	}

	public static void d(Object object, String msg, Throwable tr) {
		if (DEBUG_STATE) {
			Log.d(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] "
					+ msg, tr);
		}
	}

	public static void i(Object object, String msg, Throwable tr) {
		if (DEBUG_STATE) {
			Log.i(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] "
					+ msg, tr);
		}
	}

	public static void w(Object object, String msg, Throwable tr) {
		Log.w(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] " + msg,
				tr);
	}

	public static void e(Object object, String msg, Throwable tr) {
		Log.e(COMMON_TAG, "[" + object.getClass().getSimpleName() + "] " + msg,
				tr);
	}

}

package com.hct.zc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.widget.LockPatternView;

/**
 * 
 * 手势密码需要用到的工具.
 * 
 * @time 2014年5月9日 下午4:20:32
 * @author jie.liu
 */
public class LockPatternUtils {

	// private static final String TAG = "LockPatternUtils";
	private final static String KEY_LOCK_PWD = "lock_pwd";

	private static Context mContext;

	private static SharedPreferences preference;

	// private final ContentResolver mContentResolver;

	public LockPatternUtils(Context context) {
		mContext = context;
		preference = PreferenceManager.getDefaultSharedPreferences(mContext);
		// mContentResolver = context.getContentResolver();
	}

	/**
	 * Deserialize a pattern.
	 * 
	 * @param string
	 *            The pattern serialized with {@link #patternToString}
	 * @return The pattern.
	 */
	public static List<LockPatternView.Cell> stringToPattern(String string) {
		List<LockPatternView.Cell> result = new ArrayList<LockPatternView.Cell>();

		final byte[] bytes = string.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			result.add(LockPatternView.Cell.of(b / 3, b % 3));
		}
		return result;
	}

	/**
	 * Serialize a pattern.
	 * 
	 * @param pattern
	 *            The pattern.
	 * @return The pattern in string form.
	 */
	public static String patternToString(List<LockPatternView.Cell> pattern) {
		if (pattern == null) {
			return "";
		}
		final int patternSize = pattern.size();

		byte[] res = new byte[patternSize];
		for (int i = 0; i < patternSize; i++) {
			LockPatternView.Cell cell = pattern.get(i);
			res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
		}
		return Arrays.toString(res);
	}

	public void saveLockPattern(List<LockPatternView.Cell> pattern) {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		String phone = null;
		if (userInfo != null) {
			phone = userInfo.getPhone();
		}

		if (TextUtils.isEmpty(phone)) {
			return;
		}

		Editor editor = preference.edit();
		editor.putString(phone, patternToString(pattern));
		editor.commit();
	}

	/**
	 * 
	 * @todo 返回手势密码
	 * @time 2014年5月27日 下午4:24:59
	 * @author jie.liu
	 * @return 返回手势密码,如果没找到相应的手势密码则返回""
	 */
	public String getLockPaternString() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		String phone = null;
		if (userInfo != null) {
			phone = userInfo.getPhone();
		}

		if (TextUtils.isEmpty(phone)) {
			return "";
		}
		return preference.getString(phone, "");
	}

	public int checkPattern(List<LockPatternView.Cell> pattern) {
		String stored = getLockPaternString();
		if (!stored.equals("")) {
			return stored.equals(patternToString(pattern)) ? 1 : 0;
		}
		return -1;
	}

	public void clearLock() {
		saveLockPattern(null);
	}

}

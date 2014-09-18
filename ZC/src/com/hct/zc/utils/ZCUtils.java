package com.hct.zc.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hct.zc.R;

/**
 * @todo 掌财工具类
 * @time 2014-5-10 上午10:54:07
 * @author liuzenglong163@gmail.com
 */

public class ZCUtils {
	/**
	 * 
	 * @todo 隐藏键盘
	 * @param v
	 */
	public static void hiddenKeybroad(View v, Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	/**
	 * 
	 * @author lzlong@zwmob.com
	 * @time 2014-2-10 上午11:24:06
	 * @todo 格式化手机号 138-8888-8888
	 */
	public static void formatTelphone(final EditText mEditText) {
		mEditText.addTextChangedListener(new TextWatcher() {
			int beforeTextLength = 0;
			int onTextLength = 0;
			boolean isChanged = false;

			int location = 0;// 记录光标的位置
			private char[] tempChar;
			private final StringBuffer buffer = new StringBuffer();
			int konggeNumberB = 0;

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				beforeTextLength = s.length();
				if (buffer.length() > 0) {
					buffer.delete(0, buffer.length());
				}
				konggeNumberB = 0;
				for (int i = 0; i < s.length(); i++) {
					if (s.charAt(i) == '-') {
						konggeNumberB++;
					}
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onTextLength = s.length();
				buffer.append(s.toString());
				if (onTextLength == beforeTextLength || onTextLength <= 3
						|| isChanged) {
					isChanged = false;
					return;
				}
				isChanged = true;
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (isChanged) {
					location = mEditText.getSelectionEnd();
					int index = 0;
					while (index < buffer.length()) {
						if (buffer.charAt(index) == '-') {
							buffer.deleteCharAt(index);
						} else {
							index++;
						}
					}

					index = 0;
					int konggeNumberC = 0;
					while (index < buffer.length()) {
						if ((index == 3 || index == 8)) {
							buffer.insert(index, '-');
							konggeNumberC++;
						}
						index++;
					}

					if (konggeNumberC > konggeNumberB) {
						location += (konggeNumberC - konggeNumberB);
					}

					tempChar = new char[buffer.length()];
					buffer.getChars(0, buffer.length(), tempChar, 0);
					String str = buffer.toString();
					if (location > str.length()) {
						location = str.length();
					} else if (location < 0) {
						location = 0;
					}

					mEditText.setText(str);
					Editable etable = mEditText.getText();
					Selection.setSelection(etable, location);
					isChanged = false;
				}
			}
		});
	}

	/**
	 * 
	 * @author lzlong@zwmob.com
	 * @time 2014-1-2 下午8:40:54
	 * @todo 根据银行名字设置logo
	 * @param logo
	 * @param name
	 */
	public static int getFundChannelDrawable(String name) {
		if (name.contains("工商银行")) {
			return R.drawable.ic_bc_icon;
		} else if (name.contains("光大银行")) {
			return R.drawable.guangda_icon;
		} else if (name.contains("广发银行")) {
			return R.drawable.guangfa_icon;
		} else if (name.contains("汇付天下")) {
			return R.drawable.zhifutianxia_icon;
		} else if (name.contains("建设银行")) {
			return R.drawable.jianshe_icon;
		} else if (name.contains("交通银行")) {
			return R.drawable.jiaotong_icon;
		} else if (name.contains("农业银行")) {
			return R.drawable.nongye_icon;
		} else if (name.contains("浦发银行")) {
			return R.drawable.shanghaiputong_icon;
		} else if (name.contains("上海农商银行")) {
			return R.drawable.shanghainongshang_icon;
		} else if (name.contains("上海银行")) {
			return R.drawable.shanghai_icon;
		} else if (name.contains("通联支付")) {
			return R.drawable.tongliantianxia_icon;
		} else if (name.contains("兴业银行")) {
			return R.drawable.xingye_icon;
		} else if (name.contains("招商银行")) {
			return R.drawable.zhaoshang_icon;
		} else if (name.contains("中国银行")) {
			return R.drawable.china_bank_icon;
		} else if (name.contains("支付宝")) {
			return R.drawable.zhifubao_icon;
		} else if (name.contains("中国民生银行")) {
			return R.drawable.mingshen_icon;
		} else if (name.contains("平安银行")) {
			return R.drawable.pingan_icon;
		} else if (name.contains("中信银行")) {
			return R.drawable.zhongxing_icon;
		} else if (name.contains("银联")) {
			return R.drawable.yinlian_icon;
		} else {
			return 0;
		}
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

}

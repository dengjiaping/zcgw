package com.hct.zc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFormatChecker {

	/**
	 * 用户名为手机号格式.
	 * 
	 * @param username
	 * @return
	 */
	public static boolean isUsernameEligible(String username) {
		return isPhoneNumEligible(username);
	}

	public static boolean isPhoneNumEligible(String phoneNum) {
		LogUtil.e(new Object(), phoneNum);
		return matches(phoneNum, "^1\\d{10}$");
	}

	public static boolean isEmailEligible(String email) {
		return matches(email, "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$");
	}

	public static boolean isIdCardEligible(String idCard) {
		return matches(idCard,
				"^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$")
				|| matches(idCard,
						"^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$");
	}

	public static boolean isBankCardEligible(String bankCard) {
		return matches(bankCard, "[0-9]{19}");
	}

	/**
	 * 
	 * @param matchString
	 *            带匹配的字符串
	 * @param patternString
	 *            正则表达式
	 * @return
	 */
	private static boolean matches(String matchString, String patternString) {
		Pattern p = Pattern.compile(patternString);
		Matcher m = p.matcher(matchString);
		return m.matches();
	}

	public static boolean isPasswordEligible(String password) {
		int length = password.length();
		if (length >= 6 && length < 17) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isReserveMoneyEligible(String reserveMoney,
			String minMoney, String maxMoney) {
		boolean isMoneyRight = isMoneyFormtRight(reserveMoney, "^[1-9]\\d*0$");
		boolean isRegionRight = isRegionRight(minMoney, maxMoney);
		if (isMoneyRight && isRegionRight) {
			int sum = Integer.valueOf(reserveMoney);
			int min = Integer.valueOf(minMoney);
			int max = Integer.valueOf(maxMoney);
			if (max >= min && sum >= min && sum <= max) {
				return true;
			}
		}

		return false;
	}

	private static boolean isRegionRight(String minMoney, String maxMoney) {
		boolean isMinRight = isMoneyFormtRight(minMoney, "^0|^[1-9]\\d*0$");
		boolean isMaxRight = isMoneyFormtRight(maxMoney, "^0|^[1-9]\\d*0$");
		return isMinRight && isMaxRight;
	}

	private static boolean isMoneyFormtRight(String money, String pattern) {
		boolean isInteger = matches(money, pattern);
		if (!isInteger) {
			return false;
		}

		return true;
	}
}

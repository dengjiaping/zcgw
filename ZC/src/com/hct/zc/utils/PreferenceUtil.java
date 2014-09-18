package com.hct.zc.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;

public class PreferenceUtil {
	private final SharedPreferences sp;
	private final SharedPreferences.Editor editor;

	@SuppressLint("CommitPrefEdits")
	public PreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public boolean isFirstRun() {
		return sp.getBoolean("isFirstRun", true);
	}

	public void setIsFirstRun(boolean isFirstOpen) {
		editor.putBoolean("isFirstRun", isFirstOpen);
		editor.commit();
	}

	public String getFlashImageUrl() {
		return sp.getString("flashUrl", "");
	}

	public void setFlashImageUrl(String url) {
		editor.putString("flashUrl", url);
		editor.commit();
	}

	public void setShouldShowCommission(boolean shouldShowCommission) {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		String phone = null;
		if (userInfo != null) {
			phone = userInfo.getPhone();
		}

		if (TextUtils.isEmpty(phone)) {
			return;
		}
		editor.putBoolean(phone, shouldShowCommission);
		editor.commit();
	}

	public boolean getShouldShowCommission() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		String phone = null;
		if (userInfo != null) {
			phone = userInfo.getPhone();
		}

		if (TextUtils.isEmpty(phone)) {
			return false;
		}
		return sp.getBoolean(phone, false);
	}

	public void setUserInfo(UserInfo userInfo) {
		editor.putString("userId", userInfo.getUserId());
		editor.putString("name", userInfo.getName());
		editor.putString("phone", userInfo.getPhone());
		editor.putString("email", userInfo.getEmail());
		editor.putString("idcard", userInfo.getIdcard());
		editor.putString("bankcard", userInfo.getBankcard());
		editor.putString("address", userInfo.getAddress());
		editor.putString("alias", userInfo.getAlias());
		editor.putString("allCom", userInfo.getAllcom());
		editor.putString("threeCom", userInfo.getThreecom());
		editor.putString("grade", userInfo.getGrade());
		editor.putString("bankname", userInfo.getBankname());
		editor.commit();
	}

	public UserInfo getUserInfo() {
		String userId = sp.getString("userId", "");
		String name = sp.getString("name", "");
		String phone = sp.getString("phone", "");
		String email = sp.getString("email", "");
		String idcard = sp.getString("idcard", "");
		String bankcard = sp.getString("bankcard", "");
		String address = sp.getString("address", "");
		String alias = sp.getString("alias", "");
		String allCom = sp.getString("allCom", "");
		String threeCom = sp.getString("threeCom", "");
		String grade = sp.getString("grade", "");
		String bankname = sp.getString("bankname", "");

		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setName(name);
		userInfo.setPhone(phone);
		userInfo.setEmail(email);
		userInfo.setIdcard(idcard);
		userInfo.setBankcard(bankcard);
		userInfo.setAddress(address);
		userInfo.setAlias(alias);
		userInfo.setAllcom(allCom);
		userInfo.setThreecom(threeCom);
		userInfo.setGrade(grade);
		userInfo.setBankname(bankname);

		return userInfo;
	}

	public void setUsername(String username) {
		editor.putString("username", username);
		editor.commit();
	}

	public String getUsername() {
		return sp.getString("username", "");
	}

	public void setPassword(String password) {
		editor.putString("password", password);
		editor.commit();
	}

	public String getPassword() {
		return sp.getString("password", "");
	}

	public void setPageCachedDate(String date) {
		editor.putString("lastDate", date);
		editor.commit();
	}

	public String getPageCachedDate() {
		return sp.getString("lastDate", "2013-01-01 10:10:00");
	}

	public void setProductListCache(String jsonData) {
		editor.putString("productListData", jsonData);
		editor.commit();
	}

	public String getProductListCache() {
		return sp.getString("productListData", "");
	}
}

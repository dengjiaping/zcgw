package com.hct.zc.utils;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {

	public static <T> T from(String str, Class<T> t) {
		Gson gson = new Gson();
		return gson.fromJson(str, t);
	}

	public static <T> List<T> from(String str) {
		Gson gson = new Gson();
		return gson.fromJson(str, new TypeToken<List<T>>() {
		}.getType());
	}

}

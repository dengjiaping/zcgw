package com.hct.zc.utils;

import junit.framework.Assert;
import android.widget.EditText;

public class StringUtils {

	public static String getText(EditText editText) {
		return editText.getText().toString().trim();
	}

	public static String optimizePhone(String phone) {
		Assert.assertEquals(11, phone.length());
		return phone.substring(0, 3) + "****" + phone.substring(7, 11);
	}

	public static String optimizeBankCard(String bankCard) {
		Assert.assertEquals(19, bankCard.length());
		return bankCard.substring(0, 3) + "*******"
				+ bankCard.substring(15, 19);
	}

}

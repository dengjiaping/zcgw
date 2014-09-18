package com.hct.zc.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public class DateUtils {
	
	public static String convertUtilDateToStringWithTime(java.util.Date date) {
		return convertUtilDateToString(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static String convertUtilDateToStringWithoutTime(java.util.Date date) {
		return convertUtilDateToString(date, "yyyy-MM-dd");
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String convertUtilDateToString(java.util.Date date, String dateFormat) {
		String strDate = "";
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			strDate = format.format(new Date());
		}
		return strDate;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static java.util.Date convertStringToUtilDate(String dateStr) {
		java.util.Date date = null;
		if (dateStr != null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				date = format.parse(dateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}
	
	
	 
    /**  
     * 银行卡四位加空格  
     * 如6226 8558 3344 9954 225
     * @param mEditText  
     */  
    public static void formatBankCard(final EditText mEditText) {  
        mEditText.addTextChangedListener(new TextWatcher() {  
            int beforeTextLength = 0;  
            int onTextLength = 0;  
            boolean isChanged = false;  
  
            int location = 0;// 记录光标的位置  
            private char[] tempChar;  
            private StringBuffer buffer = new StringBuffer();  
            int konggeNumberB = 0;  
            
            
  
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {  
                beforeTextLength = s.length();  
                
                if(mEditText.getText().length()>25)
                	return;
                
                if (buffer.length() > 0) {  
                    buffer.delete(0, buffer.length());  
                }  
                konggeNumberB = 0;  
                for (int i = 0; i < s.length(); i++) {  
                    if (s.charAt(i) == ' ') {  
                        konggeNumberB++;  
                    }  
                }  
            }  
  
            @Override  
            public void onTextChanged(CharSequence s, int start, int before,  
                    int count) {  
                onTextLength = s.length();  
                
                if(mEditText.getText().length()>25)
                	return;
                
                
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
            	
                if(mEditText.getText().length()>25)
                	return;
            	
                if (isChanged) {  
                    location = mEditText.getSelectionEnd();  
                    int index = 0;  
                    while (index < buffer.length()) {  
                        if (buffer.charAt(index) == ' ') {  
                            buffer.deleteCharAt(index);  
                        } else {  
                            index++;  
                        }  
                    }  
  
                    index = 0;  
                    int konggeNumberC = 0;  
                    while (index < buffer.length()) {  
                        if ((index == 4 || index == 9 || index == 14 || index == 19)) {  
                            buffer.insert(index, ' ');  
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
	 * @todo  格式化银行卡号 如  6226 **** **** 3344
	 *  @param bankCode
	 *  @return
	 */
	public static String formatBankCode(String bankCode){
		if(!TextUtils.isEmpty(bankCode)){
			int length = bankCode.length();
			String strReplace = bankCode.substring(4, length-4);		//取得需要被替代的内容
			String newBankcode = bankCode.replace(strReplace," **** **** ");	//替换
			return newBankcode;
		}
		return "";
	}
}

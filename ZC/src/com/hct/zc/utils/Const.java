package com.hct.zc.utils;

import java.io.File;

import android.os.Environment;

/**
 * @todo 常量类
 * @time 2014-5-16 下午12:04:41
 * @author liuzenglong163@gmail.com
 */

public class Const {
	
	/**登录对话框的比例*/
	public static final double DIALOG_LOGIN_WIDTH = 0.8;
	public static final double DIALOG_LOGIN_HEIGHT = 0.3;
	
	/**地址对话框的比例*/
	public static final double DIALOG_ADDRESS_WIDTH = 0.9;
	public static final double DIALOG_ADDRESS_HEIGHT = 0.65;

	/** 身份证图片存储位置 */
	public static final String CID_IMG_STRING_NAME = ".jpg";
	public static final String CID_IMG_STRING_PATH_STRING = Environment
			.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	public static final String CID_IMG_STRING_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ System.currentTimeMillis() + CID_IMG_STRING_NAME;

	/** 汇款凭条存储位置 **/
	public static final String REMIT_SLIP = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ System.currentTimeMillis() + CID_IMG_STRING_NAME;

}

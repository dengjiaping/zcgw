package com.hct.zc.utils;

import java.io.File;

import android.os.Environment;

/**
 * @todo TODO
 * @time 2014年6月9日 上午11:33:20
 * @author liuzenglong163@gmail.com
 */

public class SDCardUtil {

	/**
	 * 
	 * @todo 获取SDCard路径
	 * @time 2014年6月9日 上午11:34:10
	 * @author liuzenglong163@gmail.com
	 * @return 返回SDCard绝对路径，如果SDCard没有挂载，则返回""
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}
}

package com.hct.zc.service;

import java.io.File;

import com.hct.zc.utils.ZipUtils;
import com.hct.zc.utils.ZipUtils.onZipListener;

/**
 * 解压压缩文件到指定目录
 * 
 * @time 2014年6月9日 下午5:43:27
 * @author liuzenglong163@gmail.com
 */

public class UnzipFiler {

	private onZipListener mOnZipListener;

	public void setOnZipListener(onZipListener onZipListener) {
		mOnZipListener = onZipListener;
	}

	/**
	 * 
	 * @todo 解压dirPath目录下的file文件
	 * @time 2014年6月10日 上午11:02:58
	 * @author jie.liu
	 * @param dirPath
	 *            压缩文件所在的文件夹路径
	 * @param fileName
	 *            压缩文件名
	 * @param targetDirPath
	 *            解压到这个目标文件夹下
	 */
	public void unzipFile(String dirPath, String fileName, String targetDirPath) {
		File targetDir = new File(targetDirPath);
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}

		if (!dirPath.endsWith(File.separator)) {
			dirPath += File.separator;
		}

		String filePath = dirPath + fileName;
		File zipFile = new File(filePath);
		if (zipFile.exists()) {
			ZipUtils utils = new ZipUtils();
			utils.setOnZipListener(mOnZipListener);
			utils.upZipFile(zipFile, targetDirPath);
		}
	}

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径
	private void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	private boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}

		if (!file.isDirectory()) {
			return flag;
		}

		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}
}

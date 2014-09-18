package com.hct.zc.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

/**
 * @todo 解压zip文件包
 * @time 2014年6月9日 上午11:19:05
 * @author liuzenglong163@gmail.com
 */

public class ZipUtils {

	private onZipListener mOnZipListener;

	public enum ZipError {
		ZIP_FILE_NOT_EXISTS, TARGET_FILE_HAS_EXISTS, IOEXCEPTION, ZIPEXCEPTION
	}

	/**
	 * 
	 * 解压缩功能. 将zipFile文件解压到指定目录下. 如果目标文件已经存在解压时会自动生成的文件夹，则会覆盖该文件夹
	 * 
	 * @time 2014年6月9日 下午12:03:44
	 * @author liuzenglong163@gmail.com
	 * @param zipFile
	 * @param targetDirPath
	 *            解压后自动生成的文件夹会在该文件夹下
	 * @return 解压成功返回 0， 解压失败返回-1
	 * @throws ZipException
	 * @throws IOException
	 */
	public void upZipFile(File zipFile, String targetDirPath) {
		if (!zipFile.exists()) {
			if (mOnZipListener != null) {
				mOnZipListener.onUnzipError(ZipError.ZIP_FILE_NOT_EXISTS);
			}
			return;
		}

		// File targetFile = new File(targetDirPath);
		// if (targetFile.exists()) {
		// if (mOnZipListener != null) {
		// mOnZipListener.onUnzipError(ZipError.TARGET_FILE_HAS_EXISTS);
		// }
		// return;
		// }

		new ZipTask(zipFile, targetDirPath).execute();

	}

	public void setOnZipListener(onZipListener onZipListener) {
		mOnZipListener = onZipListener;
	}

	private class ZipTask extends AsyncTask<String, Void, Integer> {

		private final File mZipFile;
		private String mDirPath;

		// 压缩文件的时候，软件会自动生成一个文件夹，把需要压缩的一堆文件放在文件夹中，然后将该文件夹压缩
		// 所以解压时，也会生成该文件夹，并把压缩的一堆文件解压到该文件夹中
		private String mRootDir;

		public ZipTask(File zipFile, String dirPath) {
			mZipFile = zipFile;
			mDirPath = dirPath;
		}

		@Override
		protected void onPreExecute() {
			if (mOnZipListener != null) {
				mOnZipListener.onUnzipBegin();
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			ZipFile zfile = null;
			try {
				zfile = new ZipFile(mZipFile);
				Enumeration zList = zfile.entries();
				ZipEntry ze = null;
				byte[] buf = new byte[1024];
				while (zList.hasMoreElements()) {
					ze = (ZipEntry) zList.nextElement();
					if (ze.isDirectory()) {
						Log.d("upZipDir", "ze.getName() = " + ze.getName());
						if (!mDirPath.endsWith(File.separator)) {
							mDirPath += File.separator;
						}

						if (TextUtils.isEmpty(mRootDir)) {
							mRootDir = mDirPath + ze.getName();
						}

						String dirstr = mDirPath + ze.getName();
						dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
						Log.d("upZipFile", "str = " + dirstr);
						File f = new File(dirstr);
						f.mkdir();
						continue;
					}
					Log.d("upZipFile", "ze.getName() = " + ze.getName());
					OutputStream os = new BufferedOutputStream(
							new FileOutputStream(getRealFileName(mDirPath,
									ze.getName())));
					InputStream is = new BufferedInputStream(
							zfile.getInputStream(ze));
					int readLen = 0;
					while ((readLen = is.read(buf, 0, 1024)) != -1) {
						os.write(buf, 0, readLen);
					}
					is.close();
					os.close();
				}
				zfile.close();
			} catch (ZipException e) {
				if (mOnZipListener != null) {
					mOnZipListener.onUnzipError(ZipError.ZIPEXCEPTION);
				}
				e.printStackTrace();
				return -1;
			} catch (IOException e) {
				if (mOnZipListener != null) {
					mOnZipListener.onUnzipError(ZipError.IOEXCEPTION);
				}
				e.printStackTrace();
				return -1;
			} finally {
				if (zfile != null) {
					try {
						zfile.close();
						zfile = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Log.d("upZipFile", "finishssssssssssssssssssss");
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 0 && mOnZipListener != null) {
				mOnZipListener.onUnzipFinished(result.intValue(), mZipFile,
						mRootDir);
			}
		}
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	private File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				ret = new File(ret, substr);

			}
			Log.d("upZipFile", "1ret = " + ret);
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			Log.d("upZipFile", "2ret = " + ret);
			return ret;
		}
		return ret;
	}

	public interface onZipListener {

		/**
		 * 
		 * @todo 开始解压文件
		 * @time 2014年6月9日 下午1:55:15
		 * @author liuzenglong163@gmail.com
		 */
		void onUnzipBegin();

		/**
		 * 
		 * @todo 解压完成调用此方法
		 * @time 2014年6月9日 下午1:53:56
		 * @author liuzenglong163@gmail.com
		 * @param resultCode
		 *            0:成功 , -1:失败
		 * @param zipFile
		 *            被解压的zip文件
		 * @param targetDir
		 *            将文件解压到此文件夹下,该文件夹是压缩时自动生成的文件夹
		 */
		void onUnzipFinished(int resultCode, File zipFile, String targetDir);

		/**
		 * 
		 * @todo 解压出错调用
		 * @time 2014年6月9日 下午2:00:18
		 * @author liuzenglong163@gmail.com
		 * @param zipError
		 *            解压出错类型
		 */
		void onUnzipError(ZipError zipError);
	}
}

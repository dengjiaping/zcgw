package com.hct.zc.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class HttpHelper {

	/** 响应成功 */
	public static final int RESPONSE_SUCCESS = 200;
	private static final String CHARSET = "UTF-8";
	private static final int DEFAULT_CONNECTION_TIMEOUT = (20 * 1000); // milliseconds
	private static final int DEFAULT_SOCKET_TIMEOUT = (20 * 1000); // milliseconds
	private OnHttpResponse mOnResponse;
	private OnDownloadResponse mOnDwladResponse;

	public void post(Activity context, final String path,
			final Map<String, String> params) {
		if (!new NetworkChecker().isNetworkAvailable(context)) {
			if (mOnResponse != null) {
				mOnResponse.onHttpNetworkNotFound(path);
			}
			return;
		}

		new NetworkAsynTask(path, params).execute();
	}

	private String parseParams(Map<String, String> params) {
		StringBuilder builder = new StringBuilder();
		if (params == null)
			params = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			builder.append(entry.getKey() + "="
					+ URLEncoder.encode(entry.getValue()) + "&");
		}
		if (builder.length() > 0)
			builder.deleteCharAt(builder.length() - 1);
		System.out.println(builder.toString());
		return builder.toString();
	}

	public void setOnHttpResponse(OnHttpResponse onHttpResponse) {
		if (onHttpResponse != mOnResponse) {
			mOnResponse = onHttpResponse;
		}
	}

	public interface OnHttpResponse {
		/***************** 使用方法：在BaseHttpActivity中实现此接口，可在子类中选择需要复写的方法 ****************/

		/**
		 * 
		 * 未检测到网络，运行在主线程
		 * 
		 * @time 2014年5月22日 下午6:14:51
		 * @param path
		 *            请求的url
		 * @author jie.liu
		 */
		void onHttpNetworkNotFound(String path);

		/**
		 * 
		 * 网络请求返回调用此接口, 运行在主线程
		 * 
		 * @time 2014年5月22日 下午6:07:47
		 * @author jie.liu
		 * @param path
		 *            请求的url
		 * @param response
		 *            返回码
		 * @param result
		 *            返回的结果
		 */
		void onHttpReturn(String path, int response, String result);

		/**
		 * 
		 * 网络请求抛出异常会调用此接口,运行在子线程
		 * 
		 * @time 2014年6月7日 下午3:17:57
		 * @author liuzenglong163@gmail.com
		 * @param path
		 *            请求的URL
		 * @param exception
		 *            捕获的异常
		 */
		void onHttpError(String path, Exception exception);

		/**
		 * 
		 * 网络请求出错会调用此接口, 运行在子线程
		 * 
		 * @time 2014年5月22日 下午6:10:43
		 * @author jie.liu
		 * @param path
		 *            请求的url
		 * @param response
		 *            返回码
		 */
		void onHttpError(String path, int response);

		/**
		 * 
		 * 网络请求成功会调用此接口,运行在主线程
		 * 
		 * @time 2014年5月22日 下午6:11:23
		 * @author jie.liu
		 * @param path
		 *            请求的url
		 * @param result
		 *            返回的结果
		 */
		void onHttpSuccess(String path, String result);

	}

	public static class NetworkChecker {
		public boolean isNetworkAvailable(Activity context) {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null)
					for (int i = 0; i < info.length; i++)
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
			}
			return false;
		}
	}

	private class NetworkAsynTask extends AsyncTask<Void, Void, String> {

		private final String mPath;

		private final Map<String, String> mParams;

		private int mRes;

		public NetworkAsynTask(String path, Map<String, String> params) {
			mPath = path;
			mParams = params;
		}

		@Override
		protected String doInBackground(Void... params) {
			StringBuffer sb = new StringBuffer();
			HttpURLConnection conn = null;
			DataOutputStream outStream = null;
			try {
				URL url = new URL(mPath);
				byte[] data = parseParams(mParams).getBytes(CHARSET);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
				conn.setReadTimeout(DEFAULT_SOCKET_TIMEOUT);
				conn.setUseCaches(false);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Charset", CHARSET);
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length",
						String.valueOf(data.length));
				outStream = new DataOutputStream(conn.getOutputStream());
				outStream.write(data);
				outStream.flush();
				outStream.close();

				int res = conn.getResponseCode();
				mRes = res;
				if (res == RESPONSE_SUCCESS) {
					InputStream in = conn.getInputStream();
					InputStreamReader reader = new InputStreamReader(in,
							CHARSET);
					char[] buff = new char[1024];
					int len;
					while ((len = reader.read(buff)) > 0) {
						sb.append(buff, 0, len);
					}
					reader.close();
					in.close();
				} else {
					if (mOnResponse != null) {
						mOnResponse.onHttpError(mPath, res);
					}
				}
			} catch (Exception e) {
				if (mOnResponse != null) {
					mOnResponse.onHttpError(mPath, e);
				}
				e.printStackTrace();
			} finally {
				if (null != conn) {
					conn.disconnect();
				}
			}

			return sb.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			if (mOnResponse != null) {
				mOnResponse.onHttpReturn(mPath, mRes, result);
				if (mRes == 200) {
					mOnResponse.onHttpSuccess(mPath, result);
				}
			}
		}
	}

	/**
	 * 
	 * @todo 下载文件
	 * @time 2014年6月10日 上午10:03:35
	 * @author liuzenglong163@gmail.com
	 * @param context
	 * @param url
	 * @param targetDir
	 */
	public void downloadFile(Activity context, String url, String targetDir) {
		if (!new NetworkChecker().isNetworkAvailable(context)) {
			if (mOnDwladResponse != null) {
				mOnDwladResponse.onNetworkNotFound(url);
			}
			return;
		}

		new DownloadAsynTask(url, targetDir).execute();
	}

	private class DownloadAsynTask extends AsyncTask<Void, Void, Integer> {

		private final String mUrl;
		private String mTargetDir;
		private String mTargetFileUrl;

		public DownloadAsynTask(String url, String targetDir) {
			mUrl = url;
			mTargetDir = targetDir;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			URLConnection con = null;
			InputStream is = null;
			OutputStream os = null;
			try {
				URL url = new URL(mUrl);
				// 打开连接
				con = url.openConnection();
				// 输入流
				is = con.getInputStream();
				File targetDir = new File(mTargetDir);
				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}

				// 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
				String filename = con.getHeaderField("Content-Disposition");
				if (filename == null || filename.length() < 1) {
					filename = UUID.randomUUID() + "";
				}
				// String filename = mUrl.substring(mUrl.lastIndexOf("/") + 1);
				if (!mTargetDir.endsWith(File.separator)) {
					mTargetDir += File.separator;
				}

				mTargetFileUrl = mTargetDir + filename;
				File file = new File(mTargetFileUrl);
				// 如果目标文件已经存在，则删除。产生覆盖旧文件的效果
				if (file.exists()) {
					file.delete();
				}

				// 1K的数据缓冲
				byte[] bs = new byte[1024];
				// 读取到的数据长度
				int len;
				// 输出的文件流
				os = new FileOutputStream(mTargetFileUrl);
				// 开始读取
				while ((len = is.read(bs)) != -1) {
					os.write(bs, 0, len);
				}
				// 完毕，关闭所有链接
				os.flush();
				os.close();
				is.close();
				return 0;
			} catch (MalformedURLException e) {
				if (mOnDwladResponse != null) {
					mOnDwladResponse.onDownloadError(mUrl, e);
				}
				e.printStackTrace();
			} catch (IOException e) {
				if (mOnDwladResponse != null) {
					mOnDwladResponse.onDownloadError(mUrl, e);
				}
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (mOnDwladResponse != null && result.intValue() == 0) {
				mOnDwladResponse.onDownloadSuccess(mUrl, mTargetFileUrl);
			}
		}
	}

	public void setOnDownloadResponse(OnDownloadResponse onDownloadResponse) {
		mOnDwladResponse = onDownloadResponse;
	}

	public interface OnDownloadResponse {

		/**
		 * 
		 * 下载异常调用，运行在子线程
		 * 
		 * @time 2014年6月10日 上午10:42:39
		 * @author jie.liu
		 * @param url
		 * @param e
		 */
		void onDownloadError(String url, Exception e);

		/**
		 * 
		 * 未检测到网络调用，运行在主线程
		 * 
		 * @time 2014年6月10日 上午10:43:08
		 * @author jie.liu
		 * @param url
		 */
		void onNetworkNotFound(String url);

		/**
		 * 
		 * 下载成功调用，运行在主线程
		 * 
		 * @time 2014年6月10日 上午10:43:33
		 * @author jie.liu
		 * @param url
		 *            下载文件在服务器上的地址
		 * @param targetUrl
		 *            下载文件保存的地址
		 */
		void onDownloadSuccess(String url, String targetUrl);
	}
}

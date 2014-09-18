package com.hct.zc.activity.more;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.hct.zc.R;

/**
 * @todo app更新服务
 * @time 2014-5-22 下午2:02:29
 * @author liuzenglong163@gmail.com
 */
public class UpdateService extends Service {
	// 标题
	private int titleId = 0;
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	private final static int DOWNLOAD_ING = 2;
	// 文件存储
	private File updateDir = null;
	private File updateFile = null;

	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// <strong>通知</strong>栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	private String downLoadUrl; // 下载地址

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null == intent)
			return 0;
		// 获取传值
		titleId = intent.getIntExtra("titleId", 0);
		downLoadUrl = intent.getStringExtra("downloadUrl");
		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(),
					"app/download/");
			updateFile = new File(updateDir.getPath(), getResources()
					.getString(titleId) + ".apk");
		}

		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();

		// 设置<strong>下载</strong>过程中，点击<strong>通知</strong>栏，回到主界面
		updateIntent = new Intent(this, MoreActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
				0);
		// 设置<strong>通知</strong>栏<strong>显示</strong>内容
		updateNotification.icon = R.drawable.ic_update_notification;
		updateNotification.tickerText = "开始下载";
		updateNotification.setLatestEventInfo(this,
				getResources().getString(R.string.app_name), "0%",
				updatePendingIntent);
		// 发出<strong>通知</strong>
		updateNotificationManager.notify(0, updateNotification);
		// 开启一个新的线程<strong>下载</strong>，如果使用Service同步<strong>下载</strong>，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是<strong>下载</strong>的重点，是<strong>下载</strong>的过程

		return super.onStartCommand(intent, flags, startId);
	}

	private final Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				updateNotification.flags |= updateNotification.FLAG_AUTO_CANCEL;
				// 点击<strong>安装</strong>PendingIntent
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				updatePendingIntent = PendingIntent.getActivity(
						UpdateService.this, 0, installIntent, 0);
				updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
				updateNotification.setLatestEventInfo(UpdateService.this,
						getResources().getString(R.string.app_name),
						"下载完成,点击安装", updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);

				installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(installIntent);
				// 停止服务
				stopService(updateIntent);
				break;
			case DOWNLOAD_FAIL:
				// 下载失败
				updateNotification.setLatestEventInfo(UpdateService.this,
						getResources().getString(R.string.app_name), "下载失败",
						updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);
				stopService(updateIntent);
				break;
			default:
				stopService(updateIntent);
			}
		}
	};

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		@Override
		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {
				// 增加权限<uses-permission
				// android:name="android.permission.WRITE_EXTERNAL_STORAGE">;
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				long downloadSize = downloadUpdateFile(downLoadUrl, updateFile);
				if (downloadSize > 0) {
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				updateHandler.sendMessage(message);
			}
		}
	}

	public long downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;
		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的<strong>通知</strong>导致<strong>应用</strong>吃紧，百分比增加10才<strong>通知</strong>一次
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
					downloadCount += 10;
					updateNotification.setLatestEventInfo(UpdateService.this,
							"正在下载", (int) totalSize * 100 / updateTotalSize
									+ "%", updatePendingIntent);
					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

}

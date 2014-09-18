package com.hct.zc.utils;

import android.app.Activity;
import android.app.ProgressDialog;

public class LoadingProgress {

	private ProgressDialog mDialog;

	private static LoadingProgress intance = new LoadingProgress();

	private int mShownCount;

	public static LoadingProgress getInstance() {
		return intance;
	}

	private LoadingProgress() {
	}

	public void show(Activity context) {
		show(context, "正在加载中...");
	}

	public void show(Activity context, String message) {
		if (haveShown()) {
			mShownCount++;
			return;
		}

		createLoadingDialog(context, message);

		if (!mDialog.isShowing()) {
			LogUtil.i(context, "显示ProgressDialog");
			mDialog.show();
		}
	}

	private boolean haveShown() {
		return mShownCount != 0 ? true : false;
	}

	private void createLoadingDialog(Activity context, String message) {
		mDialog = new ProgressDialog(context);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setIndeterminate(false);
		mDialog.setInverseBackgroundForced(false);
		mDialog.setMessage(message);
	}

	public void dismiss() {
		if (mDialog != null && mDialog.isShowing()) {
			System.out.println("关闭ProgressDialog");
			mShownCount = 0;
			mDialog.cancel();
		}
	}

}

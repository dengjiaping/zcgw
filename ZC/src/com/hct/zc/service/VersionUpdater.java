package com.hct.zc.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.more.UpdateService;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.ApkBean;
import com.hct.zc.http.HttpHelper.OnHttpResponse;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.ApkResult;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;

/**
 * 用于更新版本
 * 
 * @time 2014年7月2日 下午3:04:49
 * @author liuzenglong163@gmail.com
 */

public class VersionUpdater implements OnHttpResponse {

	private final Activity mActivity;

	private boolean mToastEnable = true;

	public VersionUpdater(Activity activity) {
		mActivity = activity;
	}

	public void checkVersion() {
		HttpRequest.doUpdateVersion(mActivity, this);
	}

	public void setToastEnable(boolean enable) {
		mToastEnable = enable;
	}

	@Override
	public void onHttpNetworkNotFound(String path) {

	}

	@Override
	public void onHttpReturn(String path, int response, String result) {
	}

	@Override
	public void onHttpError(String path, Exception exception) {
		LogUtil.e(this, "检查版本更新，网络异常");
	}

	@Override
	public void onHttpError(String path, int response) {
		LogUtil.e(this, "检查版本更新，网络超时");
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		if (TextUtils.isEmpty(result)) {
			if (mToastEnable) {
				Toaster.showShort(mActivity, "服务器出错了，请重试");
			}
			LogUtil.e(this, "网络返回的数据为空");
			return;
		}

		Gson gson = new Gson();
		ApkResult r = gson.fromJson(result, ApkResult.class);
		ApkBean apkBean = r.getVersioninfo();
		String returnCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(returnCode)) {
			ZCApplication.apkBean = apkBean;
			doUpdate(apkBean);
		} else if (HttpResult.ARG_ERROR.equals(returnCode)
				|| HttpResult.SYS_ERROR.equals(returnCode)) {
			if (mToastEnable) {
				Toaster.showShort(mActivity, "没有发现新版本");
			}
		} else {
			if (mToastEnable) {
				Toaster.showShort(mActivity, r.getResult().getErrormsg());
			}
		}
	}

	private void doUpdate(final ApkBean apkBean) {
		new AlertDialog.Builder(mActivity).setTitle("发现新版本")
				.setMessage(apkBean.explain)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						yesBtnWork(apkBean);
					}
				}).setNegativeButton("取消", null).create().show();
	}

	/**
	 * @time 2014-5-22 下午2:19:22
	 * @author liuzenglong163@gmail.com
	 */
	private void yesBtnWork(ApkBean apkBean) {
		Intent updateIntent = new Intent(mActivity, UpdateService.class);
		updateIntent.putExtra("titleId", R.string.app_name);
		updateIntent.putExtra("downloadUrl", apkBean.download);
		mActivity.startService(updateIntent);
	}

}

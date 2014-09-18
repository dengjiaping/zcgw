package com.hct.zc.activity.base;

import com.hct.zc.http.HttpHelper.OnHttpResponse;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;

/**
 * @todo 带有网络请求的Activity
 * @time 2014年6月7日 下午2:56:12
 * @author liuzenglong163@gmail.com
 */

public class BaseHttpActivity extends BaseActivity implements OnHttpResponse {

	@Override
	public void onHttpNetworkNotFound(String path) {
		Toaster.showShort(this, "未检测到可用的网络");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpReturn(String path, int response, String result) {

	}

	@Override
	public void onHttpError(String path, Exception exception) {
		Toaster.showShort(this, "网络连接异常，请重试");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpError(String path, int response) {
		Toaster.showShort(this, "网络连接超时，请重试");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		StringBuilder sb = new StringBuilder();
		sb.append("请求路径：").append(path).append("\n").append(result);
		LogUtil.d(this, sb.toString());
		dismissDialogIfNeeded();
	}

	private void dismissDialogIfNeeded() {
		// 如果有启动进度条对话框将被关闭
		LoadingProgress.getInstance().dismiss();
	}

}

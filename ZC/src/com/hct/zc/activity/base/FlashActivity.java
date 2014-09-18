package com.hct.zc.activity.base;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.constants.Constants;
import com.hct.zc.http.HttpHelper.NetworkChecker;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.FlashImageResult;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.utils.imagecache.ImageCacheManager;
import com.hct.zc.utils.imagecache.ImageCacheManager.OnBitmapFromHttpListener;

/**
 * @todo 启动页 .
 * @time 2014年5月4日 下午2:10:06
 * @author jie.liu
 */
public class FlashActivity extends BaseHttpActivity implements
		OnBitmapFromHttpListener {

	private final long DELAY_MILLIS = 4000;

	private PreferenceUtil mUtil;
	private ImageView mFlashIV;
	private Bitmap mBitmapShown;
	private final Handler mHandler = new FinishHandler(this);

	private static class FinishHandler extends Handler {

		private final WeakReference<FlashActivity> mRef;

		public FinishHandler(FlashActivity activity) {
			mRef = new WeakReference<FlashActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mRef == null)
				return;
			FlashActivity activity = mRef.get();
			if (activity == null) {
				return;
			}

			boolean isFirstRun = activity.isFirstRunAPP();
			if (isFirstRun) {
				ContextUtil.pushToActivity(activity, NavigationActivity.class);
				activity.mUtil.setIsFirstRun(false);
			} else {
				ContextUtil.pushToActivity(activity, HomePageActivity.class);
			}

			activity.finish();
		}
	}

	private boolean isFirstRunAPP() {
		boolean isFirstRun = mUtil.isFirstRun();
		return isFirstRun;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_activity);

		mUtil = new PreferenceUtil(FlashActivity.this,
				Constants.PREFERENCE_FILE);
		mFlashIV = (ImageView) findViewById(R.id.flash_iv);

		loadBitmapIfNotExist();
	}

	private void loadBitmapIfNotExist() {
		// 无网络采用默认图片，并跳转
		if (!new NetworkChecker().isNetworkAvailable(FlashActivity.this)) {
			fillWithDefaultImg();
			return;
		}

		String url = mUtil.getFlashImageUrl();
		if (haveFlashImgUrl(url)) {
			loadBitmap(url);
		} else {
			fillWithDefaultImg();
		}

		HttpRequest.getLoadingPageUrl(this, this);
	}

	private boolean haveFlashImgUrl(String url) {
		return TextUtils.isEmpty(url) ? false : true;
	}

	private void loadBitmap(String url) {
		ImageCacheManager imageCache = new ImageCacheManager(this);
		imageCache.setOnGetFromHttpListener(this);
		mBitmapShown = imageCache.getBitmap(url, mFlashIV);
		if (mBitmapShown != null) {
			// 图片是从本地缓存中取，并不是网络下载
			LogUtil.d(FlashActivity.this, "显示本地缓存图片，并跳转");
			animShowImgAndScheduleFinish();
		} else {
			// 图片从网络上下
			LogUtil.d(FlashActivity.this, "从网络上下载图");
		}
	}

	@Override
	public void onGetBitmapNetworkNotFound(String url) {
		treatGetBitmapFail();
	}

	@Override
	public void onGetBitmapBegin(String url) {
		LogUtil.d(FlashActivity.this, "开始下载图片");
	}

	@Override
	public void onGetBitmapError(String url, Exception e) {
		treatGetBitmapFail();
	}

	private void treatGetBitmapFail() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				fillWithDefaultImg();
			}
		});
	}

	@Override
	public void onGetBitmapOver(String url, Bitmap bitmap) {
		LogUtil.d(FlashActivity.this, "图片下载完毕");
		mBitmapShown = bitmap;
		animShowImgAndScheduleFinish();
	}

	@Override
	public void onHttpError(String path, Exception exception) {
		super.onHttpError(path, exception);
		treatHttpError(path);
	}

	@Override
	public void onHttpError(String path, int response) {
		super.onHttpError(path, response);
		treatHttpError(path);
	}

	private void treatHttpError(String path) {
		if (HttpUrl.LOADING_PAGE.equals(path)) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					fillWithDefaultImg();
				}
			});
		}
	}

	void fillWithDefaultImg() {
		mBitmapShown = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg_flash_top);
		mFlashIV.setImageBitmap(mBitmapShown);
		animShowImgAndScheduleFinish();
	}

	private void animShowImgAndScheduleFinish() {
		startShowAnimation();
		mFlashIV.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageDelayed(0, DELAY_MILLIS);
	}

	private void startShowAnimation() {
		AlphaAnimation animationClose;
		animationClose = new AlphaAnimation(0f, 1f);
		animationClose.setDuration(3000);
		mFlashIV.setAnimation(animationClose);
		animationClose.startNow();
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		super.onHttpSuccess(path, result);
		if (TextUtils.isEmpty(result)) {
			Toaster.showShort(this, "服务器出错了，请重试");
			LogUtil.e(this, "网络返回的数据为空");
			return;
		}
		dealWithHttpReturned(result);
	}

	private void dealWithHttpReturned(String result) {
		Gson gson = new Gson();
		Log.e("result", result);
		FlashImageResult r = gson.fromJson(result, FlashImageResult.class);
		if (r == null) {
			Log.e("result", "FlashImageResult is null");
		}

		if (r.getResult() == null) {
			Log.e("result", "Result is null");
		}
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			// 获取图片地址成功，保存图片地址
			if (r != null && r.loadpic != null) {
				String url = r.loadpic.url;
				mUtil.setFlashImageUrl(url);
			}
		} else if (HttpResult.FAIL.equals(resultCode)) {
			LogUtil.w(this, "获取加载页图片地址失败");
		} else {
			LogUtil.e(this, "获取加载页图片地址出错：" + resultCode);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 消耗掉返回键，不让其用返回键关闭
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmapShown != null) {
			mBitmapShown.recycle();
			mBitmapShown = null;
		}
	}

}

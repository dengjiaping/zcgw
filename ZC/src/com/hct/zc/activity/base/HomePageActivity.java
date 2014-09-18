package com.hct.zc.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.gesturelock.GestureLockSettingActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.constants.BroadcastAction;
import com.hct.zc.fragment.CenterFragment;
import com.hct.zc.fragment.LeftMenuFragment;
import com.hct.zc.http.HttpHelper.OnHttpResponse;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.SysMsgResult;
import com.hct.zc.service.VersionUpdater;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;

public class HomePageActivity extends BaseSlidingActivity implements
		OnHttpResponse {
	// 首先在您的Activity中添加如下成员变量
	private static final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share", RequestType.SOCIAL);

	private ImageView mMenuGuideIV;
	private ImageView mCommissionGuideIV;
	private CenterFragment mCenterFragment;

	private AfterRegisterBroadcastRecevier mAfterRegisterRecevier;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			String FRAGMENTS_TAG = "android:support:fragments";
			// remove掉保存的Fragment
			savedInstanceState.remove(FRAGMENTS_TAG);
		}
		super.onCreate(savedInstanceState);

		UmengUpdateAgent.update(this);
		MobclickAgent.openActivityDurationTrack(false);

		// set the Above View
		View contentView = getLayoutInflater().inflate(
				R.layout.home_page_content_frame, null);
		setContentView(contentView);
		mCenterFragment = new CenterFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mCenterFragment).commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setMode(SlidingMenu.RIGHT);
		sm.setShadowDrawable(R.drawable.shadowright);
		sm.setSecondaryMenu(R.layout.menu_frame);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new LeftMenuFragment()).commit();

		registerAfterRegisterRecevier();
		initGuideViews(contentView);

		// 检测新版本：
		if (savedInstanceState == null) {
			VersionUpdater updater = new VersionUpdater(this);
			updater.setToastEnable(false);
			updater.checkVersion();
		}

		//
		// //获取系统消息通知：
		// if(null!=ZCApplication.getInstance().getUserInfo()){
		// String userId =
		// ZCApplication.getInstance().getUserInfo().getUserId();
		// HttpRequest.doRequestSysMsg(this,userId, "1", "100", this);
		// }
	}

	// public boolean isOpening() {
	// SlidingMenu sm = getSlidingMenu();
	// return sm.isOpening();
	// }

	private void initGuideViews(View rootView) {
		mMenuGuideIV = (ImageView) rootView.findViewById(R.id.menu_guide_iv);
		mCommissionGuideIV = (ImageView) rootView
				.findViewById(R.id.commission_guide_iv);
		// TODO 改成onTouch或者，此时不让滑动菜单滑动
		ClickListener listener = new ClickListener();
		mMenuGuideIV.setOnClickListener(listener);
		mCommissionGuideIV.setOnClickListener(listener);
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.menu_guide_iv:
				mMenuGuideIV.setVisibility(View.GONE);
				mCommissionGuideIV.setVisibility(View.VISIBLE);
				break;
			case R.id.commission_guide_iv:
				ContextUtil.pushToActivity(HomePageActivity.this,
						GestureLockSettingActivity.class);
				refreshUiAfterGuideFinish();
				break;
			default:
				LogUtil.w(HomePageActivity.this, "点击了未知的按钮");
			}
		}
	}

	private void refreshUiAfterGuideFinish() {
		mMenuGuideIV.setVisibility(View.GONE);
		mCommissionGuideIV.setVisibility(View.GONE);
		if (!getSlidingMenu().isSecondaryMenuShowing()) {
			toggle();
		}
	}

	private void registerAfterRegisterRecevier() {
		mAfterRegisterRecevier = new AfterRegisterBroadcastRecevier();
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(BroadcastAction.ACTION_GUIDE_TO_COMMISSION_SWITCHER);
		registerReceiver(mAfterRegisterRecevier, iFilter);
	}

	private class AfterRegisterBroadcastRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mMenuGuideIV.setVisibility(View.VISIBLE);
			SlidingMenu sm = getSlidingMenu();
			if (sm.isSecondaryMenuShowing()) {
				sm.toggle();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onDestroy() {
		ZCApplication.getInstance().storeUserInfo(null);
		unregisterReceiver(mAfterRegisterRecevier);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		mCenterFragment.backPressed();
	}

	@Override
	public void onHttpReturn(String path, int response, String result) {
		LoadingProgress.getInstance().dismiss();
		LogUtil.d(this, result);
		if (response == 200) {
			if (HttpUrl.GET_SYS_MSG.equals(path)) {
				Gson gson = new Gson();
				SysMsgResult r = gson.fromJson(result, SysMsgResult.class);
				String returnCode = r.getResult().getErrorcode();
				if (HttpResult.SUCCESS.equals(returnCode)) {
					ZCApplication.msgList = r.getMyMsgs();
				} else {
					Toaster.showShort(HomePageActivity.this, r.getResult()
							.getErrormsg());
				}
			}
		} else {
			Toaster.showShort(this, "请求出错:" + response);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onHttpNetworkNotFound(String path) {

	}

	@Override
	public void onHttpError(String path, Exception exception) {

	}

	@Override
	public void onHttpError(String path, int response) {

	}

	@Override
	public void onHttpSuccess(String path, String result) {

	}
}

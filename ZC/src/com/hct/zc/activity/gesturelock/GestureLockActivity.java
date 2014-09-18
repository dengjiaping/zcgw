package com.hct.zc.activity.gesturelock;

import java.lang.ref.WeakReference;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.constants.BroadcastAction;
import com.hct.zc.constants.Constants;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LockPatternUtils;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.LockPatternView;
import com.hct.zc.widget.LockPatternView.Cell;
import com.hct.zc.widget.LockPatternView.DisplayMode;
import com.hct.zc.widget.LockPatternView.OnPatternListener;

/**
 * @todo 手势密码识别.
 * @time 2014年5月4日 下午2:10:31
 * @author jie.liu
 */
public class GestureLockActivity extends BaseHttpActivity {

	private LockPatternView mLockPatternView;
	private LockPatternUtils mLockUtil;
	private TextView mPromptTV;
	private Handler mHandler;
	private boolean mIsGestureRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesture_lock_activity);
		mHandler = new DismissHandler(new WeakReference<GestureLockActivity>(
				this));
		mLockUtil = new LockPatternUtils(GestureLockActivity.this);
		initViews();
	}

	private void initViews() {
		initTitlebar();
		mPromptTV = (TextView) findViewById(R.id.prompt_tv);
		mLockPatternView = (LockPatternView) findViewById(R.id.lock_pattern_view);
		mLockPatternView.setOnPatternListener(new PatternListener());
	}

	private void initTitlebar() {
		Button leftBtn = (Button) findViewById(R.id.left_btn);
		Button rightBtn = (Button) findViewById(R.id.right_btn);
		ClickListener listener = new ClickListener();
		leftBtn.setOnClickListener(listener);
		rightBtn.setOnClickListener(listener);
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_btn:
				finish();
				break;
			case R.id.right_btn:
				UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
				if (userInfo == null) {
					Toaster.showShort(GestureLockActivity.this, "请先登录");
					return;
				}

				final EditText passwordET = new EditText(
						GestureLockActivity.this);
				new AlertDialog.Builder(GestureLockActivity.this)
						.setTitle("验证登录密码")
						.setView(passwordET)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										isPasswordRight(StringUtils
												.getText(passwordET));
									}
								}).setNegativeButton("取消 ", null).show();
				break;
			default:
				LogUtil.w(GestureLockActivity.this, "点击了未知的按钮");
			}
		}
	}

	private boolean isPasswordRight(String password) {
		if (TextUtils.isEmpty(password)) {
			Toaster.showShort(GestureLockActivity.this, "密码不能为空");
			return false;
		}

		if (!InputFormatChecker.isPasswordEligible(password)) {
			Toaster.showShort(GestureLockActivity.this, "密码格式不正确");
			return false;
		}

		HttpRequest.doVerfiyPwd(this, ZCApplication.getInstance().getUserInfo()
				.getPhone(), password, this);
		return false;
	}

	private class PatternListener implements OnPatternListener {

		@Override
		public void onPatternStart() {
		}

		@Override
		public void onPatternCleared() {
		}

		@Override
		public void onPatternCellAdded(List<Cell> pattern) {
		}

		@Override
		public void onPatternDetected(List<Cell> pattern) {
			LogUtil.d(GestureLockActivity.this, "onPatternDetected");
			if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
				mPromptTV.setTextColor(Color.RED);
				mPromptTV
						.setText(R.string.lockpattern_recording_incorrect_too_short);
				mLockPatternView.setDisplayMode(DisplayMode.Wrong);
				mLockPatternView.disableInput();
				mHandler.sendEmptyMessageDelayed(0, 1000);
				mIsGestureRight = false;
				return;
			}

			int result = mLockUtil.checkPattern(pattern);
			if (result == -1) {
				LogUtil.w(GestureLockActivity.this, "手势识别理解有错，请修改");
			}

			if (result == 1) {
				mIsGestureRight = true;
				mPromptTV.setTextColor(Color.BLACK);
				mPromptTV.setText("密码正确");
				new PreferenceUtil(GestureLockActivity.this,
						Constants.PREFERENCE_FILE)
						.setShouldShowCommission(true);
				sendRefreshComRecevier();
			} else {
				mIsGestureRight = false;
				mPromptTV.setTextColor(Color.RED);
				mPromptTV.setText("密码有误，请再次输入");
				mLockPatternView.setDisplayMode(DisplayMode.Wrong);
			}

			mLockPatternView.disableInput(); // 禁止输入，在手势消失后会重新enableInput
			mHandler.sendEmptyMessageDelayed(0, 1000);
		}
	}

	private static class DismissHandler extends Handler {

		private final WeakReference<GestureLockActivity> mContext;

		public DismissHandler(WeakReference<GestureLockActivity> context) {
			mContext = context;
		}

		@Override
		public void handleMessage(Message msg) {
			GestureLockActivity activity = mContext.get();
			if (activity == null) {
				return;
			}

			activity.mLockPatternView.clearPattern();
			activity.mLockPatternView.enableInput();
			if (activity.mIsGestureRight) {
				activity.finish();
			}
		}
	}

	private void sendRefreshComRecevier() {
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_REFRESH_LOGIN_STATE);
		sendBroadcast(intent);
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
		HttpResult r = gson.fromJson(result, HttpResult.class);
		String responseCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(responseCode)) {
			LockPatternUtils util = new LockPatternUtils(this);
			util.clearLock();
			Toaster.showShort(this, "请重新设置密码");
			ContextUtil.pushToActivity(this, GestureLockSettingActivity.class);
			finish();
		} else if (HttpResult.FAIL.equals(responseCode)) {
			Toaster.showShort(this, r.getResult().getErrormsg());
		} else {
			Toaster.showShort(this, "重置密码失败，请重试");
		}
	}
}

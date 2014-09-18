package com.hct.zc.activity.reglogin;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.constants.BroadcastAction;
import com.hct.zc.constants.Constants;
import com.hct.zc.fragment.CenterFragment.State;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.LoginResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.http.result.VerifyCodeResult;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * @todo 电话验证与设置密码，注册时用的.
 * @time 2014年5月4日 下午3:44:50
 * @author jie.liu
 */

public class PhoneVerificationActivity extends BaseHttpActivity {

	/**
	 * 获取短信验证码时间间隔60秒
	 */
	private final int TIME_INTERVAL = 60;

	private String mPhoneNum;
	private EditText mCaptchaET;
	private EditText mNewPasswordET;
	private EditText mDupPasswordET;
	private CheckBox mGetCaptchaCB;
	private int mCountBackwords = TIME_INTERVAL;
	private Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_verification_activity);
		mPhoneNum = getIntent().getExtras().getString("phone");
		initViews();

		getSmsCaptcha();
	}

	private void initViews() {
		initTitlebar();
		mGetCaptchaCB = (CheckBox) findViewById(R.id.get_sms_captcha_cb);
		mCaptchaET = (EditText) findViewById(R.id.input_sms_captcha_et);
		mNewPasswordET = (EditText) findViewById(R.id.input_new_password_et);
		mDupPasswordET = (EditText) findViewById(R.id.input_new_password_again_et);
		Button certainBtn = (Button) findViewById(R.id.certain_btn);
		ClickListener listener = new ClickListener();
		mGetCaptchaCB.setOnClickListener(listener);
		certainBtn.setOnClickListener(listener);
	}

	private void initTitlebar() {
		new TitleBar(PhoneVerificationActivity.this).initTitleBar("短信验证");
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.get_sms_captcha_cb:
				getSmsCaptcha();
				break;
			case R.id.certain_btn:
				requestAdivisor();
				break;
			default:
				LogUtil.w(PhoneVerificationActivity.this, "点击了未知的按钮");
			}
		}
	}

	private void getSmsCaptcha() {
		if (canGetSmsCaptcha()) {
			performGetSmsCaptcha();
			scheduleCountBackwords();
		} else {
			mGetCaptchaCB.setChecked(false);
		}
	}

	private boolean canGetSmsCaptcha() {
		if (TextUtils.isEmpty(mPhoneNum)) {
			Toaster.showShort(PhoneVerificationActivity.this,
					R.string.phone_num_can_not_be_null);
			return false;
		}

		boolean isEligible = InputFormatChecker.isPhoneNumEligible(mPhoneNum);
		if (!isEligible) {
			Toaster.showShort(PhoneVerificationActivity.this,
					R.string.phone_num_has_wrong_format);
		}
		return isEligible;
	}

	private void performGetSmsCaptcha() {
		Toaster.showShort(PhoneVerificationActivity.this,
				R.string.sms_captcha_is_sending);

		HttpRequest.getVerifyCode(this, mPhoneNum, "0", this);
	}

	/**
	 * 改变“获取短信验证码”的状态.
	 */
	private void scheduleCountBackwords() {
		mGetCaptchaCB.setClickable(false);
		mGetCaptchaCB.setChecked(true);
		// markCountBackwordsTime();
		startCountBackwords();
	}

	/**
	 * 开始倒计时，60秒后可重新获取验证码.
	 */
	private void startCountBackwords() {
		final Handler handler = new ChangeStateHandler(this);
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				msg.arg1 = mCountBackwords--;
				handler.sendMessage(msg);
			}
		}, 0, 1000);
	}

	private static class ChangeStateHandler extends Handler {

		private final WeakReference<PhoneVerificationActivity> mActivity;

		ChangeStateHandler(PhoneVerificationActivity activity) {
			mActivity = new WeakReference<PhoneVerificationActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			PhoneVerificationActivity activity = mActivity.get();
			if (activity != null) {
				// 显示倒计时
				int countBackwords = msg.arg1;
				CheckBox getCaptchaCB = (CheckBox) activity
						.findViewById(R.id.get_sms_captcha_cb);
				getCaptchaCB.setText(countBackwords + "后再获取");
				// 为0秒时，恢复可获取验证码状态
				if (countBackwords == 0) {
					String getCaptcha = activity
							.getString(R.string.get_sms_captcha);
					getCaptchaCB.setClickable(true);
					getCaptchaCB.setChecked(false);
					getCaptchaCB.setText(getCaptcha);
					activity.mCountBackwords = 60;
					cancelTheTimer(activity);
				}
			}
		}

		private void cancelTheTimer(PhoneVerificationActivity activity) {
			if (activity.mTimer != null) {
				activity.mTimer.cancel();
				activity.mTimer = null;
			}
		}
	}

	/**
	 * 
	 * @todo 发出请求，注册
	 * @time 2014年5月5日 上午9:50:55
	 * @author jie.liu
	 */
	private void requestAdivisor() {
		String captcha = StringUtils.getText(mCaptchaET);
		if (TextUtils.isEmpty(captcha)) {
			Toaster.showShort(this, "短信验证码不能为空");
			return;
		}

		// if (!TextUtils.isEmpty(mSmsCode)) {
		// if (mSmsCode.equals(captcha)) {
		// Toaster.showShort(this, "短信验证码不正确");
		// return;
		// }
		// }

		String newPassword = StringUtils.getText(mNewPasswordET);
		if (TextUtils.isEmpty(newPassword)) {
			Toaster.showShort(this, "密码不能为空");
			return;
		}

		if (!InputFormatChecker.isPasswordEligible(newPassword)) {
			Toaster.showShort(this, "密码必须是6-16位");
			return;
		}

		String dupNewPassword = StringUtils.getText(mDupPasswordET);
		if (TextUtils.isEmpty(dupNewPassword)) {
			Toaster.showShort(this, "重复密码不能为空");
			return;
		}

		if (!InputFormatChecker.isPasswordEligible(dupNewPassword)) {
			Toaster.showShort(this, "重复密码必须是6-16位");
			return;
		}

		if (!dupNewPassword.equals(newPassword)) {
			Toaster.showShort(this, "重复密码与新密码不一致");
			return;
		}

		HttpRequest.register(this, mPhoneNum, newPassword, captcha, this);
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		super.onHttpSuccess(path, result);
		if (TextUtils.isEmpty(result)) {
			Toaster.showShort(this, "服务器出错了，请重试");
			LogUtil.e(this, "网络返回的数据为空");
			return;
		}
		dealWithHttpReturned(path, result);
	}

	private void dealWithHttpReturned(String path, String result) {
		Gson gson = new Gson();
		if (HttpUrl.VERIFICATION_CODE.equals(path)) {
			VerifyCodeResult r = gson.fromJson(result, VerifyCodeResult.class);
			Result rs = r.getResult();
			if (HttpResult.SUCCESS.equals(rs.getErrorcode())) {
			} else if (HttpResult.FAIL.equals(rs.getErrorcode())) {
				Toaster.showShort(PhoneVerificationActivity.this,
						rs.getErrormsg());
			} else {
				Toaster.showShort(PhoneVerificationActivity.this, "验证码获取失败，请重试");
			}
		} else { // 注册返回
			LoginResult r = gson.fromJson(result, LoginResult.class);
			Result rs = r.getResult();
			if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
				ZCApplication.getInstance().storeUserInfo(r.getUserInfo());
				ZCApplication.getInstance().storeState(State.LOGINED);
				Toaster.showShort(PhoneVerificationActivity.this,
						"注册成功,正在登录中... ");
				new PreferenceUtil(this, Constants.PREFERENCE_FILE)
						.setUsername(mPhoneNum);
				finish();
				sendBroadcast();
			} else if ("-3".equals(rs.getErrorcode())
					|| "-5".equals(rs.getErrorcode())) {
				Toaster.showShort(PhoneVerificationActivity.this, r.getResult()
						.getErrormsg());
			} else {
				Toaster.showShort(PhoneVerificationActivity.this, "申请失败，请重试");
			}

		}
	}

	private void sendBroadcast() {
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_GUIDE_TO_COMMISSION_SWITCHER);
		sendBroadcast(intent);

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName()); // 统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName()); // 保证 onPageEnd
																// 在onPause
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		super.onDestroy();
	}
}

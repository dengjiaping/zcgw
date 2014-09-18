package com.hct.zc.activity.reglogin;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

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
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.http.result.VerifyCodeResult;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * @todo 忘记密码.
 * @time 2014年5月4日 下午2:10:20
 * @author jie.liu
 */
public class ForgetPasswordActivity extends BaseHttpActivity {
	/**
	 * 获取短信验证码时间间隔60秒
	 */
	private final int TIME_INTERVAL = 60;

	private EditText mPhoneNumET;
	private CheckBox mGetCaptchaCB;
	private EditText mInputSmsCaptchaET;
	private EditText mNewPasswordET;
	private EditText mDupNewPasswordET;
	private Button mCertainBtn;
	private int mCountBackwords = TIME_INTERVAL;
	private Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_password_activity);
		initViews();
	}

	private void initViews() {
		new TitleBar(this).initTitleBar(R.string.forget_password);
		mPhoneNumET = (EditText) findViewById(R.id.phone_number_et);
		mGetCaptchaCB = (CheckBox) findViewById(R.id.get_sms_captcha_cb);
		mInputSmsCaptchaET = (EditText) findViewById(R.id.input_sms_captcha_et);
		mNewPasswordET = (EditText) findViewById(R.id.input_new_password_et);
		mDupNewPasswordET = (EditText) findViewById(R.id.input_new_password_again_et);
		mCertainBtn = (Button) findViewById(R.id.certain_btn);

		ClickListener listener = new ClickListener();
		mCertainBtn.setOnClickListener(listener);
		mGetCaptchaCB.setOnClickListener(listener);

	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.get_sms_captcha_cb:
				getSmsCaptcha();
				break;
			case R.id.certain_btn:
				resetPassword();
				break;
			default:
				LogUtil.w(ForgetPasswordActivity.this, "点击了未知的按钮");
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
		String phoneNum = StringUtils.getText(mPhoneNumET);
		if (TextUtils.isEmpty(phoneNum)) {
			Toaster.showShort(ForgetPasswordActivity.this,
					R.string.phone_num_can_not_be_null);
			return false;
		}

		boolean isEligible = InputFormatChecker.isPhoneNumEligible(phoneNum);
		if (!isEligible) {
			mPhoneNumET.setText("");
			Toaster.showShort(ForgetPasswordActivity.this,
					R.string.phone_num_has_wrong_format);
		}
		return isEligible;
	}

	private void performGetSmsCaptcha() {
		Toaster.showShort(ForgetPasswordActivity.this,
				R.string.sms_captcha_is_sending);

		HttpRequest.getVerifyCode(this, StringUtils.getText(mPhoneNumET), "1",
				this);
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

		private final WeakReference<ForgetPasswordActivity> mActivity;

		ChangeStateHandler(ForgetPasswordActivity activity) {
			mActivity = new WeakReference<ForgetPasswordActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ForgetPasswordActivity activity = mActivity.get();
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

		private void cancelTheTimer(ForgetPasswordActivity activity) {
			if (activity.mTimer != null) {
				activity.mTimer.cancel();
				activity.mTimer = null;
			}
		}
	}

	private void resetPassword() {
		String smsCaptcha = StringUtils.getText(mInputSmsCaptchaET);
		if (TextUtils.isEmpty(smsCaptcha)) {
			Toaster.showShort(ForgetPasswordActivity.this,
					R.string.sms_captcha_can_not_be_null);
			return;
		}

		if (passwordFormatRight()) {
			HttpRequest.doResetPwd(this, StringUtils.getText(mPhoneNumET),
					smsCaptcha, StringUtils.getText(mNewPasswordET), this);
		}
	}

	private boolean passwordFormatRight() {
		String password = StringUtils.getText(mNewPasswordET);
		String dupPassword = StringUtils.getText(mDupNewPasswordET);
		// 新密码为空
		if (TextUtils.isEmpty(password)) {
			Toaster.showShort(ForgetPasswordActivity.this,
					R.string.new_password_can_not_be_null);
			clearInput();
			return false;
		}
		// 新密码格式不正确
		if (!InputFormatChecker.isPasswordEligible(password)) {
			Toaster.showShort(ForgetPasswordActivity.this,
					R.string.new_password_has_wrong_format);
			clearInput();
			return false;
		}
		// 重复密码为空
		if (TextUtils.isEmpty(dupPassword)) {
			Toaster.showShort(ForgetPasswordActivity.this,
					R.string.dup_password_can_not_be_null);
			clearInput();
			return false;
		}
		// 重复密码格式不正确
		if (!InputFormatChecker.isPasswordEligible(dupPassword)) {
			Toaster.showShort(ForgetPasswordActivity.this,
					R.string.dup_password_has_wrong_format);
			clearInput();
			return false;
		}
		// 重复密码与新密码一致
		if (password.equals(dupPassword)) {
			return true;
		} else {
			// 重复密码与新密码不一致
			Toaster.showShort(ForgetPasswordActivity.this,
					R.string.dup_password_is_diff_from_new_password);
			clearInput();
			return false;
		}
	}

	private void clearInput() {
		mNewPasswordET.setText("");
		mDupNewPasswordET.setText("");
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
				Toaster.showShort(this, rs.getErrormsg());
			} else {
				Toaster.showShort(this, "验证码获取失败，请重试");
			}
		} else { // 重置密码返回
			HttpResult r = gson.fromJson(result, HttpResult.class);
			Result rs = r.getResult();
			if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
				Toaster.showShort(this, "密码修改成功 ");
				finish();
			} else if ("-1".equals(rs.getErrorcode())
					|| "-3".equals(rs.getErrorcode())
					|| "-5".equals(rs.getErrorcode())
					|| "-6".equals(rs.getErrorcode())
					|| "-7".equals(rs.getErrorcode())
					|| "-8".equals(rs.getErrorcode())) {
				Toaster.showShort(this, r.getResult().getErrormsg());
			} else {
				Toaster.showShort(this, "修改密码失败，请重试");
			}
		}
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
package com.hct.zc.widget;

import java.util.Timer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.hct.zc.R;
import com.hct.zc.utils.PreferenceUtil;

/**
 * @todo 倒计时控件.
 * @time 2014年5月4日 下午4:37:30
 * @author jie.liu
 */

public class CountBackwordsCB extends LinearLayout {

	/**
	 * 获取短信验证码时间间隔60秒
	 */
	private final int TIME_INTERVAL = 60;

	private Context mContext;
	private CheckBox mCountBackwordsCB;
	private PreferenceUtil mUtil;
	private final int mCountBackwords = TIME_INTERVAL;
	private Timer mTimer;
	private OnCountBackwordsListener mOnCountBackwordsListener;

	public CountBackwordsCB(Context context) {
		super(context);
		init(context);
	}

	public CountBackwordsCB(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.count_backwords_cb, this);
		mCountBackwordsCB = (CheckBox) view
				.findViewById(R.id.count_backwords_cb);
		mCountBackwordsCB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSmsCaptcha();
			}
		});
	}

	private void getSmsCaptcha() {
		if (canEnableView()) {
			if (mOnCountBackwordsListener != null) {
				mOnCountBackwordsListener.onClicked();
			}
			scheduleCountBackwords();
		} else {
			mCountBackwordsCB.setChecked(false);
		}
	}

	private boolean canEnableView() {
		if (mOnCountBackwordsListener != null) {
			return mOnCountBackwordsListener.onCanEnableView();
		}

		return false;
	}

	/**
	 * 改变“获取短信验证码”的状态.
	 */
	private void scheduleCountBackwords() {
		mCountBackwordsCB.setClickable(false);
		mCountBackwordsCB.setChecked(true);
		// markCountBackwordsTime();
		// startCountBackwords();
	}

	// private void markCountBackwordsTime() {
	// long now = new Date().getTime();
	// if (mUtil == null) {
	// mUtil = new PreferenceUtil(mContext, Constants.PREFERENCE_FILE);
	// }
	// mUtil.setCountBackwordsBeginTime(now);
	// }

	// /**
	// * 开始倒计时，60秒后可重新获取验证码.
	// */
	// private void startCountBackwords() {
	// // final Handler handler = new ChangeStateHandler(this);
	// mTimer = new Timer();
	// mTimer.schedule(new TimerTask() {
	//
	// @Override
	// public void run() {
	// Message msg = Message.obtain();
	// msg.arg1 = mCountBackwords--;
	// handler.sendMessage(msg);
	// }
	// }, 0, 1000);
	// }
	//
	// private static class ChangeStateHandler extends Handler {
	//
	// private final WeakReference<ForgetPasswordActivity> mActivity;
	//
	// ChangeStateHandler(ForgetPasswordActivity activity) {
	// mActivity = new WeakReference<ForgetPasswordActivity>(activity);
	// }
	//
	// @Override
	// public void handleMessage(Message msg) {
	// ForgetPasswordActivity activity = mActivity.get();
	// if (activity != null) {
	// // 显示倒计时
	// int countBackwords = msg.arg1;
	// CheckBox getCaptchaCB = (CheckBox) activity
	// .findViewById(R.id.get_sms_captcha_cb);
	// getCaptchaCB.setText(countBackwords + "后再获取");
	// // 为0秒时，恢复可获取验证码状态
	// if (countBackwords == 0) {
	// String getCaptcha = activity
	// .getString(R.string.get_sms_captcha);
	// getCaptchaCB.setClickable(true);
	// getCaptchaCB.setChecked(false);
	// getCaptchaCB.setText(getCaptcha);
	// activity.mCountBackwords = 60;
	// cancelTheTimer(activity);
	// }
	// }
	// }
	//
	// private void cancelTheTimer(ForgetPasswordActivity activity) {
	// if (activity.mTimer != null) {
	// activity.mTimer.cancel();
	// activity.mTimer = null;
	// }
	// }
	// }

	public interface OnCountBackwordsListener {
		/**
		 * 
		 * @todo 是否可以点击该控件
		 * @time 2014年5月4日 下午4:50:09
		 * @author jie.liu
		 * @return
		 */
		boolean onCanEnableView();

		/**
		 * 
		 * @todo 被点击的响应.
		 * @time 2014年5月4日 下午4:56:37
		 * @author jie.liu
		 */
		void onClicked();
	}

}

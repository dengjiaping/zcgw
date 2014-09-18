package com.hct.zc.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.utils.LogUtil;

/**
 * 倒计时控件
 * 
 * @time 2014年7月1日 下午1:52:02
 * @author liuzenglong163@gmail.com
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class TimeBackwordsView extends LinearLayout {

	private Context mContext;

	private final Handler mHandler = new InternalHanlder();

	private OnBackwordsListener mOnBackwordsListener;

	private Timer mTimer;

	private long mTotalSeconds = 0l;

	private int mHour;
	private int mMinute;
	private int mSecond;

	/**
	 * true时文字是红色的，false时文字是灰色的
	 */
	private boolean mEnable = false;

	private TextView mHourTV;
	private TextView mMinuteTV;
	private TextView mSecondTV;

	private final int TIME_COLOR_JIUHONE;
	private final int TIME_COLOR_GRAY;

	private static final int MESSAGE_REFRESH_VIEW = 0x01;
	private static final int MESSAGE_BACKWORDS_OVER = 0X02;

	public TimeBackwordsView(Context context) {
		super(context);
		TIME_COLOR_JIUHONE = context.getResources().getColor(R.color.jiuhong);
		TIME_COLOR_GRAY = context.getResources().getColor(R.color.gray_dark);
		init(context);
	}

	public TimeBackwordsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TIME_COLOR_JIUHONE = context.getResources().getColor(R.color.jiuhong);
		TIME_COLOR_GRAY = context.getResources().getColor(R.color.gray_dark);
		init(context);
	}

	public TimeBackwordsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TIME_COLOR_JIUHONE = context.getResources().getColor(R.color.jiuhong);
		TIME_COLOR_GRAY = context.getResources().getColor(R.color.gray_dark);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.time_backwords_view, this);
		initViews(view);
	}

	private void initViews(View rootView) {
		mHourTV = (TextView) rootView.findViewById(R.id.hour_tv);
		mMinuteTV = (TextView) rootView.findViewById(R.id.minute_tv);
		mSecondTV = (TextView) rootView.findViewById(R.id.second_tv);
		setTime(0, 0, 0);
		refreshView();
	}

	public void setTime(long totalSeconds) {
		mTotalSeconds = totalSeconds;
		clacHourMinuteSecond();
		refreshView();
	}

	/**
	 * 
	 * 设置倒计时的启示时间.
	 * 
	 * @time 2014年7月1日 下午2:34:57
	 * @author liuzenglong163@gmail.com
	 * @param hour
	 * @param munite
	 * @param second
	 */
	public void setTime(int hour, int munite, int second) {
		boolean isTimeEligible = isTimeEligible(hour, munite, second);
		if (isTimeEligible) {
			mHour = hour;
			mMinute = munite;
			mSecond = second;

			calcTotalSeconds();
			refreshView();
		}
	}

	private boolean isTimeEligible(int hour, int munite, int second) {
		if (!isHourEligible(hour)) {
			return false;
		}

		if (!isMuniteEligible(munite)) {
			return false;
		}

		if (!isSecondEligible(second)) {
			return false;
		}

		return true;
	}

	private void calcTotalSeconds() {
		long secondFromHour = 0l;
		long secondFromMunite = 0l;
		long second = 0L;
		if (isHourEligible(mHour)) {
			secondFromHour = mHour * 60 * 60;
		}

		if (isMuniteEligible(mMinute)) {
			secondFromMunite = mMinute * 60;
		}

		if (isSecondEligible(mSecond)) {
			second = mSecond;
		}

		mTotalSeconds = secondFromHour + secondFromMunite + second;
	}

	private boolean isHourEligible(int hour) {
		if (hour < 0 || hour > 23) {
			return false;
		} else {
			return true;
		}
	}

	private boolean isMuniteEligible(int munite) {
		return isSecondEligible(munite);
	}

	private boolean isSecondEligible(int second) {
		if (second < 0 || second > 59) {
			return false;
		} else {
			return true;
		}
	}

	public void setTimeEnable(boolean enable) {
		mEnable = enable;
		refreshView();
	}

	/**
	 * 
	 * 改方法会在主线程中刷新UI
	 * 
	 * @time 2014年7月2日 上午10:03:27
	 * @author liuzenglong163@gmail.com
	 */
	public void refreshView() {
		if (mEnable) {
			setTimeColor(TIME_COLOR_JIUHONE);
		} else {
			setTimeColor(TIME_COLOR_GRAY);
		}

		String hourStr = optimizeTime(mHour);
		String muniteStr = optimizeTime(mMinute);
		String secondStr = optimizeTime(mSecond);

		mHourTV.setText(hourStr);
		mMinuteTV.setText(muniteStr);
		mSecondTV.setText(secondStr);
	}

	public void setTimeColor(int color) {
		mHourTV.setTextColor(color);
		mMinuteTV.setTextColor(color);
		mSecondTV.setTextColor(color);
	}

	public void startBackwords() {
		if (mTimer == null) {
			mTimer = new Timer();
		}

		mTimer.schedule(new MyTimerTask(), 0, 1000);
	}

	public void stopBackwords() {
		if (mTimer != null) {
			mTimer.cancel();
		}

		mHour = 0;
		mMinute = 0;
		mSecond = 0;
		refreshView();
	}

	private class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			if (mTotalSeconds > 0) {
				mTotalSeconds--;
				clacHourMinuteSecond();
				// 主线程刷新UI
				mHandler.sendEmptyMessage(MESSAGE_REFRESH_VIEW);
			} else {
				// 主线程调用回调函数
				mHandler.sendEmptyMessage(MESSAGE_BACKWORDS_OVER);
			}
		}
	}

	/**
	 * 
	 * 根据总的秒数计算出小时，分钟，秒
	 * 
	 * @time 2014年7月1日 下午3:06:53
	 * @author liuzenglong163@gmail.com
	 */
	private void clacHourMinuteSecond() {
		mSecond = (int) (mTotalSeconds % 60);
		mMinute = (int) ((mTotalSeconds / 60) % 60);
		mHour = (int) (mTotalSeconds / (60 * 60));
		LogUtil.d(this, mHour + "时" + mMinute + "分" + mSecond + "秒");
	}

	private String optimizeTime(int time) {
		String timeStr = String.valueOf(time);
		if (timeStr.length() > 1) {
			return timeStr;
		}

		return "0" + timeStr;
	}

	public void setOnBackwordsListener(OnBackwordsListener onBackwordsListener) {
		mOnBackwordsListener = onBackwordsListener;
	}

	public interface OnBackwordsListener {

		/**
		 * 
		 * 倒计时结束
		 * 
		 * @time 2014年7月1日 下午4:02:28
		 * @author liuzenglong163@gmail.com
		 */
		void onBackwordsOver();
	}

	private class InternalHanlder extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_REFRESH_VIEW:
				refreshView();
				break;
			case MESSAGE_BACKWORDS_OVER:
				setTimeEnable(false);
				if (mOnBackwordsListener != null) {
					mOnBackwordsListener.onBackwordsOver();
				}
				break;
			default:
				LogUtil.w(this, "TimeBackwordsView中收到了未知的消息");
			}
		}
	}
}

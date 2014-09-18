package com.hct.zc.activity.gesturelock;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.activity.base.BaseActivity;
import com.hct.zc.constants.Constants;
import com.hct.zc.utils.LockPatternUtils;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.LockPatternView;
import com.hct.zc.widget.LockPatternView.Cell;
import com.hct.zc.widget.LockPatternView.DisplayMode;
import com.hct.zc.widget.LockPatternView.OnPatternListener;
import com.hct.zc.widget.TitleBar;

/**
 * @todo 手势密码设置.
 * @time 2014年5月4日 下午2:10:43
 * @author jie.liu
 */
public class GestureLockSettingActivity extends BaseActivity {

	private GridView mPromptGV;
	private PromptAdapter mAdapter;
	private final boolean mMarks[] = { false, false, false, false, false,
			false, false, false, false };
	private TextView mPromptTV;

	private LockPatternView mLockPatternView;
	private LockPatternUtils mLockUtil;
	private Handler mHandler;
	private boolean mIsFirstTimeSetUp = true;
	private boolean mIsGestureRight = true;
	private boolean mIsGestureSetSuccee = false;
	private List<Cell> mPatternChoosen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesture_lock_setting_activity);
		mHandler = new DismissHandler(
				new WeakReference<GestureLockSettingActivity>(this));
		mLockUtil = new LockPatternUtils(GestureLockSettingActivity.this);
		initViews();
	}

	private void initViews() {
		initTitlebar();
		mPromptGV = (GridView) findViewById(R.id.prompt_gv);
		mAdapter = new PromptAdapter();
		mPromptGV.setAdapter(mAdapter);
		mPromptTV = (TextView) findViewById(R.id.prompt_tv);
		mLockPatternView = (LockPatternView) findViewById(R.id.lock_pattern_view);
		mLockPatternView.setOnPatternListener(new PatternListener());
	}

	private void initTitlebar() {
		new TitleBar(GestureLockSettingActivity.this).initTitleBar("设置手势密码");
	}

	private class PromptAdapter extends BaseAdapter {

		private final LayoutInflater inflater;

		private PromptAdapter() {
			inflater = LayoutInflater.from(GestureLockSettingActivity.this);
		}

		@Override
		public int getCount() {
			return mMarks.length;
		}

		@Override
		public Object getItem(int position) {
			return mMarks[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.gvitem_mark, null);
			ImageView markIV = (ImageView) view.findViewById(R.id.mark_iv);
			if (mMarks[position] == true) {
				markIV.setImageResource(R.drawable.bg_gesture_prompt_mark);
			} else {
				markIV.setImageResource(R.drawable.bg_gesture_prompt_normal);
			}
			return view;
		}
	}

	private class PatternListener implements OnPatternListener {

		@Override
		public void onPatternStart() {
			LogUtil.d(GestureLockSettingActivity.this, "onPatternStart");
		}

		@Override
		public void onPatternCleared() {
			LogUtil.d(GestureLockSettingActivity.this, "onPatternCleared");
		}

		@Override
		public void onPatternCellAdded(List<Cell> pattern) {
			LogUtil.d(GestureLockSettingActivity.this, "onPatternCellAdded");
		}

		@Override
		public void onPatternDetected(List<Cell> pattern) {
			LogUtil.d(GestureLockSettingActivity.this, "onPatternDetected");
			// 输入的手势不符合要求
			if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
				Toaster.showShort(GestureLockSettingActivity.this,
						R.string.lockpattern_recording_incorrect_too_short);
				mLockPatternView.setDisplayMode(DisplayMode.Wrong);
				mLockPatternView.disableInput();
				mHandler.sendEmptyMessageDelayed(0, 1000);
				mIsGestureRight = false;
				return;
			}

			// 输入的手势符合要求
			mIsGestureRight = true;
			if (mIsFirstTimeSetUp) {
				// 第一次输入手势完毕,记录手势，更新提示
				clearPatternChoosen();
				mPatternChoosen.addAll(pattern);
				refreshPromptGV(pattern);
				mPromptTV.setTextColor(Color.GRAY);
				mPromptTV.setText("请再输入一次");
			} else {
				// 第二次输入手势完毕
				if (mPatternChoosen.equals(pattern)) {
					// 与第一次输入的相同，手势设置成功，保存手势
					mLockUtil.saveLockPattern(mPatternChoosen);
					mIsGestureSetSuccee = true;
					turnOnComSwitcher();
					mPromptTV.setTextColor(Color.GRAY);
					mPromptTV.setText("设置成功");
				} else {
					// 与第一次输入的不同，手势设置失败
					clearPatternChoosen();
					mLockPatternView.setDisplayMode(DisplayMode.Wrong);
					mIsGestureSetSuccee = false;
					mPromptTV.setTextColor(Color.RED);
					mPromptTV.setText("两次密码不符，请重新输入");
				}
			}

			mLockPatternView.disableInput();
			mHandler.sendEmptyMessageDelayed(0, 1000);
		}
	}

	private void turnOnComSwitcher() {
		new PreferenceUtil(GestureLockSettingActivity.this,
				Constants.PREFERENCE_FILE).setShouldShowCommission(true);
	}

	private void reset() {
		clearPatternChoosen();
		mIsFirstTimeSetUp = true;
		mIsGestureRight = false;
		mIsGestureSetSuccee = false;
		for (int i = 0; i < 9; i++) {
			mMarks[i] = false;
		}
		mAdapter.notifyDataSetChanged();
	}

	private void clearPatternChoosen() {
		if (mPatternChoosen == null) {
			mPatternChoosen = new ArrayList<LockPatternView.Cell>();
		} else {
			mPatternChoosen.clear();
		}
	}

	private void refreshPromptGV(List<Cell> pattern) {
		for (Cell cell : pattern) {
			int row = cell.getRow();
			int column = cell.getColumn();
			int index = row * 3 + column;
			mMarks[index] = true;
		}
		mAdapter.notifyDataSetChanged();
	}

	private static class DismissHandler extends Handler {

		private final WeakReference<GestureLockSettingActivity> mContext;

		public DismissHandler(WeakReference<GestureLockSettingActivity> context) {
			mContext = context;
		}

		@Override
		public void handleMessage(Message msg) {
			GestureLockSettingActivity activity = mContext.get();
			if (activity == null) {
				return;
			}

			activity.mLockPatternView.clearPattern();
			activity.mLockPatternView.enableInput();
			// 输入额手势符合要求
			if (activity.mIsGestureRight == true) {
				// 手势符合要求的情况
				if (activity.mIsFirstTimeSetUp) {
					activity.mIsFirstTimeSetUp = false;
				} else {
					// 第二次输入手势，设置成功则关闭Activity，设置失败则重置
					if (activity.mIsGestureSetSuccee) {
						activity.finish();
					} else {
						activity.reset();
					}
				}
			}
		}
	}
}

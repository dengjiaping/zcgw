package com.hct.zc.activity.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.constants.Constants;
import com.hct.zc.fragment.CenterFragment.State;
import com.hct.zc.service.VersionUpdater;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 更多.
 * @time 2014年5月4日 下午2:13:38
 * @author jie.liu
 */
public class MoreActivity extends BaseHttpActivity {

	private TextView mSystemMsgCountTV;
	private TextView mVersionMarkTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_activity);
		initViews();
		initData();
	}

	private void initViews() {
		initTitlebar();
		LinearLayout systemMsgLlyt = (LinearLayout) findViewById(R.id.system_msg_llyt);
		LinearLayout feedbackLlyt = (LinearLayout) findViewById(R.id.feedback_llyt);
		LinearLayout userGuideLlyt = (LinearLayout) findViewById(R.id.user_guide_llyt);
		LinearLayout aboutLlyt = (LinearLayout) findViewById(R.id.about_llyt);
		LinearLayout scoreLlyt = (LinearLayout) findViewById(R.id.score_llyt);
		LinearLayout versionLlyt = (LinearLayout) findViewById(R.id.version_llyt);
		Button logoutBtn = (Button) findViewById(R.id.logout_btn);
		mSystemMsgCountTV = (TextView) findViewById(R.id.system_msg_count_tv);
		mVersionMarkTV = (TextView) findViewById(R.id.new_version_mark_tv);

		ClickListener listener = new ClickListener();
		systemMsgLlyt.setOnClickListener(listener);
		feedbackLlyt.setOnClickListener(listener);
		userGuideLlyt.setOnClickListener(listener);
		aboutLlyt.setOnClickListener(listener);
		scoreLlyt.setOnClickListener(listener);
		versionLlyt.setOnClickListener(listener);
		logoutBtn.setOnClickListener(listener);

		// 获取更新数据
		if (null != ZCApplication.msgList && ZCApplication.msgList.size() > 0)
			mSystemMsgCountTV.setText(String.valueOf(ZCApplication.msgList
					.size()));

		// 获取更新版本
		if (null != ZCApplication.apkBean) {
			mVersionMarkTV.setText(String
					.valueOf(ZCApplication.apkBean.highestCode));
			mVersionMarkTV.setVisibility(View.VISIBLE);
		} else {
			mVersionMarkTV.setVisibility(View.GONE);
		}

		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			logoutBtn.setVisibility(View.GONE);
		}
	}

	private void initTitlebar() {
		new TitleBar(MoreActivity.this).initTitleBar("更多");
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.system_msg_llyt:
				ContextUtil.pushToActivity(MoreActivity.this,
						SystemMsgActivity.class);
				break;
			case R.id.feedback_llyt:
				ContextUtil.pushToActivity(MoreActivity.this,
						FeedbackActivity.class);
				break;
			case R.id.user_guide_llyt:
				ContextUtil.pushToActivity(MoreActivity.this,
						UserGuideActivity.class);
				break;
			case R.id.about_llyt:
				ContextUtil.pushToActivity(MoreActivity.this,
						AboutActivity.class);
				break;
			case R.id.score_llyt:
				score();
				break;
			case R.id.version_llyt:
				new VersionUpdater(MoreActivity.this).checkVersion();
				break;
			case R.id.logout_btn:
				logout();
				break;
			default:
				LogUtil.w(MoreActivity.this, "点击了未知的按钮");
			}
		}
	}

	private void score() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void logout() {
		ZCApplication.getInstance().storeUserInfo(null);
		ZCApplication.getInstance().storeState(State.NOT_LOGINED);
		new PreferenceUtil(this, Constants.PREFERENCE_FILE).setPassword("");
		finish();
	}

	private void initData() {
		// TODO 获取系统消息数，版本更新.
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

}

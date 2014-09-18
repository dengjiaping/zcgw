package com.hct.zc.activity.more;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.PhraseCountEditText;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * @todo 用户反馈.
 * @time 2014年5月4日 下午2:09:53
 * @author jie.liu
 */
public class FeedbackActivity extends BaseHttpActivity {

	private PhraseCountEditText mFeedbackET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_activity);
		initViews();
	}

	private void initViews() {
		initTitlebar();
		mFeedbackET = (PhraseCountEditText) findViewById(R.id.feedback_et);
	}

	private void initTitlebar() {
		new TitleBar(FeedbackActivity.this).initTitleBar("用户反馈");
	}

	public void feedback(View view) {
		String feedbackContent = mFeedbackET.getText();
		if (TextUtils.isEmpty(feedbackContent)) {
			Toaster.showShort(FeedbackActivity.this, "内容不能为空");
			return;
		}

		performFeedback(feedbackContent);
	}

	private void performFeedback(String feedbackContent) {
		HttpRequest.doFeedback(this, feedbackContent, this);
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
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			Toaster.showShort(FeedbackActivity.this, "提交成功，感谢你的反馈");
			FeedbackActivity.this.finish();
		} else {
			Toaster.showShort(FeedbackActivity.this, "提交失败，请重试");
		}
	}
}

package com.hct.zc.activity.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.CstEditText;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * @todo 设置邮箱
 * @time 2014-5-10 下午3:35:35
 * @author liuzenglong163@gmail.com
 */

public class SetEmailActivity extends BaseHttpActivity {

	private CstEditText etMail;

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
		MobclickAgent.onPause(this);
	}

	/**
	 * @todo TODO
	 * @time 2014-5-10 下午3:35:35
	 * @author liuzenglong163@gmail.com
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_email_layout);
		initView();

	}

	private void initView() {
		new TitleBar(mActivity).initTitleBar("设置邮箱");
		etMail = (CstEditText) findViewById(R.id.tv_email);
		etMail.setInitName(R.string.setting_title_email,
				R.string.setting_title_email_input);
		etMail.setText(mUserInfo.getEmail());
	}

	public void submitEmail(View view) {
		String email = etMail.getText();
		if (TextUtils.isEmpty(email)) {
			Toaster.showShort(mActivity, "email不能为空");
			return;
		}

		if (!InputFormatChecker.isEmailEligible(email)) {
			Toaster.showShort(mActivity, "email格式不正确");
			return;
		}

		if (mUserInfo == null) {
			Toaster.showShort(mActivity, "请先登录");
			return;
		}
		requestModifyEmail();
	}

	/**
	 * 
	 * @todo 请求修改邮箱
	 * @time 2014-5-12 下午5:10:50
	 * @author liuzenglong163@gmail.com
	 */
	private void requestModifyEmail() {
		HttpRequest.doModifyEmail(mActivity, String.valueOf(mUserInfo
				.getUserId()), etMail.getText().toString(), this);
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
		String returnCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(returnCode)) {
			Toaster.showShort(mActivity, "更新成功");
			updateCache();
		} else if (HttpResult.ARG_ERROR.equals(returnCode)
				|| HttpResult.SYS_ERROR.equals(returnCode)) {
			Toaster.showShort(mActivity, "更新失败，请重试");
		} else {
			Toaster.showShort(mActivity, r.getResult().getErrormsg());
		}
	}

	private void updateCache() {
		mUserInfo.setEmail(etMail.getText().toString());
		finish();
	}
}

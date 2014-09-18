package com.hct.zc.activity.setting;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.CstEditText;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 修改密码
 * @time 2014-5-14 下午4:50:27
 * @author liuzenglong163@gmail.com
 */
public class SetPwdActivity extends BaseHttpActivity {

	private CstEditText mOriginalET, mNewET, mDupET;
	private UserInfo mUserInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_setting_activity);
		mUserInfo = ZCApplication.getInstance().getUserInfo();
		initViews();
	}

	private void initViews() {
		initTitleBar();
		mOriginalET = (CstEditText) findViewById(R.id.origina_password_et);
		mOriginalET.setInitName(R.string.setting_title_oldpwd,
				R.string.setting_title_oldpwd_input);
		mOriginalET.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		mNewET = (CstEditText) findViewById(R.id.new_password_et);
		mNewET.setInitName(R.string.setting_title_pwd,
				R.string.setting_title_pwd_input);
		mNewET.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		mDupET = (CstEditText) findViewById(R.id.dup_new_password_et);
		mDupET.setInitName(R.string.setting_title_pwdagain,
				R.string.setting_title_pwdagain_input);
		mDupET.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		Button certainBtn = (Button) findViewById(R.id.certain_btn);
		certainBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updatePassword();
			}
		});
	}

	private void initTitleBar() {
		new TitleBar(SetPwdActivity.this).initTitleBar("修改密码");
	}

	private void updatePassword() {
		String originalPassword = mOriginalET.getText();
		String newPassword = mNewET.getText();
		String dupPassword = mDupET.getText();
		if (TextUtils.isEmpty(originalPassword)) {
			Toaster.showShort(SetPwdActivity.this, "原密码不能为空");
			return;
		}
		if (!InputFormatChecker.isPasswordEligible(originalPassword)) {
			Toaster.showShort(SetPwdActivity.this, "原密码格式不正确");
			return;
		}

		if (TextUtils.isEmpty(newPassword)) {
			Toaster.showShort(SetPwdActivity.this, "新密码不能为空");
			return;
		}
		if (!InputFormatChecker.isPasswordEligible(newPassword)) {
			Toaster.showShort(SetPwdActivity.this, "新密码格式不正确");
			return;
		}

		if (TextUtils.isEmpty(dupPassword)) {
			Toaster.showShort(SetPwdActivity.this, "重复密码不能为空");
			return;
		}
		if (!InputFormatChecker.isPasswordEligible(dupPassword)) {
			Toaster.showShort(SetPwdActivity.this, "重复密码格式不正确");
			return;
		}
		if (!dupPassword.equals(newPassword)) {
			Toaster.showShort(SetPwdActivity.this, "重复密码与新密码不一致");
			return;
		}
		requestModifyPwd(newPassword, originalPassword);
	}

	/**
	 * 
	 * @todo 请求修改密码
	 * @time 2014-5-14 下午4:49:14
	 * @author liuzenglong163@gmail.com
	 * @param password
	 * @param oldPassword
	 */
	private void requestModifyPwd(String password, String oldPassword) {
		HttpRequest.doModifyPwd(mActivity,
				String.valueOf(mUserInfo.getUserId()),
				String.valueOf(mUserInfo.getPhone()), password, oldPassword,
				this);
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
		HttpResult r = gson.fromJson(result, HttpResult.class);
		String responseCode = r.getResult().getErrorcode();
		if (HttpUrl.UPDATE_PERSON_INFO.equals(path)) {
			if (HttpResult.SUCCESS.equals(responseCode)) {
				Toaster.showShort(SetPwdActivity.this, "更新成功");
				finish();
			} else if (HttpResult.ARG_ERROR.equals(responseCode)
					|| HttpResult.SYS_ERROR.equals(responseCode)) {
				Toaster.showShort(SetPwdActivity.this, " 更新失败，请重试");
			} else {
				Toaster.showShort(SetPwdActivity.this, r.getResult()
						.getErrormsg());
			}
			LoadingProgress.getInstance().dismiss();
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

}

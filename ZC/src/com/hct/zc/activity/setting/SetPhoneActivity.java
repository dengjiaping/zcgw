package com.hct.zc.activity.setting;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.bean.VerifyCode;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.http.result.VerifyCodeResult;
import com.hct.zc.utils.Const;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.CstEditText;
import com.hct.zc.widget.TitleBar;
import com.hct.zc.widget.ZCDialog;

/**
 * @todo 设置手机号
 * @time 2014-5-10 下午3:36:51
 * @author liuzenglong163@gmail.com
 */

public class SetPhoneActivity extends BaseHttpActivity implements
		OnClickListener {

	private CstEditText etPhone, etVerifyCode;
	private RelativeLayout ll_verifyLayout;
	private Button btnModify;
	private CheckBox btnGetCode;
	private boolean hasVerfiy = false; // 是否已经验证
	private int time = 60;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_phone_layout);
		initView();
	}

	private void initView() {
		new TitleBar(mActivity).initTitleBar("手机设置");

		etPhone = (CstEditText) findViewById(R.id.et_phone);
		etPhone.setInitName(R.string.setting_title_phone,
				R.string.setting_title_phone_input);
		etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
		btnModify = (Button) findViewById(R.id.btn_ok);
		btnModify.setText("修改");
		btnModify.setOnClickListener(this);

		ll_verifyLayout = (RelativeLayout) findViewById(R.id.ll_verify);
		etVerifyCode = (CstEditText) findViewById(R.id.et_verify_code);
		etVerifyCode.setInitName(R.string.setting_title_verify_code,
				R.string.setting_title_verify_code_input);

		btnGetCode = (CheckBox) findViewById(R.id.btn_get_code);
		btnGetCode.setOnClickListener(this);

		if (TextUtils.isEmpty(mUserInfo.getPhone())) {
			ll_verifyLayout.setVisibility(View.VISIBLE);
		} else {
			ll_verifyLayout.setVisibility(View.GONE);
			if (null != mUserInfo.getPhone()
					&& mUserInfo.getPhone().length() > 3) {
				etPhone.setText(mUserInfo.getPhone().replace(
						mUserInfo.getPhone().substring(3,
								mUserInfo.getPhone().length() - 4), "****"));
				etPhone.setEditable(false);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			if (!hasVerfiy) { // 如果没有验证则需要验证
				createTimeDialog();
			} else {
				requestModifyPhone();
			}
			break;
		case R.id.btn_get_code:
			requestVerifyCode();
			break;
		}
	}

	Handler timeHandler = new Handler();
	Runnable bannerStartRun = new Runnable() {
		@Override
		public void run() {
			timeHandler.postDelayed(this, 1000);
			if (time < 0) {
				btnGetCode.setEnabled(true);
				time = 60;
				timeHandler.removeCallbacks(bannerStartRun);
			}
			if (btnGetCode.isEnabled()) {
				btnGetCode.setText(getString(R.string.verify_wait_code_normal));
			} else {
				btnGetCode
						.setText(getString(R.string.verify_wait_code, time--));
			}
		}
	};

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
		if (HttpUrl.CHECK_PASSWORD.endsWith(path)) { // 验证密码
			Gson gson = new Gson();
			Result r = gson.fromJson(result, HttpResult.class).getResult();
			if (HttpResult.SUCCESS.equals(r.getErrorcode())) {
				btnModify.setText("修改");
				ll_verifyLayout.setVisibility(View.VISIBLE);
				hasVerfiy = true;
				etPhone.setText(mUserInfo.getPhone());
				etPhone.getEtContent().setSelection(etPhone.getText().length());
				etPhone.setEditable(true);
			} else if (HttpResult.FAIL.equals(r.getErrorcode())
					|| HttpResult.ARG_ERROR.equals(r.getErrorcode())) {
				Toaster.showShort(mActivity, r.getErrormsg());
			}
		} else if (HttpUrl.VERIFICATION_CODE.equals(path)) { // 获取验证码
			Gson gson = new Gson();
			VerifyCodeResult verfityResult = gson.fromJson(result,
					VerifyCodeResult.class);
			VerifyCode r = verfityResult.getResultcode();
			if (HttpResult.SUCCESS.equals(verfityResult.getResult()
					.getErrorcode())) {
				etVerifyCode.setText(r.getCode());
				btnGetCode.setEnabled(false);
				timeHandler.postDelayed(bannerStartRun, 0);

			} else if (HttpResult.FAIL.equals(verfityResult.getResult()
					.getErrorcode())
					|| HttpResult.ARG_ERROR.equals(verfityResult.getResult()
							.getErrorcode())) {
				Toaster.showShort(mActivity, verfityResult.getResult()
						.getErrormsg());
			}
		} else if (HttpUrl.UPDATE_PERSON_INFO.equals(path)) {
			Gson gson = new Gson();
			Result r = gson.fromJson(result, HttpResult.class).getResult();
			if (HttpResult.SUCCESS.equals(r.getErrorcode())) {
				Toaster.showShort(mActivity, "修改成功");
				savePhone();
				finish();
			} else// if (HttpResult.FAIL.equals(r.getErrorcode())||
					// HttpResult.ARG_ERROR.equals(r.getErrorcode()))
			{
				Toaster.showShort(mActivity, r.getErrormsg());
			}
		}
	}

	/**
	 * 
	 * @todo 时间选择器
	 * @author lzlong@zwmob.com
	 * @time 2014-3-26 下午2:09:30
	 */
	private void createTimeDialog() {

		int screenWidth = (int) (getWindowManager().getDefaultDisplay()
				.getWidth() * Const.DIALOG_LOGIN_WIDTH); // 屏幕宽
		int screenHeight = (int) (getWindowManager().getDefaultDisplay()
				.getHeight() * Const.DIALOG_LOGIN_HEIGHT); // 屏幕高

		final ZCDialog dialog = new ZCDialog(mActivity, R.style.user_dialog,
				screenWidth, screenHeight);

		final View verifyView = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_verify_code_layout, null);

		verifyView.requestFocus();

		final EditText etPwd = (EditText) verifyView.findViewById(R.id.et_pwd);

		final Button btnCancel = (Button) verifyView
				.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button btnOk = (Button) verifyView.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (TextUtils.isEmpty(etPwd.getText().toString())) {
					Toaster.showShort(mActivity, "请输入密码");
					return;
				} else {
					requestVerifyPwd(etPwd.getText().toString());
					dialog.dismiss();
				}

			}
		});

		dialog.setCancelable(true);
		dialog.showDialog(verifyView);
	}

	/**
	 * 
	 * @todo 提交密码验证信息
	 * @time 2014-5-12 上午11:17:51
	 * @author liuzenglong163@gmail.com
	 */
	private void requestVerifyPwd(String password) {
		HttpRequest
				.doVerfiyPwd(mActivity, mUserInfo.getPhone(), password, this);
	}

	/**
	 * 
	 * @todo 获取验证码
	 * @time 2014-5-12 下午3:48:30
	 * @author liuzenglong163@gmail.com
	 */
	private void requestVerifyCode() {
		String strPhoneString = etPhone.getText().toString();
		if (TextUtils.isEmpty(strPhoneString)) {
			Toaster.showShort(mActivity, "请输入手机号");
			return;
		}
		HttpRequest.getVerifyCode(mActivity, etPhone.getText().toString(), "1",
				this);
	}

	/**
	 * 
	 * @todo 修改手机号
	 * @time 2014-5-12 下午4:08:52
	 * @author liuzenglong163@gmail.com
	 */
	private void requestModifyPhone() {
		String userId = String.valueOf(mUserInfo.getUserId());
		String strVerifyCode = etVerifyCode.getText().toString();
		String strPhoneString = etPhone.getText().toString();
		if (TextUtils.isEmpty(strPhoneString)) {
			Toaster.showShort(mActivity, "请输入手机号");
			return;
		} else if (TextUtils.isEmpty(strVerifyCode)) {
			Toaster.showShort(mActivity, "请输入验证码");
			return;
		}
		HttpRequest.doModifyPhone(mActivity, userId, strPhoneString,
				strVerifyCode, this);
	}

	/**
	 * 
	 * @todo 保存密码
	 * @time 2014-5-12 下午4:38:54
	 * @author liuzenglong163@gmail.com
	 */
	private void savePhone() {
		mUserInfo.setPhone(etPhone.getText().toString());
	}
}

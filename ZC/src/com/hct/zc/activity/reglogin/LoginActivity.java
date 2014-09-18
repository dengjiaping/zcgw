package com.hct.zc.activity.reglogin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.constants.Constants;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.LoginResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.service.Loginer;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 登录.
 * @time 2014年5月4日 下午2:13:24
 * @author jie.liu
 */
public class LoginActivity extends BaseHttpActivity {

	private EditText mUsernameET;
	private EditText mPasswordET;
	private Button mLoginBtn;
	private TextView mForgetPwdTV;
	private CheckBox mAutoLoginCB;
	private Button mRequestBeAdvisor;
	private PreferenceUtil mUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		mUtil = new PreferenceUtil(this, Constants.PREFERENCE_FILE);
		initViews();
	}

	private void initViews() {
		initTitlebar();
		mUsernameET = (EditText) findViewById(R.id.username_et);
		mPasswordET = (EditText) findViewById(R.id.password_et);
		mLoginBtn = (Button) findViewById(R.id.login_btn);
		mAutoLoginCB = (CheckBox) findViewById(R.id.auto_login_cb);
		mForgetPwdTV = (TextView) findViewById(R.id.forget_password_tv);
		mRequestBeAdvisor = (Button) findViewById(R.id.request_be_advisor_btn);

		ClickListener listener = new ClickListener();
		mLoginBtn.setOnClickListener(listener);
		mForgetPwdTV.setOnClickListener(listener);
		mRequestBeAdvisor.setOnClickListener(listener);
		// mUsernameET.setText("13597220572");
		// mPasswordET.setText("220572");
		// mUsernameET.setText("13691680839");
		// mPasswordET.setText("123456");
	}

	private void initTitlebar() {
		new TitleBar(this).initTitleBar("登录");
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login_btn:
				login();
				break;
			case R.id.forget_password_tv:
				ContextUtil.pushToActivity(LoginActivity.this,
						ForgetPasswordActivity.class);
				break;
			case R.id.request_be_advisor_btn:
				ContextUtil.pushToActivity(LoginActivity.this,
						RequestAdvisorActivity.class);
				finish();
				break;
			default:
				LogUtil.w(LoginActivity.this, "在登录界面点击了未知的按钮");
			}
		}

		private void login() {
			String username = StringUtils.getText(mUsernameET);
			String password = StringUtils.getText(mPasswordET);
			new Loginer(LoginActivity.this).login(username, password,
					LoginActivity.this);
		}
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
		LoginResult loginResult = gson.fromJson(result, LoginResult.class);
		Result r = loginResult.getResult();
		if (HttpResult.SUCCESS.equals(r.getErrorcode())) {
			UserInfo userInfo = loginResult.getUserInfo();
			if (userInfo == null) {
				LogUtil.e(LoginActivity.this, "登录返回用户信息失败或者解析有问题");
				userInfo = new UserInfo();
			}
			new Loginer(LoginActivity.this).loginSuccess(userInfo);
			rememberAutoLoginOrNot();
			finish();
		} else if ("-1".equals(r.getErrorcode())
				|| "-3".equals(r.getErrorcode())) {
			Toaster.showShort(LoginActivity.this, r.getErrormsg());
		}
	}

	private void rememberAutoLoginOrNot() {
		boolean autoLogin = mAutoLoginCB.isChecked();
		mUtil.setUsername(StringUtils.getText(mUsernameET));

		// 记住帐号，密码为自动登录，下次可以检测是否有帐号密码来判断用户是否选择了自动登录
		if (autoLogin) {
			mUtil.setPassword(StringUtils.getText(mPasswordET));
		} else {
			mUtil.setPassword("");
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

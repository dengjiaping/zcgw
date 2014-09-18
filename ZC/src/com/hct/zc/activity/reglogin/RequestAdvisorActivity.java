package com.hct.zc.activity.reglogin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 申请成为理财顾问 .
 * @time 2014年5月4日 下午2:17:40
 * @author jie.liu
 */
public class RequestAdvisorActivity extends BaseHttpActivity {

	private EditText mPhoneET;

	private CheckBox mProtocolCB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request_advisor_activity);

		initViews();
	}

	private void initViews() {
		initTitleBar();
		mPhoneET = (EditText) findViewById(R.id.phone_et);
		mProtocolCB = (CheckBox) findViewById(R.id.read_protocol_cb);
		TextView protocolTV = (TextView) findViewById(R.id.protocol_tv);
		protocolTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProtocol();
			}
		});
	}

	private void initTitleBar() {
		new TitleBar(RequestAdvisorActivity.this).initTitleBar("注册");
	}

	private void showProtocol() {
		new AlertDialog.Builder(this).setTitle("掌财顾问用户协议")
				.setMessage(R.string.client_protocol)
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mProtocolCB.setChecked(true);
					}
				}).show();
	}

	public void buttonClick(View view) {
		String phoneNum = StringUtils.getText(mPhoneET);
		if (TextUtils.isEmpty(phoneNum)) {
			Toaster.showShort(RequestAdvisorActivity.this, "电话号码不能为空");
			return;
		}

		if (!InputFormatChecker.isPhoneNumEligible(phoneNum)) {
			Toaster.showShort(RequestAdvisorActivity.this, "电话格式不正确");
			return;
		}

		if (!mProtocolCB.isChecked()) {
			Toaster.showShort(RequestAdvisorActivity.this, "请先阅读掌财顾问协议");
			return;
		}

		HttpRequest.checkPhoneIsOnly(this, phoneNum, this);
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
		String responseCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(responseCode)) {
			Intent intent = new Intent(RequestAdvisorActivity.this,
					PhoneVerificationActivity.class);
			intent.putExtra("phone", StringUtils.getText(mPhoneET));
			startActivity(intent);
			finish();
		} else if (HttpResult.FAIL.equals(responseCode)) {
			Toaster.showShort(RequestAdvisorActivity.this, r.getResult()
					.getErrormsg());
		} else {
			Toaster.showShort(RequestAdvisorActivity.this, "检测失败，请重试");
		}
	}
}

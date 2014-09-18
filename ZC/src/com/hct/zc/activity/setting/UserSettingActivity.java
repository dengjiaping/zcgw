package com.hct.zc.activity.setting;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpHelper;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.LoginResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.service.Loginer;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 个人设置
 * @time 2014-5-10 上午9:48:15
 * @author liuzenglong163@gmail.com
 */
public class UserSettingActivity extends BaseHttpActivity implements
		OnClickListener {

	private TextView tvPhone;
	private TextView tvRealName;
	private TextView tvCid;
	private TextView tvBank;
	private TextView tvAddress;
	private TextView tvEmail;
	private LinearLayout llCidLayout1;
	private LinearLayout llCidLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_user_layout);
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName()); // 统计页面
		MobclickAgent.onResume(this);
		refreshUserInfo();
		initData();
	}

	private void refreshUserInfo() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo != null && !TextUtils.isEmpty(userInfo.getUserId())) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", userInfo.getUserId());

			HttpHelper httpHelper = new HttpHelper();
			httpHelper.setOnHttpResponse(this);
			try {
				httpHelper.post(this, HttpUrl.GET_ADISOR_INFO, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName()); // 保证 onPageEnd
		MobclickAgent.onPause(this);
	}

	/**
	 * 
	 * @todo 初始化标题名字
	 * @time 2014-5-10 上午10:29:09
	 * @author liuzenglong163@gmail.com
	 */
	private void initTitlebar() {
		new TitleBar(UserSettingActivity.this).initTitleBar(getResources()
				.getString(R.string.user_setting_title));
	}

	private void initViews() {
		initTitlebar();
		tvPhone = (TextView) findViewById(R.id.phone_tv);
		tvRealName = (TextView) findViewById(R.id.tv_realname);
		tvCid = (TextView) findViewById(R.id.tv_cid);
		tvBank = (TextView) findViewById(R.id.bank_card_tv);
		tvAddress = (TextView) findViewById(R.id.mailing_addr_tv);
		tvEmail = (TextView) findViewById(R.id.email_tv);

		llCidLayout = (LinearLayout) findViewById(R.id.cid_llyt);
		llCidLayout.setOnClickListener(this);
		llCidLayout1 = (LinearLayout) findViewById(R.id.real_id_llyt);
		llCidLayout1.setOnClickListener(this);

		LinearLayout click = (LinearLayout) findViewById(R.id.phone_llyt);
		click.setOnClickListener(this);

		click = (LinearLayout) findViewById(R.id.bank_card_llyt);
		click.setOnClickListener(this);

		click = (LinearLayout) findViewById(R.id.mailing_addr_llyt);
		click.setOnClickListener(this);

		click = (LinearLayout) findViewById(R.id.email_llyt);
		click.setOnClickListener(this);

		click = (LinearLayout) findViewById(R.id.password_llyt);
		click.setOnClickListener(this);
	}

	private void initData() {
		if (mUserInfo == null) {
			Toaster.showShort(this, "请先登录");
			return;
		}

		if (!TextUtils.isEmpty(mUserInfo.getPhone())
				&& mUserInfo.getPhone().length() == 11) {
			tvPhone.setText(mUserInfo.getPhone().replace(
					mUserInfo.getPhone().substring(3,
							mUserInfo.getPhone().length() - 4), "****"));
		}

		if (UserInfo.HAS_ACCREADE.equals(mUserInfo.getAccreditation())) { // 如果已经验证，
			tvCid.setBackgroundResource(R.drawable.icon_sfz_rz);
			if (!TextUtils.isEmpty(mUserInfo.getName())
					&& mUserInfo.getName().length() > 1) {
				tvRealName.setText("*"
						+ mUserInfo.getName().substring(1,
								mUserInfo.getName().length()));
			}
			llCidLayout.setClickable(false); // 已经验证了，不能再进入
			llCidLayout1.setClickable(false);

		} else if (UserInfo.NO_ACCREADE.equals(mUserInfo.getAccreditation())) {
			tvCid.setText("未验证");
			// 不能用以下方法清除背景，用以下方法清除背景，系统会调用默认的一张透明图片去填充，但是有些系统会把这个图片阉割掉,就会崩溃
			// tvCid.setBackground(null);
			// tvCid.setBackground(new BitmapDrawable());
			tvCid.setBackgroundColor(Color.TRANSPARENT);
			tvRealName.setText("");
		} else {
			tvCid.setText("正在审核中");
			tvCid.setBackgroundColor(Color.TRANSPARENT);
			tvRealName.setText("");
		}

		tvBank.setText(TextUtils.isEmpty(mUserInfo.getBankcard()) ? "未绑定"
				: "已绑定");

		if (!TextUtils.isEmpty(mUserInfo.getAddress())
				&& mUserInfo.getAddress().length() > 6) {
			tvAddress.setText(mUserInfo.getAddress().replace(
					mUserInfo.getAddress().substring(6,
							mUserInfo.getAddress().length()), "******"));
		} else {
			tvAddress.setText("请设置您的邮寄地址");
		}

		tvEmail.setText(TextUtils.isEmpty(mUserInfo.getEmail()) ? "请设置您的电子邮箱"
				: mUserInfo.getEmail());
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

	/**
	 * 
	 * 登陆和获取顾问信息返回的信息一样
	 * 
	 * @time 2014年6月13日 下午2:13:21
	 * @author liuzenglong163@gmail.com
	 * @param path
	 * @param result
	 */
	private void dealWithHttpReturned(String path, String result) {
		Gson gson = new Gson();
		LoginResult loginResult = gson.fromJson(result, LoginResult.class);
		Result r = loginResult.getResult();

		if (HttpResult.SUCCESS.equals(r.getErrorcode())) {
			UserInfo userInfo = loginResult.getUserInfo();
			if (userInfo != null) {
				if (HttpUrl.LOGIN.equals(path)) {
					new Loginer(this).loginSuccess(userInfo);
				} else if (HttpUrl.GET_ADISOR_INFO.equals(path)) {
					ZCApplication.getInstance().storeUserInfo(userInfo);
					mUserInfo = userInfo;
				}
			}

			initData();
		} else if ("-1".equals(r.getErrorcode())
				|| "-3".equals(r.getErrorcode())) {
			Toaster.showShort(this, r.getErrormsg());
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.phone_llyt:
			intent = new Intent(UserSettingActivity.this,
					SetPhoneActivity.class);
			break;
		case R.id.real_id_llyt:
		case R.id.cid_llyt:
			intent = new Intent(UserSettingActivity.this, SetCidActivity.class);
			break;
		case R.id.email_llyt:
			intent = new Intent(UserSettingActivity.this,
					SetEmailActivity.class);
			break;
		case R.id.bank_card_llyt:
			// if (!UserInfo.HAS_ACCREADE.equals(mUserInfo.getAccreditation()))
			// {
			// Toaster.showShort(mActivity, "请先进行身份认证！");
			// return;
			// } else {
			// }
			intent = new Intent(UserSettingActivity.this, SetBankActivity.class);
			break;
		case R.id.mailing_addr_llyt:
			intent = new Intent(UserSettingActivity.this,
					SetAddressActivity.class);
			break;
		case R.id.password_llyt:
			intent = new Intent(UserSettingActivity.this, SetPwdActivity.class);
			break;

		}
		startActivity(intent);
	}

}

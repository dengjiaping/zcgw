package com.hct.zc.fragment;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.academy.AcademyActivity;
import com.hct.zc.activity.client.MyClientsActivity;
import com.hct.zc.activity.gesturelock.GestureLockActivity;
import com.hct.zc.activity.gesturelock.GestureLockSettingActivity;
import com.hct.zc.activity.mine.MyBusinessFormActivity;
import com.hct.zc.activity.mine.MyCommissionActivity;
import com.hct.zc.activity.more.MoreActivity;
import com.hct.zc.activity.reglogin.LoginActivity;
import com.hct.zc.activity.setting.UserSettingActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.constants.BroadcastAction;
import com.hct.zc.constants.Constants;
import com.hct.zc.http.HttpHelper;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.LoginResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.service.Loginer;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LockPatternUtils;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 首页左侧菜单.
 * @time 2014年5月4日 下午2:11:01
 * @author jie.liu
 */
public class LeftMenuFragment extends BaseFragment {

	private TextView mPhoneTV;
	private TextView mLevelTV;
	private TextView mAllCommissionTV;
	private TextView mThreeMonthComTV;
	private TextView mMyBusFormTV;
	private TextView mMyClientsTV;
	private LinearLayout mCommissionSwitchLlyt;
	private ImageView mCommissionIV;
	private TextView mPersonalSettingTV;
	private Button mLoginBtn;
	private PreferenceUtil mUtil;

	private Drawable mBusFormsLogined;
	private Drawable mBusFormsNotLogined;
	private Drawable mClientsLogined;
	private Drawable mClientsNotLogined;
	private final int BG_SHOW_COMMISSION = R.drawable.bg_commission_on;
	private final int BG_NOT_SHOW_COMMISSION = R.drawable.bg_commission_off;
	private final String PLEASE_LOGIN = "您还未登录，请登录!";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left_menu_fragment, null);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LeftMenuFragment"); // 统计页面
		mUtil = new PreferenceUtil(getActivity(), Constants.PREFERENCE_FILE);
		initStateDrawbles();
		initViews(getView());
		handleAutoLogin();
		refreshUI();

		refreshUserInfo();
	}

	private void initStateDrawbles() {
		mBusFormsLogined = initDrawble(R.drawable.ic_bus_forms_logined);
		mBusFormsNotLogined = initDrawble(R.drawable.ic_bus_forms_not_logined);
		mClientsLogined = initDrawble(R.drawable.ic_clients_logined);
		mClientsNotLogined = initDrawble(R.drawable.ic_clients_not_logined);
	}

	private Drawable initDrawble(int drawableId) {
		Drawable drawble = getResources().getDrawable(drawableId);
		// / 这一步必须要做,否则不会显示.
		drawble.setBounds(0, 0, drawble.getMinimumWidth(),
				drawble.getMinimumHeight());
		return drawble;
	}

	private void initViews(View view) {
		mPhoneTV = (TextView) view.findViewById(R.id.phone_num_tv);
		mLevelTV = (TextView) view.findViewById(R.id.level_tv);
		mAllCommissionTV = (TextView) view.findViewById(R.id.all_commission_tv);
		mThreeMonthComTV = (TextView) view
				.findViewById(R.id.three_month_commission_tv);
		mMyBusFormTV = (TextView) view.findViewById(R.id.business_forms_tv);
		mMyClientsTV = (TextView) view.findViewById(R.id.clients_tv);
		mCommissionSwitchLlyt = (LinearLayout) view
				.findViewById(R.id.commission_switch_llyt);
		mCommissionIV = (ImageView) view.findViewById(R.id.commission_iv);

		LinearLayout commissionLlyt = (LinearLayout) view
				.findViewById(R.id.commission_llyt);
		TextView schoolTV = (TextView) view.findViewById(R.id.school_tv);
		TextView moreTV = (TextView) view.findViewById(R.id.more_tv);
		LinearLayout clientServiceLlyt = (LinearLayout) view
				.findViewById(R.id.client_service_llyt);
		mPersonalSettingTV = (TextView) view
				.findViewById(R.id.personal_setting_tv);
		mLoginBtn = (Button) view.findViewById(R.id.login_btn);

		ClickListener listener = new ClickListener();

		mPhoneTV.setOnClickListener(listener);
		commissionLlyt.setOnClickListener(listener);
		mMyBusFormTV.setOnClickListener(listener);
		mMyClientsTV.setOnClickListener(listener);
		schoolTV.setOnClickListener(listener);
		clientServiceLlyt.setOnClickListener(listener);
		moreTV.setOnClickListener(listener);
		mCommissionIV.setOnClickListener(listener);
		mPersonalSettingTV.setOnClickListener(listener);
		mLoginBtn.setOnClickListener(listener);
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.phone_num_tv:
				pushToLoginActivityIfNeeded();
				break;
			case R.id.commission_llyt:
				ContextUtil.pushToActivityWithLogin(getActivity(),
						MyCommissionActivity.class);
				break;
			case R.id.business_forms_tv:
				ContextUtil.pushToActivityWithLogin(getActivity(),
						MyBusinessFormActivity.class);
				break;
			case R.id.clients_tv:
				pushToClientsActivity();
				break;
			case R.id.school_tv:
				ContextUtil
						.pushToActivity(getActivity(), AcademyActivity.class);
				break;
			case R.id.more_tv:
				ContextUtil.pushToActivity(getActivity(), MoreActivity.class);
				break;
			case R.id.client_service_llyt:
				String clientService = getResources().getString(
						R.string.client_service_phone);
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ clientService));
				getActivity().startActivity(intent);
				break;
			case R.id.commission_iv:
				commissionSwitcherClicked();
				break;
			case R.id.personal_setting_tv:
				ContextUtil.pushToActivityWithLogin(getActivity(),
						UserSettingActivity.class);
				break;
			case R.id.login_btn:
				ContextUtil.pushToActivity(getActivity(), LoginActivity.class);
				break;
			default:
				LogUtil.w(LeftMenuFragment.this, "点击了未知的按钮");
			}
		}
	}

	private void pushToLoginActivityIfNeeded() {
		String context = mPhoneTV.getText().toString().trim();
		if (PLEASE_LOGIN.equals(context)) {
			ContextUtil.pushToActivityWithLogin(getActivity(),
					LoginActivity.class);
		}
	}

	private void pushToClientsActivity() {
		Intent intent = new Intent(getActivity(), MyClientsActivity.class);
		intent.putExtra("clientsType", MyClientsActivity.CLIENTS_NORMAL);
		ContextUtil.pushToActivityWithLogin(getActivity(), intent);
	}

	/**
	 * 
	 * @todo 佣金开关按钮被点击
	 * @time 2014年5月26日 上午10:50:59
	 * @author jie.liu
	 */
	private void commissionSwitcherClicked() {
		boolean originalShowComState = mUtil.getShouldShowCommission();
		if (originalShowComState) {
			mUtil.setShouldShowCommission(false);
			refreshCommissionSwitcher();
		} else {
			openGestureLockIfNeeded();
		}

		sendRefreshComRecevier();
	}

	private void openGestureLockIfNeeded() {
		String lockPattern = new LockPatternUtils(getActivity())
				.getLockPaternString();
		if (TextUtils.isEmpty(lockPattern)) {
			ContextUtil.pushToActivity(getActivity(),
					GestureLockSettingActivity.class);
		} else {
			ContextUtil
					.pushToActivity(getActivity(), GestureLockActivity.class);
		}
	}

	/**
	 * 
	 * @todo 发送广播更新菜单中间页面和产品列表
	 * @time 2014年5月26日 上午10:51:41
	 * @author jie.liu
	 */
	private void sendRefreshComRecevier() {
		Intent intent = new Intent();
		intent.setAction(BroadcastAction.ACTION_REFRESH_LOGIN_STATE);
		getActivity().sendBroadcast(intent);

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("LeftMenuFragment");
	}

	/**
	 * 
	 * @todo 判断是否有记住帐号密码，如果有则自动登录
	 * @time 2014年5月5日 下午12:01:29
	 * @author jie.liu
	 */
	private void handleAutoLogin() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo != null) {
			return;
		}

		String username = mUtil.getUsername();
		if (TextUtils.isEmpty(username)) {
			return;
		}

		String password = mUtil.getPassword();
		if (TextUtils.isEmpty(password)) {
			return;
		}

		new Loginer(getActivity()).login(username, password, this);
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		super.onHttpSuccess(path, result);
		if (TextUtils.isEmpty(result)) {
			Toaster.showShort(getActivity(), "服务器出错了，请重试");
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
					new Loginer(getActivity()).loginSuccess(userInfo);
				} else if (HttpUrl.GET_ADISOR_INFO.equals(path)) {
					ZCApplication.getInstance().storeUserInfo(userInfo);
				}
			}

			refreshUI();
		} else if ("-1".equals(r.getErrorcode())
				|| "-3".equals(r.getErrorcode())) {
			Toaster.showShort(getActivity(), r.getErrormsg());
		}
	}

	private void refreshUI() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			refreshTextViewNotLogined(mPhoneTV, PLEASE_LOGIN);
			mLevelTV.setVisibility(View.GONE);
			refreshTextViewNotLogined(mAllCommissionTV, "￥0.00");
			refreshTextViewNotLogined(mThreeMonthComTV, "￥0.00");
			mMyBusFormTV.setCompoundDrawables(null, mBusFormsNotLogined, null,
					null);
			mMyClientsTV.setCompoundDrawables(null, mClientsNotLogined, null,
					null);
			// 隐藏佣金比率开关
			mCommissionSwitchLlyt.setVisibility(View.GONE);
			refreshCommissionSwitcher();
			mPersonalSettingTV.setVisibility(View.GONE);
			mLoginBtn.setVisibility(View.VISIBLE);
			return;
		}

		fillUserInfoWhenIsLogined(userInfo);
	}

	private void refreshTextViewNotLogined(TextView textView, String content) {
		refreshTextView(textView, content, Color.GRAY);
	}

	private void fillUserInfoWhenIsLogined(UserInfo userInfo) {
		fillPhone(userInfo);
		fillGrade(userInfo);
		fillCommission(userInfo);
		mMyBusFormTV.setCompoundDrawables(null, mBusFormsLogined, null, null);
		mMyClientsTV.setCompoundDrawables(null, mClientsLogined, null, null);
		// 显示佣金比率开关
		mCommissionSwitchLlyt.setVisibility(View.VISIBLE);
		refreshCommissionSwitcher();
		mPersonalSettingTV.setVisibility(View.VISIBLE);
		mLoginBtn.setVisibility(View.GONE);
	}

	private void fillPhone(UserInfo userInfo) {
		if (TextUtils.isEmpty(userInfo.getPhone())) {
			userInfo.setPhone("***********");
		}
		String phoneNum = StringUtils.optimizePhone(userInfo.getPhone());
		refreshTextView(mPhoneTV, phoneNum, Color.BLACK);
	}

	private void refreshTextView(TextView textView, String content,
			int textColor) {
		textView.setText(content);
		textView.setTextColor(textColor);
	}

	private void fillGrade(UserInfo userInfo) {
		if (TextUtils.isEmpty(userInfo.getGrade())) {
			mLevelTV.setVisibility(View.GONE);
		} else {
			mLevelTV.setVisibility(View.VISIBLE);
			mLevelTV.setText(userInfo.getGrade());
		}
	}

	private void fillCommission(UserInfo userInfo) {
		if (TextUtils.isEmpty(userInfo.getAllcom())) {
			userInfo.setAllcom("未知");
		}
		if (TextUtils.isEmpty(userInfo.getThreecom())) {
			userInfo.setThreecom("未知");
		}
		int color = getResources().getColor(R.color.jiuhong);
		refreshTextView(mAllCommissionTV, "￥" + userInfo.getAllcom(), color);
		refreshTextView(mThreeMonthComTV, "￥" + userInfo.getThreecom(), color);
	}

	private void refreshCommissionSwitcher() {
		boolean shouleShowCommission = mUtil.getShouldShowCommission();
		if (shouleShowCommission) {
			mCommissionIV.setBackgroundResource(BG_SHOW_COMMISSION);
		} else {
			mCommissionIV.setBackgroundResource(BG_NOT_SHOW_COMMISSION);
		}
	}

	private void refreshUserInfo() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo != null && !TextUtils.isEmpty(userInfo.getUserId())) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", userInfo.getUserId());

			HttpHelper httpHelper = new HttpHelper();
			httpHelper.setOnHttpResponse(this);
			try {
				httpHelper.post(getActivity(), HttpUrl.GET_ADISOR_INFO, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// @Override
	// public void onGuideFinished() {
	// mCommissionIV.performClick();
	// }
}

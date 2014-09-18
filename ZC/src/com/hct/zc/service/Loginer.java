package com.hct.zc.service;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.hct.zc.R;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.constants.BroadcastAction;
import com.hct.zc.fragment.CenterFragment.State;
import com.hct.zc.http.HttpHelper.OnHttpResponse;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;

/**
 * @todo 用来登录
 * @time 2014年5月5日 上午11:17:47
 * @author jie.liu
 */

public class Loginer {

	private final Activity mContext;

	public Loginer(Activity context) {
		mContext = context;
	}

	public boolean login(String username, String password,
			OnHttpResponse onHttpResponse) {
		if (isUsernameEligible(username) && isPasswordligible(password)) {
			HttpRequest.doLogin(mContext, username, password, onHttpResponse);
			// Map<String, String> params = new HashMap<String, String>();
			// params.put("mytoken", ContextUtil.getIMEI(mContext));
			// params.put("phone", username);
			// params.put("password", password);
			//
			// HttpHelper.setOnHttpResponse(onHttpResponse);
			// try {
			// LoadingProgress.getInstance().show(mContext, "正在登录...");
			// HttpHelper.post(mContext, HttpUrl.LOGIN, params);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			return true;
		} else {
			return false;
		}
	}

	private boolean isUsernameEligible(String username) {
		if (TextUtils.isEmpty(username)) {
			Toaster.showShort(mContext, R.string.username_can_not_be_null);
			return false;
		}

		boolean isEligible = InputFormatChecker.isUsernameEligible(username);
		if (!isEligible) {
			Toaster.showShort(mContext, R.string.username_has_wrong_format);
		}
		return isEligible;
	}

	private boolean isPasswordligible(String password) {
		if (TextUtils.isEmpty(password)) {
			Toaster.showShort(mContext, "密码不能为空");
			return false;
		}

		return InputFormatChecker.isPasswordEligible(password);
	}

	/**
	 * 
	 * @todo 登录成功的处理
	 * @time 2014年5月27日 上午11:40:25
	 * @author jie.liu
	 * @param userInfo
	 */
	public void loginSuccess(UserInfo userInfo) {
		ZCApplication.getInstance().storeUserInfo(userInfo);
		ZCApplication.getInstance().storeState(State.LOGINED);
		JPushInterface.setAlias(mContext, userInfo.getAlias(), null);
		LogUtil.d(mContext, userInfo.toString());
		sendRefreshComRecevier();
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
		mContext.sendBroadcast(intent);
	}
}

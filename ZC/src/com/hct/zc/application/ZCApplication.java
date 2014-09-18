package com.hct.zc.application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.hct.zc.bean.ApkBean;
import com.hct.zc.bean.MsgBean;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.constants.Constants;
import com.hct.zc.fragment.CenterFragment.State;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.utils.imagecache.ImageCacheManager;

public class ZCApplication extends Application {
	private final List<Activity> activityList = new LinkedList<Activity>();
	private static ZCApplication instance;

	public static List<MsgBean> msgList = new ArrayList<MsgBean>(); // 消息列表

	public static ApkBean apkBean; // 存放版本升级信息

	private UserInfo mUserInfo;

	private State mState;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static synchronized ZCApplication getInstance() {
		if (null == instance) {
			instance = new ZCApplication();
		}
		return instance;

	}

	/**
	 * 添加实例
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 退出
	 */
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.d(this, "开始时间：" + System.currentTimeMillis());
		LogUtil.d("Application", "执行onCreaet");
		// 极光推送
		JPushInterface.setDebugMode(false);
		JPushInterface.init(this);
		instance = this;
		PreferenceUtil util = new PreferenceUtil(this,
				Constants.PREFERENCE_FILE);
		UserInfo userInfo = util.getUserInfo();
		// 有UserId, 说明此时是登录状态
		String userId = userInfo.getUserId();
		if (!TextUtils.isEmpty(userId)) {
			storeUserInfo(userInfo);
			storeState(State.LOGINED);
		}
		LogUtil.d(this, "结束时间：" + System.currentTimeMillis());
		// Thread.setDefaultUncaughtExceptionHandler(new
		// MyUncaughtExceptionHandler());
	}

	public State getState() {
		return mState;
	}

	public void storeState(State state) {
		mState = state;
	}

	/**
	 * 
	 * @todo 缓存用户信息，有内存和外村缓存
	 * @time 2014年5月20日 上午11:50:00
	 * @author jie.liu
	 * @param userInfo
	 *            传入null表示退出
	 */
	public void storeUserInfo(UserInfo userInfo) {
		mUserInfo = userInfo;

		if (userInfo == null) {
			// 退出
			PreferenceUtil util = new PreferenceUtil(this,
					Constants.PREFERENCE_FILE);
			util.setUserInfo(UserInfo.getEmptyInstance());
		} else {
			new PreferenceUtil(this, Constants.PREFERENCE_FILE)
					.setUserInfo(userInfo);
		}
	}

	/**
	 * 
	 * @todo 获取用户的缓存信息
	 * @time 2014年5月20日 上午11:50:37
	 * @author jie.liu
	 * @return 用户没有登录则返回null
	 */
	public UserInfo getUserInfo() {
		if (mUserInfo != null) {
			return mUserInfo;
		}

		PreferenceUtil util = new PreferenceUtil(instance,
				Constants.PREFERENCE_FILE);
		UserInfo userInfo = util.getUserInfo();
		String userId = userInfo.getUserId();
		if (!TextUtils.isEmpty(userId)) {
			// 说明此时是登录状态
			storeUserInfo(userInfo);
			storeState(State.LOGINED);
			return mUserInfo;
		} else {
			// 未登录，返回null
			return null;
		}
	}

	/**
	 * @todo 判断是否登录
	 * @time 2014-5-22 下午4:26:03
	 * @author liuzenglong163@gmail.com
	 */
	public boolean doJugdgeLogin(Activity activity) {
		if (getUserInfo() == null) {
			Toaster.showShort(activity, "您还没有登录，请先登录!");
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onLowMemory() {
		new ImageCacheManager(instance).clearMemCache();
		super.onLowMemory();
	}

}

package com.hct.zc.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.utils.LoadingProgress;

/**
 * 
 * @todo 所有activity的基类
 * @time 2014-5-10 上午10:17:29
 * @author liuzenglong163@gmail.com
 */
public class BaseActivity extends Activity {

	public Activity mActivity;
	public UserInfo mUserInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		mUserInfo = ZCApplication.getInstance().getUserInfo();
		ZCApplication.getInstance().addActivity(this); // 管理activity
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(mActivity);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(mActivity);
	}

	/**
	 * 双选框
	 * 
	 * @param c
	 * @param msg
	 */
	public void showDoubleAlertDlg(String title, String msg, String yes,
			String no) {
		new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
				.setPositiveButton(yes, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						yesBtnWork();
					}
				}).setNegativeButton(no, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	public void yesBtnWork() {
	}

	public void noBtnWork() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LoadingProgress.getInstance().dismiss();
	}

}

package com.hct.zc.activity.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.activity.base.BaseActivity;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.utils.ZCUtils;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.MultiStatus;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.MulStatusListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.db.OauthHelper;
import com.umeng.socialize.exception.SocializeException;

/**
 * 、
 * 
 * @todo 关于掌财
 * @time 2014-5-21 下午5:17:15
 * @author liuzenglong163@gmail.com
 */
public class AboutActivity extends BaseActivity implements OnClickListener {
	// 首先在您的Activity中添加如下成员变量
	private static final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share", RequestType.SOCIAL);

	private int mPhoneType = -1;
	private final int TYPE_COOPERATION_PHONE = 0;
	private final int TYPE_SERVER_PHONE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
		initViews();
	}

	private void initViews() {
		initTitlebar();
		TextView tvVersion = (TextView) findViewById(R.id.tv_version);
		tvVersion.setText("版本号：V" + ZCUtils.getVersionName(mActivity));
		findViewById(R.id.ll_official_website).setOnClickListener(this);
		findViewById(R.id.ll_cooperation_line).setOnClickListener(this);
		findViewById(R.id.ll_hot_line).setOnClickListener(this);

	}

	@Override
	public void yesBtnWork() {
		String phoneNum = getPhoneNum();
		if (!TextUtils.isEmpty(phoneNum)) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phoneNum));
			mActivity.startActivity(intent);
		}
		mPhoneType = -1;
	};

	private String getPhoneNum() {
		String phone = null;
		switch (mPhoneType) {
		case TYPE_COOPERATION_PHONE:
			phone = getResources().getString(R.string.about_coope_phone_num);
			break;
		case TYPE_SERVER_PHONE:
			phone = getResources().getString(R.string.client_service_phone);
			break;
		default:
			LogUtil.w(this, "找不到用户要拨打的电话号码");
			break;
		}

		return phone;
	}

	private void initTitlebar() {
		new TitleBar(AboutActivity.this).initTitleBar("关于掌财顾问");
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_official_website:
			openWebsite();
			break;
		case R.id.ll_hot_line:
			mPhoneType = TYPE_SERVER_PHONE;
			showDoubleAlertDlg("",
					getResources()
							.getString(R.string.about_server_phone_number),
					"拨号", "取消");
			break;
		case R.id.ll_cooperation_line:
			mPhoneType = TYPE_COOPERATION_PHONE;
			showDoubleAlertDlg("",
					getResources().getString(R.string.about_coope_phone_num),
					"拨号", "取消");
			break;
		default:
			break;
		}
	}

	private void openWebsite() {
		String webUrl = getResources().getString(R.string.about_website_url);
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse("http://" + webUrl);
		intent.setData(content_url);
		startActivity(intent);
	}

	public void followQQ(View view) {

	}

	public void followWX(View view) {
		SocializeConfig config = mController.getConfig();
		// 添加关注对象(平台，关注用户的uid)
		config.addFollow(SHARE_MEDIA.WEIXIN, new String[] { "1914100420",
				"1914100421" });
		// 添加follow 时的回调
		config.setOauthDialogFollowListener(new MulStatusListener() {
			@Override
			public void onStart() {
				LogUtil.d(AboutActivity.this, "开始关注微信");
			}

			@Override
			public void onComplete(MultiStatus multiStatus, int st,
					SocializeEntity entity) {
				if (st == 200) {
					Toaster.showShort(AboutActivity.this, "关注成功...");
				} else {
					Toaster.showShort(AboutActivity.this, "关注失败...");
				}
			}
		});
	}

	public void followSina(View view) {
		// SocializeConfig config = mController.getConfig();
		// // 添加关注对象(平台，关注用户的uid)
		// config.addFollow(SHARE_MEDIA.SINA, new String[] { "1914100420",
		// "1914100421" });
		// // 添加follow 时的回调
		// config.setOauthDialogFollowListener(new MulStatusListener() {
		// @Override
		// public void onStart() {
		// LogUtil.d(AboutActivity.this, "开始关注新浪微博");
		// }
		//
		// @Override
		// public void onComplete(MultiStatus multiStatus, int st,
		// SocializeEntity entity) {
		// if (st == 200) {
		// Toaster.showShort(AboutActivity.this, "关注成功...");
		// } else {
		// Toaster.showShort(AboutActivity.this, "关注失败...");
		// }
		// }
		// });
		if (!OauthHelper.isAuthenticated(AboutActivity.this, SHARE_MEDIA.SINA)) {
			mController.doOauthVerify(AboutActivity.this, SHARE_MEDIA.SINA,
					new UMAuthListener() {

						@Override
						public void onStart(SHARE_MEDIA platform) {
						}

						@Override
						public void onError(SocializeException e,
								SHARE_MEDIA platform) {
						}

						@Override
						public void onComplete(Bundle value,
								SHARE_MEDIA platform) {
							if (!TextUtils.isEmpty(value.getString("uid"))) {
								follow(SHARE_MEDIA.SINA);
							} else {
								// error deal
							}
						}

						@Override
						public void onCancel(SHARE_MEDIA platform) {
						}
					});
		} else {
			follow(SHARE_MEDIA.SINA);
		}
	}

	private void follow(SHARE_MEDIA platform) {
		mController.follow(this, platform, new MulStatusListener() {
			@Override
			public void onStart() {
			}

			@Override
			public void onComplete(MultiStatus multiStatus, int st,
					SocializeEntity entity) {
				if (st == 200) {
					// 关注成功
					Toaster.showShort(AboutActivity.this, "关注成功");
				}
			}
		}, new String[] { "3516851151" });
	}
}

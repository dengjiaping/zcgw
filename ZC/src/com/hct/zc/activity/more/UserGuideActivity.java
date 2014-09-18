package com.hct.zc.activity.more;

import android.os.Bundle;

import com.hct.zc.R;
import com.hct.zc.activity.base.BaseActivity;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 使用指南.
 * @time 2014年5月4日 下午2:19:12
 * @author jie.liu
 */
public class UserGuideActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_guide_activity);

		initViews();
	}

	private void initViews() {
		initTitlebar();
	}

	private void initTitlebar() {
		new TitleBar(UserGuideActivity.this).initTitleBar("使用指南");
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

package com.hct.zc.activity.academy;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.activity.base.BaseActivity;
import com.hct.zc.bean.AcademyContent;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * @todo 掌财学院常见问题.
 * @time 2014年5月4日 下午2:08:53
 * @author jie.liu
 */
public class AcademyFAQActivity extends BaseActivity {

	private AcademyContent mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.academy_fqa_activity);
		mContent = (AcademyContent) getIntent().getExtras().get(
				"academyContent");
		initViews();
	}

	private void initViews() {
		initTitlebar();
		TextView questionTV = (TextView) findViewById(R.id.question_tv);
		questionTV.setText(mContent.getTitle());
		WebView contentWV = (WebView) findViewById(R.id.content_wv);
		int grayNormal = getResources().getColor(R.color.gray_nomal);
		contentWV.setBackgroundColor(grayNormal);
		contentWV.getSettings().setDefaultTextEncodingName("UTF-8");
		contentWV.loadDataWithBaseURL(null, mContent.getContext(), "text/html",
				"utf-8", null);
	}

	private void initTitlebar() {
		new TitleBar(AcademyFAQActivity.this).initTitleBar("信托常见问题");
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

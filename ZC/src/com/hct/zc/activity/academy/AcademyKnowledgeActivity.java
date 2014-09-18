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
 * @todo 掌财学院知识.
 * @time 2014年5月4日 下午2:09:13
 * @author jie.liu
 */
public class AcademyKnowledgeActivity extends BaseActivity {

	private AcademyContent mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.academy_knowledge_activity);
		mContent = (AcademyContent) getIntent().getExtras().get(
				"academyContent");
		initViews();
	}

	private void initViews() {
		initTitlebar();
		TextView titleTV = (TextView) findViewById(R.id.question_tv);
		titleTV.setText(mContent.getTitle());
		WebView contentWV = (WebView) findViewById(R.id.content_wv);
		contentWV.getSettings().setDefaultTextEncodingName("UTF-8");
		int grayNormal = getResources().getColor(R.color.gray_nomal);
		contentWV.setBackgroundColor(grayNormal);
		contentWV.loadDataWithBaseURL(null, mContent.getContext(), "text/html",
				"utf-8", null);
	}

	private void initTitlebar() {
		new TitleBar(AcademyKnowledgeActivity.this).initTitleBar("掌财知识");
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

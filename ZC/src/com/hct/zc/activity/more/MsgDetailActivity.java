package com.hct.zc.activity.more;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;

/**
 * 
 * @todo 我的消息详情页
 * @time 2014-5-22 上午10:39:41
 * @author liuzenglong163@gmail.com
 */
public class MsgDetailActivity extends BaseHttpActivity {
	private String title, msg_id, content, date;
	private TextView content_txt, mTimeTV, mTitleTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getIntent().getStringExtra("title");
		msg_id = getIntent().getStringExtra("msg_id");
		content = getIntent().getStringExtra("content");
		date = getIntent().getStringExtra("date");

		setContentView(R.layout.msg_detail_layout);
		new TitleBar(mActivity).initTitleBar("消息详情");
		mTitleTV = (TextView) findViewById(R.id.tv_msg_title);
		mTimeTV = (TextView) findViewById(R.id.tv_msg_time);
		content_txt = (TextView) findViewById(R.id.content_txt);
		mTitleTV.setText(title);
		mTimeTV.setText(date);
		content_txt.setText(content);
		changeUnRead();
	}

	/**
	 * 设置已读
	 */
	private void changeUnRead() {
		HttpRequest.doSetMsgRead(mActivity,
				String.valueOf(mUserInfo.getUserId()), msg_id, this);
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
		String returnCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(returnCode)) {
		} else if (HttpResult.ARG_ERROR.equals(returnCode)
				|| HttpResult.SYS_ERROR.equals(returnCode)) {
		} else {
		}
	}
}

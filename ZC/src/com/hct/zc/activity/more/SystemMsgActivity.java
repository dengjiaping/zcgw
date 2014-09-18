package com.hct.zc.activity.more;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.adapter.SysMsgAdapter;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.MsgBean;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.SysMsgResult;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.hct.zc.widget.XListView;
import com.hct.zc.widget.XListView.IXListViewListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 系统消息
 * @time 2014-5-22 上午10:12:26
 * @author liuzenglong163@gmail.com
 */
public class SystemMsgActivity extends BaseHttpActivity implements
		IXListViewListener {
	private static final int PAGE_SIZE = 15; // 每页消息次数
	private int current_page = 1;
	private XListView xListView;
	private SysMsgAdapter adapter;
	private final List<MsgBean> msg_list = new ArrayList<MsgBean>();
	private boolean isRefresh = true; // 判断是不是刷新操作
	private boolean no_load_more = false; // 禁止加载更多操作了

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_msg_activity);
		initViews();
		doRequestMsg();
	}

	private void initViews() {
		new TitleBar(SystemMsgActivity.this).initTitleBar("消息中心");
		xListView = (XListView) findViewById(R.id.system_msg_lv);
		xListView.setPullLoadEnable(false);
		xListView.setPullRefreshEnable(true);
		xListView.setXListViewListener(this);
		xListView.setOnItemClickListener(listener);
		adapter = new SysMsgAdapter(msg_list, mActivity);
		xListView.setAdapter(adapter);
	}

	OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			Intent intent = new Intent(mActivity, MsgDetailActivity.class);
			final MsgBean msgBean = msg_list.get(position - 1);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("title", msgBean.title);
			intent.putExtra("msg_id", msgBean.msgid);
			intent.putExtra("content", msgBean.context);
			intent.putExtra("date", msgBean.date);
			startActivity(intent);
			msgBean.isread = MsgBean.HAS_READ;
			adapter.notifyDataSetChanged();
		}
	};

	/**
	 * 加载我的消息
	 */
	private void doRequestMsg() {
		if (ZCApplication.getInstance().doJugdgeLogin(mActivity))
			HttpRequest.doRequestSysMsg(mActivity, mUserInfo.getUserId(),
					String.valueOf(current_page), String.valueOf(PAGE_SIZE),
					this);
	}

	@Override
	public void onRefresh() {
		isRefresh = true;
		doRequestMsg();
	}

	@Override
	public void onLoadMore() {
		isRefresh = false;
		if (!no_load_more) {
			doRequestMsg();
		} else {
			onLoad();
			Toaster.showShort(mActivity, "没有更多数据");
		}
	}

	private void onLoad() {
		xListView.stopRefresh();
		xListView.stopLoadMore();
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
		MobclickAgent.onPause(this);
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
		SysMsgResult r = gson.fromJson(result, SysMsgResult.class);
		String returnCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(returnCode)) {
			if (isRefresh) {
				current_page = 1;
				no_load_more = false;
				msg_list.clear();
				msg_list.addAll(r.getMyMsgs());
			} else {
				current_page++;
				no_load_more = r.getMyMsgs().size() < PAGE_SIZE ? true : false; // 如果获取的条数小于每页的，则表示数据已经全部获取完了，不能再获取更多数据
				msg_list.addAll(r.getMyMsgs());
			}
			adapter.notifyDataSetChanged();
		} else if (HttpResult.ARG_ERROR.equals(returnCode)
				|| HttpResult.SYS_ERROR.equals(returnCode)) {
			Toaster.showShort(mActivity, "更新失败，请重试");
		} else {
			Toaster.showShort(mActivity, r.getResult().getErrormsg());
		}
		onLoad();
	}

	// @Override
	// public void onHttpResponse(String path, int response, String result) {
	// LoadingProgress.getInstance().dismiss();
	// if (response == HttpHelper.RESPONSE_SUCCESS) {
	// Gson gson = new Gson();
	// SysMsgResult r = gson.fromJson(result, SysMsgResult.class);
	// String returnCode = r.getResult().getErrorcode();
	// if (HttpResult.SUCCESS.equals(returnCode)) {
	// if (isRefresh) {
	// current_page = 1;
	// no_load_more = false;
	// msg_list.clear();
	// msg_list.addAll(r.getMyMsgs());
	// } else {
	// current_page++;
	// no_load_more = r.getMyMsgs().size() < PAGE_SIZE ? true
	// : false; // 如果获取的条数小于每页的，则表示数据已经全部获取完了，不能再获取更多数据
	// msg_list.addAll(r.getMyMsgs());
	// }
	// adapter.notifyDataSetChanged();
	// } else if (HttpResult.ARG_ERROR.equals(returnCode)
	// || HttpResult.SYS_ERROR.equals(returnCode)) {
	// Toaster.showShort(mActivity, "更新失败，请重试");
	// } else {
	// Toaster.showShort(mActivity, r.getResult().getErrormsg());
	// }
	// onLoad();
	// }
	// }
}

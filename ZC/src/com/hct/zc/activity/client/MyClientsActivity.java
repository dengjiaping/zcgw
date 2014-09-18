package com.hct.zc.activity.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.adapter.ClientAdapter;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.Client;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpHelper;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.MyClientsResult;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 我的客户.一种是进来查看我的客户，一种是添加感兴趣用户时，进来选择一个用户.
 * @time 2014年5月4日 下午2:14:24
 * @author jie.liu
 */
public class MyClientsActivity extends BaseHttpActivity {

	public static final int CLIENTS_NORMAL = 0;
	public static final int CLIENTS_ADDED_RETURN = 1;

	private int mClientsType;

	private String mProductId;

	private final List<Client> mItems = new ArrayList<Client>();

	private ClientAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_clients_activity);
		// 默认是 CLIENTS_NORMAL
		mClientsType = getIntent().getExtras().getInt("clientsType");
		mProductId = getIntent().getExtras().getString("productId");
		initViews();
	}

	private void initViews() {
		initTitlebar();
		ListView clientsLV = (ListView) findViewById(R.id.clients_lv);

		mAdapter = new ClientAdapter(MyClientsActivity.this, mItems);
		clientsLV.setAdapter(mAdapter);

		ItemClickListener listener = new ItemClickListener();
		clientsLV.setOnItemClickListener(listener);

		LinearLayout addClientLlyt = (LinearLayout) findViewById(R.id.add_client_llyt);
		ClickListener clickListener = new ClickListener();
		addClientLlyt.setOnClickListener(clickListener);
	}

	private void initTitlebar() {
		new TitleBar(MyClientsActivity.this).initTitleBar("我的客户");
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_client_llyt:
				Intent intent = new Intent(MyClientsActivity.this,
						AddClientActivity.class);
				intent.putExtra("addClientType", AddClientActivity.ADD_CLIENT);
				ContextUtil.pushToActivityWithLogin(MyClientsActivity.this,
						intent);
				break;
			default:
				LogUtil.w(MyClientsActivity.this, "点击了位置的位置");
			}
		}
	}

	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Client client = mItems.get(position);
			if (mClientsType == CLIENTS_NORMAL) {
				Intent intent = new Intent(MyClientsActivity.this,
						ClientEditorActivity.class);
				intent.putExtra("client", client);
				ContextUtil.pushToActivityWithLogin(MyClientsActivity.this,
						intent);
			} else {
				// 选取一位客户，返回到添加感兴趣用户页面
				Intent intent = new Intent();
				intent.putExtra("client", client);
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}

	private void initData() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			Toaster.showShort(MyClientsActivity.this, "请先登录");
			return;
		}

		// 查询我的客户列表
		String queryUrl = HttpUrl.QUERY_ALL_CLIENTS;
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", userInfo.getUserId() + "");
		// 查询对某一产品未关注的客户列表
		if (!TextUtils.isEmpty(mProductId)) {
			params.put("proid", mProductId);
			queryUrl = HttpUrl.QUERY_CLIENTS_NOT_ATTENTION_PRODUCT;
		}
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(MyClientsActivity.this);

		try {
			LoadingProgress.getInstance().show(MyClientsActivity.this);
			httpHelper.post(MyClientsActivity.this, queryUrl, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		MyClientsResult r = gson.fromJson(result, MyClientsResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			List<Client> clients = r.getCustomerInfos();
			mItems.clear();
			mItems.addAll(clients);
			mAdapter.notifyDataSetChanged();
		} else if (HttpResult.FAIL.equals(r.getResult().getErrorcode())) {
			mItems.clear();
			mAdapter.notifyDataSetChanged();
		} else {
			Toaster.showShort(MyClientsActivity.this, "获取数据失败，请重试");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName()); // 统计页面
		MobclickAgent.onResume(this);
		initData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName()); // 保证 onPageEnd
																// 在onPause
		MobclickAgent.onPause(this);
	}

}

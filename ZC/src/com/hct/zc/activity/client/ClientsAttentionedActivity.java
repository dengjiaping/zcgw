package com.hct.zc.activity.client;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.adapter.ClientAdapter;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.Client;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.MyClientsResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.umeng.analytics.MobclickAgent;

/**
 * @todo 关注产品的客户列表
 * @time 2014年5月7日 下午5:05:45
 * @author jie.liu
 */

public class ClientsAttentionedActivity extends BaseHttpActivity {

	private String mProductId;

	private int mIndexOfDeleteItem = -1;

	private TextView mTitleTV;

	private ClientAdapter mAdapter;

	private final List<Client> mItems = new ArrayList<Client>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clients_attention_activity);
		mProductId = getIntent().getExtras().getString("productId");
		initViews();
	}

	private void initViews() {
		ListView clientsLV = (ListView) findViewById(R.id.clients_lv);
		mAdapter = new ClientAdapter(ClientsAttentionedActivity.this, mItems);
		clientsLV.setAdapter(mAdapter);
		clientsLV.setOnItemLongClickListener(new ItemLongClickListener());

		mTitleTV = (TextView) findViewById(R.id.title_tv);
		Button backBtn = (Button) findViewById(R.id.back_btn);
		Button incAttenClientBtn = (Button) findViewById(R.id.right_btn);
		Button sendSmsBtn = (Button) findViewById(R.id.mul_send_sms_btn);

		ClickListener listener = new ClickListener();
		backBtn.setOnClickListener(listener);
		incAttenClientBtn.setOnClickListener(listener);
		sendSmsBtn.setOnClickListener(listener);
	}

	private class ItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final Client client = mItems.get(position);
			mIndexOfDeleteItem = position; // 记录将要删除的项的索引
			new AlertDialog.Builder(ClientsAttentionedActivity.this)
					.setTitle("温馨提示")
					.setMessage("你确定要取消该客户关注此产品么?")
					.setNegativeButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									deleteClient(client.getUserId());
								}
							})
					.setPositiveButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mIndexOfDeleteItem = -1;
								}
							}).show();
			return true;
		}
	}

	private void deleteClient(String clientId) {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			Toaster.showShort(this, "请先登录");
			return;
		}

		HttpRequest.doCancelAttention(this, clientId, mProductId, this);
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.right_btn:
				pushToAddClientActivity();
				break;
			case R.id.mul_send_sms_btn:
				pushToSendSmsActivity();
				break;
			default:
				LogUtil.w(ClientsAttentionedActivity.this, "点击了未知的按钮");
			}
		}
	}

	private void pushToAddClientActivity() {
		Intent intent = new Intent(this, AddClientActivity.class);
		intent.putExtra("addClientType",
				AddClientActivity.ADD_ATTENTION_PRODUCT_CLIENT);
		intent.putExtra("productId", mProductId);
		ContextUtil.pushToActivityWithLogin(this, intent);
	}

	private void pushToSendSmsActivity() {
		String mobile = getPhoneNums();
		if (TextUtils.isEmpty(mobile)) {
			Toaster.showShort(ClientsAttentionedActivity.this, "请添加感兴趣客户");
			return;
		}

		// TODO 群发短信回不到自己的APP
		Intent intent = new Intent();
		Uri smsUri = Uri.parse("smsto:" + mobile);
		intent.setAction(Intent.ACTION_SENDTO);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setType("vnd.android-dir/mms-sms");
		// intent.putExtra("exit_on_sent", true);
		// intent.putExtra("sms_body", "");
		intent.setData(smsUri);
		startActivity(intent);
	}

	private String getPhoneNums() {
		if (mItems == null || mItems.size() == 0) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		for (Client client : mItems) {
			sb.append(client.getPhone()).append(";");
		}

		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	private void initData() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			Toaster.showShort(ClientsAttentionedActivity.this, "请先登录");
			return;
		}

		HttpRequest.doGetClientsAtten(this, userInfo.getUserId(), mProductId,
				this);
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		super.onHttpSuccess(path, result);
		if (TextUtils.isEmpty(result)) {
			Toaster.showShort(this, "服务器出错了，请重试");
			LogUtil.e(this, "网络返回的数据为空");
			return;
		}
		dealWithHttpReturned(path, result);
	}

	private void dealWithHttpReturned(String path, String result) {
		Gson gson = new Gson();
		if (HttpUrl.CLIENTS_ATTENTION_PRODUCT.equals(path)) {
			clientsAttentionReturned(result, gson);
		} else {
			deleteClientReturned(result, gson);
		}
	}

	private void clientsAttentionReturned(String result, Gson gson) {
		MyClientsResult r = gson.fromJson(result, MyClientsResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			List<Client> clients = r.getCustomerInfos();
			mItems.clear();
			mItems.addAll(clients);
			mAdapter.notifyDataSetChanged();
			mTitleTV.setText("已有" + mItems.size() + "人关注");
		} else if (HttpResult.FAIL.equals(r.getResult().getErrorcode())) {
			mTitleTV.setText("已有0人关注");
		} else {
			Toaster.showShort(ClientsAttentionedActivity.this, "获取数据失败，请重试");
		}
	}

	private void deleteClientReturned(String result, Gson gson) {
		HttpResult r = gson.fromJson(result, HttpResult.class);
		Result rs = r.getResult();
		String responseCode = rs.getErrorcode();
		if (HttpResult.SUCCESS.equals(responseCode)) {
			if (mIndexOfDeleteItem != -1) {
				mItems.remove(mIndexOfDeleteItem);
				mAdapter.notifyDataSetChanged();
				mIndexOfDeleteItem = -1;
			}
			Toaster.showShort(this, rs.getErrormsg());
		} else if (HttpResult.ARG_ERROR.equals(responseCode)
				|| HttpResult.SYS_ERROR.equals(responseCode)) {
			Toaster.showShort(this, "取消关注失败，请重试");
		} else {
			Toaster.showShort(this, rs.getErrormsg());
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

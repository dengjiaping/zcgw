package com.hct.zc.activity.mine;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.Commission;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.CommissionResult;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 我的佣金明细.
 * @time 2014年5月4日 下午2:14:38
 * @author jie.liu
 */
public class MyCommissionActivity extends BaseHttpActivity {

	private ListView mComLV;
	private CommissionAdapter mAdapter;
	private final List<Commission> mItems = new ArrayList<Commission>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_commission_activity);
		initViews();
		initData();
	}

	private void initViews() {
		initTitlebar();
		mComLV = (ListView) findViewById(R.id.commission_lv);
		mAdapter = new CommissionAdapter();
		mComLV.setAdapter(mAdapter);
	}

	private void initTitlebar() {
		new TitleBar(MyCommissionActivity.this).initTitleBar("佣金明细");
	}

	private class CommissionAdapter extends BaseAdapter {

		private final LayoutInflater mInflater;

		public CommissionAdapter() {
			mInflater = LayoutInflater.from(MyCommissionActivity.this);
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.lvitem_commission,
						null);
				viewHolder = new ViewHolder();
				viewHolder.mTitleTV = (TextView) convertView
						.findViewById(R.id.product_title_tv);
				viewHolder.mInvestorTV = (TextView) convertView
						.findViewById(R.id.investor_tv);
				viewHolder.mMoneyTV = (TextView) convertView
						.findViewById(R.id.money_tv);
				viewHolder.mDateTV = (TextView) convertView
						.findViewById(R.id.date_tv);
				viewHolder.mComTV = (TextView) convertView
						.findViewById(R.id.commission_tv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Commission com = mItems.get(position);
			fillData(viewHolder, com);

			return convertView;
		}

		private void fillData(ViewHolder viewHolder, Commission com) {
			viewHolder.mTitleTV.setText(com.getPname());
			viewHolder.mComTV.setText("+" + com.getAmount());
			viewHolder.mDateTV.setText("结算日期:" + com.getDate());
			viewHolder.mInvestorTV.setText("投资人:" + com.getCname());
			viewHolder.mMoneyTV.setText("投资金额:" + com.getMoney() + "万");
		}

		private class ViewHolder {
			TextView mTitleTV;
			TextView mInvestorTV;
			TextView mMoneyTV;
			TextView mDateTV;
			TextView mComTV;
		}
	}

	private void initData() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			Toaster.showShort(this, "请先登录");
			return;
		}

		HttpRequest.getMyCommissionS(this, userInfo.getUserId(), this);
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
		CommissionResult r = gson.fromJson(result, CommissionResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			List<Commission> coms = r.getComms();
			mItems.addAll(coms);
			mAdapter.notifyDataSetChanged();
		} else if (HttpResult.FAIL.equals(resultCode)) {
			mItems.clear();
			mAdapter.notifyDataSetChanged();
		} else {
			Toaster.showShort(MyCommissionActivity.this, "请求出错，请重试");
		}
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
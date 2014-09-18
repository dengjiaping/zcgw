package com.hct.zc.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.Product;
import com.hct.zc.constants.Constants;
import com.hct.zc.fragment.CenterFragment.State;
import com.hct.zc.utils.PreferenceUtil;

public class ProductAdapter extends BaseAdapter {

	private final PreferenceUtil mUtil;
	private final LayoutInflater mInflater;
	private final List<Product> mItems;
	private final String FLAG_SELLING = "1";
	private State mState;

	public ProductAdapter(Activity context, List<Product> items) {
		mUtil = new PreferenceUtil(context, Constants.PREFERENCE_FILE);
		mInflater = LayoutInflater.from(context);
		mItems = items;
		mState = ZCApplication.getInstance().getState();
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lvitem_product, null);
			viewHolder = new ViewHolder();
			viewHolder.mNameTV = (TextView) convertView
					.findViewById(R.id.product_title_tv);
			viewHolder.mExpectedRevenueTV = (TextView) convertView
					.findViewById(R.id.expected_revenue_tv);
			viewHolder.mProductDeadlineTV = (TextView) convertView
					.findViewById(R.id.product_deadline_tv);
			viewHolder.mCommissionLlyt = (LinearLayout) convertView
					.findViewById(R.id.commission_llyt);
			viewHolder.mCommissionTV = (TextView) convertView
					.findViewById(R.id.commission_tv);
			viewHolder.mSellOutIV = (ImageView) convertView
					.findViewById(R.id.sell_out_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Product product = mItems.get(position);
		fillData(product, viewHolder);
		return convertView;
	}

	private void fillData(Product product, ViewHolder viewHolder) {
		viewHolder.mNameTV.setText(product.getName());
		viewHolder.mExpectedRevenueTV.setText("预期收益："
				+ product.getExpectedrevenue());
		viewHolder.mProductDeadlineTV.setText("产品期限：" + product.getPeriod());
		viewHolder.mCommissionTV.setText(product.getCommission() + "%");
		shouldShowCommi(viewHolder);
		isHot(product, viewHolder);
	}

	private void shouldShowCommi(ViewHolder viewHolder) {
		if (mState != State.LOGINED) {
			setCommissionVisibility(viewHolder, View.GONE);
			return;
		}

		boolean shouldShow = mUtil.getShouldShowCommission();
		if (shouldShow) {
			setCommissionVisibility(viewHolder, View.VISIBLE);
		} else {
			setCommissionVisibility(viewHolder, View.GONE);
		}
	}

	private void setCommissionVisibility(ViewHolder viewHolder, int visibility) {
		viewHolder.mCommissionLlyt.setVisibility(visibility);
	}

	private void isHot(Product product, ViewHolder viewHolder) {
		if (FLAG_SELLING.equals(product.getAppointstate())) {
			viewHolder.mSellOutIV.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.mSellOutIV.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		mState = ZCApplication.getInstance().getState();
		super.notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView mNameTV;
		TextView mExpectedRevenueTV;
		TextView mProductDeadlineTV;
		LinearLayout mCommissionLlyt;
		TextView mCommissionTV;
		ImageView mSellOutIV;
	}
}

package com.hct.zc.activity.product;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.adapter.ZBaseAdapter;
import com.hct.zc.bean.ComDetail;
import com.hct.zc.widget.TitleBar;

/**
 * @todo TODO
 * @time 2014年6月13日 上午10:11:27
 * @author liuzenglong163@gmail.com
 */

public class ComsDetailActivity extends BaseHttpActivity {

	private String mSettlement;

	private String mProductFullName;

	private List<ComDetail> mItems;

	private ComsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coms_detail_activity);
		getData();

		initView();
	}

	private void getData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mProductFullName = bundle.getString("productName");
			mItems = bundle.getParcelableArrayList("comList");
			mSettlement = bundle.getString("settlement");
		} else {
			mSettlement = "0";
			mProductFullName = "";
			mItems = Collections.emptyList();
		}
	}

	private void initView() {
		initTitlebar();

		TextView productName = (TextView) findViewById(R.id.product_title_tv);
		productName.setText(mProductFullName);
		ListView listView = (ListView) findViewById(R.id.coms_lv);
		mAdapter = new ComsAdapter(mItems, this);
		listView.setAdapter(mAdapter);

		TextView captionTv = (TextView) findViewById(R.id.com_caption);
		captionTv.setText(mSettlement);
	}

	private void initTitlebar() {
		new TitleBar(this).initTitleBar("佣金明细");
	}

	private class ComsAdapter extends ZBaseAdapter {

		public ComsAdapter(List<?> mList, Activity mActivity) {
			super(mList, mActivity);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.lvitem_com_detail,
						null);
				viewHolder = new ViewHolder();
				viewHolder.mPurchaseAmountTV = (TextView) convertView
						.findViewById(R.id.purchase_amount_tv);
				viewHolder.mYieldTV = (TextView) convertView
						.findViewById(R.id.yield_tv);
				viewHolder.mComTV = (TextView) convertView
						.findViewById(R.id.com_tv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			ComDetail com = mItems.get(position);
			fillData(com, viewHolder);
			return convertView;
		}

		private void fillData(ComDetail com, ViewHolder viewHolder) {
			viewHolder.mComTV.setText(com.comm + "%");
			viewHolder.mPurchaseAmountTV.setText(com.explain);
			viewHolder.mYieldTV.setText(com.estimate + "%");
		}

		private class ViewHolder {
			TextView mPurchaseAmountTV;
			TextView mYieldTV;
			TextView mComTV;
		}
	}
}

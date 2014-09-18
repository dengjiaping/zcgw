package com.hct.zc.activity.mine;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.DealRecord;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.DealRecordsResult;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.hct.zc.widget.ViewPagerCustomDuration;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 我的业务单.
 * @time 2014年5月4日 下午2:14:00
 * @author jie.liu
 */
public class MyBusinessFormActivity extends BaseHttpActivity {

	private ViewPagerCustomDuration mViewPager;
	private final List<View> mPageViews = new ArrayList<View>();
	private ImageView mGlideBarIV;
	private int mCurrentPage = 0;
	/**
	 * 把页面的宽12等分，mOffset就是1/12的宽度
	 */
	private int mOffset;
	private final List<DealRecord> mDealingList = new ArrayList<DealRecord>();
	private final List<DealRecord> mDealedList = new ArrayList<DealRecord>();;
	private final List<DealRecord> mCanceledList = new ArrayList<DealRecord>();

	private BusinessFormAdapter mDealingAdapter;
	private BusinessFormAdapter mDealedAdapter;
	private BusinessFormAdapter mCanceledAdapter;

	private ImageView mDealingEmptyIV;
	private ImageView mDealedEmptyIV;
	private ImageView mCanceledEmptyIV;

	private String mType = TYPE_DEALING;
	// 是否请求过数据了，三个列表的数据
	private final boolean[] mHaveRequested = { false, false, false };

	private final float SCROLL_FACTOR = 1.5f;
	/** 进行中 */
	private static final String TYPE_DEALING = "1";
	/** 已结算 */
	private static final String STATE_DEALED = "2";
	/** 已取消 */
	private static final String STATE_CANCELED = "3";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_business_form_activity);
		initViews();
	}

	private void initViews() {
		initTitlebar();
		initTopbar();
		initViewPager();
	}

	private void initTitlebar() {
		new TitleBar(this).initTitleBar("我的业务单");
	}

	private void initTopbar() {
		Button dealingBtn = (Button) findViewById(R.id.dealing_btn);
		Button dealedBtn = (Button) findViewById(R.id.dealed_btn);
		Button canceledBtn = (Button) findViewById(R.id.canceled_btn);
		mGlideBarIV = (ImageView) findViewById(R.id.glide_bar_iv);

		ClickListener listener = new ClickListener();
		dealingBtn.setOnClickListener(listener);
		dealedBtn.setOnClickListener(listener);
		canceledBtn.setOnClickListener(listener);

		initSlidingOffset();
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dealing_btn:
				dealingBtnClicked();
				break;
			case R.id.dealed_btn:
				dealedBtnClicked();
				break;
			case R.id.canceled_btn:
				canceledBtnClicked();
				break;
			default:
				LogUtil.w(MyBusinessFormActivity.this, "点击了未知的按钮");
			}
		}
	}

	private void dealingBtnClicked() {
		mViewPager.setCurrentItem(0);
	}

	private void dealedBtnClicked() {
		mViewPager.setCurrentItem(1);
	}

	private void canceledBtnClicked() {
		mViewPager.setCurrentItem(2);
	}

	private void initSlidingOffset() {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		mOffset = screenWidth / 12;
	}

	private void initViewPager() {
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup dealingVG = (ViewGroup) inflater.inflate(
				R.layout.pager_bus_form, null);
		ViewGroup dealedVG = (ViewGroup) inflater.inflate(
				R.layout.pager_bus_form, null);
		ViewGroup canceledVG = (ViewGroup) inflater.inflate(
				R.layout.pager_bus_form, null);

		mViewPager = (ViewPagerCustomDuration) findViewById(R.id.bus_form_vp);
		mPageViews.add(dealingVG);
		mPageViews.add(dealedVG);
		mPageViews.add(canceledVG);

		mViewPager.setCurrentItem(mCurrentPage);
		mViewPager.setAdapter(new ContactPageAdapter());
		mViewPager.setOnPageChangeListener(new ContactOnPageChangeListener());
		mViewPager.setScrollDurationFactor(SCROLL_FACTOR); // 设置切换页面时间的倍数

		initListViews(dealingVG, dealedVG, canceledVG);
	}

	private class ContactPageAdapter extends PagerAdapter {
		private static final String TAG = "ContactPageAdapter";

		public ContactPageAdapter() {
		}

		@Override
		public int getCount() {
			return mPageViews.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			LogUtil.d(TAG, "destroy item: " + position);
			container.removeViewAt(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LogUtil.d(TAG, "instantiate item: " + position);
			container.addView(mPageViews.get(position), position);
			return mPageViews.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	private class ContactOnPageChangeListener implements OnPageChangeListener {
		private static final String TAG = "ContactOnPageChangeListener";

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			LogUtil.i(TAG, "page slip to page" + arg0);

			Animation animation = instantiateAnimation(arg0);
			animation.setFillAfter(true);
			animation.setDuration(300);
			mGlideBarIV.startAnimation(animation);

			mCurrentPage = arg0;
			requestData(arg0);
		}

		private Animation instantiateAnimation(int pageIndex) {
			LogUtil.d(MyBusinessFormActivity.this, "从第" + mCurrentPage + "页滑动到"
					+ "第" + pageIndex + "页");
			Animation animation = null;
			int one = mOffset * 4;
			int two = mOffset * 8;

			switch (pageIndex) {
			case 0:
				animation = animationTo(one, two, 0);
				break;
			case 1:
				animation = animationTo(one, two, one);
				break;
			case 2:
				animation = animationTo(one, two, two);
				break;
			}

			return animation;
		}

		private Animation animationTo(int one, int two, float toXDelta) {
			Animation animation = null;
			if (mCurrentPage == 0) {
				animation = new TranslateAnimation(0, toXDelta, 0, 0);
			} else if (mCurrentPage == 1) {
				animation = new TranslateAnimation(one, toXDelta, 0, 0);
			} else if (mCurrentPage == 2) {
				animation = new TranslateAnimation(two, toXDelta, 0, 0);
			}
			return animation;
		}
	}

	private void initListViews(ViewGroup dealingVG, ViewGroup dealedVG,
			ViewGroup canceledVG) {
		mDealingEmptyIV = (ImageView) dealingVG.findViewById(R.id.empty_iv);
		mDealedEmptyIV = (ImageView) dealedVG.findViewById(R.id.empty_iv);
		mCanceledEmptyIV = (ImageView) canceledVG.findViewById(R.id.empty_iv);

		ListView dealingLV = (ListView) dealingVG
				.findViewById(R.id.bus_form_lv);
		ListView dealedLV = (ListView) dealedVG.findViewById(R.id.bus_form_lv);
		ListView canceledLV = (ListView) canceledVG
				.findViewById(R.id.bus_form_lv);

		mDealingAdapter = new BusinessFormAdapter(mDealingList);
		mDealedAdapter = new BusinessFormAdapter(mDealedList);
		mCanceledAdapter = new BusinessFormAdapter(mCanceledList);

		Resources res = getResources();
		mDealingAdapter.setStateColor(res.getColor(R.color.red_dark));
		mDealedAdapter.setStateColor(res.getColor(R.color.gray_dark));
		mCanceledAdapter.setStateColor(res.getColor(R.color.gray_dark));

		dealingLV.setAdapter(mDealingAdapter);
		dealedLV.setAdapter(mDealedAdapter);
		canceledLV.setAdapter(mCanceledAdapter);

		ItemClickListener dealingListener = new ItemClickListener(mDealingList);
		ItemClickListener dealedListener = new ItemClickListener(mDealedList);
		ItemClickListener canceledListener = new ItemClickListener(
				mCanceledList);

		dealingLV.setOnItemClickListener(dealingListener);
		dealedLV.setOnItemClickListener(dealedListener);
		canceledLV.setOnItemClickListener(canceledListener);

		requestData(0); // 请求第0页的数据
	}

	private class BusinessFormAdapter extends BaseAdapter {

		private final List<DealRecord> mItems;
		private final LayoutInflater mInflater;
		private int mStateColor;

		public BusinessFormAdapter(List<DealRecord> items) {
			mItems = items;
			mInflater = getLayoutInflater();
		}

		public void setStateColor(int color) {
			mStateColor = color;
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
				convertView = mInflater.inflate(R.layout.lvitem_deal_record,
						null);
				viewHolder = new ViewHolder();
				viewHolder.mTitleTV = (TextView) convertView
						.findViewById(R.id.product_title_tv);
				viewHolder.mStateTV = (TextView) convertView
						.findViewById(R.id.state_tv);
				viewHolder.mStateTV.setTextColor(mStateColor);
				viewHolder.mInvestorTV = (TextView) convertView
						.findViewById(R.id.investor_tv);
				viewHolder.mMoneyTV = (TextView) convertView
						.findViewById(R.id.money_tv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			DealRecord record = mItems.get(position);
			fillBackground(convertView, position);
			fillData(viewHolder, record);

			return convertView;
		}

		private void fillBackground(View convertView, int position) {
			if (position % 2 == 0) {
				convertView.setBackgroundColor(Color.WHITE);
			} else {
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		private void fillData(ViewHolder viewHolder, DealRecord record) {
			viewHolder.mTitleTV.setText(record.getPro_name());
			viewHolder.mMoneyTV.setText(record.getMoney() + "万");
			viewHolder.mInvestorTV.setText("投资人:" + record.getCus_name());
			viewHolder.mStateTV.setText(record.getState());
		}

		private class ViewHolder {
			TextView mTitleTV;
			TextView mStateTV;
			TextView mInvestorTV;
			TextView mMoneyTV;
		}
	}

	private class ItemClickListener implements OnItemClickListener {

		private final List<DealRecord> mItems;

		public ItemClickListener(List<DealRecord> items) {
			mItems = items;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			DealRecord record = mItems.get(position);
			Intent intent = new Intent(MyBusinessFormActivity.this,
					BusinessFormDetailActivity.class);
			intent.putExtra("dealRecord", record);
			LogUtil.d(MyBusinessFormActivity.this, "点击了" + position + "项");
			startActivity(intent);
		}
	}

	private void requestData(int pageIndex) {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			return;
		}

		boolean shouldRequestData = shouldRequestData(pageIndex);
		if (shouldRequestData == false) {
			return;
		}

		markRequestType(pageIndex);
		HttpRequest.getMyBusForms(this, userInfo.getUserId(), mType, this);
	}

	private boolean shouldRequestData(int pageIndex) {
		switch (pageIndex) {
		case 0:
			// 第0页数据已经请求过了
			if (mHaveRequested[0] == true) {
				return false;
			}
			break;
		case 1:
			// 第1页数据已经请求过了
			if (mHaveRequested[1] == true) {
				return false;
			}
			break;
		case 2:
			// 第2页数据已经请求过了
			if (mHaveRequested[2] == true) {
				return false;
			}
			break;
		}

		return true;
	}

	private void markRequestType(int pageIndex) {
		switch (pageIndex) {
		case 0:
			mType = TYPE_DEALING;
			break;
		case 1:
			mType = STATE_DEALED;
			break;
		case 2:
			mType = STATE_CANCELED;
			break;
		default:
			mType = TYPE_DEALING;
			LogUtil.w(this, "选中未知页");
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
		DealRecordsResult r = gson.fromJson(result, DealRecordsResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (TYPE_DEALING.equals(mType)) {
			busFormReturned(r, resultCode, mDealingList, mDealingAdapter,
					mDealingEmptyIV);
			mHaveRequested[0] = true;
		} else if (STATE_DEALED.equals(mType)) {
			busFormReturned(r, resultCode, mDealedList, mDealedAdapter,
					mDealedEmptyIV);
			mHaveRequested[1] = true;
		} else if (STATE_CANCELED.equals(mType)) {
			busFormReturned(r, resultCode, mCanceledList, mCanceledAdapter,
					mCanceledEmptyIV);
			mHaveRequested[2] = true;
		}
	}

	/**
	 * 
	 * @todo 业务单返回处理
	 * @time 2014年5月14日 下午4:11:00
	 * @author jie.liu
	 * @param result
	 * @param resultCode
	 * @param items
	 * @param adapter
	 * @return 业务单有值返回true，无值返回false
	 */
	private void busFormReturned(DealRecordsResult result, String resultCode,
			List<DealRecord> items, BusinessFormAdapter adapter,
			ImageView emptyIV) {
		if (HttpResult.SUCCESS.equals(resultCode)) {
			List<DealRecord> records = result.getIvnInfos();
			items.addAll(records);
			adapter.notifyDataSetChanged();
		} else if (HttpResult.FAIL.equals(resultCode)) {
			emptyIV.setVisibility(View.VISIBLE);
		} else {
			Toaster.showShort(this, "请求出错，请重试");
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

package com.hct.zc.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.product.ProductDetailActivity;
import com.hct.zc.adapter.ProductAdapter;
import com.hct.zc.bean.Product;
import com.hct.zc.bean.ProductOption;
import com.hct.zc.constants.BroadcastAction;
import com.hct.zc.constants.Constants;
import com.hct.zc.http.HttpHelper;
import com.hct.zc.http.HttpHelper.NetworkChecker;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.FoundsToInvestResult;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.ProductClassifyResult;
import com.hct.zc.http.result.ProductDeadlineResult;
import com.hct.zc.http.result.ProductsResult;
import com.hct.zc.http.result.ProfitsResult;
import com.hct.zc.utils.DateUtils;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.XListView;
import com.hct.zc.widget.XListView.IXListViewListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 产品列表.
 * 
 * @time 2014年5月4日 下午2:17:28
 * @author jie.liu
 */
public class ProductsListFragment extends BaseFragment implements
		IXListViewListener {

	private CheckListener mListener;

	/**
	 * PopWindow锚点
	 */
	private RelativeLayout mTitleBarRlyt;

	/**
	 * PopWindow锚点
	 */
	private LinearLayout mOptionLlyt;

	private CheckBox mProductTypeCB;
	private CheckBox mFoundsToInvestCB;
	private CheckBox mProductDeadlineCB;
	private CheckBox mProfitsCB;

	// 四种选项的数据
	private List<ProductOption> mClassify;
	private List<ProductOption> mFoundsToInvest;
	private List<ProductOption> mProductDeadline;
	private List<ProductOption> mProfits;

	private PopupWindow mPopWind;
	private View mPopWindContentView;

	/**
	 * 资金投向，产品期限，担保情况列表的父View
	 */
	private LinearLayout mProductOptionsLlyt;

	// 四种选项的列表
	private ListView mProductTypeLV;
	private ListView mFoundsToInvestLV;
	private ListView mProductDeadlineLV;
	private ListView mProfitsLV;

	// 四种选项的Adapter
	private OptionAdapter mProductTypeAdapter;
	private OptionAdapter mFoundsToInvestAdapter;
	private OptionAdapter mProductDeadlineAdapter;
	private OptionAdapter mProfitsAdapter;

	// 四种筛选条件的Id
	private String mProductTypeId;
	private String mFoundsToInvestId;
	private String mProductDeadlineId;
	private String mProfitsId;

	private int mCurrentPage = 1;
	private int mTotalPages = 0;

	private boolean mIsLoadMoring = false;
	private boolean mIsRefreshing = false;

	private final Handler mHandler = new Handler();
	private XListView mXListView;
	private ProductAdapter mProductAdapter;
	private final List<Product> mItems = new ArrayList<Product>();

	private ItemClickListener mItemClickListener;

	private ClickListener mClickListener;

	private EditText mKeywordET;
	private BroadcastReceiver mRefreshComRecevier;
	/**
	 * 产品类别，资金投向，产品期限，担保情况，默认列表请求完成情况标识.
	 */
	private final boolean[] mDataReturned = { false, false, false, false, false };
	private boolean mIsRefreshingList;

	private final int PAGE_SIZE = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.products_list_fragment,
				container, false);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName()); // 统计页面

		if (mClassify == null || mClassify.size() == 0) {
			initViews(getView());
			initData();
			registerRefreshProductListRecevier();
		}
	}

	private void initViews(View view) {
		initTitlebar(view);

		mKeywordET = (EditText) view.findViewById(R.id.keyword_et);

		initSearchbar(view);

		initOptions(view);

		initProductList(view);

	}

	private void initTitlebar(View view) {
		mTitleBarRlyt = (RelativeLayout) view.findViewById(R.id.titlebar_rlyt);
		Button backBtn = (Button) view.findViewById(R.id.back_btn);
		Button menuBtn = (Button) view.findViewById(R.id.right_btn);
		mClickListener = new ClickListener();
		backBtn.setOnClickListener(mClickListener);
		menuBtn.setOnClickListener(mClickListener);

		CheckBox productTypeCB = (CheckBox) view
				.findViewById(R.id.product_type_cb);
		if (mListener == null) {
			mListener = new CheckListener();
		}
		productTypeCB.setOnCheckedChangeListener(mListener);

	}

	private void initSearchbar(View view) {
		Button searchBtn = (Button) view.findViewById(R.id.search_btn);
		if (mClickListener == null) {
			mClickListener = new ClickListener();
		}
		searchBtn.setOnClickListener(mClickListener);

		mKeywordET.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				searchKeyword();
				return true;
			}
		});

	}

	private void initOptions(View view) {
		mOptionLlyt = (LinearLayout) view.findViewById(R.id.option_llyt);
		mProductTypeCB = (CheckBox) view.findViewById(R.id.product_type_cb);
		mFoundsToInvestCB = (CheckBox) view
				.findViewById(R.id.founds_to_invest_cb);
		mProductDeadlineCB = (CheckBox) view
				.findViewById(R.id.product_deadline_cb);
		mProfitsCB = (CheckBox) view.findViewById(R.id.profits_cb);

		if (mListener == null) {
			mListener = new CheckListener();
		}

		mProductTypeCB.setOnCheckedChangeListener(mListener);
		mFoundsToInvestCB.setOnCheckedChangeListener(mListener);
		mProductDeadlineCB.setOnCheckedChangeListener(mListener);
		mProfitsCB.setOnCheckedChangeListener(mListener);

		RelativeLayout foundsTotInvestRlyt = (RelativeLayout) view
				.findViewById(R.id.founds_to_invest_rlyt);
		RelativeLayout productDeadlineRelyt = (RelativeLayout) view
				.findViewById(R.id.product_deadline_rlyt);
		RelativeLayout profitsRlyt = (RelativeLayout) view
				.findViewById(R.id.profits_rlyt);

		foundsTotInvestRlyt.setOnClickListener(mClickListener);
		productDeadlineRelyt.setOnClickListener(mClickListener);
		profitsRlyt.setOnClickListener(mClickListener);
	}

	private void initProductList(View view) {
		mXListView = (XListView) view.findViewById(R.id.products_lv);
		mProductAdapter = new ProductAdapter(getActivity(), mItems);
		mXListView.setPullLoadEnable(true);
		mXListView.setPullRefreshEnable(true);
		mXListView.setXListViewListener(this);
		mXListView.setAdapter(mProductAdapter);

		if (mItemClickListener == null) {
			mItemClickListener = new ItemClickListener();
		}
		mXListView.setOnItemClickListener(mItemClickListener);
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.right_btn:
				showOrHideMenu();
				break;
			case R.id.search_btn:
				searchKeyword();
				break;
			case R.id.founds_to_invest_rlyt:
				mFoundsToInvestCB.performClick();
				break;
			case R.id.product_deadline_rlyt:
				mProductDeadlineCB.performClick();
				break;
			case R.id.profits_rlyt:
				mProfitsCB.performClick();
				break;
			default:
				if (mPopWind != null && mPopWind.isShowing()) {
					mPopWind.dismiss();
				}
				break;
			}
		}
	}

	private void showOrHideMenu() {
		if (getActivity() instanceof SlidingFragmentActivity) {
			SlidingFragmentActivity sfa = (SlidingFragmentActivity) getActivity();
			sfa.toggle();
		}
	}

	private void searchKeyword() {
		String keyword = StringUtils.getText(mKeywordET);
		if (TextUtils.isEmpty(keyword)) {
			Toaster.showShort(getActivity(), "关键字不能为空");
			return;
		}

		mCurrentPage = 1; // 回复初始状态

		Map<String, String> params = new HashMap<String, String>();
		params.put("value", keyword);

		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(this);
		try {
			LoadingProgress.getInstance().show(getActivity());
			httpHelper.post(getActivity(), HttpUrl.SEARCH_PRODUCT, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class CheckListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.product_type_cb:
				productTypeClicked(isChecked);
				LogUtil.d(ProductsListFragment.this, "产品类别的CheckBox被点击:"
						+ isChecked);
				break;
			case R.id.founds_to_invest_cb:
				foundsToInvestClicked(isChecked);
				LogUtil.d(ProductsListFragment.this, "资金投向的CheckBox被点击:"
						+ isChecked);
				break;
			case R.id.product_deadline_cb:
				productDeadlineClicked(isChecked);
				LogUtil.d(ProductsListFragment.this, "产品期限的CheckBox被点击:"
						+ isChecked);
				break;
			case R.id.profits_cb:
				guaranteesClicked(isChecked);
				LogUtil.d(ProductsListFragment.this, "担保情况的CheckBox被点击:"
						+ isChecked);
				break;
			default:
				LogUtil.w(ProductsListFragment.this, "点击了未知的CheckBox");
			}
		}
	}

	/**
	 * 产品类型的CheckBox被点击.
	 * 
	 * @param isChecked
	 */
	private void productTypeClicked(boolean isChecked) {
		initPopWindow();

		initProductTypeLV();

		showOrHideProductType(isChecked);

		hidePopWindIfNeeded();
	}

	private void initProductTypeLV() {
		mProductTypeAdapter = initOptionsLV(mClassify, mProductTypeAdapter,
				mProductTypeLV);
	}

	private void showOrHideProductType(boolean isChecked) {
		if (isChecked) {
			showProductTypeLV();
			mFoundsToInvestCB.setChecked(false);
			mProductDeadlineCB.setChecked(false);
			mProfitsCB.setChecked(false);
		} else {
			mProductTypeLV.setVisibility(View.GONE);
		}
	}

	private void showProductTypeLV() {
		// 显示PopWindow
		if (!mPopWind.isShowing()) {
			mPopWind.showAsDropDown(mTitleBarRlyt);
		}
		// 显示列表
		mProductTypeLV.setVisibility(View.VISIBLE);
	}

	/**
	 * 资金投向的CheckBox被点击.
	 * 
	 * @param isChecked
	 */
	private void foundsToInvestClicked(boolean isChecked) {
		initPopWindow();

		initFoundsToInvestLV();

		showOrHideFoundsToInvestLV(isChecked);

		hideOptionLayoutIfNeeded();

		hidePopWindIfNeeded();
	}

	private void initFoundsToInvestLV() {
		mFoundsToInvestAdapter = initOptionsLV(mFoundsToInvest,
				mFoundsToInvestAdapter, mFoundsToInvestLV);
	}

	private void showOrHideFoundsToInvestLV(boolean isChecked) {
		showOrHideOptionsLV(mFoundsToInvestLV, isChecked);
	}

	/**
	 * 产品期限的CheckBox被点击.
	 * 
	 * @param isChecked
	 */
	private void productDeadlineClicked(boolean isChecked) {
		initPopWindow();

		initProductDeadlineLV();

		showOrHideProductDeadlineLV(isChecked);

		hideOptionLayoutIfNeeded();

		hidePopWindIfNeeded();
	}

	private void initProductDeadlineLV() {
		mProductDeadlineAdapter = initOptionsLV(mProductDeadline,
				mProductDeadlineAdapter, mProductDeadlineLV);
	}

	private void showOrHideProductDeadlineLV(boolean isChecked) {
		showOrHideOptionsLV(mProductDeadlineLV, isChecked);
	}

	/**
	 * 担保情况的CheckBox被点击.
	 * 
	 * @param isChecked
	 */
	private void guaranteesClicked(boolean isChecked) {
		initPopWindow();

		initGuaranteesLV();

		showOrHideGuaranteesLV(isChecked);

		hideOptionLayoutIfNeeded();

		hidePopWindIfNeeded();
	}

	private void initGuaranteesLV() {
		mProfitsAdapter = initOptionsLV(mProfits, mProfitsAdapter, mProfitsLV);
	}

	private void showOrHideGuaranteesLV(boolean isChecked) {
		showOrHideOptionsLV(mProfitsLV, isChecked);
	}

	private void initPopWindow() {
		if (mPopWind == null) {
			LogUtil.i(getActivity(), "PopWindow is null now initiale it");
			mPopWindContentView = LayoutInflater.from(getActivity()).inflate(
					R.layout.product_pop_window, null);
			mPopWind = new PopupWindow(mPopWindContentView,
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
			mPopWind.setBackgroundDrawable(new BitmapDrawable()); // TODO替换掉bitmapDrawable
			mPopWind.setOutsideTouchable(true);
			mPopWind.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					mProductTypeCB.setChecked(false);
					mFoundsToInvestCB.setChecked(false);
					mProductDeadlineCB.setChecked(false);
					mProfitsCB.setChecked(false);
				}
			});

			if (mClickListener == null) {
				mClickListener = new ClickListener();
			}
			mPopWindContentView.setOnClickListener(mClickListener);
			mProductTypeLV = (ListView) mPopWindContentView
					.findViewById(R.id.product_type_lv);
			mProductOptionsLlyt = (LinearLayout) mPopWindContentView
					.findViewById(R.id.product_option_llyt);
			mFoundsToInvestLV = (ListView) mPopWindContentView
					.findViewById(R.id.founds_to_invest_lv);
			mProductDeadlineLV = (ListView) mPopWindContentView
					.findViewById(R.id.product_deadline_lv);
			mProfitsLV = (ListView) mPopWindContentView
					.findViewById(R.id.guarantees_lv);
		}
	}

	private OptionAdapter initOptionsLV(List<ProductOption> options,
			OptionAdapter adapter, ListView optionsLV) {
		if (options == null) {
			options = new ArrayList<ProductOption>();
		}

		if (adapter == null) {
			LogUtil.d(ProductsListFragment.this, "adapter is null");
			adapter = new OptionAdapter(options);
			optionsLV.setAdapter(adapter);
			if (mItemClickListener == null) {
				LogUtil.d(ProductsListFragment.this,
						"ItemClickListener is null");
				mItemClickListener = new ItemClickListener();
			}
			optionsLV.setOnItemClickListener(mItemClickListener);
		}
		return adapter;
	}

	private void showOrHideOptionsLV(ListView listView, boolean isChecked) {
		if (isChecked) {
			showFoundsToInvestLV(listView);
			mProductTypeCB.setChecked(false);
		} else {
			listView.setVisibility(View.INVISIBLE);
		}
	}

	private void showFoundsToInvestLV(ListView listView) {
		if (!mPopWind.isShowing()) {
			mPopWind.showAsDropDown(mOptionLlyt);
		}

		listView.setVisibility(View.VISIBLE);
	}

	private void hideOptionLayoutIfNeeded() {
		if (shouldHideOptionLayout()) {
			mProductOptionsLlyt.setVisibility(View.GONE);
		} else {
			mProductOptionsLlyt.setVisibility(View.VISIBLE);
		}
	}

	private boolean shouldHideOptionLayout() {
		return (!mFoundsToInvestCB.isChecked())
				& (!mProductDeadlineCB.isChecked()) & (!mProfitsCB.isChecked());
	}

	private void hidePopWindIfNeeded() {
		if (shouldHidePopWind()) {
			if (mPopWind.isShowing()) {
				mPopWind.dismiss();
			}
		}
	}

	private boolean shouldHidePopWind() {
		return (!mProductTypeCB.isChecked()) & (!mFoundsToInvestCB.isChecked())
				& (!mProductDeadlineCB.isChecked()) & (!mProfitsCB.isChecked());
	}

	private void initData() {
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(this);
		try {
			showProgressDialog();
			httpHelper.post(getActivity(), HttpUrl.PRODUCT_CLASSIFY, null);
			httpHelper.post(getActivity(), HttpUrl.FOUNDS_TO_INVEST, null);
			httpHelper.post(getActivity(), HttpUrl.PRODUCT_DEADLINE, null);
			httpHelper.post(getActivity(), HttpUrl.PROFITS, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHttpError(String path, Exception exception) {
		treatQueryFailed();
	}

	@Override
	public void onHttpError(String path, int response) {
		treatQueryFailed();
	}

	@Override
	public void onHttpNetworkNotFound(String path) {
		super.onHttpNetworkNotFound(path);
		treatQueryFailed();
	}

	private void treatQueryFailed() {
		if (getActivity() == null) {
			return;
		}

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				dismissProgressDialog();
				fillListWithCachData();
			}
		});
	}

	private void fillListWithCachData() {
		mCurrentPage = 1;
		mTotalPages = 1;
		hanldeIfIsRefreshList();
		List<Product> cacheData = getCacheData();
		mItems.clear();
		mItems.addAll(cacheData);
		mProductAdapter.notifyDataSetChanged();
	}

	private List<Product> getCacheData() {
		String jsonData = new PreferenceUtil(getActivity(),
				Constants.PREFERENCE_FILE).getProductListCache();
		Gson gson = new Gson();
		ProductsResult r = gson.fromJson(jsonData, ProductsResult.class);
		return r.getPerInfos();

	}

	@Override
	public void onHttpSuccess(String path, String result) {
		// do nothing
	}

	@Override
	public void onHttpReturn(String path, int response, String result) {
		if (!isAdded()) {
			// 当前页面不是该fragment
			return;
		}
		if (TextUtils.isEmpty(result)) {
			Toaster.showShort(getActivity(), "服务器出错了，请重试");
			LogUtil.e(this, "网络返回的数据为空");
			return;
		}

		LogUtil.d(ProductsListFragment.this, result);
		if (response == 200) {
			Gson gson = new Gson();
			if (HttpUrl.PRODUCT_CLASSIFY.equals(path)) {
				// 产品类别
				mDataReturned[0] = true;
				productTypeReturned(result, gson);
			} else if (HttpUrl.FOUNDS_TO_INVEST.equals(path)) {
				// 资金投向
				mDataReturned[1] = true;
				foundsToInvestReturned(result, gson);
			} else if (HttpUrl.PRODUCT_DEADLINE.equals(path)) {
				// 产品期限
				mDataReturned[2] = true;
				productDeadlineReturned(result, gson);
			} else if (HttpUrl.PROFITS.equals(path)) {
				// 受益分配
				mDataReturned[3] = true;
				profitsReturned(result, gson);
			} else if (HttpUrl.QUERY_PRODUCT.equals(path)) {
				// 产品列表查询结果
				mDataReturned[4] = true;
				queryProductsReturned(result, gson);
				dismissProgressDialog();
				onLoad();
			} else if (HttpUrl.SEARCH_PRODUCT.equals(path)) {
				// 搜索结果返回
				searchProductReturened(result, gson);
				dismissProgressDialog();
			}

			if (mDataReturned[0] & mDataReturned[1] & mDataReturned[2]
					& mDataReturned[3] & mDataReturned[4]) {
				for (int i = 0; i < 5; i++) {
					mDataReturned[i] = false;
				}
			}

			if (mDataReturned[0] & mDataReturned[1] & mDataReturned[2]
					& mDataReturned[3]) {
				queryProducts();
			}
		} else {
			Toaster.showShort(getActivity(), "请求出错:" + response + "，请重试");
		}
	}

	private void productTypeReturned(String result, Gson gson) {
		ProductClassifyResult r = gson.fromJson(result,
				ProductClassifyResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			mClassify = getOptions(r.getClassify());
			addTheAllOption(mClassify);
			initProductTypeCBText();
			if (!isOptionEmpty(mClassify)) {
				mProductTypeId = mClassify.get(0).getId();
			}
		}
	}

	private void foundsToInvestReturned(String result, Gson gson) {
		FoundsToInvestResult r = gson.fromJson(result,
				FoundsToInvestResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			mFoundsToInvest = getOptions(r.getDirections());
			addTheAllOption(mFoundsToInvest);
		}
	}

	private void productDeadlineReturned(String result, Gson gson) {
		ProductDeadlineResult r = gson.fromJson(result,
				ProductDeadlineResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			mProductDeadline = getOptions(r.getPeriods());
			addTheAllOption(mProductDeadline);
		}
	}

	private void profitsReturned(String result, Gson gson) {
		ProfitsResult r = gson.fromJson(result, ProfitsResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			mProfits = getOptions(r.getProfits());
			addTheAllOption(mProfits);
		}
	}

	private void addTheAllOption(List<ProductOption> options) {
		ProductOption option = new ProductOption();
		option.setId("");
		option.setName("全部");
		options.add(0, option);
	}

	private void queryProductsReturned(String result, Gson gson) {
		ProductsResult r = gson.fromJson(result, ProductsResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			restoreCache(result);
			mCurrentPage = r.getCurrentPage();
			mTotalPages = r.getTotalPages();
			hanldeIfIsRefreshList();
			mItems.addAll(r.getPerInfos());
			mProductAdapter.notifyDataSetChanged();
		} else if (HttpResult.FAIL.equals(r.getResult().getErrorcode())) {
			mItems.clear();
			mProductAdapter.notifyDataSetChanged();
		} else {
			Toaster.showShort(getActivity(), "查询失败，请重试");
		}
	}

	private void restoreCache(String data) {
		PreferenceUtil util = new PreferenceUtil(getActivity(),
				Constants.PREFERENCE_FILE);
		util.setProductListCache(data);
	}

	private void hanldeIfIsRefreshList() {
		if (mIsRefreshingList) {
			mItems.clear();
			mIsRefreshingList = false;
		}
	}

	private void searchProductReturened(String result, Gson gson) {
		ProductsResult r = gson.fromJson(result, ProductsResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			mKeywordET.setText("");
			mItems.clear();
			mItems.addAll(r.getPerInfos());
			mProductAdapter.notifyDataSetChanged();
		} else if (HttpResult.FAIL.equals(r.getResult().getErrorcode())) {
			mKeywordET.setText("");
			mItems.clear();
			mProductAdapter.notifyDataSetChanged();
		} else {
			Toaster.showShort(getActivity(), "查询失败，请重试");
		}
	}

	private boolean returnSuccess(HttpResult result) {
		return HttpResult.SUCCESS.equals(result.getResult().getErrorcode());
	}

	private List<ProductOption> getOptions(List<ProductOption> options) {
		if (options == null) {
			return new ArrayList<ProductOption>();
		}

		return options;
	}

	private void initProductTypeCBText() {
		if (mClassify.size() > 0) {
			mProductTypeCB.setText(mClassify.get(0).getName());
		}
	}

	private class OptionAdapter extends BaseAdapter {

		private final List<ProductOption> mItems;

		private int mCheckedItem;

		private final int mTextCheckedColor;
		private final int mTextNormalColor;

		private final Drawable mLVItemChecked;

		public OptionAdapter(List<ProductOption> options) {
			mItems = options;
			mTextCheckedColor = getResources().getColor(R.color.red_dark);
			mTextNormalColor = getResources().getColor(R.color.gray_dark);
			mLVItemChecked = getResources().getDrawable(
					R.drawable.bg_lvitem_checked);
			// / 这一步必须要做,否则不会显示.
			mLVItemChecked.setBounds(0, 0, mLVItemChecked.getMinimumWidth(),
					mLVItemChecked.getMinimumHeight());
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
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				convertView = inflater.inflate(R.layout.lvitem_popwindow, null);
				viewHolder = new ViewHolder();
				viewHolder.mOptionTV = (TextView) convertView
						.findViewById(R.id.option_tv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			fillData(position, viewHolder);
			return convertView;
		}

		private void fillData(int position, ViewHolder viewHolder) {
			ProductOption option = mItems.get(position);
			renderItem(position, viewHolder);
			viewHolder.mOptionTV.setText(option.getName());
		}

		private void renderItem(int position, ViewHolder viewHolder) {
			if (mCheckedItem == position) {
				viewHolder.mOptionTV.setTextColor(mTextCheckedColor);
				viewHolder.mOptionTV.setCompoundDrawables(null, null,
						mLVItemChecked, null);
			} else {
				viewHolder.mOptionTV.setTextColor(mTextNormalColor);
				viewHolder.mOptionTV.setCompoundDrawables(null, null, null,
						null);
			}
		}

		public void setCheckedItem(int index) {
			mCheckedItem = index;
		}

		private class ViewHolder {
			TextView mOptionTV;
		}
	}

	private class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (!new NetworkChecker().isNetworkAvailable(getActivity())) {
				if (parent.getId() == R.id.products_lv) {
					Toaster.showShort(getActivity(), "无网络情况下，无法查看详情");
				}
				return;
			}

			switch (parent.getId()) {
			case R.id.product_type_lv:
				LogUtil.d(ProductsListFragment.this, "产品类别列表项:" + position
						+ "   被点击  currentPage:" + mCurrentPage);
				mProductTypeId = optionItemClicked(mProductTypeAdapter,
						position);
				mProductTypeCB.setText(mClassify.get(position).getName());
				queryWithOption();
				break;
			case R.id.founds_to_invest_lv:
				LogUtil.d(ProductsListFragment.this, "资金投向列表项:" + position
						+ "   被点击");
				mFoundsToInvestId = optionItemClicked(mFoundsToInvestAdapter,
						position);
				queryWithOption();
				break;
			case R.id.product_deadline_lv:
				LogUtil.d(ProductsListFragment.this, "产品期限列表项:" + position
						+ "   被点击");
				mProductDeadlineId = optionItemClicked(mProductDeadlineAdapter,
						position);
				queryWithOption();
				break;
			case R.id.guarantees_lv:
				LogUtil.d(ProductsListFragment.this, "担保情况列表项:" + position
						+ "   被点击");
				mProfitsId = optionItemClicked(mProfitsAdapter, position);
				queryWithOption();
				break;
			case R.id.products_lv:
				LogUtil.d(ProductsListFragment.this, "产品列表项:" + position
						+ "   被点击");
				productItemClicked(position);
				break;
			default:
				LogUtil.w(getActivity(), "点击了未知的列表项");
			}
		}

		private void queryWithOption() {
			mItems.clear();
			mProductAdapter.notifyDataSetChanged();
			showProgressDialog();
			mCurrentPage = 1;
			queryProducts();
		}
	}

	private String optionItemClicked(OptionAdapter adapter, int position) {
		adapter.setCheckedItem(position);
		adapter.notifyDataSetChanged();

		if (mPopWind.isShowing()) {
			mPopWind.dismiss();
		}

		ProductOption option = (ProductOption) adapter.getItem(position);
		return option.getId();
	}

	private void productItemClicked(int position) {
		Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
		intent.putExtra("product", mItems.get(position - 1));
		startActivity(intent);
	}

	private void queryProducts() {
		if (isOptionEmpty(mClassify) || isOptionEmpty(mFoundsToInvest)
				|| isOptionEmpty(mProductDeadline) || isOptionEmpty(mProfits)) {
			LogUtil.w(getActivity(), "查询条件不完善");
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("currentPage", String.valueOf(mCurrentPage));
		params.put("pageSize", String.valueOf(PAGE_SIZE));

		if (!TextUtils.isEmpty(mProductTypeId)) {
			params.put("typeid", mProductTypeId);
		}

		if (!TextUtils.isEmpty(mFoundsToInvestId)) {
			params.put("directionid", mFoundsToInvestId);
		}

		if (!TextUtils.isEmpty(mProductDeadlineId)) {
			params.put("periodid", mProductDeadlineId);
		}

		if (!TextUtils.isEmpty(mProfitsId)) {
			params.put("profitsid", mProfitsId);
		}

		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(this);
		try {
			httpHelper.post(getActivity(), HttpUrl.QUERY_PRODUCT, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isOptionEmpty(List<ProductOption> options) {
		if (options == null || options.size() == 0) {
			return true;
		}
		return false;
	}

	private void registerRefreshProductListRecevier() {
		mRefreshComRecevier = new RefreshProductListBroadcastRecevier();
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(BroadcastAction.ACTION_REFRESH_LOGIN_STATE);
		getActivity().registerReceiver(mRefreshComRecevier, iFilter);
	}

	private class RefreshProductListBroadcastRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mProductAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onRefresh() {
		if (!new NetworkChecker().isNetworkAvailable(getActivity())) {
			onLoad();
			return;
		}

		if (mIsRefreshing == false) {
			mHandler.postDelayed(refreshRun, 2000);
			mIsRefreshing = true;
		}
	}

	private final Runnable refreshRun = new Runnable() {

		@Override
		public void run() {
			LogUtil.d(ProductsListFragment.this, "onRefresh");
			mCurrentPage = 1;
			mIsRefreshingList = true;
			queryProducts();
		}
	};

	@Override
	public void onLoadMore() {
		if (!new NetworkChecker().isNetworkAvailable(getActivity())) {
			onLoad();
			return;
		}

		if (mCurrentPage >= mTotalPages) {
			mXListView.stopLoadMore();
			Toaster.showShort(getActivity(), "当前已经是最后一页了");
			return;
		}

		if (mIsLoadMoring == false) {
			mHandler.postDelayed(loadMoreRun, 2000);
			mIsLoadMoring = true;
		}
	}

	private final Runnable loadMoreRun = new Runnable() {

		@Override
		public void run() {
			mCurrentPage++;
			queryProducts();
		}
	};

	private void onLoad() {
		mIsLoadMoring = false;
		mIsRefreshing = false;
		mXListView.stopRefresh();
		mXListView.stopLoadMore();
		mXListView.setRefreshTime(DateUtils
				.convertUtilDateToStringWithTime(new Date()));
	}

	private void showProgressDialog() {
		LoadingProgress.getInstance().show(getActivity());
	}

	private void dismissProgressDialog() {
		LoadingProgress.getInstance().dismiss();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName()); // 保证 onPageEnd
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mRefreshComRecevier);
		removeRunningTask();
		super.onDestroy();
	}

	private void removeRunningTask() {
		if (mHandler != null) {
			mHandler.removeCallbacks(refreshRun);
			mHandler.removeCallbacks(loadMoreRun);
		}
	}
}
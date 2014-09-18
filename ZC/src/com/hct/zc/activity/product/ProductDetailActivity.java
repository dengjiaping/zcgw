package com.hct.zc.activity.product;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.activity.client.ClientsAttentionedActivity;
import com.hct.zc.activity.reglogin.LoginActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.Client;
import com.hct.zc.bean.ComDetail;
import com.hct.zc.bean.Product;
import com.hct.zc.bean.ProductDetailInfo;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.constants.Constants;
import com.hct.zc.fragment.CenterFragment.State;
import com.hct.zc.http.HttpHelper;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.MyClientsResult;
import com.hct.zc.http.result.ProductDetailResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.SharedUtil;
import com.hct.zc.utils.Toaster;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 
 * @todo 产品详情.
 * @time 2014年5月4日 下午2:17:16
 * @author jie.liu
 */
public class ProductDetailActivity extends BaseHttpActivity {
	// 首先在您的Activity中添加如下成员变量
	private static final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.share", RequestType.SOCIAL);

	private ProductDetailInfo mProductDetailInfo;

	private List<ComDetail> mComList;

	private Product mProduct;

	private LinearLayout mPromptLlyt;

	private TextView mCommissionTV;

	private String mProductId;

	private ClickListener mListener;

	private LinearLayout mAttentionLlyt;

	private TextView mCountTV;

	/**
	 * 标志"获取详情"与 "获取关注用户数"的处理是否处理完毕
	 */
	private final boolean[] mDetailClientsMark = { false, false };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_detail_activity);
		mProduct = (Product) getIntent().getSerializableExtra("product");
		initViews();
	}

	private void initViews() {
		mListener = new ClickListener();
		initTitlebar();

		SharedUtil.initSharedPane(this);

		mAttentionLlyt = (LinearLayout) findViewById(R.id.attention_llyt);
		mCountTV = (TextView) findViewById(R.id.count_tv);
		mPromptLlyt = (LinearLayout) findViewById(R.id.prompt_llyt);
		mCommissionTV = (TextView) findViewById(R.id.commission_tv);
		TextView reserveTV = (TextView) findViewById(R.id.reserve_tv);
		Button shareBtn = (Button) findViewById(R.id.share_btn);

		mPromptLlyt.setOnClickListener(mListener);
		mAttentionLlyt.setOnClickListener(mListener);
		reserveTV.setOnClickListener(mListener);
		shareBtn.setOnClickListener(mListener);

		if (mProduct != null) {
			mProductId = mProduct.getId();
			initBottombar(mProduct.getAppointstate());
		} else {
			mProductId = getIntent().getExtras().getString("productId");
		}

		fillDetailShow();
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.attention_llyt:
				pushToClientList();
				break;
			case R.id.prompt_llyt:
				pushToComsDetailActivity();
				break;
			case R.id.reserve_tv:
				pushToReserveActivity();
				break;
			case R.id.share_btn:
				shareInfoIfLogined();
				break;
			default:
				LogUtil.w(ProductDetailActivity.this, "点击了未知的按钮");
			}
		}
	}

	private void pushToClientList() {
		Intent intent = new Intent(ProductDetailActivity.this,
				ClientsAttentionedActivity.class);
		intent.putExtra("productId", mProductId);
		startActivity(intent);
	}

	private void pushToComsDetailActivity() {
		Intent intent = new Intent(ProductDetailActivity.this,
				ComsDetailActivity.class);
		if (mProductDetailInfo != null) {
			intent.putExtra("productName", mProductDetailInfo.getFullname());
			intent.putExtra("settlement", mProductDetailInfo.getSettlement());
			intent.putParcelableArrayListExtra("comList",
					(ArrayList<? extends Parcelable>) mComList);
		}
		ContextUtil.pushToActivityWithLogin(ProductDetailActivity.this, intent);
	}

	private void pushToReserveActivity() {
		Intent intent = new Intent(ProductDetailActivity.this,
				ReserveProductActivity.class);
		intent.putExtra("productId", mProductId);
		if (mProductDetailInfo != null) {
			intent.putExtra("productName", mProductDetailInfo.getFullname());
			intent.putExtra("minInvoice", mProductDetailInfo.getMin_invoice());
			intent.putExtra("maxInvoice", mProductDetailInfo.getMaxappoint());
		} else {
			intent.putExtra("productName", "");
			intent.putExtra("minInvoice", "0");
			intent.putExtra("maxInvoice", "10000");
		}
		ContextUtil.pushToActivityWithLogin(ProductDetailActivity.this, intent);
	}

	private void shareInfoIfLogined() {
		if (mProductDetailInfo == null) {
			Toaster.showShort(ProductDetailActivity.this, "获取产品详情失败，请稍后重试");
			return;
		}

		if (!ZCApplication.getInstance().doJugdgeLogin(this)) {
			ContextUtil.pushToActivity(this, LoginActivity.class);
			return;
		}

		SharedUtil.openShare(ProductDetailActivity.this,
				mProductDetailInfo.getName(),
				mProductDetailInfo.getShare_explain(),
				mProductDetailInfo.sharepic);
	}

	private void initData() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", mProductId);

		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(this);
		try {
			LoadingProgress.getInstance().show(ProductDetailActivity.this);
			httpHelper.post(ProductDetailActivity.this,
					HttpUrl.QUERY_PRODUCT_DETAIL, params);
			getClientsCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getClientsCount() throws Exception {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			mAttentionLlyt.setVisibility(View.GONE);
			mDetailClientsMark[1] = true;
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("proid", mProductId);
		params.put("conid", userInfo.getUserId() + "");

		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(this);
		httpHelper.post(ProductDetailActivity.this,
				HttpUrl.CLIENTS_ATTENTION_PRODUCT, params);
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
		if (HttpUrl.QUERY_PRODUCT_DETAIL.equals(path)) {
			queryDetailReturned(result, gson);
			mDetailClientsMark[0] = true;
		} else if (HttpUrl.CLIENTS_ATTENTION_PRODUCT.equals(path)) {
			attentionCountReturned(result, gson);
			mDetailClientsMark[1] = true;
		}

		if (mDetailClientsMark[0] && mDetailClientsMark[1]) {
			LoadingProgress.getInstance().dismiss();
		}
	}

	private void queryDetailReturned(String result, Gson gson) {
		ProductDetailResult r = gson
				.fromJson(result, ProductDetailResult.class);
		Result rs = r.getResult();
		String resultCode = rs.getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			mProductDetailInfo = r.getPerInfo();
			mComList = r.getCommlist();
			initBottombar(mProductDetailInfo.getAppointstate());
			initComPrompt(mProductDetailInfo.getCommission());
		} else {
			Toaster.showShort(ProductDetailActivity.this, "获取产品详情失败，请稍后重试");
		}
	}

	private void attentionCountReturned(String result, Gson gson) {
		MyClientsResult r = gson.fromJson(result, MyClientsResult.class);
		if (HttpResult.SUCCESS.equals(r.getResult().getErrorcode())) {
			List<Client> clients = r.getCustomerInfos();
			mCountTV.setText(clients.size() + "");
		} else if (HttpResult.FAIL.equals(r.getResult().getErrorcode())) {
			mCountTV.setText("0");
		} else {
			Toaster.showShort(ProductDetailActivity.this, "获取数据失败，请重试");
		}
	}

	private void fillDetailShow() {
		String sdcardUrl = Constants.WEB_DIR + "/orderList_detail_noHead.html";
		File sdcardFile = new File(sdcardUrl);
		if (sdcardFile.exists()) {
			LogUtil.d(this, "打开详情页路径:" + "file://" + sdcardUrl);
			performFillDetailShow("file://" + sdcardUrl);
		} else {
			LogUtil.d(this, "打开详情页路径:" + HttpUrl.PREFIX
					+ "orderList_detail_noHead.html");
			performFillDetailShow(HttpUrl.PREFIX
					+ "orderList_detail_noHead.html");
		}
	}

	private void performFillDetailShow(String url) {
		WebView contentWV = (WebView) findViewById(R.id.product_detail_wv);
		contentWV.getSettings().setDefaultTextEncodingName("UTF-8");
		contentWV.getSettings().setJavaScriptEnabled(true);
		contentWV.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		contentWV.loadUrl(url + "?id=" + mProductId);
	}

	private void initTitlebar() {
		Button backBtn = (Button) findViewById(R.id.back_btn);
		TextView titleTV = (TextView) findViewById(R.id.title_tv);
		titleTV.setText("产品详情");
		backBtn.setOnClickListener(mListener);
	}

	private void initBottombar(String appointState) {
		// "1"为可预约
		if (!"1".equals(appointState)) {
			LinearLayout bottomBarLlyt = (LinearLayout) findViewById(R.id.bottom_bar_llyt);
			bottomBarLlyt.setVisibility(View.GONE);
		}
	}

	private void initComPrompt(String commission) {
		State state = ZCApplication.getInstance().getState();
		if (state == State.LOGINED) {
			mCommissionTV.setText(commission + "%");
			PreferenceUtil util = new PreferenceUtil(
					ProductDetailActivity.this, Constants.PREFERENCE_FILE);
			if (util.getShouldShowCommission()) {
				mPromptLlyt.setVisibility(View.VISIBLE);
			} else {
				mPromptLlyt.setVisibility(View.GONE);
			}
		} else {
			mPromptLlyt.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getSimpleName()); // 统计页面
		initData();
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getSimpleName()); // 保证 onPageEnd
	}
}

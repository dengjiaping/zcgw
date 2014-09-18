package com.hct.zc.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.client.AddClientActivity;
import com.hct.zc.activity.product.ComsDetailActivity;
import com.hct.zc.activity.product.ProductDetailActivity;
import com.hct.zc.activity.reglogin.LoginActivity;
import com.hct.zc.activity.reglogin.RequestAdvisorActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.ComDetail;
import com.hct.zc.bean.Product;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.constants.BroadcastAction;
import com.hct.zc.constants.Constants;
import com.hct.zc.http.HttpHelper;
import com.hct.zc.http.HttpHelper.NetworkChecker;
import com.hct.zc.http.HttpHelper.OnDownloadResponse;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.CachePageUrlResult;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.MainProductResult;
import com.hct.zc.service.UnzipFiler;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.PreferenceUtil;
import com.hct.zc.utils.SDCardUtil;
import com.hct.zc.utils.SharedUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.utils.ZipUtils.ZipError;
import com.hct.zc.utils.ZipUtils.onZipListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 首页.
 * @time 2014年5月4日 下午2:09:36
 * @author jie.liu
 */
public class CenterFragment extends BaseFragment implements OnDownloadResponse,
		onZipListener {

	private Button mAddClientBtn;
	private Button mMenuBtn;
	private Button mLoginRegShareBtn;
	private ClickListener mListener;
	private LinearLayout mPromptLlyt;
	private TextView mCommissionTV;
	private WebView mContentWV;
	private State mState = State.IDLE;
	private PreferenceUtil mUtil;
	private BroadcastReceiver mRefreshComRecevier;
	private Product mProduct;
	/*********** 佣金明细 **************/
	private List<ComDetail> mComList;
	private long mLastBackPressedTime;
	private String mLastUpdateCachePageTime;
	private boolean mHasUpdateCachePage;

	public enum State {
		IDLE, NOT_LOGINED, LOGINED
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.center_fragment, null);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("CenterFragment"); // 统计页面
		SharedUtil.initSharedPane(getActivity());
		mUtil = new PreferenceUtil(getActivity(), Constants.PREFERENCE_FILE);

		initViews();
		// requestMainProduct();

		registerRefreshProductListRecevier();

		refreshState();
		refreshUI();
	}

	private void initViews() {
		View view = getView();
		initTitleBar(view);

		mPromptLlyt = (LinearLayout) view.findViewById(R.id.prompt_llyt);
		mCommissionTV = (TextView) view.findViewById(R.id.commission_tv);
		mLoginRegShareBtn = (Button) view
				.findViewById(R.id.login_reg_share_btn);
		TextView moreProductBtn = (TextView) view
				.findViewById(R.id.more_product_tv);

		mPromptLlyt.setOnClickListener(mListener);
		mLoginRegShareBtn.setOnClickListener(mListener);
		moreProductBtn.setOnClickListener(mListener);

		initWebView();
	}

	private void initTitleBar(View view) {
		mAddClientBtn = (Button) view.findViewById(R.id.left_btn);
		mMenuBtn = (Button) view.findViewById(R.id.right_btn);

		if (mListener == null) {
			mListener = new ClickListener();
		}
		mMenuBtn.setOnClickListener(mListener);
		mAddClientBtn.setOnClickListener(mListener);
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_btn:
				pushToAddClientActivity();
				break;
			case R.id.right_btn:
				showOrHideMenu();
				break;
			case R.id.prompt_llyt:
				commissionPromptClicked();
				break;
			case R.id.login_reg_share_btn:
				loginRegOrShare();
				break;
			case R.id.more_product_tv:
				moreProduct();
				break;
			default:
				LogUtil.w(CenterFragment.this, "点击了未知按钮");
			}
		}
	}

	private void pushToAddClientActivity() {
		Intent intent = new Intent(getActivity(), AddClientActivity.class);
		intent.putExtra("addClientType", AddClientActivity.ADD_CLIENT);
		startActivity(intent);
	}

	private void showOrHideMenu() {
		if (getActivity() instanceof SlidingFragmentActivity) {
			SlidingFragmentActivity sfa = (SlidingFragmentActivity) getActivity();
			sfa.toggle();
		}
	}

	private void commissionPromptClicked() {
		if (mState == State.LOGINED) {
			Intent intent = new Intent(getActivity(), ComsDetailActivity.class);
			if (mProduct != null) {
				intent.putExtra("productName", mProduct.getFullname());
				intent.putExtra("settlement", mProduct.getSettlement());
				intent.putParcelableArrayListExtra("comList",
						(ArrayList<ComDetail>) mComList);
			}
			ContextUtil.pushToActivityWithLogin(getActivity(), intent);
		} else {
			ContextUtil.pushToActivity(getActivity(), LoginActivity.class);
		}
	}

	private void loginRegOrShare() {
		switch (mState) {
		case IDLE:
			ContextUtil.pushToActivity(getActivity(),
					RequestAdvisorActivity.class);
			break;
		case NOT_LOGINED:
			ContextUtil.pushToActivity(getActivity(), LoginActivity.class);
			break;
		case LOGINED:
			if (mProduct != null && !TextUtils.isEmpty(mProduct.shareexplain)) {
				SharedUtil.openShare(getActivity(), mProduct.getName(),
						mProduct.shareexplain, mProduct.sharepic);
			} else {
				requestMainProduct();
			}
			break;
		default:
			LogUtil.w(CenterFragment.this, "左下角按钮处于未知状态，程序有误");
		}
	}

	private void moreProduct() {
		pushToFragment(new ProductsListFragment());
	}

	private void initWebView() {
		mContentWV = (WebView) getView().findViewById(R.id.content_wv);
		mContentWV.setBackgroundColor(Color.WHITE);
		WebSettings settings = mContentWV.getSettings();
		settings.setDefaultTextEncodingName("UTF-8");
		settings.setJavaScriptEnabled(true);
		// settings.setDomStorageEnabled(true);
		// settings.setDatabaseEnabled(true);
		// String cacheDirPath = SDCardUtil.getSDPath() + "/ZCAdisor";
		// settings.setDatabasePath(cacheDirPath);
		//
		// settings.setAppCacheEnabled(true);
		// settings.setAppCacheMaxSize(Long.MAX_VALUE);
		// String appCacheDir = getActivity().getApplicationContext()
		// .getDir("cache", Context.MODE_PRIVATE).getPath();
		// settings.setAppCachePath(appCacheDir);
		// setCacheMode(settings);
		mContentWV.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				String data = getLocalData();
				data = optimizeData(data);
				LogUtil.i(this, "加载本地页面 data=" + data);
				mContentWV.loadUrl("javascript:doInit('" + data + "')");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
			}

		});
		mContentWV.setWebChromeClient(new WebChromeClient());
		mContentWV.setOnTouchListener(new OnTouchListener() {

			private boolean mMaybeClickEvent;
			private float xOriginal = 0;
			private float yOriginal = 0;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				LogUtil.i(CenterFragment.this, "检测到touch事件");
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mMaybeClickEvent = true;
					xOriginal = event.getRawX();
					yOriginal = event.getRawY();
					LogUtil.i(CenterFragment.this, "检测到ACTION_DOWN事件");
					break;
				case MotionEvent.ACTION_MOVE:
					LogUtil.i(CenterFragment.this, "检测到ACTION_MOVE事件");
					int scaledTouchSlop = ViewConfiguration.get(getActivity())
							.getScaledTouchSlop();
					float xDelta = Math.abs(xOriginal - event.getRawX());
					float yDelta = Math.abs(yOriginal - event.getRawY());
					if (xDelta >= scaledTouchSlop || yDelta >= scaledTouchSlop) {
						// 说明是滑动事件，不会是点击事件.点击事件是down->up，但是用户很容易出现move，所以允许稍微的move
						mMaybeClickEvent = false;
					}
					break;
				case MotionEvent.ACTION_UP:
					LogUtil.i(CenterFragment.this, "检测到ACTION_UP事件");
					if (mMaybeClickEvent == true) {
						mMaybeClickEvent = false;
						pushToProductDetailActivity();
						return true;
					}
					break;
				default:
					mMaybeClickEvent = false;
				}
				return false;
			}

			private void pushToProductDetailActivity() {
				if (!new NetworkChecker().isNetworkAvailable(getActivity())) {
					Toaster.showShort(getActivity(), "无网络情况下，无法查看详情");
					return;
				}

				if (mProduct == null) {
					Toaster.showShort(getActivity(), "重新获取详情中...");
					requestMainProduct();
					return;
				}

				Intent intent = new Intent(getActivity(),
						ProductDetailActivity.class);
				intent.putExtra("productId", mProduct.getId());
				startActivity(intent);
			}
		});

		loadWeb();
		// mContentWV.loadUrl(HttpUrl.PRODUCT_INDEX);
	}

	// private void setCacheMode(WebSettings settings) {
	// if (new NetworkChecker().isNetworkAvailable(getActivity())) {
	// // 从网络上获取， 网络上没有，则出现报错页面
	// settings.setCacheMode(WebSettings.LOAD_DEFAULT);
	// } else {
	// // 没网络, 只要本地有缓存，就去缓存，本地没有缓存，从网络上获取
	// settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
	// }
	// }

	private void loadWeb() {
		if (mHasUpdateCachePage == false) {
			String lastDate = mUtil.getPageCachedDate();
			HttpRequest.updateCachePage(getActivity(), lastDate, this);
			mHasUpdateCachePage = true;
		}
		LogUtil.i(this, "请求过了是否需要更新ZIP包，直接加载本地网页");
		loadLocalPage();
		requestMainProduct();
	}

	/**
	 * 
	 * @todo 如果有记住帐号，则用户成功登录过一次，则是有注册，但还未登录用户. 如果有帐号密码，则是自动登录用户.<br/>
	 *       如果有缓存{@link UserInfo} 则是已登录用户
	 * @time 2014年5月5日 下午12:02:29
	 * @author jie.liu
	 */
	private void refreshState() {
		LogUtil.i(CenterFragment.this, "在 onResume 中 refresh state");

		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo != null) {
			mState = State.LOGINED;
		} else {
			String username = mUtil.getUsername();
			if (TextUtils.isEmpty(username)) {
				mState = State.IDLE;
			} else {
				mState = State.NOT_LOGINED;
			}
		}

		ZCApplication.getInstance().storeState(mState);
	}

	private void refreshUI() {
		refreshLeftBottomBtn();
		refreshCommissionMark();
	}

	private void refreshLeftBottomBtn() {
		if (mState == State.IDLE) {
			mLoginRegShareBtn.setText("立即成为理财顾问");
		} else {
			mLoginRegShareBtn.setText("立即分享");
		}
	}

	/**
	 * @todo 更新右上脚的标志
	 * @time 2014年5月5日 下午1:54:08
	 * @author jie.liu
	 */
	private void refreshCommissionMark() {
		LogUtil.i(CenterFragment.this, "更新 右上角的标志时， STATE:" + mState.name());
		if (mState == State.LOGINED) {
			if (mUtil.getShouldShowCommission()) {
				mPromptLlyt.setVisibility(View.VISIBLE);
			} else {
				mPromptLlyt.setVisibility(View.GONE);
			}
			fillCommissionData();
		} else {
			mPromptLlyt.setVisibility(View.VISIBLE);
			mCommissionTV.setText("马上登录查看");
		}
	}

	private void fillCommissionData() {
		if (mProduct == null) {
			mCommissionTV.setText("0.00%");
		} else {
			mCommissionTV.setText(mProduct.getCommission() + "%");
		}
	}

	private void requestMainProduct() {
		mProduct = null;
		Map<String, String> params = new HashMap<String, String>();
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(this);
		try {
			httpHelper.post(getActivity(), HttpUrl.MAIN_PRODUCT, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		super.onHttpSuccess(path, result);
		if (TextUtils.isEmpty(result)) {
			Toaster.showShort(getActivity(), "服务器出错了，请重试");
			LogUtil.e(this, path + "网络返回的数据为空");
			return;
		}
		Gson gson = new Gson();
		if (HttpUrl.UPDATE_CACHE_PAGE.equals(path)) {
			updateCachePageReturned(result, gson);
		} else {
			mainProductReturned(result, gson);
		}
	}

	private void updateCachePageReturned(String result, Gson gson) {
		CachePageUrlResult r = gson.fromJson(result, CachePageUrlResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			mLastUpdateCachePageTime = mUtil.getPageCachedDate();
			if (mLastUpdateCachePageTime.compareTo(r.html.time) < 0) {
				mLastUpdateCachePageTime = r.html.time;
				String zipUrl = r.html.url;
				if (!TextUtils.isEmpty(zipUrl)) {
					downloadZipFile(zipUrl);
				}
			}
		} else if (HttpResult.FAIL.equals(resultCode)) {
			LogUtil.d(this, "请求是否需要更新网页的缓存包返回失败");
			requestMainProduct();
		} else {
			LogUtil.w(CenterFragment.this, "请求是否需要更新网页的缓存包出错");
		}
	}

	private void downloadZipFile(String zipUrl) {
		LogUtil.i(this, "开始下载文件");
		HttpHelper httpHelper = new HttpHelper();
		String targetDir = SDCardUtil.getSDPath() + File.separator
				+ Constants.ZC_ROOT_DIR;
		httpHelper.setOnDownloadResponse(this);
		httpHelper.downloadFile(getActivity(), zipUrl, targetDir);
	}

	@Override
	public void onDownloadError(String url, Exception e) {
		LogUtil.w(this, "下载网页包出错");
		loadHttpPage();
	}

	@Override
	public void onNetworkNotFound(String url) {
		LogUtil.i(this, "未检测到网络");
	}

	@Override
	public void onHttpError(String path, Exception exception) {
		super.onHttpError(path, exception);
		treatHttpError();
	}

	@Override
	public void onHttpError(String path, int response) {
		super.onHttpError(path, response);
		treatHttpError();
	}

	@Override
	public void onHttpNetworkNotFound(String path) {
		super.onHttpNetworkNotFound(path);
		LogUtil.i(this, "未检测到网络");
		treatHttpError();
	}

	private void treatHttpError() {
		Activity activity = getActivity();
		if (activity != null) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					LogUtil.e(this, "网络错误，加载本地网页");
					loadLocalPage();
				}
			});
		}
	}

	private String getLocalData() {
		StringBuilder sb = new StringBuilder();
		File file = new File(Constants.WEB_DIR + "/js/index.txt");
		if (file.exists()) {
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader(file);
				br = new BufferedReader(fr);
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeReader(fr, br);
			}

		}
		return sb.toString();
	}

	private void closeReader(FileReader fr, BufferedReader br) {
		try {
			if (br != null) {
				br.close();
			}

			if (fr != null) {
				fr.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDownloadSuccess(String url, String targetUrl) {
		LogUtil.i(this, "下载文件成功");
		UnzipFiler unZipFiler = new UnzipFiler();
		unZipFiler.setOnZipListener(this);
		int index = targetUrl.lastIndexOf(File.separator);
		String dirPath = targetUrl.substring(0, index);
		String fileName = targetUrl.substring(index + 1);
		unZipFiler.unzipFile(dirPath, fileName, dirPath);
	}

	@Override
	public void onUnzipBegin() {
		LogUtil.i(this, "开始解压文件");
	}

	@Override
	public void onUnzipError(ZipError zipError) {
		LogUtil.e(this, "解压文件出错:" + zipError.name());
		loadHttpPage();
	}

	@Override
	public void onUnzipFinished(int resultCode, File zipFile, String targetDir) {
		LogUtil.i(this, "解压文件成功");
		requestMainProduct();
		if (!TextUtils.isEmpty(mLastUpdateCachePageTime)) {
			mUtil.setPageCachedDate(mLastUpdateCachePageTime);
		} else {
			LogUtil.e(this, "网页更新时间为空");
		}
	}

	private void loadLocalPage() {
		LogUtil.i(this, "加载本地网页");
		File file = new File(Constants.WEB_DIR + "/index_native.html");
		if (!file.exists()) {
			return;
		}

		mContentWV
				.loadUrl("file://" + Constants.WEB_DIR + "/index_native.html");
	}

	private String optimizeData(String data) {
		String temp = data;
		temp = temp.replace("\\r", "@r");
		temp = temp.replace("\\n", "@n");
		return temp;
	}

	private void loadHttpPage() {
		Activity activity = getActivity();
		if (activity == null) {
			return;
		}

		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mContentWV.loadUrl(HttpUrl.PRODUCT_INDEX);
			}
		});
	}

	private void mainProductReturned(String result, Gson gson) {
		MainProductResult r = gson.fromJson(result, MainProductResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			mProduct = r.perInfo;
			mComList = r.commlist;
			refreshCommissionMark();
			writeDataToTxtAndLoadPage(result);
		} else if (HttpResult.FAIL.equals(resultCode)) {
			Toaster.showShort(getActivity(), "获取主推产品失败");
		} else {
			Toaster.showShort(getActivity(), "获取主推产品出错");
		}
	}

	private void writeDataToTxtAndLoadPage(String result) {
		File file = new File(Constants.WEB_DIR + "/js/index.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		performWriteDataToTxt(file, result);

	}

	private void performWriteDataToTxt(File file, String data) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(data);
			bw.flush();
			bw.close();
			fw.close();
			LogUtil.e(this, "获取主推产品数据返回，写到TXT文件完毕，加载本地网页");
			loadLocalPage();
		} catch (FileNotFoundException e) {
			LogUtil.e(this, "写数据到index.txt里面异常 FileNotFound");
			requestMainProduct();
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.e(this, "写数据到index.txt里面异常 IOException");
			requestMainProduct();
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
			mState = ZCApplication.getInstance().getState();
			refreshUI();
		}
	}

	public void backPressed() {
		if (getFragmentManager().getBackStackEntryCount() > 0) {
			finish();
		} else {
			if ((System.currentTimeMillis() - mLastBackPressedTime) > 2000) {
				Toaster.showShort(getActivity(), "再按一次退出程序");
				mLastBackPressedTime = System.currentTimeMillis();
			} else {
				ZCApplication.getInstance().storeUserInfo(null);
				ZCApplication.getInstance().exit();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("CenterFragment");
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mRefreshComRecevier);
		super.onDestroy();
	}

}

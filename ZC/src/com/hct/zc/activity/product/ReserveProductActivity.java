package com.hct.zc.activity.product;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.activity.client.MyClientsActivity;
import com.hct.zc.activity.mine.MyBusinessFormActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.Client;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpHelper;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.service.ContactsImporter;
import com.hct.zc.service.PhoneNumFormater;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.CustomEditText;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @todo 预约产品.
 * @time 2014年5月4日 下午2:18:00
 * @author jie.liu
 */
public class ReserveProductActivity extends BaseHttpActivity {

	private CustomEditText mNameET;
	private EditText mReserveMoneyET;

	private Client mClientImported;

	private String mProductId;
	private String mProductName;
	private String mMinMoney;
	private String mMaxMoney;

	private final int PICK_CONTACT = 0;
	private final int PICK_CLIENT = 1;
	private int mImportFromClients = PICK_CONTACT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reserve_product_activity);
		getDataFromIntent();
		initViews();
	}

	private void getDataFromIntent() {
		Bundle data = getIntent().getExtras();
		if (data != null) {
			mProductId = data.getString("productId");
			mProductName = data.getString("productName");
			mMinMoney = data.getString("minInvoice");
			mMaxMoney = data.getString("maxInvoice");

			if (TextUtils.isEmpty(mMinMoney)) {
				mMinMoney = "10"; // 默认十万
			}
			if (TextUtils.isEmpty(mMaxMoney)) {
				mMaxMoney = "10000"; // 默认一亿
			}
		}
	}

	private void initViews() {
		initTitlebar();

		TextView productNameTV = (TextView) findViewById(R.id.product_title_tv);
		showProductName(productNameTV);
		mNameET = (CustomEditText) findViewById(R.id.name_et);
		mReserveMoneyET = (EditText) findViewById(R.id.reserve_money_et);
		TextView minInvoiceTV = (TextView) findViewById(R.id.min_invoice_tv);
		showMinInvoice(minInvoiceTV);
		Button fromContactsBtn = (Button) findViewById(R.id.from_contacts_btn);
		Button fromClientsBtn = (Button) findViewById(R.id.from_clients_btn);
		Button certainBtn = (Button) findViewById(R.id.certain_btn);

		mNameET.init(R.string.name, R.string.please_input_client_name);

		ClickListener listener = new ClickListener();
		fromContactsBtn.setOnClickListener(listener);
		fromClientsBtn.setOnClickListener(listener);
		certainBtn.setOnClickListener(listener);
	}

	private void showProductName(TextView productTitleTV) {
		if (TextUtils.isEmpty(mProductName)) {
			mProductName = "你注意预约金额填写规范";
		}
		productTitleTV.setText(mProductName);
	}

	private void showMinInvoice(TextView minInvoiceTV) {
		if (TextUtils.isEmpty(mMinMoney)) {
			mMinMoney = "10";
		}

		StringBuilder sb = new StringBuilder(8);
		sb.append(mMinMoney).append("万起售");
		minInvoiceTV.setText(sb.toString());
	}

	private void initTitlebar() {
		new TitleBar(ReserveProductActivity.this).initTitleBar("填写预约信息");
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.from_contacts_btn:
				mImportFromClients = PICK_CONTACT;
				new ContactsImporter(ReserveProductActivity.this)
						.importInfoFromContacts(PICK_CONTACT);
				break;
			case R.id.from_clients_btn:
				mImportFromClients = PICK_CLIENT;
				importClientFromMyClients();
				break;
			case R.id.certain_btn:
				reserveNow();
				break;
			default:
				LogUtil.d(ReserveProductActivity.this, "点击了未知的按钮");
			}
		}
	}

	/**
	 * 
	 * @todo 从我的客户列表中导入客户
	 * @time 2014年5月19日 上午10:07:46
	 * @author jie.liu
	 */
	private void importClientFromMyClients() {
		Intent intent = new Intent(ReserveProductActivity.this,
				MyClientsActivity.class);
		intent.putExtra("clientsType", MyClientsActivity.CLIENTS_ADDED_RETURN);
		startActivityForResult(intent, PICK_CLIENT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PICK_CONTACT && resultCode == RESULT_OK
				&& data != null) {
			mClientImported = new ContactsImporter(ReserveProductActivity.this)
					.getContactInfo(data);
			formatPhoneNum();
		} else if (requestCode == PICK_CLIENT && resultCode == RESULT_OK
				&& data != null) {
			mClientImported = (Client) data.getExtras().get("client");
		}

		showClientImported();
	}

	private void formatPhoneNum() {
		String phone = mClientImported.getPhone();
		if (!TextUtils.isEmpty(phone)) {
			String phoneFormated = PhoneNumFormater.formatPhoneNum(phone);
			mClientImported.setPhone(phoneFormated);
		}
	}

	private void showClientImported() {
		if (mClientImported != null
				&& !TextUtils.isEmpty(mClientImported.getName())) {
			mNameET.setText(mClientImported.getName());
		}
	}

	private void reserveNow() {
		String name = mNameET.getText();
		String reserveMoney = StringUtils.getText(mReserveMoneyET);

		if (TextUtils.isEmpty(name)) {
			Toaster.showShort(ReserveProductActivity.this, "姓名不能为空");
			return;
		}

		if (TextUtils.isEmpty(reserveMoney)) {
			Toaster.showShort(ReserveProductActivity.this, "预约金额不能为空");
			return;
		}

		boolean isReserveMoneyRight = isReserveMoneyRight(reserveMoney);
		if (!isReserveMoneyRight) {
			return;
		}

		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			Toaster.showShort(this, "请先登录");
			return;
		}

		postReserve(userInfo.getUserId(), name, reserveMoney);

	}

	private boolean isReserveMoneyRight(String reserveMoney) {
		if (!InputFormatChecker.isReserveMoneyEligible(reserveMoney, mMinMoney,
				mMaxMoney)) {
			StringBuilder sb = new StringBuilder(30);
			sb.append("预约金额必须是").append(mMinMoney).append("万到")
					.append(mMaxMoney).append("万之间");
			Toaster.showShort(ReserveProductActivity.this, sb.toString());
			return false;
		} else {
			return true;
		}
	}

	private void postReserve(String userId, String name, String reserveMoney) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", userId);
		params.put("proid", mProductId);
		params.put("name", name);
		params.put("appmoney", StringUtils.getText(mReserveMoneyET));
		if (mClientImported != null) {
			putParamsFromClientImported(params);
			mClientImported = null;
		}

		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(ReserveProductActivity.this);
		try {
			LoadingProgress.getInstance().show(ReserveProductActivity.this,
					"正在预约...");
			httpHelper.post(ReserveProductActivity.this, HttpUrl.ADD_CLIENT,
					params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void putParamsFromClientImported(Map<String, String> params) {
		if (mImportFromClients == PICK_CLIENT) {
			params.put("cusid", mClientImported.getUserId());
		} else {
			fillInfoFromContacts(params);
		}
	}

	private void fillInfoFromContacts(Map<String, String> params) {
		if (!TextUtils.isEmpty(mClientImported.getPhone())) {
			params.put("phone", mClientImported.getPhone());
		}

		if (!TextUtils.isEmpty(mClientImported.getEmail())) {
			params.put("email", mClientImported.getEmail());
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
		HttpResult r = gson.fromJson(result, HttpResult.class);
		Result rs = r.getResult();
		String responseCode = rs.getErrorcode();
		if (HttpResult.SUCCESS.equals(responseCode)) {
			String resultPrompt = "预约成功";
			if (mUserInfo != null) {
				if (TextUtils.isEmpty(mUserInfo.cardpic0)) {
					resultPrompt += "，请尽快完善资料";
				}
			}
			Toaster.showShort(this, resultPrompt);
			finish();
			ContextUtil.pushToActivity(this, MyBusinessFormActivity.class);
		} else if (HttpResult.ARG_ERROR.equals(responseCode)
				|| HttpResult.SYS_ERROR.equals(responseCode)) {
			Toaster.showShort(this, "预约失败，请重试");
		} else {
			Toaster.showShort(this, rs.getErrormsg());
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

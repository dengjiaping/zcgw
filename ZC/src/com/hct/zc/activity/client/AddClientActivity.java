package com.hct.zc.activity.client;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.Client;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpHelper;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.service.ContactsImporter;
import com.hct.zc.service.PhoneNumFormater;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.CustomEditText;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**
 * @todo 增加客户.
 * @time 2014年5月4日 下午2:09:25
 * @author jie.liu
 */
public class AddClientActivity extends BaseHttpActivity {

	public static final int ADD_CLIENT = 0;
	public static final int ADD_ATTENTION_PRODUCT_CLIENT = 1;

	private int mAddClientType;
	private String mProductId;
	private Client mClientImported;

	private CustomEditText mNameET;
	private CustomEditText mEmailET;
	private CustomEditText mPhoneNumET;
	private CustomEditText mRemarkET;

	private final int PICK_CONTACT = 0;
	private final int PICK_CLIENT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_client_activity);
		// 默认是 ADD_CLIENT
		mAddClientType = getIntent().getExtras().getInt("addClientType");
		mProductId = getIntent().getExtras().getString("productId");
		initViews();
	}

	private void initViews() {
		initTitleBar();

		mNameET = (CustomEditText) findViewById(R.id.name_et);
		mEmailET = (CustomEditText) findViewById(R.id.email_et);
		mPhoneNumET = (CustomEditText) findViewById(R.id.phone_num_et);
		mRemarkET = (CustomEditText) findViewById(R.id.remark_et);

		mNameET.init(R.string.name, R.string.please_input_client_name);
		mEmailET.init(R.string.email, R.string.please_input_your_email);
		mEmailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
				| InputType.TYPE_CLASS_TEXT);
		mPhoneNumET
				.init(R.string.phone_num, R.string.please_input_phone_number);
		mPhoneNumET.setInputType(InputType.TYPE_CLASS_PHONE);
		mRemarkET.init(R.string.remark, R.string.please_input_remark);
		mRemarkET.setBackGround(R.drawable.bg_edit_down);

		initImportBtn();
	}

	private void initTitleBar() {
		if (ADD_CLIENT == mAddClientType) {
			new TitleBar(AddClientActivity.this)
					.initTitleBar(R.string.add_client);
		} else {
			new TitleBar(AddClientActivity.this).initTitleBar("添加感兴趣客户");
		}
	}

	/***
	 * 
	 * @todo 初始化导入按钮:"从通讯录导入"/"从我的客户导入"
	 * @time 2014年5月26日 上午9:19:03
	 * @author jie.liu
	 */
	private void initImportBtn() {
		if (ADD_CLIENT == mAddClientType) {
			Button importFromClientsBtn = (Button) findViewById(R.id.from_clients_btn);
			importFromClientsBtn.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @todo 从通讯录导入
	 * @time 2014年5月23日 下午7:35:01
	 * @author jie.liu
	 * @param view
	 */
	public void fromContactsClick(View view) {
		new ContactsImporter(this).importInfoFromContacts(PICK_CONTACT);
	}

	/**
	 * 
	 * @todo 从我的客户导入
	 * @time 2014年5月23日 下午7:35:12
	 * @author jie.liu
	 * @param view
	 */
	public void fromClientsClick(View view) {
		importClientFromMyClients();
	}

	public void buttonClick(View view) {
		String name = getText(mNameET);
		if (TextUtils.isEmpty(name)) {
			Toaster.showShort(AddClientActivity.this, "请填写名字");
			return;
		}

		String email = getText(mEmailET);
		if (!TextUtils.isEmpty(email)) {
			if (!InputFormatChecker.isEmailEligible(email)) {
				Toaster.showShort(AddClientActivity.this, "email格式不正确");
				return;
			}
		}

		String phoneNum = getText(mPhoneNumET);
		if (TextUtils.isEmpty(phoneNum)) {
			Toaster.showShort(AddClientActivity.this, "请填写电话号码");
			return;
		}

		if (!InputFormatChecker.isPhoneNumEligible(phoneNum)) {
			Toaster.showShort(AddClientActivity.this, "电话格式不正确");
			return;
		}

		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			Toaster.showShort(this, "请先登录");
			return;
		}

		postInfo(name, email, phoneNum, userInfo);
	}

	private void postInfo(String name, String email, String phoneNum,
			UserInfo userInfo) {
		// 纯粹的添加用户
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", userInfo.getUserId() + "");
		params.put("name", name);
		params.put("phone", phoneNum);
		params.put("email", email);
		params.put("remark", getText(mRemarkET));
		// 添加感兴趣的用户才需要的参数
		if (mAddClientType == ADD_ATTENTION_PRODUCT_CLIENT) {
			params.put("proid", mProductId);
			if (mClientImported != null && mClientImported.getUserId() != null) {
				// 添加的“感兴趣的用户”是从“我的客户”中选取的一位
				params.put("cusid", mClientImported.getUserId() + "");
				mClientImported = null;
			}
		}

		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(AddClientActivity.this);
		LoadingProgress.getInstance().show(AddClientActivity.this, "正在增加...");
		try {
			httpHelper.post(AddClientActivity.this, HttpUrl.ADD_CLIENT, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getText(CustomEditText editText) {
		return editText.getText();
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
			Toaster.showShort(AddClientActivity.this, "添加成功");
			finish();
		} else if (HttpResult.ARG_ERROR.equals(responseCode)
				|| HttpResult.SYS_ERROR.equals(responseCode)) {
			Toaster.showShort(AddClientActivity.this, "添加失败，请重试");
		} else {
			Toaster.showShort(AddClientActivity.this, rs.getErrormsg());
		}
	}

	/**
	 * 
	 * @todo 从我的客户列表中导入客户
	 * @time 2014年5月19日 上午10:07:46
	 * @author jie.liu
	 */
	private void importClientFromMyClients() {
		Intent intent = new Intent(this, MyClientsActivity.class);
		intent.putExtra("clientsType", MyClientsActivity.CLIENTS_ADDED_RETURN);
		intent.putExtra("productId", mProductId);
		startActivityForResult(intent, PICK_CLIENT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
			mClientImported = new ContactsImporter(AddClientActivity.this)
					.getContactInfo(intent);
			formatPhoneNum();
		} else if (requestCode == PICK_CLIENT && resultCode == RESULT_OK) {
			mClientImported = (Client) intent.getExtras().get("client");
		}

		fillViewWithData();
	}

	private void formatPhoneNum() {
		String phone = mClientImported.getPhone();
		if (!TextUtils.isEmpty(phone)) {
			String phoneFormated = PhoneNumFormater.formatPhoneNum(phone);
			mClientImported.setPhone(phoneFormated);
		}
	}

	private void fillViewWithData() {
		if (mClientImported != null) {
			mNameET.setText(mClientImported.getName());
			mPhoneNumET.setText(mClientImported.getPhone());
			mEmailET.setText(mClientImported.getEmail());
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

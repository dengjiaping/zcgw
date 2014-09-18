package com.hct.zc.activity.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.adapter.HCTSpinnerAdapter;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.utils.Const;
import com.hct.zc.utils.DateUtils;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.utils.ZCUtils;
import com.hct.zc.widget.CstEditText;
import com.hct.zc.widget.TitleBar;
import com.hct.zc.widget.ZCDialog;

/**
 * @todo 绑定银行卡
 * @time 2014-5-10 下午3:36:51
 * @author liuzenglong163@gmail.com
 */

public class SetBankActivity extends BaseHttpActivity implements
		OnClickListener {

	private CstEditText etBankCode, etBankAddress;
	private Button btnModify;
	private boolean hasVerfiy = false; // 是否已经验证
	Spinner mSpinnerBank;
	private HCTSpinnerAdapter ibaAdapter;
	private LinearLayout ll_showLayout, ll_modifyLayout;
	private TextView tvBankName, tvBankAddress, tvBankCode, tvName,
			tvShowBankCode;
	private final List<String> banksList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_bank_layout);
		banksList.addAll(Arrays.asList(getResources().getStringArray(
				R.array.bank_select)));
		initView();
	}

	private void initView() {
		new TitleBar(mActivity).initTitleBar("设置银行卡");

		ll_showLayout = (LinearLayout) findViewById(R.id.ll_show);
		ll_modifyLayout = (LinearLayout) findViewById(R.id.ll_modify);

		tvBankName = (TextView) findViewById(R.id.tv_bank_name);
		tvBankAddress = (TextView) findViewById(R.id.tv_bank_address);
		tvBankCode = (TextView) findViewById(R.id.tv_bank_code);

		tvName = (TextView) findViewById(R.id.tv_name);

		etBankCode = (CstEditText) findViewById(R.id.et_bank_code);
		etBankCode.setInitName(R.string.setting_title_bankcode,
				R.string.setting_title_bankcode_input);
		etBankCode.setInputType(InputType.TYPE_CLASS_NUMBER);
		etBankCode.setTextChangListener(new bankCodeWatch());

		etBankAddress = (CstEditText) findViewById(R.id.et_bank_address);
		etBankAddress.setInitName(R.string.setting_title_bankname,
				R.string.setting_title_bankname_input);

		tvShowBankCode = (TextView) findViewById(R.id.tv_show_bankcode);
		tvShowBankCode.setVisibility(View.GONE);

		btnModify = (Button) findViewById(R.id.btn_modify_ok);
		btnModify.setOnClickListener(this);

		mSpinnerBank = (Spinner) findViewById(R.id.sp_bank_type);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			mSpinnerBank.setPadding(10, 0, 0, 0);
		}

		ibaAdapter = new HCTSpinnerAdapter(banksList, mActivity);
		mSpinnerBank.setAdapter(ibaAdapter);
		mSpinnerBank.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				etBankAddress.setText(banksList.get(position));
				etBankAddress.getEtContent().setSelection(
						etBankAddress.getText().length());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setSpinnerPopupStyle();
		}

		if (!TextUtils.isEmpty(mUserInfo.getBankcard())
				&& !TextUtils.isEmpty(mUserInfo.getBankname())) {// 如果都不为空，说明已经做了绑卡操作
			ll_showLayout.setVisibility(View.VISIBLE);
			ll_modifyLayout.setVisibility(View.GONE);
			hasVerfiy = false;
			btnModify.setText("解除绑定");

			String bankName = mUserInfo.getBankname().replace(
					mUserInfo.getBankname().substring(4,
							mUserInfo.getBankname().length()), "");
			String bankAddress = mUserInfo.getBankname().replace(
					mUserInfo.getBankname().substring(0, 4), "");

			tvBankName.setCompoundDrawablesWithIntrinsicBounds(
					mActivity.getResources().getDrawable(
							ZCUtils.getFundChannelDrawable(bankName)), null,
					null, null);
			tvBankName.setText(" " + bankName);
			tvBankAddress.setText(bankAddress);
			tvBankCode
					.setText(DateUtils.formatBankCode(mUserInfo.getBankcard()));

			for (int i = 0; i < banksList.size(); i++) {
				if (banksList.get(i).equals(bankName)) {
					mSpinnerBank.setSelection(i, true); // 选中来源渠道银行
					break;
				}
			}

		} else {// 没有绑卡操作
			ll_showLayout.setVisibility(View.GONE);
			ll_modifyLayout.setVisibility(View.VISIBLE);
			DateUtils.formatBankCard(etBankCode.getEtContent());
			tvName.setText(getResources().getString(
					R.string.setting_title_bankower, mUserInfo.getName()));
			hasVerfiy = true;
			btnModify.setText("绑定");
		}

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	// TODO:低于JELLY_BEAN版本的android系统无法使用此功能，需要换一种方法实现
	private void setSpinnerPopupStyle() {
		mSpinnerBank.setPopupBackgroundResource(R.drawable.channel_1_bg);
		mSpinnerBank.setDropDownVerticalOffset(5);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_modify_ok:
			if (!hasVerfiy) { // 如果没有验证则需要验证
				createTimeDialog();
			} else {
				requestModifyBank();
			}
			break;
		}
	}

	/**
	 * 
	 * @todo 修改银行卡信息
	 * @time 2014-5-12 下午6:38:21
	 * @author liuzenglong163@gmail.com
	 */
	private void requestModifyBank() {
		String bankNo = etBankCode.getText().toString();
		String bankName = etBankAddress.getText().toString();
		if (TextUtils.isEmpty(bankNo)) {
			Toaster.showShort(mActivity, "请输入银行账号");
			return;

		} else if (bankNo.length() < 15 || bankNo.length() > 28) {
			Toaster.showShort(mActivity, "银行卡位数不对");
			return;
		} else if (TextUtils.isEmpty(bankName)) {
			Toaster.showShort(mActivity, "请输入银行的支行信息");
			return;
		}

		HttpRequest.doModifyBankInfo(mActivity,
				String.valueOf(mUserInfo.getUserId()), bankNo.replace(" ", ""),
				bankName, this);
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
		if (HttpUrl.CHECK_PASSWORD.endsWith(path)) { // 验证密码
			Gson gson = new Gson();
			Result r = gson.fromJson(result, HttpResult.class).getResult();
			if (HttpResult.SUCCESS.equals(r.getErrorcode())) {
				btnModify.setText("修改");
				hasVerfiy = true;
				tvName.setText("开户名：" + mUserInfo.getName());
				DateUtils.formatBankCard(etBankCode.getEtContent());
				etBankCode.setText(mUserInfo.getBankcard());
				etBankCode.getEtContent().setSelection(
						etBankCode.getText().toString().length());
				etBankAddress.setText(mUserInfo.getBankname());

				ll_modifyLayout.setVisibility(View.VISIBLE);
				ll_showLayout.setVisibility(View.GONE);

			} else if (HttpResult.FAIL.equals(r.getErrorcode())
					|| HttpResult.ARG_ERROR.equals(r.getErrorcode())) {
				Toaster.showShort(mActivity, r.getErrormsg());
			}
		} else if (HttpUrl.UPDATE_PERSON_INFO.equals(path)) {
			Gson gson = new Gson();
			Result r = gson.fromJson(result, HttpResult.class).getResult();
			if (HttpResult.SUCCESS.equals(r.getErrorcode())) {
				Toaster.showShort(mActivity, "修改成功");
				saveBank();
				finish();
			} else// if (HttpResult.FAIL.equals(r.getErrorcode())||
					// HttpResult.ARG_ERROR.equals(r.getErrorcode()))
			{
				Toaster.showShort(mActivity, r.getErrormsg());
			}
		}
	}

	/**
	 * 
	 * @todo 时间选择器
	 * @author lzlong@zwmob.com
	 * @time 2014-3-26 下午2:09:30
	 */
	private void createTimeDialog() {

		int screenWidth = (int) (getWindowManager().getDefaultDisplay()
				.getWidth() * Const.DIALOG_LOGIN_WIDTH); // 屏幕宽
		int screenHeight = (int) (getWindowManager().getDefaultDisplay()
				.getHeight() * Const.DIALOG_LOGIN_HEIGHT); // 屏幕高

		final ZCDialog dialog = new ZCDialog(mActivity, R.style.user_dialog,
				screenWidth, screenHeight);

		final View verifyView = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_verify_code_layout, null);

		verifyView.requestFocus();

		final EditText etPwd = (EditText) verifyView.findViewById(R.id.et_pwd);

		final Button btnCancel = (Button) verifyView
				.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button btnOk = (Button) verifyView.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (TextUtils.isEmpty(etPwd.getText().toString())) {
					Toaster.showShort(mActivity, "请输入密码");
					return;
				} else {
					requestVerifyPwd(etPwd.getText().toString());
					dialog.dismiss();
				}

			}
		});

		dialog.setCancelable(true);
		dialog.showDialog(verifyView);
	}

	/**
	 * 
	 * @todo 提交密码验证信息
	 * @time 2014-5-12 上午11:17:51
	 * @author liuzenglong163@gmail.com
	 */
	private void requestVerifyPwd(String password) {
		HttpRequest
				.doVerfiyPwd(mActivity, mUserInfo.getPhone(), password, this);
	}

	/**
	 * 
	 * @todo 保存银行卡信息
	 * @time 2014-5-12 下午4:38:54
	 * @author liuzenglong163@gmail.com
	 */
	private void saveBank() {
		mUserInfo.setBankcard(etBankCode.getText().toString());
		mUserInfo.setBankname(etBankAddress.getText().toString());
	}

	class bankCodeWatch implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			/**
			 * @todo TODO
			 * @time 2014-5-20 下午4:01:33
			 * @author liuzenglong163@gmail.com
			 */

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			/**
			 * @todo TODO
			 * @time 2014-5-20 下午4:01:33
			 * @author liuzenglong163@gmail.com
			 */

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			tvShowBankCode
					.setVisibility(TextUtils.isEmpty(etBankCode.getText()) ? View.GONE
							: View.VISIBLE);
			tvShowBankCode.setText(etBankCode.getText());
		}
	}

}

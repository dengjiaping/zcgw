package com.hct.zc.activity.client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.Client;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.StringUtils;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;

/**
 * @todo 编辑客户资料，或者删除客户.
 * @time 2014年5月7日 上午10:50:23
 * @author jie.liu
 */

public class ClientEditorActivity extends BaseHttpActivity {

	private Client mClient;

	private EditText mNameET;

	private EditText mPhoneET;

	private EditText mEmailET;

	private EditText mRemarkET;

	private Button mEditBtn;

	private static final int TEXT_COLOR_ENABLE = Color.BLACK;
	private int TEXT_COLOR_DISABLE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_editor_activity);
		TEXT_COLOR_DISABLE = getResources().getColor(R.color.gray_light);
		mClient = (Client) getIntent().getExtras().get("client");
		initViews();
	}

	private void initViews() {
		initTitlebar();
		mNameET = (EditText) findViewById(R.id.name_et);
		mPhoneET = (EditText) findViewById(R.id.phone_et);
		mEmailET = (EditText) findViewById(R.id.email_et);
		mRemarkET = (EditText) findViewById(R.id.remark_et);
		mNameET.setText(mClient.getName());
		mPhoneET.setText(mClient.getPhone());
		mEmailET.setText(mClient.getEmail());
		mRemarkET.setText(mClient.getRemark());

		Button deleteBtn = (Button) findViewById(R.id.delete_btn);
		mEditBtn = (Button) findViewById(R.id.edit_btn);
		ClickListener listener = new ClickListener();
		deleteBtn.setOnClickListener(listener);
		mEditBtn.setOnClickListener(listener);
	}

	private void initTitlebar() {
		new TitleBar(ClientEditorActivity.this).initTitleBar("编辑客户资料");
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.delete_btn:
				handleDeleteClient();
				break;
			case R.id.edit_btn:
				updateClient();
				break;
			default:
			}
		}
	}

	private void handleDeleteClient() {
		new AlertDialog.Builder(ClientEditorActivity.this).setTitle("温馨提示")
				.setMessage("您确定要删除此客户么?")
				.setNegativeButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						performDeleteClient();
					}
				}).setPositiveButton("取消", null).show();
	}

	private void performDeleteClient() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			Toaster.showShort(ClientEditorActivity.this, "请先登录");
			return;
		}

		HttpRequest.doDelClient(this, mClient.getUserId() + "",
				userInfo.getUserId() + "", this);
	}

	private void updateClient() {
		if (!mNameET.isEnabled()) {
			mEditBtn.setText("保存");
			setEditable(true);
			return;
		}

		String name = StringUtils.getText(mNameET);
		if (TextUtils.isEmpty(name)) {
			Toaster.showShort(this, "请填写名字");
			return;
		}

		String phone = StringUtils.getText(mPhoneET);
		if (TextUtils.isEmpty(phone)) {
			Toaster.showShort(this, "请填写电话号码");
			return;
		}

		String email = StringUtils.getText(mEmailET);
		String remark = StringUtils.getText(mRemarkET);
		if (name.equals(mClient.getName()) && phone.equals(mClient.getPhone())
				&& email.equals(mClient.getEmail())
				&& remark.equals(mClient.getRemark())) {
			Toaster.showShort(this, "资料未更改");
			mEditBtn.setText("编辑");
			setEditable(false);
			return;
		}

		performUpdateClient();
	}

	private void performUpdateClient() {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			Toaster.showShort(ClientEditorActivity.this, "请先登录");
			return;
		}

		HttpRequest.doUpdateClient(this, mClient.getUserId(),
				StringUtils.getText(mNameET), StringUtils.getText(mPhoneET),
				StringUtils.getText(mEmailET), StringUtils.getText(mRemarkET),
				this);
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
		HttpResult r = gson.fromJson(result, HttpResult.class);
		String responseCode = r.getResult().getErrorcode();
		if (HttpUrl.DELETE_CLIENT.equals(path)) {
			deleteClientReturned(responseCode);
		} else {
			updateClientReturned(responseCode);
		}
	}

	private void deleteClientReturned(String responseCode) {
		if (HttpResult.SUCCESS.equals(responseCode)) {
			Toaster.showShort(this, "删除成功");
			finish();
		} else if (HttpResult.FAIL.equals(responseCode)) {
			Toaster.showShort(this, "删除失败");
		} else {
			Toaster.showShort(this, "删除失败，请重试");
		}
	}

	private void updateClientReturned(String responseCode) {
		if (HttpResult.SUCCESS.equals(responseCode)) {
			Toaster.showShort(this, "更新成功");
			finish();
		} else if (HttpResult.FAIL.equals(responseCode)) {
			Toaster.showShort(this, "更新失败");
			mEditBtn.setText("编辑");
			setEditable(false);
		} else {
			Toaster.showShort(this, "更新失败，请重试");
		}
	}

	private void setEditable(boolean b) {
		mNameET.setEnabled(b);
		mPhoneET.setEnabled(b);
		mEmailET.setEnabled(b);
		mRemarkET.setEnabled(b);

		changeETColorDepOn(b);
	}

	private void changeETColorDepOn(boolean b) {
		if (b == true) {
			changeETColor(TEXT_COLOR_ENABLE);
		} else {
			changeETColor(TEXT_COLOR_DISABLE);
		}
	}

	private void changeETColor(int color) {
		mNameET.setTextColor(color);
		mPhoneET.setTextColor(color);
		mEmailET.setTextColor(color);
		mRemarkET.setTextColor(color);
	}
}

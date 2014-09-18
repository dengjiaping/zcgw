package com.hct.zc.activity.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.adapter.AddressAdapter;
import com.hct.zc.bean.AddressBean;
import com.hct.zc.city.CityAdapter;
import com.hct.zc.city.CityUtils;
import com.hct.zc.city.MyListItem;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.AddressBeansResult;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.Const;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.utils.ZCUtils;
import com.hct.zc.widget.CstEditText;
import com.hct.zc.widget.TitleBar;
import com.hct.zc.widget.XListView;
import com.hct.zc.widget.ZCDialog;

/**
 * @todo 设置通讯地址
 * @time 2014-5-10 下午3:36:51
 * @author liuzenglong163@gmail.com
 */

public class SetAddressActivity extends BaseHttpActivity implements
		OnClickListener {

	public static final int DIALOG_ADD_ADDRESS = 100; // 添加地址
	public static final int DIALOG_UPDATE_ADDRESS = DIALOG_ADD_ADDRESS + 1; // 更新地址
	public static final int DIALOG_PROVINCE = DIALOG_UPDATE_ADDRESS + 1; // 展示省份
	public static final int DIALOG_CITY = DIALOG_PROVINCE + 1; // 展示城市
	public static final int DIALOG_COUNTRY = DIALOG_CITY + 1; // 展示县城
	private Button btnAddAddress;
	private ImageView ivNOdate;
	private XListView mListView;
	private AddressAdapter mAddressAdapter;
	private List<AddressBean> listAddressBeans;
	private AddressBean mAddressBean; // 要处理的当前对象

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_address_layout);
		initView();
		requestQueryAddress();
	}

	private void initView() {
		new TitleBar(mActivity).initTitleBar("邮寄地址");
		listAddressBeans = new ArrayList<AddressBean>();
		btnAddAddress = (Button) findViewById(R.id.btn_address);
		btnAddAddress.setOnClickListener(this);
		ivNOdate = (ImageView) findViewById(R.id.iv_nodate);
		mListView = (XListView) findViewById(R.id.xlv_address);
		mAddressAdapter = new AddressAdapter(listAddressBeans, mActivity, this);
		mListView.setAdapter(mAddressAdapter);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_address:
			showDialog(DIALOG_ADD_ADDRESS); // 显示添加地址Dialog
			break;
		case R.id.tv_address_city:
			showDialog(DIALOG_PROVINCE);
			break;
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
		dealWithHttpReturned(path, result);
	}

	private void dealWithHttpReturned(String path, String result) {
		Gson gson = new Gson();
		if (HttpUrl.REQUEST_QUERY_ADDRESS.equals(path)) {
			AddressBeansResult addressBeansResult = gson.fromJson(result,
					AddressBeansResult.class);
			if (HttpResult.SUCCESS.equals(addressBeansResult.getResult()
					.getErrorcode())) {
				updateView(addressBeansResult.getAddress());
			} else // if(HttpResult.FAIL.equals(addressBeansResult.getResult().getErrorcode()))
			{
				Toaster.showShort(mActivity, addressBeansResult.getResult()
						.getErrormsg());
			}
		} else if (HttpUrl.REQUEST_ADD_ADDRESS.equals(path)) {
			HttpResult rAdd = gson.fromJson(result, HttpResult.class);
			if (HttpResult.SUCCESS.equals(rAdd.getResult().getErrorcode())) {
				// insertDate();
				LoadingProgress.getInstance().dismiss();
				requestQueryAddress();
				Toaster.showShort(mActivity, "添加成功");
				saveAddress();
			} else // if(HttpResult.FAIL.equals(rAdd.getResult().getErrorcode()))
			{
				Toaster.showShort(mActivity, "添加失败");
			}
		} else if (HttpUrl.REQUEST_UPDATE_ADDRESS.equals(path)) {
			HttpResult rUpdate = gson.fromJson(result, HttpResult.class);
			if (HttpResult.SUCCESS.equals(rUpdate.getResult().getErrorcode())) {
				mAddressAdapter.notifyDataSetChanged();
				saveAddress();
				Toaster.showShort(mActivity, "更新成功");
				LoadingProgress.getInstance().dismiss();
				requestQueryAddress();
			} else // if(HttpResult.FAIL.equals(rUpdate.getResult().getErrorcode()))
			{
				Toaster.showShort(mActivity, "更新失败");
			}
		} else if (HttpUrl.REQUEST_DELETE_ADDRESS.equals(path)) {
			HttpResult rDelete = gson.fromJson(result, HttpResult.class);
			if (HttpResult.SUCCESS.equals(rDelete.getResult().getErrorcode())) {
				Toaster.showShort(mActivity, "删除成功");
				deleteDate(mAddressBean);
			} else // if(HttpResult.FAIL.equals(rDelete.getResult().getErrorcode()))
			{
				Toaster.showShort(mActivity, "删除失败");
			}

		}
	}

	/**
	 * 
	 * @todo 更新视图
	 * @time 2014-5-14 上午9:28:33
	 * @author liuzenglong163@gmail.com
	 * @param addressBeans
	 */
	private void updateView(List<AddressBean> addressBeans) {
		if (null != addressBeans && addressBeans.size() > 0) {
			listAddressBeans.clear();
			listAddressBeans.addAll(addressBeans);
			mAddressAdapter.notifyDataSetChanged();
			ivNOdate.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		} else {
			ivNOdate.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	// /**
	// *
	// * @todo 插入一条数据
	// * @time 2014-5-14 上午10:03:00
	// * @author liuzenglong163@gmail.com
	// * @param addressBean
	// */
	// private void insertDate(){
	// if(null!=mAddressBean){
	// listAddressBeans.add(mAddressBean);
	// mAddressAdapter.notifyDataSetChanged();
	// ivNOdate.setVisibility(View.GONE);
	// mListView.setVisibility(View.VISIBLE);
	// }
	// }

	/**
	 * 
	 * @todo 删除一条数据
	 * @time 2014-5-14 上午10:03:17
	 * @author liuzenglong163@gmail.com
	 * @param addressBean
	 */
	public void deleteDate(AddressBean addressBean) {
		if (null != addressBean) {
			listAddressBeans.remove(addressBean);
			mAddressAdapter.notifyDataSetChanged();
			if (listAddressBeans.size() == 0) {
				ivNOdate.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.GONE);
				mUserInfo.setAddress(""); // 清空地址的信息
			}
		}
	}

	/**
	 * 
	 * @todo 更新地址
	 * @time 2014-5-14 上午11:27:35
	 * @author liuzenglong163@gmail.com
	 * @param addressBean
	 */
	private View updateAddressView() {
		if (null == mAddressBean)
			return null;

		final View addAddressView = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_add_address_layout, null);

		addAddressView.requestFocus();

		final TextView titleTV = (TextView) addAddressView
				.findViewById(R.id.tv_dialog_title);
		titleTV.setText("修改通讯地址");

		tvAddressCity = (TextView) addAddressView
				.findViewById(R.id.tv_address_city);
		tvAddressCity.setText(mAddressBean.province + mAddressBean.city);
		tvAddressCity.setOnClickListener(this);

		final EditText etAddressDetail = (EditText) addAddressView
				.findViewById(R.id.tv_address_detail);
		etAddressDetail.setText(mAddressBean.street);

		final CstEditText etName = (CstEditText) addAddressView
				.findViewById(R.id.et_name);
		etName.setInitName(R.string.setting_address_name,
				R.string.setting_address_name_input);
		etName.setText(mAddressBean.name);

		final CstEditText etPhone = (CstEditText) addAddressView
				.findViewById(R.id.et_phone);
		etPhone.setInitName(R.string.setting_address_phone,
				R.string.setting_address_phone_input);
		etPhone.setText(mAddressBean.phone);
		etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
		ZCUtils.formatTelphone(etPhone.getEtContent());

		final CheckBox cbDefaultBox = (CheckBox) addAddressView
				.findViewById(R.id.ck_default);
		cbDefaultBox.setChecked(mAddressBean.state
				.equals(AddressBean.ADDRESS_DEFAULT) ? true : false);
		final Button btnCancel = (Button) addAddressView
				.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				removeDialog(DIALOG_UPDATE_ADDRESS);
			}
		});

		final Button btnOk = (Button) addAddressView.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String strAddressCity = tvAddressCity.getText().toString();
				String strAddressDetail = etAddressDetail.getText().toString();
				String strName = etName.getText().toString();
				String strPhone = etPhone.getText().toString();

				if (TextUtils.isEmpty(strAddressCity)) {
					Toaster.showShort(mActivity, "请输入省份城市");
					return;
				} else if (TextUtils.isEmpty(strAddressDetail)) {
					Toaster.showShort(mActivity, "请输入街道");
					return;

				} else if (TextUtils.isEmpty(strName)) {
					Toaster.showShort(mActivity, "请输入联系人姓名");
					return;

				} else if (TextUtils.isEmpty(strPhone)) {
					Toaster.showShort(mActivity, "请输入联系人电话");
					return;

				} else if (strPhone.length() > 13) {
					Toaster.showShort(mActivity, "联系人电话号码位数不正确");
					return;

				} else {
					String addressId = mAddressBean.id;
					mAddressBean = new AddressBean(
							addressId,
							"",
							strAddressCity,
							strAddressDetail,
							cbDefaultBox.isChecked() == true ? AddressBean.ADDRESS_DEFAULT
									: AddressBean.ADDRESS_DEFAULT_NO, strPhone
									.replace("-", ""), strName);
					requestModifyAddress(mAddressBean);
					removeDialog(DIALOG_UPDATE_ADDRESS);
				}
			}
		});

		return addAddressView;
	}

	/**
	 * @todo 展示城市
	 * @time 2014-5-17 下午4:05:45
	 * @author liuzenglong163@gmail.com
	 */
	final int LEVEL_PROVINCE = 0;
	final int LEVEL_CITY = LEVEL_PROVINCE + 1;
	final int LEVEL_COUNTRY = LEVEL_CITY + 1;
	int mAddrLevel = LEVEL_PROVINCE;
	CityUtils cUtils = new CityUtils();
	String lastCodeString;
	List<MyListItem> list = new ArrayList<MyListItem>();
	Map<Integer, String> addressMap = new HashMap<Integer, String>();
	Map<Integer, String> codeMap = new HashMap<Integer, String>();

	private View showAddressView() {

		final View showAddressView = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_address_area_layout, null);
		final TextView tvTitleTextView = (TextView) showAddressView
				.findViewById(R.id.tv_title);
		ListView lvListView = (ListView) showAddressView
				.findViewById(R.id.lv_area);
		Button btnLast = (Button) showAddressView.findViewById(R.id.btn_last);
		if (mAddrLevel == LEVEL_PROVINCE) {
			btnLast.setVisibility(View.INVISIBLE);
		} else {
			btnLast.setVisibility(View.VISIBLE);
		}
		list.clear();
		switch (mAddrLevel) {
		case LEVEL_PROVINCE:
			tvTitleTextView.setText("请选择省份");
			list.addAll(cUtils.getProvince(mActivity));
			break;
		case LEVEL_CITY:
			tvTitleTextView.setText("请选择城市");
			list.addAll(cUtils.getCity(mActivity, codeMap.get(LEVEL_PROVINCE)));
			break;
		case LEVEL_COUNTRY:
			tvTitleTextView.setText("请选择地区");
			list.addAll(cUtils.getCouty(mActivity, codeMap.get(LEVEL_CITY)));
			break;
		default:
			removeDialog(DIALOG_PROVINCE);
			removeDialog(DIALOG_CITY);
			removeDialog(DIALOG_COUNTRY);
			break;
		}

		final CityAdapter cAdapter = new CityAdapter(list, mActivity);
		lvListView.setAdapter(cAdapter);
		lvListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				final MyListItem item = list.get(position);
				addressMap.put(mAddrLevel, item.getName());
				codeMap.put(mAddrLevel, item.getPcode());
				switch (mAddrLevel) {
				case LEVEL_PROVINCE: // 省份列表被点击
					mAddrLevel++;
					removeDialog(DIALOG_PROVINCE);
					showDialog(DIALOG_CITY);
					break;
				case LEVEL_CITY:// 城市列表被点击
					mAddrLevel++;
					removeDialog(DIALOG_CITY);
					showDialog(DIALOG_COUNTRY);
					break;
				case LEVEL_COUNTRY:// 地区列表被点击
					StringBuilder sbBuilder = new StringBuilder();
					for (String value : addressMap.values()) {
						sbBuilder.append(value);
					}
					tvAddressCity.setText(sbBuilder.toString());
					mAddrLevel = LEVEL_PROVINCE;
					codeMap.clear();
					addressMap.clear();
					removeDialog(DIALOG_PROVINCE);
					removeDialog(DIALOG_CITY);
					removeDialog(DIALOG_COUNTRY);
					break;

				default:
					break;
				}
			}
		});

		btnLast.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (mAddrLevel) {
				case LEVEL_CITY:
					mAddrLevel--;
					removeDialog(DIALOG_CITY);
					showDialog(DIALOG_PROVINCE);
					break;
				case LEVEL_COUNTRY:
					mAddrLevel--;
					removeDialog(DIALOG_COUNTRY);
					showDialog(DIALOG_CITY);
					break;
				default:
					break;
				}
			}
		});

		return showAddressView;

	}

	/**
	 * 
	 * @todo 添加地址
	 * @author lzlong@zwmob.com
	 */
	TextView tvAddressCity;

	private View addAddressView() {

		final View addAddressView = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_add_address_layout, null);

		final TextView titleTV = (TextView) addAddressView
				.findViewById(R.id.tv_dialog_title);
		titleTV.setText("新增通讯地址");

		tvAddressCity = (TextView) addAddressView
				.findViewById(R.id.tv_address_city);
		tvAddressCity.setOnClickListener(this);

		final EditText etAddressDetail = (EditText) addAddressView
				.findViewById(R.id.tv_address_detail);

		final CstEditText etName = (CstEditText) addAddressView
				.findViewById(R.id.et_name);
		etName.setInitName(R.string.setting_address_name,
				R.string.setting_address_name_input);

		final CstEditText etPhone = (CstEditText) addAddressView
				.findViewById(R.id.et_phone);
		etPhone.setInitName(R.string.setting_address_phone,
				R.string.setting_address_phone_input);
		etPhone.setInputType(InputType.TYPE_CLASS_PHONE);
		ZCUtils.formatTelphone(etPhone.getEtContent());

		final CheckBox cbDefaultBox = (CheckBox) addAddressView
				.findViewById(R.id.ck_default);

		final Button btnCancel = (Button) addAddressView
				.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				removeDialog(DIALOG_ADD_ADDRESS);
			}
		});

		final Button btnOk = (Button) addAddressView.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String strAddressCity = tvAddressCity.getText().toString();
				String strAddressDetail = etAddressDetail.getText().toString();
				String strName = etName.getText().toString();
				String strPhone = etPhone.getText().toString();

				if (TextUtils.isEmpty(strAddressCity)) {
					Toaster.showShort(mActivity, "请输入省份城市");
					return;
				} else if (TextUtils.isEmpty(strAddressDetail)) {
					Toaster.showShort(mActivity, "请输入街道");
					return;
				} else if (TextUtils.isEmpty(strName)) {
					Toaster.showShort(mActivity, "请输入联系人姓名");
					return;
				} else if (TextUtils.isEmpty(strPhone)) {
					Toaster.showShort(mActivity, "请输入联系人电话");
					return;
				} else if (strPhone.length() > 13) {
					Toaster.showShort(mActivity, "联系人电话号码位数不正确");
					return;

				} else {
					mAddressBean = new AddressBean(
							String.valueOf(mUserInfo.getUserId()),
							"",
							strAddressCity,
							strAddressDetail,
							cbDefaultBox.isChecked() == true ? AddressBean.ADDRESS_DEFAULT
									: AddressBean.ADDRESS_DEFAULT_NO, strPhone
									.replace("-", ""), strName);
					requestAddAddress(mAddressBean);
					removeDialog(DIALOG_ADD_ADDRESS);
				}
			}
		});
		ZCUtils.hiddenKeybroad(etAddressDetail, mActivity);
		return addAddressView;
	}

	/**
	 * 
	 * @todo 查询所有的通信地址
	 * @time 2014-5-13 下午3:45:41
	 * @author liuzenglong163@gmail.com
	 * @param mAddressBean
	 */
	private void requestQueryAddress() {
		HttpRequest.getAllAddress(mActivity,
				String.valueOf(mUserInfo.getUserId()), "", this);
	}

	/**
	 * 
	 * @todo 添加通信地址
	 * @time 2014-5-13 下午3:45:41
	 * @author liuzenglong163@gmail.com
	 * @param mAddressBean
	 */
	private void requestAddAddress(final AddressBean mAddressBean) {
		if (null == mAddressBean)
			return;

		HttpRequest.doInSertAddress(mActivity, mAddressBean, this);
	}

	/**
	 * 
	 * @todo 修改通讯地址
	 * @time 2014-5-13 下午3:46:38
	 * @author liuzenglong163@gmail.com
	 * @param mAddressBean
	 */
	public void requestModifyAddress(final AddressBean addressBean) {
		if (null == addressBean)
			return;
		mAddressBean = addressBean;

		HttpRequest.doUpdateAddress(mActivity, addressBean, this);
	}

	/**
	 * 
	 * @todo 删除通讯地址
	 * @time 2014-5-13 下午3:47:43
	 * @author liuzenglong163@gmail.com
	 */
	public void requestDeleteAddress(final AddressBean addressBean) {
		if (null == addressBean)
			return;

		mAddressBean = addressBean;
		HttpRequest.doDeleteAddress(mActivity, mAddressBean.id, this);
	}

	/**
	 * 
	 * @todo 更新通信地址
	 * @time 2014-5-14 下午5:46:51
	 * @author liuzenglong163@gmail.com
	 * @param addressBean
	 */
	@SuppressWarnings("deprecation")
	public void doUpdateAddress(final AddressBean addressBean) {
		mAddressBean = addressBean;
		showDialog(DIALOG_UPDATE_ADDRESS);
	}

	/**
	 * 
	 * @todo 保存本地的地址信息
	 * @time 2014-5-12 下午4:38:54
	 * @author liuzenglong163@gmail.com
	 */
	private void saveAddress() {
		if (null == mAddressBean)
			return;

		if (AddressBean.ADDRESS_DEFAULT.equals(mAddressBean.state)) {
			String addr = mAddressBean.province + mAddressBean.city
					+ mAddressBean.street;
			mUserInfo.setAddress(addr);
			saveReturnedData(addr);
		}
	}

	private void saveReturnedData(String addr) {
		Intent intent = new Intent();
		intent.putExtra("address", addr);
		setResult(RESULT_OK, intent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		ZCDialog dialog = null;

		int screenWidth = (int) (getWindowManager().getDefaultDisplay()
				.getWidth() * Const.DIALOG_ADDRESS_WIDTH); // 屏幕宽
		int screenHeight = (int) (getWindowManager().getDefaultDisplay()
				.getHeight() * Const.DIALOG_ADDRESS_HEIGHT); // 屏幕高

		switch (id) {
		case DIALOG_ADD_ADDRESS:
			dialog = new ZCDialog(mActivity, R.style.user_dialog, screenWidth,
					screenHeight);
			dialog.setCancelable(true);
			dialog.showDialog(addAddressView());
			break;
		case DIALOG_UPDATE_ADDRESS:
			dialog = new ZCDialog(mActivity, R.style.user_dialog, screenWidth,
					screenHeight);
			dialog.setCancelable(true);
			dialog.showDialog(updateAddressView());
			break;
		case DIALOG_PROVINCE:
		case DIALOG_CITY:
		case DIALOG_COUNTRY:
			dialog = new ZCDialog(mActivity, R.style.user_dialog, screenWidth,
					screenHeight);
			dialog.setAnimationStyle(R.style.dialogAnimation2);
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new cancelAddress());
			dialog.showDialog(showAddressView());
			break;

		}
		return dialog;
	}

	/**
	 * 
	 * @todo 按旁边取消的处理事件
	 * @time 2014-5-21 上午11:07:40
	 * @author liuzenglong163@gmail.com
	 */
	class cancelAddress implements OnCancelListener {

		@Override
		public void onCancel(DialogInterface dialog) {
			codeMap.clear();
			addressMap.clear();
			mAddrLevel = LEVEL_PROVINCE;
			removeDialog(DIALOG_PROVINCE);
			removeDialog(DIALOG_CITY);
			removeDialog(DIALOG_COUNTRY);
		}
	}
}

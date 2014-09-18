package com.hct.zc.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.activity.setting.SetAddressActivity;
import com.hct.zc.bean.AddressBean;
import com.hct.zc.utils.Toaster;

/**
 * @todo 通讯地址适配器
 * @time 2014-5-13 下午5:36:28
 * @author liuzenglong163@gmail.com
 */

public class AddressAdapter extends ZBaseAdapter {

	public List<AddressBean> mList;
	private final SetAddressActivity addressActivity;
	private final Map<String, AddressBean> setDefaults = new HashMap<String, AddressBean>(); // 以键值对的形式存放设置默认的，《default,Object》

	public AddressAdapter(List<AddressBean> list, Activity activity,
			SetAddressActivity address) {
		super(list, activity);
		this.mList = list;
		this.addressActivity = address;
		this.mInflater = LayoutInflater.from(mActivity);
		setDefaultAddress();
	}

	/**
	 * 
	 * @time 2014-5-19 上午9:59:04
	 * @author liuzenglong163@gmail.com
	 */
	private void setDefaultAddress() {
		if (mList.size() == 0)
			return;
		boolean hasDefault = false; // 判断是否有设置默认地址
		for (AddressBean bean : mList) {
			if (AddressBean.ADDRESS_DEFAULT.equals(bean.state)) {
				hasDefault = true;
				break;
			}
		}
		if (!hasDefault) { // 如果没有默认地址，强制设置第一个地址为默认地址
			AddressBean bean = mList.get(0);
			bean.state = AddressBean.ADDRESS_DEFAULT;
			addressActivity.requestModifyAddress(bean);
		}
	}

	ViewHolder viewHolder;
	clickAddressListener listener;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_setting_show_address,
					null, true);
			viewHolder.tvName = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.tvPhone = (TextView) convertView
					.findViewById(R.id.tv_phone);
			viewHolder.tvAddress = (TextView) convertView
					.findViewById(R.id.tv_address);
			viewHolder.ckDefault = (CheckBox) convertView
					.findViewById(R.id.ck_default);
			viewHolder.ivDelete = (ImageView) convertView
					.findViewById(R.id.iv_delete);
			viewHolder.ivEdit = (ImageView) convertView
					.findViewById(R.id.iv_edit);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		listener = new clickAddressListener(position);
		final AddressBean addressBean = mList.get(position);
		viewHolder.tvName.setText(addressBean.name);
		viewHolder.tvPhone.setText(addressBean.phone);
		viewHolder.tvAddress.setText(addressBean.province + addressBean.city
				+ addressBean.street);
		viewHolder.ckDefault.setChecked(addressBean.state
				.equals(AddressBean.ADDRESS_DEFAULT_NO) ? false : true);
		viewHolder.ckDefault.setOnCheckedChangeListener(listener);
		viewHolder.ivEdit.setOnClickListener(listener);
		viewHolder.ivDelete.setOnClickListener(listener);

		if (AddressBean.ADDRESS_DEFAULT.equals(addressBean.state)) {
			setDefaults.put(AddressBean.ADDRESS_DEFAULT, addressBean);
		}

		viewHolder.ckDefault
				.setChecked(setDefaults.get(AddressBean.ADDRESS_DEFAULT) == addressBean ? true
						: false);

		return convertView;
	}

	static class ViewHolder {
		TextView tvName;
		TextView tvPhone;
		TextView tvAddress;
		CheckBox ckDefault;
		ImageView ivDelete;
		ImageView ivEdit;
	}

	class clickAddressListener implements OnClickListener,
			OnCheckedChangeListener {

		AddressBean addressBean;
		int mPosition = -1;

		public clickAddressListener(int position) {
			this.mPosition = position;
			this.addressBean = mList.get(mPosition);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_delete:
				if (null != addressBean)
					addressActivity.requestDeleteAddress(addressBean);
				break;
			case R.id.iv_edit:
				if (null != addressBean)
					addressActivity.doUpdateAddress(addressBean);
				break;
			default:
				break;
			}

		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.ck_default:
				if (isChecked) {
					addressBean.state = AddressBean.ADDRESS_DEFAULT;
					setDefaults.clear();
					setDefaults.put(AddressBean.ADDRESS_DEFAULT, addressBean);
					addressActivity.requestModifyAddress(addressBean);
				} else {
					if (setDefaults.size() == 0) {
						Toaster.showShort(mActivity, "必须有一个默认地址");
					} else {
						setDefaults.remove(addressBean);
					}
				}
				break;
			}
		}
	}
}

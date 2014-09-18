package com.hct.zc.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.bean.Client;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;

/**
 * @todo 用户列表
 * @time 2014年5月7日 下午5:56:33
 * @author jie.liu
 */

public class ClientAdapter extends BaseAdapter {

	private final Activity mContext;
	private final List<Client> mItems;
	private final LayoutInflater mInflater;
	private final ClickListener mListener;

	public ClientAdapter(Activity context, List<Client> items) {
		mContext = context;
		mItems = items;
		mInflater = LayoutInflater.from(context);
		mListener = new ClickListener();
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
			convertView = mInflater.inflate(R.layout.lvitem_client, null);
			viewHolder = new ViewHolder();
			viewHolder.mNameTV = (TextView) convertView
					.findViewById(R.id.name_tv);
			viewHolder.mPhoneTV = (TextView) convertView
					.findViewById(R.id.product_title_tv);
			viewHolder.mCallPhoneBtn = (Button) convertView
					.findViewById(R.id.call_server_btn);
			viewHolder.mSendSmsBtn = (Button) convertView
					.findViewById(R.id.send_sms_btn);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		fillData(mItems.get(position), viewHolder);
		return convertView;
	}

	private void fillData(Client client, ViewHolder viewHolder) {
		viewHolder.mNameTV.setText(client.getName());
		viewHolder.mPhoneTV.setText(client.getPhone());
		mListener.setPhoneNum(client.getPhone());
		viewHolder.mCallPhoneBtn.setOnClickListener(mListener);
		viewHolder.mSendSmsBtn.setOnClickListener(mListener);
	}

	private class ViewHolder {
		TextView mNameTV;
		TextView mPhoneTV;
		Button mCallPhoneBtn;
		Button mSendSmsBtn;
	}

	private class ClickListener implements OnClickListener {

		private String mPhoneNum;

		public void setPhoneNum(String phoneNum) {
			mPhoneNum = phoneNum;
		}

		@Override
		public void onClick(View v) {
			if (TextUtils.isEmpty(mPhoneNum)) {
				Toaster.showShort(mContext, "无电话号码");
				return;
			}

			switch (v.getId()) {
			case R.id.call_server_btn:
				new AlertDialog.Builder(mContext)
						.setTitle("温馨提示")
						.setMessage("确定立即拨打该电话号码么?")
						.setNegativeButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										callPhone();
									}
								}).setPositiveButton("取消", null).show();
				break;
			case R.id.send_sms_btn:
				sendSms();
				break;
			default:
				LogUtil.w(mContext, "点击了未知的位置");
			}
		}

		private void callPhone() {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ mPhoneNum));
			mContext.startActivity(intent);
		}

		private void sendSms() {
			Uri smsToUri = Uri.parse("smsto:" + mPhoneNum);
			Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			mIntent.putExtra("sms_body",
					"尊敬的客户，您所需购买的基金额度已经所剩不多了，如有需要，请抓紧时间购买.");
			mContext.startActivity(mIntent);
		}
	}
}

package com.hct.zc.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.utils.ZCUtils;

/**
 * @todo  所有的spinner适配类
 * @author lzlong@zwmob.com
 */

public class HCTSpinnerAdapter extends ZBaseAdapter implements SpinnerAdapter {
	
	public HCTSpinnerAdapter(List<?> list, Activity activity) {
		super(list, activity);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.my_simple_spinner_item_recharge, null);
		}
		TextView view = (TextView) convertView.findViewById(R.id.recharge_spinner_view);
		
		final Object  object = (String) mList.get(position);
		if(object instanceof String){
			String str = (String) object;
			view.setText(" "+str);
			view.setCompoundDrawablesWithIntrinsicBounds(mActivity.getResources().getDrawable(ZCUtils.getFundChannelDrawable(str)), null,null,null);
		}
		return convertView;
	}
}
	
//	@Override
//	public View getDropDownView(int position, View convertView,
//			ViewGroup parent) {
//		if(convertView==null){
//			convertView=mInflater.inflate(R.layout.my_simple_spinner_dropdown_item_recharge, null);
//		}
//		CheckedTextView view=(CheckedTextView) convertView.findViewById(R.id.recharge_spinner_item);
//		final Object  object = (String) mListData.get(position);
//		if(object instanceof String){
//			String str = (String) object;
//			view.setText(str);
//			view.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(ZCUtils.getFundChannelDrawable(str)), null,null,null);
//		return convertView;
//	}

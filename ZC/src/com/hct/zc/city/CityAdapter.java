package com.hct.zc.city;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.adapter.ZBaseAdapter;

public class CityAdapter extends ZBaseAdapter {
	public CityAdapter(List<MyListItem> mList, Activity mActivity) {
		super(mList, mActivity);
	}
	
	ViewHolder viewHolder;
	public View getView(int position, View convertView, ViewGroup parent)
	{ 
		
		if(null == convertView)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_address_select, null);
			viewHolder.tView = (TextView) convertView.findViewById(R.id.option_tv);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final MyListItem item = (MyListItem) mList.get(position);
		viewHolder.tView.setText(item.getName());
		
		return convertView;
	}
	
	final class ViewHolder{
		TextView tView;
	}
	
}
package com.hct.zc.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @todo adapter的基类
 * @author liuzenglong163@gmail.com
 * @param <T>
 */

public class ZBaseAdapter extends BaseAdapter {

	public List<?> mList;
	public LayoutInflater mInflater;
	public Activity mActivity;
	public int mLayout;

	public ZBaseAdapter(List<?> mList, Activity mActivity) {
		super();
		this.mList = mList;
		this.mActivity = mActivity;
		this.mInflater = LayoutInflater.from(mActivity);
	}

	public ZBaseAdapter(List<?> mList, Activity mActivity, int resLayout) {
		this.mList = mList;
		this.mActivity = mActivity;
		this.mInflater = LayoutInflater.from(mActivity);
		this.mLayout = resLayout;
	}

	@Override
	public int getCount() {
		if (null != mList) {
			return mList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (null != mList) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (null != mList) {
			return position;
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		return null;
	}
}

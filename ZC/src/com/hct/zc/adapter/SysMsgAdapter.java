package com.hct.zc.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.bean.MsgBean;

/**
 * @todo 构建消息体
 * @time 2014-5-21 下午6:36:54
 * @author liuzenglong163@gmail.com
 */

public class SysMsgAdapter extends ZBaseAdapter {

	public SysMsgAdapter(List<MsgBean> mList, Activity mActivity) {
		super(mList, mActivity);
	}

	ViewHolder viewHolder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_msg_layout, null,
					true);
			viewHolder.tvMsgTitle = (TextView) convertView
					.findViewById(R.id.tv_msg_title);
			viewHolder.tvMsgContent = (TextView) convertView
					.findViewById(R.id.content_tv);
			viewHolder.tvMsgTime = (TextView) convertView
					.findViewById(R.id.tv_msg_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final MsgBean mBean = (MsgBean) mList.get(position);
		viewHolder.tvMsgTitle.setText(mBean.title);
		viewHolder.tvMsgContent.setText(mBean.context);
		viewHolder.tvMsgTime.setText(mBean.date.subSequence(0, 10));
		// 改变颜色体
		viewHolder.tvMsgTitle
				.setTextColor(mBean.isread.equals(MsgBean.NO_READ) ? mActivity
						.getResources().getColor(R.color.color_zc_black)
						: mActivity.getResources().getColor(
								R.color.color_zc_gray_light));
		return convertView;
	}

	final class ViewHolder {
		TextView tvMsgTitle;
		TextView tvMsgContent;
		TextView tvMsgTime;
	}

}

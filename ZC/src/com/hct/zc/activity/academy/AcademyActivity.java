package com.hct.zc.activity.academy;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.bean.AcademyClass;
import com.hct.zc.bean.AcademyContent;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.result.AcademyResult;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;

/**


teaktljakdf;aljfalsdkfjas;dlfkjasdkl;fja

*/
public class AcademyActivity extends BaseHttpActivity {

	private ExpandableListView mExpandLV;
	private BaseExpandableListAdapter mAdapter;
	private final List<AcademyClass> mItems = new ArrayList<AcademyClass>();

	private final String TYPE_NORMAL = "1";
	private final String TYPE_FAQ = "2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.academy_activity);
		initViews();
		initData();
	}

	private void initViews() {
		initTitlebar();
		mExpandLV = (ExpandableListView) findViewById(R.id.academy_elv);
		mAdapter = new AcademyAdapter();
		mExpandLV.setAdapter(mAdapter);
		mExpandLV.setOnChildClickListener(new ChildClickListener());
	}

	private void initTitlebar() {
		new TitleBar(AcademyActivity.this).initTitleBar("掌财学院");
	}

	private class AcademyAdapter extends BaseExpandableListAdapter {

		private final LayoutInflater mInflater;
		private final Drawable mGroupExpandedDraw;
		private final Drawable mGroupCollapseDraw;

		public AcademyAdapter() {
			mInflater = getLayoutInflater();

			mGroupExpandedDraw = getResources().getDrawable(
					R.drawable.ic_academy_expand);
			// / 这一步必须要做,否则不会显示.
			mGroupExpandedDraw.setBounds(0, 0,
					mGroupExpandedDraw.getMinimumWidth(),
					mGroupExpandedDraw.getMinimumHeight());

			mGroupCollapseDraw = getResources().getDrawable(
					R.drawable.ic_academy_cllapse);
			// / 这一步必须要做,否则不会显示.
			mGroupCollapseDraw.setBounds(0, 0,
					mGroupCollapseDraw.getMinimumWidth(),
					mGroupCollapseDraw.getMinimumHeight());
		}

		@Override
		public int getGroupCount() {
			return mItems.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mItems.get(groupPosition).getContexts().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mItems.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mItems.get(groupPosition).getContexts().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.lvitem_academy_group,
						null);
				viewHolder = new ViewHolder();
				viewHolder.mContentTV = (TextView) convertView
						.findViewById(R.id.group_tv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			fillGroupView(viewHolder, groupPosition, isExpanded);
			return convertView;
		}

		private void fillGroupView(ViewHolder viewHolder, int groupPosition,
				boolean isExpanded) {
			AcademyClass academyClass = mItems.get(groupPosition);
			viewHolder.mContentTV.setText(academyClass.getTitle());
			fillArrow(viewHolder.mContentTV, isExpanded);
		}

		private void fillArrow(TextView contentTV, boolean isExpanded) {
			if (isExpanded) {
				contentTV.setCompoundDrawables(null, null, mGroupExpandedDraw,
						null);
			} else {
				contentTV.setCompoundDrawables(null, null, mGroupCollapseDraw,
						null);
			}
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.lvitem_academy_child,
						null);
				viewHolder = new ViewHolder();
				viewHolder.mContentTV = (TextView) convertView
						.findViewById(R.id.child_tv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			fillChildView(viewHolder, childPosition, groupPosition);

			return convertView;
		}

		private void fillChildView(ViewHolder viewHolder, int childPosition,
				int groupPosition) {
			String title = mItems.get(groupPosition).getContexts()
					.get(childPosition).getTitle();
			viewHolder.mContentTV.setText(title);
			if (isLastItem(groupPosition, childPosition)) {
				viewHolder.mContentTV
						.setBackgroundResource(R.drawable.bg_edit_down);
			} else {
				viewHolder.mContentTV
						.setBackgroundResource(R.drawable.bg_edit_up);
			}
		}

		private boolean isLastItem(int groupPosition, int childPosition) {
			return childPosition == mItems.get(groupPosition).getContexts()
					.size() - 1 ? true : false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		private class ViewHolder {
			TextView mContentTV;
		}
	}

	private class ChildClickListener implements OnChildClickListener {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			AcademyContent content = mItems.get(groupPosition).getContexts()
					.get(childPosition);
			String typeCode = mItems.get(groupPosition).typecode;
			pushDepOnType(content, typeCode);
			return true;
		}

		private void pushDepOnType(AcademyContent content, String typeCode) {
			if (TYPE_NORMAL.equals(typeCode)) {
				push(AcademyKnowledgeActivity.class, content);
			} else if (TYPE_FAQ.equals(typeCode)) {
				push(AcademyFAQActivity.class, content);
			} else {
				LogUtil.w(AcademyActivity.this, "点击了未知组的选项");
			}
		}

		private void push(Class<?> cls, AcademyContent withContent) {
			Intent intent = new Intent(AcademyActivity.this, cls);
			intent.putExtra("academyContent", withContent);
			startActivity(intent);
		}
	}

	private void initData() {
		HttpRequest.doGetAcademy(this, this);
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
		AcademyResult r = gson.fromJson(result, AcademyResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			List<AcademyClass> academyClasses = r.getAcademys();
			mItems.addAll(academyClasses);
			mAdapter.notifyDataSetChanged();
		} else if (HttpResult.FAIL.equals(resultCode)) {
		} else {
			Toaster.showShort(AcademyActivity.this, "请求出错，请重试");
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

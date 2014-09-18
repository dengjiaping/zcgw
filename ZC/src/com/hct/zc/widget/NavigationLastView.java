package com.hct.zc.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hct.zc.R;
import com.hct.zc.activity.base.HomePageActivity;
import com.hct.zc.utils.ContextUtil;

/**
 * @todo TODO
 * @time 2014年6月19日 上午10:42:00
 * @author liuzenglong163@gmail.com
 */

public class NavigationLastView extends LinearLayout {

	private ImageView mTopIV;
	private Button mGoBtn;

	public NavigationLastView(Context context) {
		super(context);
		init(context);
	}

	public NavigationLastView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.navigation_view_last, this);
		mTopIV = (ImageView) view.findViewById(R.id.top_iv);
		mGoBtn = (Button) view.findViewById(R.id.go_btn);

	}

	public void initView(final Activity activity, Bitmap topBitmap) {
		mTopIV.setImageBitmap(topBitmap);
		mGoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ContextUtil.pushToActivity(activity, HomePageActivity.class);
				activity.finish();
			}
		});

	}
}

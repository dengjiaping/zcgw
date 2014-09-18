package com.hct.zc.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.hct.zc.R;

/**
 * 
 * @time 2014年6月10日 下午3:13:01
 * @author jie.liu
 */

@SuppressLint("NewApi")
public class NavigationView extends LinearLayout {

	private ImageView mTopIV;
	private ImageView mLeftIV; // "他/她, 去年佣金收入"
	private ImageView mGfTargetDrawIV;
	private GifView mGfView;
	private ImageView mRightIV; // "万"
	private ImageView mBottomIV;

	public NavigationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public NavigationView(Context context) {
		super(context);
		init(context);
	}

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.navigation_view, this);
		mTopIV = (ImageView) view.findViewById(R.id.top_iv);
		mLeftIV = (ImageView) view.findViewById(R.id.left_iv);

		mGfTargetDrawIV = (ImageView) findViewById(R.id.gif1);
		mGfView = (GifView) view.findViewById(R.id.gif2);
		// 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
		mGfView.setGifImageType(GifImageType.SYNC_DECODER);
		mRightIV = (ImageView) view.findViewById(R.id.right_iv);
		mBottomIV = (ImageView) view.findViewById(R.id.bottom_iv);
	}

	public void setPageContent(Bitmap topImg, int leftImg, int gf,
			int rightImg, int bottomImg) {
		mTopIV.setImageBitmap(topImg);
		mLeftIV.setImageResource(leftImg);
		mGfView.setGifImage(gf);
		mRightIV.setImageResource(rightImg);
		mBottomIV.setImageResource(bottomImg);
	}

	public void ShowGif(final Activity activity, final int targetDraw) {
		mGfTargetDrawIV.setVisibility(View.GONE);
		mGfView.setVisibility(View.VISIBLE);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mGfView.setVisibility(View.GONE);
						mGfTargetDrawIV.setImageResource(targetDraw);
						mGfTargetDrawIV.setVisibility(View.VISIBLE);
					}
				});
			}
		}, 1000);
	}

	public void relayoutTargetIV(int width, int height) {
		mGfView.setShowDimension(width, height);
		mGfTargetDrawIV.setLayoutParams(new LayoutParams(width, height));
	}

	public void returnToInit() {
		mGfView.setVisibility(View.GONE);
		mGfTargetDrawIV.setVisibility(View.VISIBLE);
		mGfTargetDrawIV.setImageResource(R.drawable.mark_000);
	}

}

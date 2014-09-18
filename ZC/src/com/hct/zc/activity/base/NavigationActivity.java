package com.hct.zc.activity.base;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.hct.zc.R;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.widget.NavigationLastView;
import com.hct.zc.widget.NavigationView;

/**
 * 
 * @todo 导航，第一次启动APP时，显示.
 * @time 2014年5月4日 下午2:14:56
 * @author jie.liu
 */
public class NavigationActivity extends BaseActivity {

	private List<View> mViewList;
	private ViewPager mPager;
	private int mCurrentPageIndex;
	private VelocityTracker mVelocityTracker; // 用于判断甩动手势
	private final int[] mTargetGfs = { R.drawable.mark_100,
			R.drawable.mark_120, R.drawable.mark_150, R.drawable.mark_300 };
	private final Bitmap[] mTopBitmap = new Bitmap[5];

	private static final int SNAP_VELOCITY = 300;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.introduction_activity);
		initViews();
	}

	private void initViews() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		float dpi = metric.density;

		int width = (int) (155.0 / 2 * dpi);
		int height = (int) (75.0 / 2 * dpi);
		LogUtil.d(this, "" + metric.density + "width:" + width + "height:"
				+ height);
		mViewList = new ArrayList<View>();
		mPager = (ViewPager) findViewById(R.id.viewpager);
		initTopBitmap();
		NavigationView nav1 = new NavigationView(this);
		nav1.setPageContent(mTopBitmap[0], R.drawable.navigation_left_men,
				R.drawable.navigation_gif_1, R.drawable.navigation_right,
				R.drawable.navigation_bottom_1);
		nav1.relayoutTargetIV(width, height);
		nav1.ShowGif(this, R.drawable.mark_100);
		NavigationView nav2 = new NavigationView(this);
		nav2.setPageContent(mTopBitmap[1], R.drawable.navigation_left_women,
				R.drawable.navigation_gif_2, R.drawable.navigation_right,
				R.drawable.navigation_bottom_2);
		nav2.relayoutTargetIV(width, height);
		NavigationView nav3 = new NavigationView(this);
		nav3.setPageContent(mTopBitmap[2], R.drawable.navigation_left_men,
				R.drawable.navigation_gif_3, R.drawable.navigation_right,
				R.drawable.navigation_bottom_3);
		nav3.relayoutTargetIV(width, height);
		NavigationView nav4 = new NavigationView(this);
		nav4.setPageContent(mTopBitmap[3], R.drawable.navigation_left_men,
				R.drawable.navigation_gif_4, R.drawable.navigation_right,
				R.drawable.navigation_bottom_4);
		nav4.relayoutTargetIV(width, height);
		NavigationLastView nav5 = new NavigationLastView(this);
		nav5.initView(this, mTopBitmap[4]);

		mViewList.add(nav1);
		mViewList.add(nav2);
		mViewList.add(nav3);
		mViewList.add(nav4);
		mViewList.add(nav5);

		mPager.setAdapter(new NavAdapter());
		noteCurrentPageIndex();
		schedulePushToHomePage();
	}

	private void initTopBitmap() {
		Resources res = getResources();
		Drawable draw1 = res.getDrawable(R.drawable.navigation_top_1);
		mTopBitmap[0] = drawableToBitmap(draw1);
		Drawable draw2 = res.getDrawable(R.drawable.navigation_top_2);
		mTopBitmap[1] = drawableToBitmap(draw2);
		Drawable draw3 = res.getDrawable(R.drawable.navigation_top_3);
		mTopBitmap[2] = drawableToBitmap(draw3);
		Drawable draw4 = res.getDrawable(R.drawable.navigation_top_4);
		mTopBitmap[3] = drawableToBitmap(draw4);
		Drawable draw5 = res.getDrawable(R.drawable.navigation_top_5);
		mTopBitmap[4] = drawableToBitmap(draw5);
	}

	private Bitmap drawableToBitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	private void schedulePushToHomePage() {
		mPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isLastPageShown() && dragTheView(event)) {
					ContextUtil.pushToActivity(NavigationActivity.this,
							HomePageActivity.class);
					NavigationActivity.this.finish();
				}
				return false;
			}

		});
	}

	private boolean isLastPageShown() {
		return mCurrentPageIndex == mViewList.size() - 1;
	}

	private void noteCurrentPageIndex() {
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mCurrentPageIndex = arg0;
				gifReturnToInit(arg0);
				View view = mViewList.get(arg0);
				if (view instanceof NavigationView) {
					NavigationView nav = (NavigationView) view;
					if (nav != null) {
						nav.ShowGif(NavigationActivity.this, mTargetGfs[arg0]);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			private void gifReturnToInit(int indexExcepted) {
				int count = mViewList.size() - 2;
				for (int i = 0; i < count; i++) {
					if (i == indexExcepted) {
						continue;
					}

					View view = mViewList.get(i);
					if (view instanceof NavigationView) {
						NavigationView nav = (NavigationView) view;
						if (nav != null) {
							nav.returnToInit();
						}
					}
				}
			}
		});

	}

	/**
	 * 判断是否拖拽的手势满足跳转的条件.
	 * 
	 * @param event
	 * @return
	 */
	private boolean dragTheView(MotionEvent event) {
		boolean result = false;
		final int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
			}

			break;
		case MotionEvent.ACTION_UP:
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}

			if (velocityX < -SNAP_VELOCITY) {
				result = true;
			}

			recycleVelocityTracker();
			break;
		case MotionEvent.ACTION_CANCEL:
			recycleVelocityTracker();
			break;
		}
		return result;
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	class NavAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mViewList.get(position));
		}

		@Override
		public Object instantiateItem(View container, final int position) {
			View view = mViewList.get(position);
			ViewPager viewPager = (ViewPager) container;
			viewPager.addView(view, new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			return mViewList.get(position);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		int count = mTopBitmap.length;
		for (int i = 0; i < count; i++) {
			if (mTopBitmap[i] != null) {
				mTopBitmap[i].recycle();
				mTopBitmap[i] = null;
			}
		}
	}

}

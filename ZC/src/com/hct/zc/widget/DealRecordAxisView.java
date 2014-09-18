package com.hct.zc.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hct.zc.R;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.imagecache.ImageCacheManager;
import com.hct.zc.utils.imagecache.ImageCacheManager.OnBitmapFromHttpListener;

public class DealRecordAxisView extends FrameLayout implements
		OnBitmapFromHttpListener {

	private Context mContext;

	private LayoutInflater mInflater;

	private RecordType mRecordType;

	private VariablePartType mVariablePartType;

	/**
	 * 左侧的索引
	 */
	private String mIndex;

	/**
	 * 日期
	 */
	private String mDate;

	/**
	 * 内容
	 */
	private String mContent;

	/**
	 * 汇款凭条图片地址
	 */
	private List<String> mRemitSlipPicUrls;

	/**
	 * 是否选择了合同地址
	 */
	private boolean mHasChoosenContractAddr;

	/**
	 * 是否选择了合同地址
	 */
	private boolean mChoosedAddr;

	/**
	 * 身份证照片, 汇款凭条照片
	 */
	private String mIdPicUrl, mRemitSlip1Url, mRemitSlip2Url, mRemitSlip3Url;

	/**
	 * 是否上传了身份证
	 */
	private boolean mUploadedIdPic;

	/**
	 * 是否上传了汇款凭条
	 */
	private boolean mUploadedRemitSlip;

	/**
	 * 根布局
	 */
	private View mRootView;

	/**
	 * 上传汇款凭条的根布局
	 */
	private View mRemitSlipRootView;

	/**
	 * 显示身份证的根布局
	 */
	private View mIdPicRootView;

	/**
	 * 单个按钮的根布局
	 */
	private View mBtnRootView;

	/**
	 * 索引
	 */
	private TextView mIndexTV;

	/**
	 * 交易记录项
	 */
	private LinearLayout mDealRecordItemLlyt;

	/**
	 * 可变部分的父布局
	 */
	private LinearLayout mVariablePartLlyt;

	/**
	 * 显示日期的控件
	 */
	private TextView mDateTV;

	/**
	 * 显示内容的控件
	 */
	private TextView mContentTV;

	/**
	 * 显示身份证的控件
	 */
	private ImageView mIdPicIV;

	/**
	 * 选择合同地址按钮
	 */
	private Button mSingleBtn;

	/**
	 * 显示打印凭条的控件
	 */
	private ImageView mRemitSlip1IV, mRemitSlip2IV, mRemitSlip3IV;

	private TextView mUploadRemitSlipTV;

	private ClickListener mClickListener;

	private OnViewClickListener mListener;

	private List<Bitmap> mBitmapShown;

	private final int TEXT_COLOR_TYPE_DONE = 0xFFD5834D;

	private final int TEXT_COLOR_TYPE_UNDONE = 0xFF9C9C9C;

	/**
	 * 
	 * 交易记录类型 (已完成:{@link RecordType#TYPE_DONE}, 待完成:
	 * {@link RecordType#TYPE_UNDONE})
	 * 
	 * @time 2014年5月15日 下午4:45:03
	 * @author jie.liu
	 */
	public enum RecordType {
		TYPE_DONE, TYPE_UNDONE
	}

	/**
	 * 
	 * 右边可变部分的类别,根据此类型，显示不同的UI(正常情况,不显示任何东西:{@link VariablePartType#TYPE_NORMAL}
	 * , 上传身份证照片: {@link VariablePartType#TYPE_SHOW_ID_PIC},上传汇款凭条:
	 * {@link VariablePartType#TYPE_UPLOAD_REMIT_SLIP},合同地址
	 * {@link VariablePartType#TYPE_SIGN_CONTRACT_DONE})
	 * 
	 * @time 2014年5月15日 下午4:47:40
	 * @author jie.liu
	 */
	public enum VariablePartType {
		TYPE_NORMAL, TYPE_SHOW_ID_PIC, TYPE_UPLOAD_REMIT_SLIP, TYPE_SIGN_CONTRACT_DONE, TYPE_SIGN_CONTRACT_UNDONE, TYPE_SEND_SMS, TYPE_UPLOAD_ID_PIC
	}

	public DealRecordAxisView(Context context) {
		super(context);
		initView(context);
	}

	public DealRecordAxisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public DealRecordAxisView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		mBitmapShown = new ArrayList<Bitmap>();
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mRootView = mInflater.inflate(R.layout.deal_record_axis_view, this);
		mIndexTV = (TextView) mRootView.findViewById(R.id.index_tv);
		mDealRecordItemLlyt = (LinearLayout) mRootView
				.findViewById(R.id.deal_record_llyt);
		mDateTV = (TextView) mRootView.findViewById(R.id.date_tv);
		mContentTV = (TextView) mRootView.findViewById(R.id.state_tv);
		mVariablePartLlyt = (LinearLayout) mRootView
				.findViewById(R.id.variable_part_llyt);

		mClickListener = new ClickListener();
	}

	public void setUiType(RecordType recordType,
			VariablePartType variablePartType) {
		setRecordType(recordType);
		setVariablePartType(variablePartType);
		invalidateUi();
	}

	public void setRecordType(RecordType recordType) {
		mRecordType = recordType;
	}

	public void setVariablePartType(VariablePartType variablePartType) {
		mVariablePartType = variablePartType;
	}

	/**
	 * 刷新UI
	 * 
	 * @time 2014年5月15日 下午5:02:56
	 * @author jie.liu
	 */
	public void invalidateUi() {
		refreshIndexUi();
		refreshDealRecordItemUi();
	}

	private void refreshIndexUi() {
		if (mIndexTV == null) {
			mIndexTV = (TextView) mRootView.findViewById(R.id.index_tv);
		}

		switch (mRecordType) {
		case TYPE_DONE:
			mIndexTV.setBackgroundResource(R.drawable.bg_index_red);
			break;
		case TYPE_UNDONE:
			mIndexTV.setBackgroundResource(R.drawable.bg_index_gray);
			break;
		default:
			LogUtil.w(mContext, "不确定的记录类型，不能确定是已完成还是未完成");
		}
	}

	private void refreshDealRecordItemUi() {
		refreshDepOnRecordType();

		switch (mVariablePartType) {
		case TYPE_NORMAL:
			refreshNormalUi();
			break;
		case TYPE_SHOW_ID_PIC:
			refreshShowIdPicUi();
			break;
		case TYPE_SIGN_CONTRACT_DONE:
			refreshContractAddressUi();
			break;
		case TYPE_SIGN_CONTRACT_UNDONE:
			refreshContractAddressUi();
			break;
		case TYPE_SEND_SMS:
			refreshSendSmsUi();
			break;
		case TYPE_UPLOAD_ID_PIC:
			refreshNormalUi();
			break;
		case TYPE_UPLOAD_REMIT_SLIP:
			refreshUploadRemitSlipUi();
			break;
		default:
			LogUtil.w(mContext, "右侧可变部分的Ui不确定");
		}
	}

	/**
	 * 
	 * 根据记录是已完成的记录还是未完成的记录来决定Ui
	 * 
	 * @time 2014年5月15日 下午6:06:42
	 * @author jie.liu
	 */
	private void refreshDepOnRecordType() {
		if (mDealRecordItemLlyt == null) {
			mDealRecordItemLlyt = (LinearLayout) mRootView
					.findViewById(R.id.deal_record_llyt);
		}

		if (mDateTV == null) {
			mDateTV = (TextView) mRootView.findViewById(R.id.date_tv);
		}

		if (mContentTV == null) {
			mContentTV = (TextView) mRootView.findViewById(R.id.state_tv);
		}

		switch (mRecordType) {
		case TYPE_DONE:
			mDealRecordItemLlyt
					.setBackgroundResource(R.drawable.bg_deal_record_item_white);
			mDateTV.setTextColor(TEXT_COLOR_TYPE_DONE);
			mContentTV.setTextColor(TEXT_COLOR_TYPE_DONE);
			mDateTV.setVisibility(View.VISIBLE);
			break;
		case TYPE_UNDONE:
			mDealRecordItemLlyt
					.setBackgroundResource(R.drawable.bg_deal_record_item_gray);
			mDateTV.setVisibility(View.GONE);
			mContentTV.setTextColor(TEXT_COLOR_TYPE_UNDONE);
			break;
		default:
			LogUtil.w(mContext, "不确定的记录类型，不能确定是已完成还是未完成");
		}
	}

	/**
	 * 
	 * 普通情况下，列表项右侧是没有东西的
	 * 
	 * @time 2014年5月16日 下午4:03:54
	 * @author jie.liu
	 */
	private void refreshNormalUi() {
		// 什么都不用做
	}

	/**
	 * 
	 * 更新显示身份证
	 * 
	 * @time 2014年5月16日 下午4:02:58
	 * @author jie.liu
	 */
	private void refreshShowIdPicUi() {
		if (mVariablePartLlyt == null) {
			mVariablePartLlyt = (LinearLayout) mRootView
					.findViewById(R.id.variable_part_llyt);
		}

		if (mIdPicRootView == null) {
			mIdPicRootView = mInflater.inflate(R.layout.variable_part_id_pic,
					mVariablePartLlyt);
			mIdPicIV = (ImageView) mIdPicRootView.findViewById(R.id.id_pic_iv);
		}
	}

	/**
	 * 
	 * 更新合同地址按钮
	 * 
	 * @time 2014年5月16日 下午4:02:45
	 * @author jie.liu
	 */
	private void refreshContractAddressUi() {
		if (mVariablePartLlyt == null) {
			mVariablePartLlyt = (LinearLayout) mRootView
					.findViewById(R.id.variable_part_llyt);
		}

		if (mBtnRootView == null) {
			mBtnRootView = mInflater.inflate(R.layout.variable_part_yellow_btn,
					mVariablePartLlyt);
			mSingleBtn = (Button) mBtnRootView.findViewById(R.id.yellow_btn);
			mSingleBtn.setOnClickListener(mClickListener);

			refreshContractBtnText();
		}
	}

	private void refreshContractBtnText() {
		if (VariablePartType.TYPE_SIGN_CONTRACT_DONE.equals(mVariablePartType)) {
			mSingleBtn.setText("查看合同地址");
		} else if (VariablePartType.TYPE_SIGN_CONTRACT_UNDONE
				.equals(mVariablePartType)) {
			// 判断有合同地址有没有，如果有，显示“查看合同地址”，如果没有，显示“选择合同地址”
			if (mChoosedAddr == true) {
				mSingleBtn.setText("查看合同地址");
			} else {
				mSingleBtn.setText("选择合同地址");
			}
		}
	}

	/**
	 * 
	 * 更新短信通知客户按钮
	 * 
	 * @time 2014年5月16日 下午4:02:28
	 * @author jie.liu
	 */
	private void refreshSendSmsUi() {
		if (mVariablePartLlyt == null) {
			mVariablePartLlyt = (LinearLayout) mRootView
					.findViewById(R.id.variable_part_llyt);
		}

		if (mBtnRootView == null) {
			mBtnRootView = mInflater.inflate(R.layout.variable_part_yellow_btn,
					mVariablePartLlyt);
			mSingleBtn = (Button) mBtnRootView.findViewById(R.id.yellow_btn);
			mSingleBtn.setOnClickListener(mClickListener);
			mSingleBtn.setText("短信通知客户");
		}
	}

	/**
	 * 
	 * 更新验证身份证按钮
	 * 
	 * @time 2014年5月17日 下午1:41:04
	 * @author jie.liu
	 */
	private void refreshUploadIdPicUi() {
		if (mVariablePartLlyt == null) {
			mVariablePartLlyt = (LinearLayout) mRootView
					.findViewById(R.id.variable_part_llyt);
		}

		if (mBtnRootView == null) {
			mBtnRootView = mInflater.inflate(R.layout.variable_part_yellow_btn,
					mVariablePartLlyt);
			mSingleBtn = (Button) mBtnRootView.findViewById(R.id.yellow_btn);
			mSingleBtn.setOnClickListener(mClickListener);
			mSingleBtn.setText("验证身份");
		}
	}

	/**
	 * 
	 * 更新上传汇款凭条
	 * 
	 * @time 2014年5月16日 下午4:06:59
	 * @author jie.liu
	 */
	private void refreshUploadRemitSlipUi() {
		if (mVariablePartLlyt == null) {
			mVariablePartLlyt = (LinearLayout) mRootView
					.findViewById(R.id.variable_part_llyt);
		}

		if (mRemitSlipRootView == null) {
			mRemitSlipRootView = mInflater.inflate(
					R.layout.variable_part_remit_slip, mVariablePartLlyt);
			mRemitSlip1IV = (ImageView) mRemitSlipRootView
					.findViewById(R.id.remit_slip_one_iv);
			mRemitSlip2IV = (ImageView) mRemitSlipRootView
					.findViewById(R.id.remit_slip_two_iv);
			mRemitSlip3IV = (ImageView) mRemitSlipRootView
					.findViewById(R.id.remit_slip_three_iv);
			mUploadRemitSlipTV = (TextView) mRemitSlipRootView
					.findViewById(R.id.upload_remit_slip_tv);
			mRemitSlip1IV.setOnClickListener(mClickListener);
			mRemitSlip2IV.setOnClickListener(mClickListener);
			mRemitSlip3IV.setOnClickListener(mClickListener);
			mUploadRemitSlipTV.setOnClickListener(mClickListener);
		}
	}

	/**
	 * 
	 * 设置索引
	 * 
	 * @time 2014年5月16日 下午4:20:19
	 * @author jie.liu
	 * @param index
	 */
	public void setIndex(String index) {
		if (mIndexTV != null && !TextUtils.isEmpty(index)) {
			mIndexTV.setText(index);
			mIndex = index;
		}
	}

	/**
	 * 
	 * 设置日期
	 * 
	 * @time 2014年5月16日 下午4:20:31
	 * @author jie.liu
	 * @param date
	 */
	public void setDate(String date) {
		if (mDateTV != null && !TextUtils.isEmpty(date)) {
			mDateTV.setText(date);
			mDate = date;
		}
	}

	/**
	 * 
	 * 设置显示的内容
	 * 
	 * @time 2014年5月16日 下午4:21:32
	 * @author jie.liu
	 * @param content
	 */
	public void setContent(String content) {
		if (mContentTV != null && !TextUtils.isEmpty(content)) {
			mContentTV.setText(content);
			mContent = content;
		}
	}

	/**
	 * 
	 * 设置单个按钮的文字.
	 * 
	 * @time 2014年5月16日 下午3:07:46
	 * @author jie.liu
	 * @param text
	 */
	public void setSingleBtnText(String text) {
		if (mSingleBtn != null) {
			mSingleBtn.setText(text);
		}
	}

	/**
	 * 
	 * 设置身份证图片.
	 * 
	 * @time 2014年5月16日 下午3:07:28
	 * @author jie.liu
	 * @param url
	 */
	public void setIdPic(String url) {
		if (mIdPicIV == null) {
			// ImageCacheManager cacheManager = new ImageCacheManager(mContext);
			// Bitmap bitmap = cacheManager.getBitmap(url, mIdPicIV);
			// if (bitmap != null) {
			// mBitmapShown.add(bitmap);
			// }
			loadBitmap(mIdPicIV, url);

			mIdPicUrl = url;
			mUploadedIdPic = true;
		}
	}

	/**
	 * 
	 * 设置汇款凭条
	 * 
	 * @time 2014年5月16日 下午6:13:23
	 * @author jie.liu
	 * @param urls
	 */
	public void setRemitSlipPics(List<String> urls) {
		if (urls == null || urls.size() == 0) {
			return;
		}

		clearRemitSlips();
		for (int i = 0; i < urls.size(); i++) {
			if (i == 0) {
				setRemitSlipPicOne(urls.get(0));
			}

			if (i == 1) {
				setRemitSlipPicTwo(urls.get(1));
			}

			if (i == 2) {
				setRemitSlipPicThree(urls.get(2));
			}
		}
	}

	public void setRemitSlipPics(String url1, String url2, String url3) {
		if (!TextUtils.isEmpty(url1)) {
			setRemitSlipPicOne(url1);
		}

		if (!TextUtils.isEmpty(url2)) {
			setRemitSlipPicTwo(url2);
		}

		if (!TextUtils.isEmpty(url3)) {
			setRemitSlipPicThree(url3);
		}
	}

	public void setRemitSlipPicOne(String url) {
		if (!TextUtils.isEmpty(url)) {
			mRemitSlip1Url = url;
			setRemitPic(mRemitSlip1IV, url);
		}
	}

	public void setRemitSlipPicTwo(String url) {
		if (!TextUtils.isEmpty(url)) {
			mRemitSlip2Url = url;
			setRemitPic(mRemitSlip2IV, url);
		}
	}

	public void setRemitSlipPicThree(String url) {
		if (!TextUtils.isEmpty(url)) {
			mRemitSlip3Url = url;
			setRemitPic(mRemitSlip3IV, url);
		}
	}

	public void clearRemitSlips() {
		mRemitSlip1Url = null;
		mRemitSlip2Url = null;
		mRemitSlip3Url = null;

		if (mRemitSlip1IV != null) {
			cleanRemitSlip(mRemitSlip1IV);
		}
		if (mRemitSlip2IV != null) {
			cleanRemitSlip(mRemitSlip2IV);
		}
		if (mRemitSlip3IV != null) {
			cleanRemitSlip(mRemitSlip3IV);
		}

		mUploadedRemitSlip = false;
		recycleBitmaps();
		refreshUploadRemitSlipTV();
	}

	private void cleanRemitSlip(ImageView imageView) {
		imageView.setVisibility(View.GONE);
		imageView.setImageDrawable(new BitmapDrawable()); // TODO换掉BitmapDrawable
	}

	private void setRemitPic(ImageView imageView, String url) {
		if (imageView != null) {
			// 显示控件
			imageView.setVisibility(View.VISIBLE);
			// 加载图片
			loadBitmap(imageView, url);
			mUploadedRemitSlip = true;
			// 更改上传汇款凭条的按钮
			refreshUploadRemitSlipTV();
		}
	}

	void loadBitmap(ImageView imageView, String url) {
		ImageCacheManager cacheManager = new ImageCacheManager(mContext);
		Bitmap bitmap = cacheManager.getBitmap(url, imageView);
		if (bitmap != null) {
			mBitmapShown.add(bitmap);
		}
	}

	private void refreshUploadRemitSlipTV() {
		if (mUploadRemitSlipTV != null) {
			if (isRemitSlipUploadedAsMuchAsPos()) {
				mUploadRemitSlipTV.setVisibility(View.GONE);
			} else {
				mUploadRemitSlipTV.setVisibility(View.VISIBLE);
				if (isRemitSlipUploaded()) {
					mUploadRemitSlipTV.setText("");
				} else {
					mUploadRemitSlipTV.setText("上传汇款凭条");
				}
			}
		}
	}

	/**
	 * 
	 * 是否上传了所有的汇款凭条
	 * 
	 * @time 2014年5月16日 下午3:38:22
	 * @author jie.liu
	 * @return
	 */
	private boolean isRemitSlipUploadedAsMuchAsPos() {
		if (isTheRemitSlipUploaded(mRemitSlip1Url)
				&& isTheRemitSlipUploaded(mRemitSlip2Url)
				&& isTheRemitSlipUploaded(mRemitSlip3Url)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 是否上传过汇款凭条
	 * 
	 * @time 2014年5月16日 下午3:36:24
	 * @author jie.liu
	 * @return
	 */
	private boolean isRemitSlipUploaded() {
		if (isTheRemitSlipUploaded(mRemitSlip1Url)
				|| isTheRemitSlipUploaded(mRemitSlip2Url)
				|| isTheRemitSlipUploaded(mRemitSlip3Url)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * 该URL的汇款凭条是否上传了
	 * 
	 * @time 2014年5月16日 下午3:37:08
	 * @author jie.liu
	 * @param url
	 * @return
	 */
	private boolean isTheRemitSlipUploaded(String url) {
		return TextUtils.isEmpty(url) ? false : true;
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mListener == null) {
				return;
			}

			switch (v.getId()) {
			case R.id.yellow_btn:
				// 黄色按钮
				mListener.onBtnClickListener(mVariablePartType);
				break;
			case R.id.remit_slip_one_iv:
				// 汇款凭条第一张
				mListener.onRemitSlipPicOneClickListener(mRemitSlip1Url);
				break;
			case R.id.remit_slip_two_iv:
				// 汇款凭条第二张
				mListener.onRemitSlipPicTwoClickListener(mRemitSlip2Url);
				break;
			case R.id.remit_slip_three_iv:
				// 汇款凭条第三张
				mListener.onRemitSlipPicThreeClickListener(mRemitSlip3Url);
				break;
			case R.id.upload_remit_slip_tv:
				// 上传汇款凭条按钮
				mListener.onUploadRemitSlipBtnClickListener();
				break;
			default:
				Log.d(DealRecordAxisView.class.getSimpleName(), "点击了未知的按钮");
			}
		}
	}

	public void setOnViewClickListener(OnViewClickListener listener) {
		if (listener != null && mListener != listener) {
			mListener = listener;
		}
	}

	private void recycleBitmaps() {
		if (mBitmapShown != null && mBitmapShown.size() > 0) {
			performRecycleBitmaps();
			mBitmapShown.clear();
		}
	}

	private void performRecycleBitmaps() {
		for (Bitmap bitmap : mBitmapShown) {
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
		}
	}

	public interface OnViewClickListener {

		/**
		 * 
		 * 可变部分，单个按钮被点击
		 * 
		 * @time 2014年5月17日 下午4:19:46
		 * @author jie.liu
		 */
		void onBtnClickListener(VariablePartType variablePartType);

		/**
		 * 
		 * 可变部分,从左数第一张汇款凭条被点击
		 * 
		 * @time 2014年5月17日 下午4:26:26
		 * @author jie.liu
		 * @param picUrl
		 *            汇款凭条的url
		 */
		void onRemitSlipPicOneClickListener(String picUrl);

		/**
		 * 
		 * 可变部分,从左数第二张汇款凭条被点击
		 * 
		 * @time 2014年5月17日 下午4:18:41
		 * @author jie.liu
		 * @param picUrl
		 *            汇款凭条的url
		 */
		void onRemitSlipPicTwoClickListener(String picUrl);

		/**
		 * 
		 * 可变部分,从左数第三张汇款凭条被点击
		 * 
		 * @time 2014年5月17日 下午4:18:41
		 * @author jie.liu
		 * @param picUrl
		 *            汇款凭条的url
		 */
		void onRemitSlipPicThreeClickListener(String picUrl);

		/**
		 * 
		 * 上传汇款凭条的按钮被点击
		 * 
		 * @time 2014年5月17日 下午4:21:58
		 * @author jie.liu
		 */
		void onUploadRemitSlipBtnClickListener();
	}

	public Context getmContext() {
		return mContext;
	}

	public RecordType getmRecordType() {
		return mRecordType;
	}

	public VariablePartType getmVariablePartType() {
		return mVariablePartType;
	}

	public String getmIndex() {
		return mIndex;
	}

	public String getmDate() {
		return mDate;
	}

	public List<String> getmRemitSlipPicUrls() {
		return mRemitSlipPicUrls;
	}

	public boolean ismHasChoosenContractAddr() {
		return mHasChoosenContractAddr;
	}

	public String getmIdPicUrl() {
		return mIdPicUrl;
	}

	public String getmRemitSlip1Url() {
		return mRemitSlip1Url;
	}

	public String getmRemitSlip2Url() {
		return mRemitSlip2Url;
	}

	public String getmRemitSlip3Url() {
		return mRemitSlip3Url;
	}

	public boolean ismUploadedIdPic() {
		return mUploadedIdPic;
	}

	public boolean ismUploadedRemitSlip() {
		return mUploadedRemitSlip;
	}

	public boolean isChoosedAddr() {
		return mChoosedAddr;
	}

	public LayoutInflater getmInflater() {
		return mInflater;
	}

	public String getmContent() {
		return mContent;
	}

	public boolean ismChoosedAddr() {
		return mChoosedAddr;
	}

	public View getmRootView() {
		return mRootView;
	}

	public View getmRemitSlipRootView() {
		return mRemitSlipRootView;
	}

	public View getmIdPicRootView() {
		return mIdPicRootView;
	}

	public View getmBtnRootView() {
		return mBtnRootView;
	}

	public TextView getmIndexTV() {
		return mIndexTV;
	}

	public LinearLayout getmDealRecordItemLlyt() {
		return mDealRecordItemLlyt;
	}

	public LinearLayout getmVariablePartLlyt() {
		return mVariablePartLlyt;
	}

	public TextView getmDateTV() {
		return mDateTV;
	}

	public TextView getmContentTV() {
		return mContentTV;
	}

	public ImageView getmIdPicIV() {
		return mIdPicIV;
	}

	public Button getmSingleBtn() {
		return mSingleBtn;
	}

	public ImageView getmRemitSlip1IV() {
		return mRemitSlip1IV;
	}

	public ImageView getmRemitSlip2IV() {
		return mRemitSlip2IV;
	}

	public ImageView getmRemitSlip3IV() {
		return mRemitSlip3IV;
	}

	public TextView getmUploadRemitSlipTV() {
		return mUploadRemitSlipTV;
	}

	public ClickListener getmClickListener() {
		return mClickListener;
	}

	public int getTEXT_COLOR_TYPE_DONE() {
		return TEXT_COLOR_TYPE_DONE;
	}

	public int getTEXT_COLOR_TYPE_UNDONE() {
		return TEXT_COLOR_TYPE_UNDONE;
	}

	public void setChoosedAddr(boolean mChoosedAddr) {
		this.mChoosedAddr = mChoosedAddr;
		invalidateUi();
	}

	@Override
	public void onGetBitmapNetworkNotFound(String url) {
	}

	@Override
	public void onGetBitmapBegin(String url) {
		LogUtil.d(mContext, "开始下载图片");
	}

	@Override
	public void onGetBitmapOver(String url, Bitmap bitmap) {
		if (bitmap != null) {
			mBitmapShown.add(bitmap);
		}
	}

	@Override
	public void onGetBitmapError(String url, Exception e) {
		LogUtil.e(mContext, "获取图片出错");
	}
}

package com.hct.zc.activity.mine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.activity.product.ProductDetailActivity;
import com.hct.zc.activity.setting.SetAddressActivity;
import com.hct.zc.activity.setting.SetCidActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.DealRecord;
import com.hct.zc.bean.ImageInfo;
import com.hct.zc.bean.Records;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.RecordsResult;
import com.hct.zc.http.result.UploadRemitSlipResult;
import com.hct.zc.pic.CropImageActivity;
import com.hct.zc.pic.UploadUtil;
import com.hct.zc.pic.UploadUtil.OnUploadProcessListener;
import com.hct.zc.utils.Const;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.utils.imagecache.ImageCacheManager;
import com.hct.zc.utils.imagecache.ImageFileCache;
import com.hct.zc.widget.DealRecordAxisView;
import com.hct.zc.widget.DealRecordAxisView.OnViewClickListener;
import com.hct.zc.widget.DealRecordAxisView.RecordType;
import com.hct.zc.widget.DealRecordAxisView.VariablePartType;
import com.hct.zc.widget.TimeBackwordsView;
import com.hct.zc.widget.TimeBackwordsView.OnBackwordsListener;
import com.hct.zc.widget.TitleBar;
import com.hct.zc.widget.ZCDialog;
import com.umeng.analytics.MobclickAgent;

public class BusinessFormDetailActivity extends BaseHttpActivity implements
		OnViewClickListener, OnUploadProcessListener, OnBackwordsListener {

	public static final int PHOTO_ZOOM = 1;
	public static final int IMAGE_RESULT = 2;

	/**
	 * 显示倒计时的布局
	 */
	private LinearLayout mTimeBackwordsLlyt;

	private TimeBackwordsView mTimeBackwordsView;

	private Button mUrgeServerBtn;

	private DealRecord mDealRecord;
	/**
	 * 流程表的父布局
	 */
	private LinearLayout mDealRecordsLlyt;
	/**
	 * 流程表的数据
	 */
	private List<Records> mRecords;

	/**
	 * 下一步流程
	 */
	private Records mNextState;

	/**
	 * 显示汇款凭条的那一项的View
	 */
	private DealRecordAxisView mRemitSlipItemDRAV;
	/**
	 * 汇款凭条
	 */
	private List<ImageInfo> mRemitSlips;
	/**
	 * 显示大图的汇款凭条
	 */
	private ImageInfo mImageShown;
	private ClickListener mClickListener;
	private ZCDialog mDialog;
	private File mTempFile;
	private Bitmap mBitmapUploaded;

	// 预约成功是当前最后一步完成的动作时才显示“发送缴费短信”按钮
	private static final String REVERSE_SUCCESS = "预约成功";
	private static final String REMIT_UNDONE = "待打款";
	private static final String REMIT_SUCCESS = "打款成功";
	private static final String SIGN_CONTRACT_DONE = "已签合同";
	private static final String SETTLE_COMMISSION_DONE = "已结算佣金";
	private static final String SIGN_CONTRACT_UNDONE = "待签合同";
	private static final String SETTLE_COMMISSION_UNDONE = "待结算佣金";

	// 隐藏银行卡的情况
	private static final String REVERSE_FAIL = "预约失败";
	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2; //
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;

	/** 从相册选择照片 **/
	private static final int FLAG_CHOOSE_FROM_IMGS = 100;
	/** 从手机获取照片 **/
	private static final int FLAG_CHOOSE_FROM_CAMERA = FLAG_CHOOSE_FROM_IMGS + 1;
	/** 选择完过后 **/
	private static final int FLAG_MODIFY_FINISH = FLAG_CHOOSE_FROM_CAMERA + 1;
	/** 选择邮寄地址 **/
	public static final int FLAG_CHOOSE_DLV_ADDR = 3;
	/** 验证身份 **/
	public static final int FLAG_VERIFY_IDENTIFICATION = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_form_detail_activity);
		mDealRecord = (DealRecord) getIntent().getExtras().get("dealRecord");
		if (mDealRecord == null) {
			mDealRecord = new DealRecord();
		}

		initViews();
		initData();
	}

	private void initViews() {
		initTitlebar();
		TextView fullNameTV = (TextView) findViewById(R.id.product_title_tv);
		TextView cusNameTV = (TextView) findViewById(R.id.cus_name_tv);
		TextView phoneTV = (TextView) findViewById(R.id.phone_tv);
		TextView moneyTV = (TextView) findViewById(R.id.money_tv);
		TextView bankNumTV = (TextView) findViewById(R.id.bank_num_tv);
		TextView accountNameTV = (TextView) findViewById(R.id.account_name_tv);
		TextView bankNameTV = (TextView) findViewById(R.id.bank_name_tv);
		Button callServerBtn = (Button) findViewById(R.id.call_server_btn);

		mTimeBackwordsLlyt = (LinearLayout) findViewById(R.id.time_backwords_llyt);
		mTimeBackwordsView = (TimeBackwordsView) findViewById(R.id.time_backwords_view);
		mTimeBackwordsView.setOnBackwordsListener(this);
		mUrgeServerBtn = (Button) findViewById(R.id.urge_btn);
		mDealRecordsLlyt = (LinearLayout) findViewById(R.id.list_llyt);

		fullNameTV.setText(mDealRecord.getFullname());
		cusNameTV.setText(mDealRecord.getCus_name());
		phoneTV.setText(mDealRecord.getCus_phone());
		moneyTV.setText(mDealRecord.getMoney() + "万");
		bankNumTV.setText("汇款账号：" + mDealRecord.getAccount_num());
		accountNameTV.setText("信托户名：" + mDealRecord.getAccount_name());
		bankNameTV.setText("开户银行：" + mDealRecord.getAccount_bank());

		if (mClickListener == null) {
			mClickListener = new ClickListener();
		}
		fullNameTV.setOnClickListener(mClickListener);
		callServerBtn.setOnClickListener(mClickListener);
		mUrgeServerBtn.setOnClickListener(mClickListener);

		hideBankcardIfNeed();
	}

	private void initTitlebar() {
		new TitleBar(this).initTitleBar(mDealRecord.getPro_name());
	}

	private void hideBankcardIfNeed() {
		if (mDealRecord == null) {
			hideBankcard();
		}

		String bankcardNum = mDealRecord.getAccount_num();
		if (TextUtils.isEmpty(bankcardNum)) {
			hideBankcard();
		}
	}

	private void initData() {
		String invid = mDealRecord.getInvid();
		if (mRecords != null) {
			mRecords.clear();
		}
		if (mDealRecordsLlyt != null) {
			mDealRecordsLlyt.removeAllViews();
			mDealRecordsLlyt.invalidate();
		}
		HttpRequest.getDealRecords(this, invid, this);
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		super.onHttpSuccess(path, result);
		if (TextUtils.isEmpty(result)) {
			Toaster.showShort(this, "服务器出错了，请重试");
			LogUtil.e(this, "网络返回的数据为空");
			return;
		}
		dealWithHttpReturned(path, result);
	}

	private void dealWithHttpReturned(String path, String result) {
		Gson gson = new Gson();
		if (path.equals(HttpUrl.DEL_REMIT_SLIP)) {
			delRemitSlipReturned(result, gson);
		} else if (path.equals(HttpUrl.REQUEST_UPDATE_DLVY_ADDRESS)) {
			updateDlvAddrReturned(result, gson);
		} else if (path.equals(HttpUrl.GET_DEAL_RECORDS)) {
			getDealRecordsReturned(result, gson);
		} else if (path.equals(HttpUrl.UPDATE_APPOINT_DATE)) {
			updateAppointDateReturned(result, gson);
		}
	}

	private void delRemitSlipReturned(String result, Gson gson) {
		HttpResult r = gson.fromJson(result, HttpResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			Toaster.showShort(this, "删除成功");
			// 更新本地对应图片的动作
			refreshLocalRemitSlip();
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
			}
		} else if (HttpResult.FAIL.equals(resultCode)) {
			Toaster.showShort(this, "删除失败");
		} else {
			Toaster.showShort(this, "请求出错，请重试");
		}
	}

	private void refreshLocalRemitSlip() {
		if (mImageShown != null && mImageShown.getPic() != null) {
			new ImageCacheManager(this).delBitmap(mImageShown.getPic());
		}

		if (mRemitSlipItemDRAV != null) {
			mRemitSlipItemDRAV.clearRemitSlips();
			removeTheImageInfo();
			mImageShown = null;
			mRemitSlipItemDRAV.setRemitSlipPics(getPicUrls());
		}
	}

	private void removeTheImageInfo() {
		if (mImageShown == null) {
			return;
		}

		for (Iterator<ImageInfo> it = mRemitSlips.iterator(); it.hasNext();) {
			ImageInfo info = it.next();
			if (mImageShown.equals(info)) {
				it.remove(); // 必须用这个方法删除，理由http://guojuanjun.blog.51cto.com/277646/1348450
			}
		}
	}

	/**
	 * 
	 * @todo 获取汇款凭条的图片地址
	 * @time 2014年5月20日 下午5:18:32
	 * @author jie.liu
	 * @return
	 */
	private List<String> getPicUrls() {
		if (mRemitSlips == null) {
			return new ArrayList<String>();
		}

		List<String> urls = new ArrayList<String>();
		for (ImageInfo info : mRemitSlips) {
			urls.add(info.getPic());
		}

		return urls;
	}

	private void updateDlvAddrReturned(String result, Gson gson) {
		HttpResult r = gson.fromJson(result, HttpResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			Toaster.showShort(BusinessFormDetailActivity.this, "更改邮寄地址成功");
			initData();
		} else if (HttpResult.FAIL.equals(resultCode)) {
			Toaster.showShort(BusinessFormDetailActivity.this, "更改邮寄地址失败,请重试!");
		} else {
			Toaster.showShort(this, "请求出错，请重试");
		}
	}

	private void updateAppointDateReturned(String result, Gson gson) {
		HttpResult r = gson.fromJson(result, HttpResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			urgeServerBtnEnables(false);
			mTimeBackwordsView.setTime(0, 30, 0);
			mTimeBackwordsView.setTimeEnable(true);
			mTimeBackwordsView.startBackwords();
		} else if (HttpResult.FAIL.equals(resultCode)) {
			Toaster.showShort(BusinessFormDetailActivity.this, "催单失败,请重试!");
		} else {
			Toaster.showShort(this, "请求出错，请重试");
		}
	}

	private void getDealRecordsReturned(String result, Gson gson) {
		RecordsResult r = gson.fromJson(result, RecordsResult.class);
		String resultCode = r.getResult().getErrorcode();
		if (HttpResult.SUCCESS.equals(resultCode)) {
			hideBankcardIfNeeded(r);
			mDealRecordsLlyt.setVisibility(View.VISIBLE);
			showRecords(r);
		} else if (HttpResult.FAIL.equals(resultCode)) {
			hideBankcardIfNeeded(r);
			mTimeBackwordsLlyt.setVisibility(View.VISIBLE);
			startTimeBackwordsIfNeeded(r.now_date);
		} else {
			Toaster.showShort(this, "请求出错，请重试");
		}
	}

	private void hideBankcardIfNeeded(RecordsResult r) {
		List<Records> records = r.getRecords();
		if (records == null || records.size() == 0) {
			hideBankcard();
			return;
		}

		LinearLayout bankCardLlyt = (LinearLayout) findViewById(R.id.bank_card_llyt);
		Records record = records.get(records.size() - 1);
		if (record.getExplain().contains(REVERSE_FAIL)) {
			bankCardLlyt.setVisibility(View.GONE);
		} else {
			bankCardLlyt.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 * @todo 隐藏银行卡帐号，开户名, 开户银行
	 * @time 2014年5月26日 下午2:44:36
	 * @author jie.liu
	 */
	private void hideBankcard() {

	}

	/**
	 * 
	 * @todo 显示流程图
	 * @time 2014年5月17日 下午5:20:27
	 * @author jie.liu
	 * @param r
	 */
	private void showRecords(RecordsResult r) {
		mRecords = r.getRecords();
		mNextState = r.nextState;
		showRecordsDone();
		showRecordsUnDone();
	}

	private void showRecordsDone() {
		if (mRecords == null || mRecords.size() == 0) {
			return;
		}

		for (int i = 0; i < mRecords.size(); i++) {
			DealRecordAxisView view = createItemDone(i);
			if (view != null) {
				mDealRecordsLlyt.addView(view);
			}
		}
	}

	private DealRecordAxisView createItemDone(int index) {
		Records record = mRecords.get(index);
		DealRecordAxisView view = null;
		String content = record.getExplain();
		// "预约成功"状态
		if (REVERSE_SUCCESS.equals(content)) {
			if (index == mRecords.size() - 1) {
				// “预约成功”是最后一步动作时，显示"发送缴费短信"按钮
				view = createDealRecordViewDone(index,
						VariablePartType.TYPE_SEND_SMS);
			} else {
				// "预约成功"不是最后一步时，不显示任何东西
				view = createDealRecordViewDone(index,
						VariablePartType.TYPE_NORMAL);
			}
		}

		// “已结算佣金”或者“待结算佣金”状态
		if (SETTLE_COMMISSION_DONE.equals(content)) {
			// 显示身份证那种Item
			List<ImageInfo> pics = record.getPic();
			if (pics != null && pics.size() != 0) {
				view = createDealRecordViewDone(index,
						VariablePartType.TYPE_SHOW_ID_PIC);
				view.setIdPic(pics.get(0).getPic());
			} else {
				// 没有身份证照片，则显示“上传身份证照片”按钮
				view = createDealRecordViewDone(index,
						VariablePartType.TYPE_UPLOAD_ID_PIC);
			}
		}

		// "已签合同"状态
		if (SIGN_CONTRACT_DONE.equals(content)) {
			view = createDealRecordViewDone(index,
					VariablePartType.TYPE_SIGN_CONTRACT_DONE);
		}

		// “打款成功”状态
		if (REMIT_SUCCESS.equals(content)) {
			view = createRemitITemDone(index, record);
		}

		if (view == null) {
			// 剩下的都是正常的情况，即右侧不显示东西
			view = createDealRecordViewDone(index, VariablePartType.TYPE_NORMAL);
		}

		view.setContent(content);
		view.setDate(record.getRdate());

		return view;
	}

	private DealRecordAxisView createRemitITemDone(int index, Records record) {
		DealRecordAxisView view;
		view = createDealRecordViewDone(index,
				VariablePartType.TYPE_UPLOAD_REMIT_SLIP);
		showRemitSlips(record, view);
		return view;
	}

	private List<String> getRemitSlipUrls() {
		List<String> picUrls = new ArrayList<String>();
		if (mRemitSlips != null && mRemitSlips.size() != 0) {
			for (ImageInfo url : mRemitSlips) {
				picUrls.add(url.getPic());
			}
		}

		return picUrls;
	}

	private DealRecordAxisView createDealRecordViewDone(int index,
			VariablePartType variablePartType) {
		return createDealRecordView(index, RecordType.TYPE_DONE,
				variablePartType);
	}

	private void showRecordsUnDone() {
		if (mNextState.getExplain() == null) {
			return;
		}
		int index = 1;
		if (mRecords != null) {
			index = mRecords.size();
		}

		DealRecordAxisView view = createItemUndone(index);
		if (view != null) {
			mDealRecordsLlyt.addView(view);
		}
	}

	private DealRecordAxisView createItemUndone(int index) {
		DealRecordAxisView view = null;
		String content = mNextState.getExplain();
		// "待打款"状态
		if (REMIT_UNDONE.equals(content)) {
			view = createRemitITemUndone(index, mNextState);
		}

		// "待签合同"状态
		if (SIGN_CONTRACT_UNDONE.equals(content)) {
			view = createDealRecordViewUndone(index,
					VariablePartType.TYPE_SIGN_CONTRACT_UNDONE);
			UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
			if (userInfo != null) {
				String address = userInfo.getAddress();
				if (!TextUtils.isEmpty(address)) {
					view.setChoosedAddr(true);
					view.invalidateUi();
				}
			}
		}

		// “待结算佣金”状态
		if (SETTLE_COMMISSION_UNDONE.equals(content)) {
			// 显示身份证那种Item
			String url = ZCApplication.getInstance().getUserInfo().cardpic0;
			if (!TextUtils.isEmpty(url)) {
				view = createDealRecordViewUndone(index,
						VariablePartType.TYPE_SHOW_ID_PIC);
				view.setIdPic(url);
			} else {
				// 没有身份证照片，则显示“上传身份证照片”按钮
				view = createDealRecordViewUndone(index,
						VariablePartType.TYPE_UPLOAD_ID_PIC);
			}
		}

		if (view == null) {
			// 剩下的都是正常的情况，即右侧不显示东西
			view = createDealRecordViewUndone(index,
					VariablePartType.TYPE_NORMAL);
		}

		view.setContent(content);
		return view;
	}

	private DealRecordAxisView createRemitITemUndone(int index, Records record) {
		DealRecordAxisView view;
		view = createDealRecordViewUndone(index,
				VariablePartType.TYPE_UPLOAD_REMIT_SLIP);
		showRemitSlips(record, view);
		return view;
	}

	private void showRemitSlips(Records record, DealRecordAxisView view) {
		mRemitSlipItemDRAV = view;
		// 显示汇款凭条那种Item
		mRemitSlips = record.getPics();
		List<String> picUrls = getRemitSlipUrls();

		if (picUrls != null && picUrls.size() != 0) {
			view.setRemitSlipPics(picUrls);
		}
	}

	private DealRecordAxisView createDealRecordViewUndone(int index,
			VariablePartType variablePartType) {
		return createDealRecordView(index, RecordType.TYPE_UNDONE,
				variablePartType);
	}

	private DealRecordAxisView createDealRecordView(int index,
			RecordType recordType, VariablePartType variablePartType) {
		DealRecordAxisView axisView = new DealRecordAxisView(this);
		axisView.setUiType(recordType, variablePartType);
		axisView.setIndex(String.valueOf(index + 1));
		axisView.setOnViewClickListener(this);
		return axisView;
	}

	@Override
	public void onBtnClickListener(VariablePartType variablePartType) {
		switch (variablePartType) {
		case TYPE_SEND_SMS:
			scheduleSendSms();
			break;
		case TYPE_SIGN_CONTRACT_DONE:
		case TYPE_SIGN_CONTRACT_UNDONE:
			Intent intent = new Intent(BusinessFormDetailActivity.this,
					SetAddressActivity.class);
			startActivityForResult(intent, FLAG_CHOOSE_DLV_ADDR);
			break;
		case TYPE_UPLOAD_ID_PIC:
			Intent cIdIntent = new Intent(BusinessFormDetailActivity.this,
					SetCidActivity.class);
			startActivityForResult(cIdIntent, FLAG_VERIFY_IDENTIFICATION);
			break;
		default:
			LogUtil.w(this, "在未知的状态下:" + variablePartType.name() + "，黄色按钮被点击了");
		}
	}

	private void scheduleSendSms() {
		Uri smsToUri = Uri.parse("smsto:" + mDealRecord.getCus_phone());
		Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		mIntent.putExtra("sms_body", "尊敬的客户，您所需购买的产品已预约成功，请抓紧时间打款.");
		startActivity(mIntent);
	}

	@Override
	public void onRemitSlipPicOneClickListener(String picUrl) {
		showBigImg(picUrl);
	}

	@Override
	public void onRemitSlipPicTwoClickListener(String picUrl) {
		showBigImg(picUrl);
	}

	@Override
	public void onRemitSlipPicThreeClickListener(String picUrl) {
		showBigImg(picUrl);
	}

	private void showBigImg(String url) {
		if (!REMIT_UNDONE.equals(mNextState.getExplain())) {
			return;
		}

		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int dialogWidth = displayMetrics.widthPixels;
		int dialogHeight = displayMetrics.heightPixels;
		mDialog = new ZCDialog(mActivity, R.style.user_dialog, dialogWidth,
				dialogHeight);
		View view = getLayoutInflater()
				.inflate(R.layout.dialog_big_image, null);

		RelativeLayout bigRlyt = (RelativeLayout) view
				.findViewById(R.id.big_img_rlyt);
		ImageView bigImgIV = (ImageView) view.findViewById(R.id.big_img_iv);
		ImageCacheManager cacheManager = new ImageCacheManager(this);
		cacheManager.getBitmap(url, bigImgIV);

		Button delBtn = (Button) view.findViewById(R.id.del_img_btn);
		if (mClickListener == null) {
			mClickListener = new ClickListener();
		}
		bigRlyt.setOnClickListener(mClickListener);
		delBtn.setOnClickListener(mClickListener);
		mDialog.showDialog(view);

		// 记住大图的信息
		rememberImageInfo(url);
	}

	private void rememberImageInfo(String url) {
		if (mRemitSlips == null)
			return;

		for (ImageInfo info : mRemitSlips) {
			if (url.equals(info.getPic())) {
				mImageShown = info;
			}
		}
	}

	@Override
	public void onUploadRemitSlipBtnClickListener() {
		new AlertDialog.Builder(mActivity)
				.setItems(R.array.phtot_select,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									dialog.dismiss();
									doTakeCamera();
									break;
								case 1:
									dialog.dismiss();
									doSelectPhoto();
									break;
								}
							}
						}).setCancelable(true).show();
	}

	/**
	 * 
	 * @todo 照相
	 * @time 2014-5-16 上午11:54:51
	 * @author liuzenglong163@gmail.com
	 */
	private void doTakeCamera() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				Intent intent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				mTempFile = new File(Const.CID_IMG_STRING_PATH);
				Uri u = Uri.fromFile(mTempFile);
				intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
				startActivityForResult(intent, FLAG_CHOOSE_FROM_CAMERA);
			} catch (ActivityNotFoundException e) {
			}
		} else {
			Toaster.showShort(mActivity, "没有可用的存储卡");
		}
	}

	/**
	 * 
	 * @todo 从相册选择照片
	 * @time 2014-5-16 上午11:55:27
	 * @author liuzenglong163@gmail.com
	 */
	private void doSelectPhoto() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, FLAG_CHOOSE_FROM_IMGS);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == FLAG_CHOOSE_FROM_IMGS) {
			// 从相册中选择图片返回
			if (data != null) {
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri,
							new String[] { MediaStore.Images.Media.DATA },
							null, null, null);
					if (null == cursor) {
						Toast.makeText(mActivity, "图片没找到", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				} else {
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == FLAG_CHOOSE_FROM_CAMERA) {
			// 拍照返回
			if (mTempFile == null) {
				Toaster.showShort(BusinessFormDetailActivity.this, "拍照出错，请重拍");
				return;
			}
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.putExtra("path", mTempFile.getAbsolutePath());
			startActivityForResult(intent, FLAG_MODIFY_FINISH);
		} else if (requestCode == FLAG_MODIFY_FINISH) {
			// 切图返回
			if (data != null) {
				final String path = data.getStringExtra("path");
				mTempFile = new File(path);
				mBitmapUploaded = BitmapFactory.decodeFile(path);
				toUploadFile();
			}
		} else if (requestCode == FLAG_CHOOSE_DLV_ADDR) {
			// 选择邮寄地址回来
			if (data != null) {
				final String address = data.getStringExtra("address");
				updateDlvAddr(address);
			}
		} else if (requestCode == FLAG_VERIFY_IDENTIFICATION) {
			// 验证身份返回
			initData();
		}
	}

	private void updateDlvAddr(final String address) {
		if (!TextUtils.isEmpty(address)) {
			HttpRequest.doUpdateDlvAddress(BusinessFormDetailActivity.this,
					mDealRecord.getInvid(), address,
					BusinessFormDetailActivity.this);
		}
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UploadUtil.TO_UPLOAD_FILE:
				toUploadFile();
				break;
			case UploadUtil.UPLOAD_INIT_PROCESS:
				break;
			case UploadUtil.UPLOAD_IN_PROCESS:
				break;
			case UploadUtil.UPLOAD_FILE_DONE:
				Gson gson = new Gson();
				String result = (String) msg.obj;
				LogUtil.d(BusinessFormDetailActivity.this, result);
				UploadRemitSlipResult r = gson.fromJson(result,
						UploadRemitSlipResult.class);
				String resultCode = r.getResult().getErrorcode();
				if (HttpResult.SUCCESS.equals(resultCode)) {
					Toaster.showShort(BusinessFormDetailActivity.this, "上传成功");
					new ImageFileCache().saveBitmap(mBitmapUploaded,
							mTempFile.getAbsolutePath());
					recycleBitmap();
					ImageInfo imageInfo = r.pic;
					mRemitSlips.add(imageInfo);
					List<String> picUrls = getRemitSlipUrls();
					mRemitSlipItemDRAV.setRemitSlipPics(picUrls);
				} else if (HttpResult.FAIL.equals(resultCode)) {
					Toaster.showShort(BusinessFormDetailActivity.this, "上传失败");
				} else {
					Toaster.showShort(BusinessFormDetailActivity.this,
							"上传图片出错，请重试");
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void toUploadFile() {
		LoadingProgress.getInstance().show(mActivity, "上传图片。。。");
		String fileKey = "pic";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();
		params.put("invid", mDealRecord.getInvid());
		params.put("zhname", mTempFile.getName());

		uploadUtil.uploadFile(mTempFile, fileKey, HttpUrl.UPLOAD_REMIT_SLIP,
				params);
	}

	private void recycleBitmap() {
		if (mBitmapUploaded != null) {
			mBitmapUploaded.recycle();
			mBitmapUploaded = null;
		}
	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.big_img_rlyt:
				mDialog.dismiss();
				break;
			case R.id.del_img_btn:
				delRemitSlip();
				break;
			case R.id.product_title_tv:
				Intent intent = new Intent(BusinessFormDetailActivity.this,
						ProductDetailActivity.class);
				intent.putExtra("productId", mDealRecord.getProid());
				startActivity(intent);
				break;
			case R.id.call_server_btn:
				String clientService = getResources().getString(
						R.string.client_service_phone);
				Intent callIntent = new Intent(Intent.ACTION_DIAL,
						Uri.parse("tel:" + clientService));
				startActivity(callIntent);
				break;
			case R.id.urge_btn:
				updateAppointDate();
				break;
			default:
				break;
			}
		}
	}

	private void delRemitSlip() {
		if (mImageShown == null) {
			LogUtil.e(BusinessFormDetailActivity.this, "无法定位需要删除的图片，请反馈");
			return;
		}

		performDelRemitSlip();
	}

	private void performDelRemitSlip() {
		String picid = mImageShown.getPicid();
		String picname = mImageShown.getPicname();

		HttpRequest.delRemitSlip(this, picid, picname, this);
	}

	/**
	 * 上传服务器响应回调
	 */
	@Override
	public void onUploadDone(int responseCode, String message) {
		LoadingProgress.getInstance().dismiss();
		Message msg = Message.obtain();
		msg.what = UploadUtil.UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler.sendMessage(msg);
	}

	@Override
	public void onUploadProcess(int uploadSize) {
		Message msg = Message.obtain();
		msg.what = UploadUtil.UPLOAD_IN_PROCESS;
		msg.arg1 = uploadSize;
		handler.sendMessage(msg);
	}

	@Override
	public void initUpload(int fileSize) {
		Message msg = Message.obtain();
		msg.what = UploadUtil.UPLOAD_INIT_PROCESS;
		msg.arg1 = fileSize;
		handler.sendMessage(msg);
	}

	private void startTimeBackwordsIfNeeded(String serviceCurrentTime) {
		if (TextUtils.isEmpty(serviceCurrentTime)) {
			return;
		}
		long currentMilSecond = Long.valueOf(serviceCurrentTime);

		if (TextUtils.isEmpty(mDealRecord.appoint_date)) {
			return;
		}
		long appointMilSecond = Long.valueOf(mDealRecord.appoint_date);

		LogUtil.d(this, "当前时间：" + currentMilSecond + "预约时间:" + appointMilSecond);

		if (!gapBiggerThanHalfAnHour(currentMilSecond, appointMilSecond)) {
			// 30分钟的秒数减去 预约到现在的时间秒数
			mTimeBackwordsView.setTime(30 * 60 - clacGap(currentMilSecond,
					appointMilSecond));
			mTimeBackwordsView.setTimeEnable(true);
			mTimeBackwordsView.startBackwords();

			urgeServerBtnEnables(false);
		} else {
			urgeServerBtnEnables(true);
		}
	}

	private void urgeServerBtnEnables(boolean enable) {
		mUrgeServerBtn.setEnabled(enable);
		mUrgeServerBtn.setClickable(enable);

		if (enable) {
			mUrgeServerBtn.setBackgroundResource(R.color.jiuhong);
		} else {
			mUrgeServerBtn.setBackgroundResource(R.color.gray_dark);
		}
	}

	private boolean gapBiggerThanHalfAnHour(long bigMilSencond,
			long smallMilSecond) {
		long gap = clacGap(bigMilSencond, smallMilSecond);
		LogUtil.d(this, "时间差:" + gap);
		if (gap > 1800) { // 1800秒 即30分钟
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * 算出相差几秒
	 * 
	 * @time 2014年7月1日 下午6:26:39
	 * @author liuzenglong163@gmail.com
	 * @param bigMilSencond
	 * @param smallMilSecond
	 * @return
	 */
	private long clacGap(long bigMilSencond, long smallMilSecond) {
		long gap = 0;
		if (bigMilSencond >= smallMilSecond) {
			gap = bigMilSencond - smallMilSecond;
		} else {
			gap = smallMilSecond - bigMilSencond;
		}
		return gap / 1000; // 去掉毫秒，留下秒级别
	}

	@Override
	public void onBackwordsOver() {
		urgeServerBtnEnables(true);
	}

	private void updateAppointDate() {
		HttpRequest.updateAppointDate(BusinessFormDetailActivity.this,
				mDealRecord.getInvid(), BusinessFormDetailActivity.this);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTimeBackwordsView.stopBackwords();
	}

}

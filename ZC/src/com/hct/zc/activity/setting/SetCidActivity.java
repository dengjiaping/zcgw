package com.hct.zc.activity.setting;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hct.zc.R;
import com.hct.zc.activity.base.BaseHttpActivity;
import com.hct.zc.application.ZCApplication;
import com.hct.zc.http.HttpRequest;
import com.hct.zc.http.HttpUrl;
import com.hct.zc.http.result.HttpResult;
import com.hct.zc.http.result.Result;
import com.hct.zc.pic.CropImageActivity;
import com.hct.zc.pic.UploadUtil;
import com.hct.zc.pic.UploadUtil.OnUploadProcessListener;
import com.hct.zc.utils.Const;
import com.hct.zc.utils.InputFormatChecker;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.LogUtil;
import com.hct.zc.utils.Toaster;
import com.hct.zc.utils.imagecache.ImageCacheManager;
import com.hct.zc.utils.imagecache.ImageFileCache;
import com.hct.zc.widget.CstEditText;
import com.hct.zc.widget.TitleBar;
import com.hct.zc.widget.ZCDialog;

/**
 * @todo 绑定身份证信息
 * @time 2014-5-10 下午3:36:51
 * @author liuzenglong163@gmail.com
 */

public class SetCidActivity extends BaseHttpActivity implements
		OnClickListener, OnUploadProcessListener {
	/** 从相册选择照片 **/
	private static final int FLAG_CHOOSE_FROM_IMGS = 100;
	/** 从手机获取照片 **/
	private static final int FLAG_CHOOSE_FROM_CAMERA = FLAG_CHOOSE_FROM_IMGS + 1;
	/** 选择完过后 **/
	private static final int FLAG_MODIFY_FINISH = FLAG_CHOOSE_FROM_CAMERA + 1;

	private final int FLAG_ID_PIC_1 = 1;
	private final int FLAG_ID_PIC_2 = 2;

	private int mFlagIdPic = FLAG_ID_PIC_1;

	private ImageView idPic1IV, idPic2IV;
	private CstEditText etName, etCid;
	private Button btnModify;
	private boolean hasVerfiy = false; // 是否已经验证
	private File tempFile = null; // 文件
	private Bitmap mBitmapUploaded;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_cid_layout);
		initView();
	}

	private void initView() {
		new TitleBar(mActivity).initTitleBar("设置身份信息");

		etName = (CstEditText) findViewById(R.id.et_name);

		etCid = (CstEditText) findViewById(R.id.et_cid_code);
		initEtName();
		initEtCid();
		// btnUpFile = (Button) findViewById(R.id.btn_upfile);
		// btnUpFile.setOnClickListener(this);

		idPic1IV = (ImageView) findViewById(R.id.iv_show);
		idPic2IV = (ImageView) findViewById(R.id.iv_show2);
		idPic1IV.setOnClickListener(this);
		idPic2IV.setOnClickListener(this);

		initIdPics();

		btnModify = (Button) findViewById(R.id.btn_ok);
		btnModify.setOnClickListener(this);

		initBindBtn();

	}

	private void initEtName() {
		etName.setInitName(R.string.setting_title_name,
				R.string.setting_title_name_input);
		mUserInfo = ZCApplication.getInstance().getUserInfo();
		if (mUserInfo == null) {
			return;
		}

		if (!TextUtils.isEmpty(mUserInfo.getName())
				&& mUserInfo.getName().length() > 1) {
			etName.setText("*"
					+ mUserInfo.getName().substring(1,
							mUserInfo.getName().length()));
			etName.setEditable(false);
		}
	}

	private void initEtCid() {
		etCid.setInitName(R.string.setting_title_cid,
				R.string.setting_title_cid_input);
		etCid.setInputType(InputType.TYPE_CLASS_NUMBER);
		mUserInfo = ZCApplication.getInstance().getUserInfo();
		if (mUserInfo == null) {
			return;
		}

		if (!TextUtils.isEmpty(mUserInfo.getIdcard())
				&& mUserInfo.getIdcard().length() > 4) {
			etCid.setText(mUserInfo.getIdcard().replace(
					mUserInfo.getIdcard().substring(4,
							mUserInfo.getIdcard().length()), "********"));
			etCid.setEditable(false);
		}
	}

	private void initIdPics() {
		mUserInfo = ZCApplication.getInstance().getUserInfo();
		if (mUserInfo == null) {
			return;
		}

		ImageCacheManager cacheManager = new ImageCacheManager(this);
		if (!TextUtils.isEmpty(mUserInfo.cardpic0)) {
			cacheManager.getBitmap(mUserInfo.cardpic0, idPic1IV);
		}

		if (!TextUtils.isEmpty(mUserInfo.cardpic1)) {
			cacheManager.getBitmap(mUserInfo.cardpic1, idPic2IV);
		}

		if (TextUtils.isEmpty(mUserInfo.getName())) {
			idPic1IV.setClickable(true);
			idPic2IV.setClickable(true);
		} else {
			idPic1IV.setClickable(false);
			idPic2IV.setClickable(false);
		}
	}

	private void initBindBtn() {
		btnModify.setText("修改");
		mUserInfo = ZCApplication.getInstance().getUserInfo();
		if (mUserInfo == null) {
			return;
		}
		if (TextUtils.isEmpty(mUserInfo.getName())
				&& TextUtils.isEmpty(mUserInfo.getIdcard())) {// 如果两个都为空，则不需要解除绑定
			hasVerfiy = true;
			btnModify.setText("绑定");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ok:
			if (!hasVerfiy) { // 如果没有验证则需要验证
				createDialog();
				btnModify.setText("修改");
			} else {
				requestModifyCId();
			}
			break;
		case R.id.iv_show:
			mFlagIdPic = FLAG_ID_PIC_1;
			schedulePickBitmap();
			break;
		case R.id.iv_show2:
			mFlagIdPic = FLAG_ID_PIC_2;
			schedulePickBitmap();
			break;
		}
	}

	private void schedulePickBitmap() {
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
				tempFile = new File(Const.CID_IMG_STRING_PATH);
				Uri u = Uri.fromFile(tempFile);
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

	/**
	 * 
	 * @todo 修改银行卡信息
	 * @time 2014-5-12 下午6:38:21
	 * @author liuzenglong163@gmail.com
	 */
	private void requestModifyCId() {
		String name = etName.getText();
		String cid = etCid.getText();
		if (TextUtils.isEmpty(name)) {
			Toaster.showShort(mActivity, "请输入您的名字");
			return;
		}
		if (name.length() < 2) {
			Toaster.showShort(mActivity, "名字长度不少于两个字符");
			return;
		}

		if (TextUtils.isEmpty(cid)) {
			Toaster.showShort(mActivity, "请输入您的身份证号");
			return;
		}
		if (!InputFormatChecker.isIdCardEligible(cid)) {
			Toaster.showShort(mActivity, "身份证号格式不正确");
			return;
		}

		HttpRequest.doModifyCID(mActivity,
				String.valueOf(mUserInfo.getUserId()), name, cid, this);
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
		if (HttpUrl.CHECK_PASSWORD.endsWith(path)) { // 验证密码
			Gson gson = new Gson();
			Result r = gson.fromJson(result, HttpResult.class).getResult();
			if (HttpResult.SUCCESS.equals(r.getErrorcode())) {
				hasVerfiy = true;
				etName.setEditable(true);
				etName.setText(mUserInfo.getName());
				etName.getEtContent().setSelection(etName.getText().length());
				etCid.setText(mUserInfo.getIdcard());
				etCid.setEditable(true);
				idPic1IV.setClickable(true);
				idPic2IV.setClickable(true);
			} else if (HttpResult.FAIL.equals(r.getErrorcode())
					|| HttpResult.ARG_ERROR.equals(r.getErrorcode())) {
				Toaster.showShort(mActivity, r.getErrormsg());
			}
		} else if (HttpUrl.UPDATE_PERSON_INFO.equals(path)) {
			Gson gson = new Gson();
			Result r = gson.fromJson(result, HttpResult.class).getResult();
			if (HttpResult.SUCCESS.equals(r.getErrorcode())) {
				Toaster.showShort(mActivity, "修改成功");
				setResult(RESULT_OK);
				saveCID();
				finish();
			} else {
				Toaster.showShort(mActivity, r.getErrormsg());
			}
		}
	}

	/**
	 * 
	 * @todo 密码验证框
	 * @author lzlong@zwmob.com
	 * @time 2014-3-26 下午2:09:30
	 */
	private void createDialog() {
		int screenWidth = (int) (getWindowManager().getDefaultDisplay()
				.getWidth() * Const.DIALOG_LOGIN_WIDTH); // 屏幕宽
		int screenHeight = (int) (getWindowManager().getDefaultDisplay()
				.getHeight() * Const.DIALOG_LOGIN_HEIGHT); // 屏幕高

		final ZCDialog dialog = new ZCDialog(mActivity, R.style.user_dialog,
				screenWidth, screenHeight);

		final View verifyView = LayoutInflater.from(mActivity).inflate(
				R.layout.dialog_verify_code_layout, null);

		verifyView.requestFocus();

		final EditText etPwd = (EditText) verifyView.findViewById(R.id.et_pwd);

		final Button btnCancel = (Button) verifyView
				.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		Button btnOk = (Button) verifyView.findViewById(R.id.btn_ok);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (TextUtils.isEmpty(etPwd.getText().toString())) {
					Toaster.showShort(mActivity, "请输入密码");
					return;
				} else {
					requestVerifyPwd(etPwd.getText().toString());
					dialog.dismiss();
				}

			}
		});

		dialog.setCancelable(true);
		dialog.showDialog(verifyView);
	}

	/**
	 * 
	 * @todo 提交密码验证信息
	 * @time 2014-5-12 上午11:17:51
	 * @author liuzenglong163@gmail.com
	 */
	private void requestVerifyPwd(String password) {
		HttpRequest
				.doVerfiyPwd(mActivity, mUserInfo.getPhone(), password, this);
	}

	/**
	 * 
	 * @todo 保存银行卡信息
	 * @time 2014-5-12 下午4:38:54
	 * @author liuzenglong163@gmail.com
	 */
	private void saveCID() {
		mUserInfo.setName(etName.getText().toString());
		mUserInfo.setIdcard(etCid.getText().toString());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == FLAG_CHOOSE_FROM_IMGS) {
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
			if (tempFile == null) {
				LogUtil.w(this, "tempFile:" + tempFile);
				initEtName();
				initEtCid();
				initIdPics();
				initBindBtn();
				idPic1IV.setClickable(true);
				idPic2IV.setClickable(true);
				Toaster.showShort(SetCidActivity.this, "拍照出错，请重拍");
				return;
			}
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.putExtra("path", tempFile.getAbsolutePath());
			startActivityForResult(intent, FLAG_MODIFY_FINISH);
		} else if (requestCode == FLAG_MODIFY_FINISH) {
			if (data != null) {
				final String path = data.getStringExtra("path");
				tempFile = new File(path);
				mBitmapUploaded = BitmapFactory.decodeFile(path);
				showIdPic();
				toUploadFile();
			}
		}
	}

	private void showIdPic() {
		if (mFlagIdPic == FLAG_ID_PIC_1) {
			idPic1IV.setImageBitmap(mBitmapUploaded);
		} else if (mFlagIdPic == FLAG_ID_PIC_2) {
			idPic2IV.setImageBitmap(mBitmapUploaded);
		}
	}

	/**
	 * 
	 * @todo 图片上传
	 * @time 2014-5-21 下午2:06:14
	 * @author liuzenglong163@gmail.com
	 */
	private void toUploadFile() {
		LoadingProgress.getInstance().show(mActivity, "上传图片。。。");
		String fileKey = "pic";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		;
		uploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", mUserInfo.getUserId());
		if (mFlagIdPic == FLAG_ID_PIC_1) {
			params.put("flag", "0");
		} else {
			params.put("flag", "1");
		}
		uploadUtil.uploadFile(tempFile, fileKey, HttpUrl.DO_UPLOAD_SFZ, params);
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
				try {
					JSONObject json = new JSONObject(msg.obj.toString())
							.getJSONObject("result");
					if (json.has("errorcode")
							&& HttpResult.SUCCESS.equals(json
									.getString("errorcode"))) {
						new ImageFileCache().saveBitmap(mBitmapUploaded,
								tempFile.getAbsolutePath());
						Toaster.showShort(mActivity, "上传成功");
					} else {
						Toaster.showShort(mActivity, "上传失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

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
}

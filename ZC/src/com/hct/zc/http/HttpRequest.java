package com.hct.zc.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.text.TextUtils;

import com.hct.zc.application.ZCApplication;
import com.hct.zc.bean.AddressBean;
import com.hct.zc.bean.UserInfo;
import com.hct.zc.http.HttpHelper.OnHttpResponse;
import com.hct.zc.pic.UploadUtil;
import com.hct.zc.utils.ContextUtil;
import com.hct.zc.utils.LoadingProgress;
import com.hct.zc.utils.ZCUtils;

/**
 * 接口请求类
 * 
 * @time 2014-5-12 上午11:20:01
 * @author liuzenglong163@gmail.com
 */

public class HttpRequest {

	/**
	 * 
	 * 验证密码
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doVerfiyPwd(Activity activity, String phone,
			String password, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("password", password);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.CHECK_PASSWORD, params);
	}

	/**
	 * 
	 * 获取验证码
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param tab
	 *            "0":注册的时候传 ，"1": 已存在的用户传
	 * @param onHttpResponse
	 */
	public static void getVerifyCode(Activity activity, String phone,
			String tab, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("tab", tab);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.VERIFICATION_CODE, params);
	}

	/**
	 * 
	 * 修改手机号
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doModifyPhone(Activity activity, String userId,
			String phone, String verifyCode, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", userId);
		params.put("phone", phone);
		params.put("verifycode", verifyCode);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.UPDATE_PERSON_INFO, params);
	}

	/**
	 * 
	 * 修改身份证信息
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doModifyCID(Activity activity, String userId,
			String name, String cid, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", userId);
		params.put("idcard", cid);
		params.put("username", name);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.UPDATE_PERSON_INFO, params);
	}

	/**
	 * 
	 * 修改银行卡
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doModifyBankInfo(Activity activity, String userId,
			String bankCard, String bankName, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", userId);
		params.put("bankcard", bankCard);
		params.put("bankname", bankName);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.UPDATE_PERSON_INFO, params);
	}

	/**
	 * 
	 * 获取地址列表
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void getAllAddress(Activity activity, String userId,
			String addressId, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", userId);
		params.put("id", addressId);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.REQUEST_QUERY_ADDRESS, params);
	}

	/**
	 * 
	 * 添加地址列表
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doInSertAddress(Activity activity,
			AddressBean mAddressBean, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", mAddressBean.id);
		params.put("phone", mAddressBean.phone);
		params.put("province", mAddressBean.province);
		params.put("city", mAddressBean.city);
		params.put("street", mAddressBean.street);
		params.put("state", mAddressBean.state);
		params.put("name", mAddressBean.name);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.REQUEST_ADD_ADDRESS, params);
	}

	/**
	 * 
	 * 更新地址
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doUpdateAddress(Activity activity,
			AddressBean mAddressBean, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", mAddressBean.id);
		params.put("phone", mAddressBean.phone);
		params.put("province", mAddressBean.province);
		params.put("city", mAddressBean.city);
		params.put("street", mAddressBean.street);
		params.put("state", mAddressBean.state);
		params.put("name", mAddressBean.name);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.REQUEST_UPDATE_ADDRESS, params);
	}

	/**
	 * 
	 * 更新我的快递地址
	 * 
	 * @time 2014年5月28日 上午11:04:34
	 * @author jie.liu
	 * @param activity
	 * @param invid
	 * @param address
	 * @param onHttpResponse
	 */
	public static void doUpdateDlvAddress(Activity activity, String invid,
			String address, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("invid", invid);
		params.put("address", address);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "正在提交快递地址");
		httpHelper.post(activity, HttpUrl.REQUEST_UPDATE_DLVY_ADDRESS, params);
	}

	/**
	 * 
	 * 删除地址
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doDeleteAddress(Activity activity, String addressId,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", addressId);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.REQUEST_DELETE_ADDRESS, params);
	}

	/**
	 * 
	 * 修改邮箱
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param onHttpResponse
	 */
	public static void doModifyEmail(Activity activity, String userId,
			String email, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", userId);
		params.put("email", email);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.UPDATE_PERSON_INFO, params);
	}

	/**
	 * 
	 * 修改密码
	 * 
	 * @time 2014-5-12 上午11:22:14
	 * @author liuzenglong163@gmail.com
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doModifyPwd(Activity activity, String userId,
			String phone, String password, String oldPassword,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", userId);
		params.put("phone", phone);
		params.put("password", password);
		params.put("oldpassword", oldPassword);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.UPDATE_PERSON_INFO, params);
	}

	/**
	 * 
	 * 从我的客户列表中删除客户
	 * 
	 * @time 2014年5月12日 下午4:31:01
	 * @author jie.liu
	 * @param activity
	 * @param cusid
	 * @param conid
	 * @param onHttpResponse
	 */
	public static void doDelClient(Activity activity, String cusid,
			String conid, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cusid", cusid);
		params.put("conid", conid);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "正在删除...");
		httpHelper.post(activity, HttpUrl.DELETE_CLIENT, params);
	}

	/**
	 * 
	 * 获取关注产品的客户列表
	 * 
	 * @time 2014年5月12日 下午4:47:23
	 * @author jie.liu
	 * @param activity
	 * @param conid
	 * @param proid
	 * @param onHttpResponse
	 */
	public static void doGetClientsAtten(Activity activity, String conid,
			String proid, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", conid);
		params.put("proid", proid);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity);
		httpHelper.post(activity, HttpUrl.CLIENTS_ATTENTION_PRODUCT, params);
	}

	/**
	 * 
	 * 取消关注某产品
	 * 
	 * @time 2014年5月12日 下午4:43:47
	 * @author jie.liu
	 * @param activity
	 * @param cusid
	 * @param proid
	 * @param onHttpResponse
	 */
	public static void doCancelAttention(Activity activity, String cusid,
			String proid, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cusid", cusid);
		params.put("proid", proid);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "正在取消关注中...");
		httpHelper.post(activity, HttpUrl.CANCEL_ATTENTION, params);
	}

	/**
	 * 
	 * 更改客户资料
	 * 
	 * @time 2014年5月12日 下午4:35:00
	 * @author jie.liu
	 * @param activity
	 * @param cusid
	 * @param name
	 * @param phone
	 * @param email
	 * @param onHttpResponse
	 */
	public static void doUpdateClient(Activity activity, String cusid,
			String name, String phone, String email, String remark,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cusid", cusid);
		params.put("name", name);
		params.put("phone", phone);
		params.put("email", email);
		params.put("remark", remark);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "正在更改...");
		httpHelper.post(activity, HttpUrl.UPDATE_CLIENT_INFO, params);
	}

	/**
	 * 
	 * 获取掌财学院的列表
	 * 
	 * @time 2014年5月12日 下午4:58:06
	 * @author jie.liu
	 * @param activity
	 * @param onHttpResponse
	 */
	public static void doGetAcademy(Activity activity,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity);
		httpHelper.post(activity, HttpUrl.ACADEMY_CONTENT, params);
	}

	public static void doFeedback(Activity activity, String content,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("context", content);
		addNameAndPhoneIfExits(params);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "提交中...");
		httpHelper.post(activity, HttpUrl.FEEDBACK, params);
	}

	private static void addNameAndPhoneIfExits(Map<String, String> params) {
		UserInfo userInfo = ZCApplication.getInstance().getUserInfo();
		if (userInfo != null) {
			addNameIfExists(params, userInfo);
			addPhoneIfExists(params, userInfo);
		}
	}

	private static void addNameIfExists(Map<String, String> params,
			UserInfo userInfo) {
		String name = userInfo.getName();
		if (!TextUtils.isEmpty(name)) {
			params.put("userName", name);
		}
	}

	private static void addPhoneIfExists(Map<String, String> params,
			UserInfo userInfo) {
		String phone = userInfo.getPhone();
		if (!TextUtils.isEmpty(phone)) {
			params.put("phone", phone);
		}
	}

	/**
	 * 
	 * 更改密码
	 * 
	 * @time 2014年5月12日 下午5:18:27
	 * @author jie.liu
	 * @param activity
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doUpdatePwd(Activity activity, String phone,
			String password, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("password", password);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity);
		httpHelper.post(activity, HttpUrl.CHECK_PASSWORD, params);
	}

	/**
	 * 
	 * 重置密码
	 * 
	 * @time 2014年5月12日 下午5:19:10
	 * @author jie.liu
	 * @param activity
	 * @param phone
	 * @param verifycode
	 * @param passwowd
	 * @param onHttpResponse
	 */
	public static void doResetPwd(Activity activity, String phone,
			String verifycode, String passwowd, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("verifycode", verifycode);
		params.put("password", passwowd);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity);
		httpHelper.post(activity, HttpUrl.FORGET_PASSWORD, params);
	}

	/**
	 * 
	 * 登录
	 * 
	 * @time 2014年5月12日 下午5:21:04
	 * @author jie.liu
	 * @param activity
	 * @param phone
	 * @param password
	 * @param onHttpResponse
	 */
	public static void doLogin(Activity activity, String phone,
			String password, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("mytoken", ContextUtil.getIMEI(activity));
		params.put("phone", phone);
		params.put("password", password);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "正在登录...");
		httpHelper.post(activity, HttpUrl.LOGIN, params);
	}

	/**
	 * 
	 * 注册
	 * 
	 * @time 2014年5月12日 下午5:26:10
	 * @author jie.liu
	 * @param activity
	 * @param phone
	 * @param password
	 * @param verifycode
	 * @param onHttpResponse
	 */
	public static void register(Activity activity, String phone,
			String password, String verifycode, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("mytoken", ContextUtil.getIMEI(activity));
		params.put("phone", phone);
		params.put("verifycode", verifycode);
		params.put("password", password);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "正在注册...");
		httpHelper.post(activity, HttpUrl.REQUEST_ADVISOR, params);
	}

	/**
	 * 
	 * 检查电话号码是否是唯一的
	 * 
	 * @time 2014年5月12日 下午5:38:59
	 * @author jie.liu
	 * @param activity
	 * @param phone
	 * @param onHttpResponse
	 */
	public static void checkPhoneIsOnly(Activity activity, String phone,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "0");
		params.put("phone", phone);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "电话号码唯一性检测中...");
		httpHelper.post(activity, HttpUrl.PHONE_CHECKE, params);
	}

	/***
	 * 
	 * 获取我的佣金明细.
	 * 
	 * @time 2014年5月13日 下午2:03:43
	 * @author jie.liu
	 * @param activity
	 * @param conid
	 * @param onHttpResponse
	 */
	public static void getMyCommissionS(Activity activity, String conid,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", conid);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity);
		httpHelper.post(activity, HttpUrl.GET_MY_COMMISSIONS, params);
	}

	/**
	 * 
	 * 获取我的业务单.
	 * 
	 * @time 2014年5月14日 下午2:49:28
	 * @author jie.liu
	 * @param activity
	 * @param conid
	 * @param type
	 * @param onHttpResponse
	 */
	public static void getMyBusForms(Activity activity, String conid,
			String type, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", conid);
		params.put("type", type);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity);
		httpHelper.post(activity, HttpUrl.GET_MY_BUS_FORMS, params);
	}

	/**
	 * 
	 * 获取业务单流程图.
	 * 
	 * @time 2014年5月16日 下午4:36:08
	 * @author jie.liu
	 * @param activity
	 * @param invid
	 * @param onHttpResponse
	 */
	public static void getDealRecords(Activity activity, String invid,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("invid", invid);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity);
		httpHelper.post(activity, HttpUrl.GET_DEAL_RECORDS, params);
	}

	/**
	 * 
	 * 上传图片到服务器
	 * 
	 * @time 2014-5-19 下午3:19:41
	 * @author liuzenglong163@gmail.com
	 * @param activity
	 * @param connid
	 * @param files
	 * @param onHttpResponse
	 */
	public static void doUpLoadFile(Activity activity, String conid, File files) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", conid);
		UploadUtil.getInstance().uploadFile(files, "pic",
				HttpUrl.DO_UPLOAD_SFZ, params);
	}

	/**
	 * 
	 * 删除汇款凭条
	 * 
	 * @time 2014年5月20日 下午5:11:31
	 * @author jie.liu
	 * @param activity
	 * @param picid
	 * @param picname
	 * @param onHttpResponse
	 */
	public static void delRemitSlip(Activity activity, String picid,
			String picname, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("picid", picid);
		params.put("picname", picname);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "正在删除...");
		httpHelper.post(activity, HttpUrl.DEL_REMIT_SLIP, params);
	}

	/**
	 * 
	 * 获取产品详情页的html5地址
	 * 
	 * @time 2014年5月20日 下午6:06:16
	 * @author jie.liu
	 * @param activity
	 * @param onHttpResponse
	 */
	public static void getProductDetailUrl(Activity activity,
			OnHttpResponse onHttpResponse) {
		getHtmlUrl(activity, "2", onHttpResponse);
	}

	private static void getHtmlUrl(Activity activity, String type,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type); // "2"：详情页 ."1":产品列表页

		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		httpHelper.post(activity, HttpUrl.PRODUCT_HTML_URL, params);
	}

	/**
	 * 
	 * 版本升级
	 * 
	 * @time 2014-5-21 下午6:00:15
	 * @author liuzenglong163@gmail.com
	 * @param activity
	 * @param onHttpResponse
	 */
	public static void doUpdateVersion(Activity activity,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("code", Integer.toString(ZCUtils.getVersionCode(activity)));
		params.put("sys", "an");
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		httpHelper.post(activity, HttpUrl.GET_NEW_VERSION, params);
	}

	/**
	 * 
	 * 消息请求
	 * 
	 * @author liuzenglong163@gmail.com
	 * @param activity
	 * @param onHttpResponse
	 */
	public static void doRequestSysMsg(Activity activity, String conid,
			String currentPage, String pageSize, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", conid);
		params.put("currentPage", currentPage);
		params.put("pageSize", pageSize);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.GET_SYS_MSG, params);
	}

	/**
	 * 
	 * 设置消息已读
	 * 
	 * @author liuzenglong163@gmail.com
	 * @param activity
	 * @param onHttpResponse
	 */
	public static void doSetMsgRead(Activity activity, String conid,
			String msgId, OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("conid", conid);
		params.put("msgid", msgId);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		LoadingProgress.getInstance().show(activity, "加载中...");
		httpHelper.post(activity, HttpUrl.SET_MSG_READ, params);
	}

	/**
	 * 
	 * 获取加载页的图片的Url
	 * 
	 * @time 2014年5月22日 下午2:10:55
	 * @author jie.liu
	 * @param activity
	 * @param onHttpResponse
	 */
	public static void getLoadingPageUrl(Activity activity,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		httpHelper.post(activity, HttpUrl.LOADING_PAGE, params);
	}

	/**
	 * 
	 * 请求服务器是否需要更新缓存网页
	 * 
	 * @time 2014年6月9日 下午5:57:28
	 * @author liuzenglong163@gmail.com
	 * @param activity
	 * @param lastData
	 * @param onHttpResponse
	 */
	public static void updateCachePage(Activity activity, String lastData,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("lastdate", lastData);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		httpHelper.post(activity, HttpUrl.UPDATE_CACHE_PAGE, params);
	}

	/**
	 * 
	 * 请求服务器更新预约时间为服务器的当前时间，重新倒计时的时候有用.
	 * 
	 * @time 2014年7月2日 下午4:34:04
	 * @author liuzenglong163@gmail.com
	 * @param activity
	 * @param invid
	 * @param onHttpResponse
	 */
	public static void updateAppointDate(Activity activity, String invid,
			OnHttpResponse onHttpResponse) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("invid", invid);
		HttpHelper httpHelper = new HttpHelper();
		httpHelper.setOnHttpResponse(onHttpResponse);
		httpHelper.post(activity, HttpUrl.UPDATE_APPOINT_DATE, params);
	}
}

package com.hct.zc.http;

public interface HttpUrl {

	String PREFIX = "http://203.195.160.107:8080/Trust/";

	// String PREFIX = "http://172.16.0.100:8080/Trust/";

	// String PREFIX = "http://172.16.0.248:8090/Trust/";

	// String PREFIX = "http://121.14.145.7:9080/Trust/"; // 外网环境

	String PRODUCT_INDEX = PREFIX + "index.html";
	/**
	 * 登录.
	 */
	String LOGIN = PREFIX + "userLogin!login.action";

	/**
	 * 申请成为理财顾问.
	 */
	String REQUEST_ADVISOR = PREFIX + "clientCon!applyCon.action";

	/**
	 * 产品类别.
	 */
	String PRODUCT_CLASSIFY = PREFIX + "clientPro!queryClassify.action";

	/**
	 * 资金投向.
	 */
	String FOUNDS_TO_INVEST = PREFIX + "clientPro!queryDirection.action";

	/**
	 * 产品期限.
	 */
	String PRODUCT_DEADLINE = PREFIX + "clientPro!queryPeriod.action";

	/**
	 * 担保情况.
	 */
	String GUARANTEES = PREFIX + "clientPro!querySurety.action";

	/**
	 * 收益分配.
	 */
	String PROFITS = PREFIX + "clientPro!queryProfits.action";

	/**
	 * 查询产品.
	 */
	String QUERY_PRODUCT = PREFIX + "clientPro!queryProducts.action";

	/**
	 * 查询产品详情.
	 */
	String QUERY_PRODUCT_DETAIL = PREFIX + "clientPro!queryProduct.action";

	/**
	 * 更新用户信息(个人设置页面).
	 */
	String UPDATE_PERSON_INFO = PREFIX + "clientCon!updateCon.action";

	/**
	 * 检查原密码是否正确.
	 */
	String CHECK_PASSWORD = PREFIX + "clientCon!ckPassword.action";

	/**
	 * 理财顾问关注的产品列表.
	 */
	String ATTENTION_PRODUCTS = PREFIX + "clientPro!attentionCon.action";

	/**
	 * 添加客户.
	 */
	String ADD_CLIENT = PREFIX + "clientCus!addCustomer.action";

	/**
	 * 查询所有的客户
	 */
	String QUERY_ALL_CLIENTS = PREFIX + "clientCus!queryMyCustomers.action";

	/**
	 * 查询某一产品未关注的客户列表
	 */
	String QUERY_CLIENTS_NOT_ATTENTION_PRODUCT = PREFIX
			+ "clientCus!queryNotAttention.action";

	/**
	 * 掌财学院内容.
	 */
	String ACADEMY_CONTENT = PREFIX + "clientAca!queryAcademys.action";

	/**
	 * 反馈.
	 */
	String FEEDBACK = PREFIX + "clientAca!addFee.action";

	/**
	 * 验证获取.
	 */
	String VERIFICATION_CODE = PREFIX + "clientCon!createVerify.action";

	/**
	 * 电话号码唯一性检测
	 */
	String PHONE_CHECKE = PREFIX + "clientCon!ckPhone.action";

	/**
	 * 忘记密码
	 */
	String FORGET_PASSWORD = PREFIX + "userLogin!forgetPwd.action";

	/**
	 * 产品搜索
	 */
	String SEARCH_PRODUCT = PREFIX + "clientPro!ProductsSearch.action";

	/**
	 * 关注产品的客户
	 */
	String CLIENTS_ATTENTION_PRODUCT = PREFIX
			+ "clientCus!queryAttention.action";

	/**
	 * 顾问佣金情况
	 */
	String QUERY_COMMISSION = PREFIX + "clientInv!queryConCommission.action";

	/**
	 * 删除客户
	 */
	String DELETE_CLIENT = PREFIX + "clientCus!delMyCustomer.action";

	/**
	 * 更新客户信息
	 */
	String UPDATE_CLIENT_INFO = PREFIX + "clientCus!updateMyCustomer.action";

	/**
	 * 密码检测
	 */
	String PASSWORD_CHECK = PREFIX + "clientCon!ckPassword.action";

	/** 删除我的地址 **/
	public static final String REQUEST_DELETE_ADDRESS = PREFIX
			+ "clientCon!deleteAddress.action";

	/** 添加我的地址 **/
	public static final String REQUEST_ADD_ADDRESS = PREFIX
			+ "clientCon!addAddress.action";

	/* 更新我的地址* */
	public static final String REQUEST_UPDATE_ADDRESS = PREFIX
			+ "clientCon!updateAddress.action";

	/** 更新快递地址 **/
	public static final String REQUEST_UPDATE_DLVY_ADDRESS = PREFIX
			+ "clientInv!addPostAddress.action";

	/** 查询我的地址 **/
	public static final String REQUEST_QUERY_ADDRESS = PREFIX
			+ "clientCon!queryAddress.action";

	/**
	 * 佣金明细
	 */
	String GET_MY_COMMISSIONS = PREFIX + "clientInv!queryConCommission.action";

	/**
	 * 业务单
	 */
	String GET_MY_BUS_FORMS = PREFIX + "clientInv!queryMyCustomerRecord.action";

	/**
	 * 交易记录流程图
	 */
	String GET_DEAL_RECORDS = PREFIX + "clientInv!getStaterecord.action";

	/**
	 * 上传身份证
	 */
	String DO_UPLOAD_SFZ = PREFIX + "clientUpload!uploadCardPic.action";
	/**
	 * 上传身份证
	 */
	// String DO_UPLOAD_SFZ
	// ="http://172.16.0.11:8081/gree_git/loading-mobile!getLoadingInfo.do";
	/**
	 * 启动页图片获取
	 */
	String LOADING_PAGE = PREFIX + "clientLoad!getLoadPic.action";

	/**
	 * 删除汇款凭条
	 */
	String DEL_REMIT_SLIP = PREFIX + "clientInv!delPic.action";

	/**
	 * 获取产品html页面地址
	 */
	String PRODUCT_HTML_URL = PREFIX + "clientLoad!getHtml.action";

	/**
	 * 获取新版本
	 */
	String GET_NEW_VERSION = PREFIX + "clientLoad!getVersionCode.action";

	/**
	 * 上传汇款凭条
	 */
	String UPLOAD_REMIT_SLIP = PREFIX + "clientUpload!uploadPic.action";

	/**
	 * 主推产品
	 */
	String MAIN_PRODUCT = PREFIX + "clientPro!queryMain.action";

	/**
	 * 取消关注
	 */
	String CANCEL_ATTENTION = PREFIX + "clientCus!deleteAttention.action";

	/**
	 * 获取消息请求
	 */
	String GET_SYS_MSG = PREFIX + "clientCon!queryMyMsg.action";

	/**
	 * 获取消息请求
	 */
	String SET_MSG_READ = PREFIX + "clientCon!updateRead.action";

	/**
	 * 请求是否需要更新页面文件
	 */
	String UPDATE_CACHE_PAGE = PREFIX + "clientLoad!getZip.action";

	/**
	 * 查询理财顾问的信息
	 */
	String GET_ADISOR_INFO = PREFIX + "clientCon!queryMyInfo.action";

	/**
	 * 更新预约时间，用于业务的详情页的倒计时
	 */
	String UPDATE_APPOINT_DATE = PREFIX + "clientInv!updateAppointDate.action";

}

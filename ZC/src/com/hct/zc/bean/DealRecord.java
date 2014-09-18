package com.hct.zc.bean;

import java.io.Serializable;

public class DealRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 投资项id
	 */
	private String invid;

	/**
	 * 产品id
	 */
	private String proid;

	/**
	 * 产品名称
	 */
	private String pro_name;

	/**
	 * 客户姓名
	 */
	private String cus_name;

	/**
	 * 客户电话号码
	 */
	private String cus_phone;

	/**
	 * 预约金额
	 */
	private String money;

	/**
	 * 大款金额
	 */
	private String money1;

	/**
	 * 状态
	 */
	private String state;

	/**
	 * 产品全名
	 */
	private String fullname;

	/**
	 * 开户行
	 */
	private String account_bank;

	/**
	 * 银行账号
	 */
	private String account_num;

	/**
	 * 开户名
	 */
	private String account_name;

	/**
	 * 预约时间
	 */
	public String appoint_date;

	/**
	 * 服务器上的当前时间
	 */
	public String now_date;

	public String getInvid() {
		return invid;
	}

	public String getCus_phone() {
		return cus_phone;
	}

	public String getProid() {
		return proid;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setCus_phone(String cus_phone) {
		this.cus_phone = cus_phone;
	}

	public String getCus_name() {
		return cus_name;
	}

	public String getMoney() {
		return money;
	}

	public String getMoney1() {
		return money1;
	}

	public String getState() {
		return state;
	}

	public String getFullname() {
		return fullname;
	}

	public String getAccount_bank() {
		return account_bank;
	}

	public String getAccount_num() {
		return account_num;
	}

	public String getAccount_name() {
		return account_name;
	}
}

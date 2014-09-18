package com.hct.zc.bean;

public class Commission {

	/**
	 * 项目名称
	 */
	private String pname;

	/**
	 * 投资人
	 */
	private String cname;

	/**
	 * 投资金额
	 */
	private String money;

	/**
	 * 佣金
	 */
	private String amount;

	/**
	 * 日期
	 */
	private String date;

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}

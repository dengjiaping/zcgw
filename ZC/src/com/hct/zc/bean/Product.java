package com.hct.zc.bean;

import java.io.Serializable;

public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	/**
	 * 佣金系数
	 */
	private String commission;

	/**
	 * 期限
	 */
	private String period;

	/**
	 * 预期收益
	 */
	private String expectedrevenue;

	/**
	 * 是否关注
	 */
	private String attention;

	/**
	 * 是否hot
	 */
	private String hot;

	/**
	 * 分享内容
	 */
	public String shareexplain;

	/**
	 * 分享时带的图片
	 */
	public String sharepic;

	/**
	 * 预约状态
	 */
	private String appointstate;

	/**
	 * 佣金结算方式
	 */
	private String settlement;

	/**
	 * 全名
	 */
	private String fullname;

	public String getSettlement() {
		return settlement;
	}

	public String getFullname() {
		return fullname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommission() {
		return commission;
	}

	public void setCommission(String commission) {
		this.commission = commission;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getExpectedrevenue() {
		return expectedrevenue;
	}

	public void setExpectedrevenue(String expectedrevenue) {
		this.expectedrevenue = expectedrevenue;
	}

	public String getAttention() {
		return attention;
	}

	public void setAttention(String attention) {
		this.attention = attention;
	}

	public String getHot() {
		return hot;
	}

	public void setHot(String hot) {
		this.hot = hot;
	}

	public String getAppointstate() {
		return appointstate;
	}

	public void setAppointstate(String appointstate) {
		this.appointstate = appointstate;
	}
}

package com.hct.zc.bean;

public class UserInfo {

	/** 已认证 */
	public static final String HAS_ACCREADE = "1";

	/** 未认证 */
	public static final String NO_ACCREADE = "0";

	/**
	 * 正在审核中
	 */
	public static final String ACCREADING = "2";

	private String userId;

	private String name;

	private String phone;

	private String email;

	private String idcard;

	private String bankcard;

	private String address;

	private String alias;

	private String allcom;

	private String threecom;

	private String grade;

	private String bankname;

	public String cardpic0;

	public String cardpic1;

	private String accreditation;// 是否认证

	public static UserInfo getEmptyInstance() {
		UserInfo instance = new UserInfo();
		instance.setUserId("");
		instance.setName("");
		instance.setPhone("");
		instance.setEmail("");
		instance.setIdcard("");
		instance.setBankcard("");
		instance.setAddress("");
		instance.setAlias("");
		instance.setAllcom("0.00");
		instance.setThreecom("0.00");
		instance.setGrade("");
		instance.setBankname("");
		instance.cardpic0 = "";
		instance.cardpic1 = "";
		return instance;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getBankcard() {
		return bankcard;
	}

	public void setBankcard(String bankcard) {
		this.bankcard = bankcard;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAllcom() {
		return allcom;
	}

	public void setAllcom(String allcom) {
		this.allcom = allcom;
	}

	public String getThreecom() {
		return threecom;
	}

	public void setThreecom(String threecom) {
		this.threecom = threecom;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getAccreditation() {
		return accreditation;
	}

	public void setAccreditation(String accreditation) {
		this.accreditation = accreditation;
	}

	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", name=" + name + ", phone="
				+ phone + ", email=" + email + ", idcard=" + idcard
				+ ", bankcard=" + bankcard + ", address=" + address
				+ ", alias=" + alias + ", allcom=" + allcom + ", threecom="
				+ threecom + ", grade=" + grade + ", bankname=" + bankname
				+ ", cardpic0=" + cardpic0 + ", cardpic1=" + cardpic1
				+ ", accreditation=" + accreditation + "]";
	}

}
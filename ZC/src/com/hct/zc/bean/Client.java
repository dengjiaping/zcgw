package com.hct.zc.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Client implements Parcelable {

	public Client() {
		super();
	}

	private String userId;

	private String name;

	private String phone;

	private String email;

	private String idcard;

	private String regDate;

	private String remark;

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userId);
		dest.writeString(name);
		dest.writeString(phone);
		dest.writeString(email);
		dest.writeString(idcard);
		dest.writeString(regDate);
		dest.writeString(remark);
	}

	public static final Parcelable.Creator<Client> CREATOR = new Parcelable.Creator<Client>() {

		@Override
		public Client createFromParcel(Parcel source) {
			return new Client(source);
		}

		@Override
		public Client[] newArray(int size) {
			return new Client[size];
		}
	};

	private Client(Parcel in) {
		userId = in.readString();
		name = in.readString();
		phone = in.readString();
		email = in.readString();
		idcard = in.readString();
		regDate = in.readString();
		remark = in.readString();
	}
}

package com.hct.zc.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ComDetail implements Parcelable {
	/**
	 * 佣金率
	 */
	public String comm;

	/**
	 * 预期收益
	 */
	public String estimate;

	/**
	 * 认购金额
	 */
	public String explain;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(comm);
		dest.writeString(estimate);
		dest.writeString(explain);
	}

	public static final Parcelable.Creator<ComDetail> CREATOR = new Parcelable.Creator<ComDetail>() {

		@Override
		public ComDetail createFromParcel(Parcel source) {
			return new ComDetail(source);
		}

		@Override
		public ComDetail[] newArray(int size) {
			return new ComDetail[size];
		}
	};

	private ComDetail(Parcel in) {
		comm = in.readString();
		estimate = in.readString();
		explain = in.readString();
	}
}

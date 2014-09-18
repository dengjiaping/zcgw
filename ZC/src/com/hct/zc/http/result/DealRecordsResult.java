package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.DealRecord;

public class DealRecordsResult {

	private List<DealRecord> ivnInfos;

	public List<DealRecord> getIvnInfos() {
		return ivnInfos;
	}

	public void setIvnInfos(List<DealRecord> ivnInfos) {
		this.ivnInfos = ivnInfos;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

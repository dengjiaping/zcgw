package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.Commission;

public class CommissionResult {

	private List<Commission> comms;

	public List<Commission> getComms() {
		return comms;
	}

	public void setComms(List<Commission> comms) {
		this.comms = comms;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

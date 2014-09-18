package com.hct.zc.http.result;

import com.hct.zc.bean.VerifyCode;

public class VerifyCodeResult {

	private VerifyCode resultcode;

	public VerifyCode getResultcode() {
		return resultcode;
	}

	public void setResultcode(VerifyCode resultcode) {
		this.resultcode = resultcode;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

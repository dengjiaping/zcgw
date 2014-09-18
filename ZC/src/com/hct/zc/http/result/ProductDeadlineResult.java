package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.ProductOption;

public class ProductDeadlineResult {

	private List<ProductOption> periods;

	public List<ProductOption> getPeriods() {
		return periods;
	}

	public void setPeriods(List<ProductOption> periods) {
		this.periods = periods;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

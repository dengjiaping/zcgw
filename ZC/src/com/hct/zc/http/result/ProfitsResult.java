package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.ProductOption;

public class ProfitsResult {

	private List<ProductOption> profits;

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public List<ProductOption> getProfits() {
		return profits;
	}

	public void setProfits(List<ProductOption> profits) {
		this.profits = profits;
	}

}

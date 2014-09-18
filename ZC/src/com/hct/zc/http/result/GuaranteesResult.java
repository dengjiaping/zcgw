package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.ProductOption;

public class GuaranteesResult {

	private List<ProductOption> surets;

	public List<ProductOption> getSurets() {
		return surets;
	}

	public void setSurets(List<ProductOption> surets) {
		this.surets = surets;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

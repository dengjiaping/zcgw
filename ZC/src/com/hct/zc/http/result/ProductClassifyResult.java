package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.ProductOption;

public class ProductClassifyResult {

	private List<ProductOption> classify;

	public List<ProductOption> getClassify() {
		return classify;
	}

	public void setClassify(List<ProductOption> classify) {
		this.classify = classify;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

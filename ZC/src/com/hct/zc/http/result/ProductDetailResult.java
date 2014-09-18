package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.ComDetail;
import com.hct.zc.bean.ProductDetailInfo;

public class ProductDetailResult {

	private ProductDetailInfo perInfo;

	private List<ComDetail> commlist;

	public ProductDetailInfo getPerInfo() {
		return perInfo;
	}

	public List<ComDetail> getCommlist() {
		return commlist;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

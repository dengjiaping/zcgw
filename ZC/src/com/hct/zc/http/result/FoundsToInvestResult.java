package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.ProductOption;

public class FoundsToInvestResult {

	private List<ProductOption> directions;

	public List<ProductOption> getDirections() {
		return directions;
	}

	public void setDirections(List<ProductOption> directions) {
		this.directions = directions;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

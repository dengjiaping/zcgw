package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.Product;

public class ProductsResult {
	private int currentPage;

	private int totalPages;

	private List<Product> perInfos;

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public List<Product> getPerInfos() {
		return perInfos;
	}

	public void setPerInfos(List<Product> perInfos) {
		this.perInfos = perInfos;
	}

}

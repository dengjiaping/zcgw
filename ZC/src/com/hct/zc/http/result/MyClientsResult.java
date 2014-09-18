package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.Client;

public class MyClientsResult {

	private List<Client> customerInfos;

	public List<Client> getCustomerInfos() {
		return customerInfos;
	}

	public void setCustomerInfos(List<Client> customerInfos) {
		this.customerInfos = customerInfos;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

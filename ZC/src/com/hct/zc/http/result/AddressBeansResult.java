package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.AddressBean;

/**
 * @todo 通讯地址结果集
 * @time 2014-5-14 上午9:15:17
 * @author liuzenglong163@gmail.com
 */

public class AddressBeansResult {

	private List<AddressBean> address;

	public List<AddressBean> getAddress() {
		return address;
	}

	public void setAddress(List<AddressBean> address) {
		this.address = address;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

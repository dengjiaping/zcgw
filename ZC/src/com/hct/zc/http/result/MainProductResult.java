package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.ComDetail;
import com.hct.zc.bean.Product;

/**
 * @todo 主推产品结果
 * @time 2014年5月21日 下午5:04:15
 * @author jie.liu
 */

public class MainProductResult {

	public Product perInfo;

	public List<ComDetail> commlist;

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

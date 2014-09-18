package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.Records;

/**
 * @todo 交易流程列表返回
 * @time 2014年5月16日 下午5:42:41
 * @author jie.liu
 */

public class RecordsResult {

	private List<Records> records;

	public Records nextState;

	public String now_date;

	public List<Records> getRecords() {
		return records;
	}

	public void setRecords(List<Records> records) {
		this.records = records;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

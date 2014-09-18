package com.hct.zc.http.result;

public class HttpResult {
	
	
	/**更新成功*/
	public static String SUCCESS = "0";
	/**更新失败*/
	public static String FAIL = "-1";
	/**参数错误*/
	public static String ARG_ERROR = "-2";
	/**系统错误*/
	public static String SYS_ERROR = "-4";
	
	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

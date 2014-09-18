package com.hct.zc.http.result;

import com.hct.zc.bean.ApkBean;

/**
 * @todo TODO
 * @time 2014-5-21 下午6:30:03
 * @author liuzenglong163@gmail.com
 */

public class ApkResult {

	private ApkBean versioninfo;

	public ApkBean getVersioninfo() {
		return versioninfo;
	}

	public void setVersioninfo(ApkBean versioninfo) {
		this.versioninfo = versioninfo;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

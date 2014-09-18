package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.AcademyClass;

public class AcademyResult {

	List<AcademyClass> academys;

	public List<AcademyClass> getAcademys() {
		return academys;
	}

	public void setAcademys(List<AcademyClass> academys) {
		this.academys = academys;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

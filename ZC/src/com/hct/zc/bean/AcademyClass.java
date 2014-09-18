package com.hct.zc.bean;

import java.util.List;

public class AcademyClass {

	private String title;

	public String typecode;

	private List<AcademyContent> contexts;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<AcademyContent> getContexts() {
		return contexts;
	}

	public void setContexts(List<AcademyContent> contexts) {
		this.contexts = contexts;
	}
}

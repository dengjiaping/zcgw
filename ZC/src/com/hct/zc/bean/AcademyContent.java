package com.hct.zc.bean;

import java.io.Serializable;

public class AcademyContent implements Serializable {

	private static final long serialVersionUID = 4158171217951251917L;

	private String title;

	private String context;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}
}

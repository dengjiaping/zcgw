package com.hct.zc.bean;

import java.util.List;

/**
 * 
 * @todo 交易流程项
 * @time 2014年5月16日 下午5:43:14
 * @author jie.liu
 */
public class Records {

	/**
	 * 时间
	 */
	private String rdate;

	/**
	 * 内容
	 */
	private String explain;

	/**
	 * 身份证图片
	 */
	private List<ImageInfo> pic;

	/**
	 * 汇款凭条
	 */
	private List<ImageInfo> pics;

	/**
	 * 时间
	 */
	public String getRdate() {
		return rdate;
	}

	/**
	 * 内容
	 */
	public String getExplain() {
		return explain;
	}

	/**
	 * 
	 * 汇款凭条
	 * 
	 */
	public List<ImageInfo> getPics() {
		return pics;
	}

	/**
	 * 身份证
	 */
	public List<ImageInfo> getPic() {
		return pic;
	}
}

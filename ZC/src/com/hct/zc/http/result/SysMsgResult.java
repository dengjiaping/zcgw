package com.hct.zc.http.result;

import java.util.List;

import com.hct.zc.bean.MsgBean;

/**
 * @todo 消息响应集合
 * @time 2014-5-21 下午6:30:25
 * @author liuzenglong163@gmail.com
 */

public class SysMsgResult {

	private List<MsgBean> myMsgs;

	public List<MsgBean> getMyMsgs() {
		return myMsgs;
	}

	public void setMyMsgs(List<MsgBean> myMsgs) {
		this.myMsgs = myMsgs;
	}

	private Result result;

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

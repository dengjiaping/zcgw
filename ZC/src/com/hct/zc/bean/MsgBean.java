package com.hct.zc.bean;
/**
 * @todo 消息实体类
 * @time 2014-5-21 下午6:27:59
 * @author liuzenglong163@gmail.com
 */

public class MsgBean {
	
	/**未读*/
	public static final String NO_READ = "0";
	/**已读*/
	public static final String HAS_READ = "1";
	
	public String msgid;//	ID	string	
	public String title;//		标题	string	
	public String context;//		内容	double	
	public String isread;//		是否已读	string	0－未读； 1－已读
	public String date;//		日期	string	XXXX－XX－XX XX：XX：XX
	
}

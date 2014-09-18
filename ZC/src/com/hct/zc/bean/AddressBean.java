package com.hct.zc.bean;

import java.io.Serializable;

/**
 * @todo 地址的实体类
 * @time 2014-5-12 上午11:53:18
 * @author liuzenglong163@gmail.com
 */

public class AddressBean implements Serializable {
	
	/**
	 * @todo TODO
	 * @time 2014-5-12 上午11:54:07
	 * @author liuzenglong163@gmail.com
	 */
	 
	/**默认状态*/
	public static final String ADDRESS_DEFAULT = "1";		
	/**不是默认状态*/
	public static final String ADDRESS_DEFAULT_NO = "0";	
	private static final long serialVersionUID = 1L;
	public String id;//	ID
	public String province;//		省
	public String city;//		市
	public String street;//		街道
	public String state;//		状态 1-默认，0－普通
	public String phone;//		电话
	public String name;//		姓名
	
	
	public AddressBean(String id, String province, String city, String street,
			String state, String phone, String name) {
		super();
		this.id = id;
		this.province = province;
		this.city = city;
		this.street = street;
		this.state = state;
		this.phone = phone;
		this.name = name;
	}

	public AddressBean() {
		super();
	}

}

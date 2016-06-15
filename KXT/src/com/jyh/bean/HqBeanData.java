package com.jyh.bean;

import java.io.Serializable;
import java.util.List;

public class HqBeanData  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String code;
	private List<TopBean> topBeans;
	private List<HqChildren> children;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<TopBean> getTopBeans() {
		return topBeans;
	}

	public void setTopBeans(List<TopBean> topBeans) {
		this.topBeans = topBeans;
	}

	public List<HqChildren> getChildren() {
		return children;
	}

	public void setChildren(List<HqChildren> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "HqBeanData [name=" + name + ", code=" + code + ", topBeans="
				+ topBeans + ", children=" + children + "]";
	}
	
	

}

package com.jyh.bean;

import java.io.Serializable;

/** 
 * 视频类型
 * @author  beginner 
 * @date 创建时间：2015年11月17日 上午9:42:44 
 * @version 1.0  
 */
public class VedioTypeTitle implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cat_name;
	private String id;
	
	
	public VedioTypeTitle() {
		super();
	}
	public String getCat_name() {
		return cat_name;
	}
	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	public VedioTypeTitle(String cat_name, String id) {
		super();
		this.cat_name = cat_name;
		this.id = id;
	}
	@Override
	public String toString() {
		return "VedioTypeTitle [cat_name=" + cat_name + ", id=" + id + "]";
	}
	
}

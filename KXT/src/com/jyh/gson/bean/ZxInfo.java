package com.jyh.gson.bean;

import java.io.Serializable;

/**
 * 文章信息
 * 
 * @author Administrator
 * 
 */
public class ZxInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * id : 1-220768 type : 1 title : 日本央行：需要关注物价趋势风险 time : 1458013073
	 * importance : 低
	 */

	private String id;
	private String type;
	private String title;
	private int time;
	private String importance;

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setImportance(String importance) {
		this.importance = importance;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public int getTime() {
		return time;
	}

	public String getImportance() {
		return importance;
	}
}

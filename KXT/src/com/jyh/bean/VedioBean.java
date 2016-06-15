package com.jyh.bean;

import java.io.Serializable;

/** 
 * 视频实体类
 * @author  beginner 
 * @date 创建时间：2015年11月13日 下午2:30:13 
 * @version 1.0  
 */
public class VedioBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String title;
	private String picture;
	private String url;
	private String istoutiao;
	private String play_count;
	private String publish_time;
	private String category_id;
	
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public String getPublish_time() {
		return publish_time;
	}
	public void setPublish_time(String publish_time) {
		this.publish_time = publish_time;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIstoutiao() {
		return istoutiao;
	}
	public void setIstoutiao(String istoutiao) {
		this.istoutiao = istoutiao;
	}
	public String getPlay_count() {
		return play_count;
	}
	public void setPlay_count(String play_count) {
		this.play_count = play_count;
	}
	public VedioBean(String id, String title, String picture, String url,
			String istoutiao, String play_count, String publish_time,
			String category_id) {
		super();
		this.id = id;
		this.title = title;
		this.picture = picture;
		this.url = url;
		this.istoutiao = istoutiao;
		this.play_count = play_count;
		this.publish_time = publish_time;
		this.category_id = category_id;
	}
	public VedioBean() {
		super();
	}
	
	
}

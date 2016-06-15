package com.jyh.gson.bean;

import java.io.Serializable;

public class Flash_ZxImgInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * title : 我是测试数据1 importance : 低 style : 1 image :
	 * http://sjzx.oss-cn-shanghai
	 * .aliyuncs.com/Uploads/Picture/2016-03-15/56e783b9130eb.png url : null
	 * content : <a href="kxt://news/12144" >我是测试链接1</a>
	 * <p>
	 * 我是测试文本
	 * </p>
	 * type : 1001 time : 1458013146 id : 662
	 */

	private String title;
	private String importance;
	private String style;
	private String image;
	private Object url;
	private String content;
	private String type;
	private int time;
	private String id;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setImportance(String importance) {
		this.importance = importance;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setUrl(Object url) {
		this.url = url;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getImportance() {
		return importance;
	}

	public String getStyle() {
		return style;
	}

	public String getImage() {
		return image;
	}

	public Object getUrl() {
		return url;
	}

	public String getContent() {
		return content;
	}

	public String getType() {
		return type;
	}

	public int getTime() {
		return time;
	}

	public String getId() {
		return id;
	}
}

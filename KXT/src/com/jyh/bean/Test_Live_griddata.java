package com.jyh.bean;
/** 
 * @author  beginner 
 * @date 创建时间：2015年11月12日 下午2:29:07 
 * @version 1.0  
 */
public class Test_Live_griddata {

	private String imageUrl;
	private String title,times,timelong,type;

//	private VedioBean bean;
	
	public Test_Live_griddata(String imageUrl, String title, String times,
			String timelong, String type) {
		super();
		this.imageUrl = imageUrl;
		this.title = title;
		this.times = times;
		this.timelong = timelong;
		this.type = type;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
	public String getTimelong() {
		return timelong;
	}
	public void setTimelong(String timelong) {
		this.timelong = timelong;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}

package com.jyh.bean;
import java.util.List;

/** 
 * @author  beginner 
 * @date 创建时间：2015年11月17日 上午11:07:25 
 * @version 1.0  
 */
public class VedioType {

	private String id;
	private String cat_name;
	private List<VedioBean> list;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCat_name() {
		return cat_name;
	}
	public void setCat_name(String cat_name) {
		this.cat_name = cat_name;
	}
	public List<VedioBean> getList() {
		return list;
	}
	public void setList(List<VedioBean> list) {
		this.list = list;
	}
	
	
	
}

package com.jyh.bean;

import java.util.List;

/** 
 * @author  beginner 
 * @date 创建时间：2015年11月12日 下午2:28:08 
 * @version 1.0  
 */
public class Test_live_listdata {

	private List<VedioBean> datas;

	public Test_live_listdata(List<VedioBean> datas) {
		super();
		this.datas = datas;
	}

	public List<VedioBean> getDatas() {
		return datas;
	}

	public void setDatas(List<VedioBean> datas) {
		this.datas = datas;
	}
	
}

package com.jyh.gson.bean;

import java.util.List;

public class CjrlBeanStr {

	private String code;
	private String msg;
	private List<CjrlDataStr> data;

	public CjrlBeanStr() {

	}

	public CjrlBeanStr(String code, String msg, List<CjrlDataStr> data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<CjrlDataStr> getData() {
		return data;
	}

	public void setData(List<CjrlDataStr> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "CjrlBean [code=" + code + ", msg=" + msg + ", data=" + data
				+ "]";
	}

}

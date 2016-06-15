package com.jyh.kxt.socket;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class CjData {
	@JSONField(name = "Type")
	public String Type; // 所属类型

	@JSONField(name = "Date")
	public String Date; // 日期

	@JSONField(name = "CjInfos")
	public List<CjInfo> CjInfos; // 财经信息集合

	// ------------------------------------------------

	/** 处理集合CjInfos，置为null */
	public void dispose() {
		CjInfos = null;
	}

	// ------------------------------

	@JSONField(name = "Type")
	public String getType() {
		return Type;
	}

	@JSONField(name = "Type")
	public void setType(String type) {
		Type = type;
	}

	@JSONField(name = "Date")
	public String getDate() {
		return Date;
	}

	@JSONField(name = "Date")
	public void setDate(String date) {
		Date = date;
	}

	@JSONField(name = "CjInfos")
	public List<CjInfo> getCjInfos() {
		return CjInfos;
	}

	@JSONField(name = "CjInfos")
	public void setCjInfos(List<CjInfo> cjInfos) {
		CjInfos = cjInfos;
	}

}

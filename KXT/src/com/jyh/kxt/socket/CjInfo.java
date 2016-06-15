package com.jyh.kxt.socket;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 国家信息
 * 
 * @author Administrator
 * 
 */
public class CjInfo  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2923356268922242993L;

	@JSONField(name = "PredictTime")
	public String PredictTime; // 预计公布时间

	@JSONField(name = "State")
	public String State; // 国家

	@JSONField(name = "Site")
	public String Site; // 网站

	@JSONField(name = "Nfi")
	public String Nfi; // 指标名称

	@JSONField(name = "Nature")
	public String Nature; // 重要性

	@JSONField(name = "Before")
	public String Before; // 前值

	@JSONField(name = "Forecast")
	public String Forecast; // 预测值

	@JSONField(name = "Reality")
	public String Reality; // 公布值

	@JSONField(name = "Effect")
	public String Effect; // 影响

	@JSONField(name = "IsOut")
	public boolean IsOut; // 过时数据

	// 利多利空 EffectBad EffectGood EffectMid

	@JSONField(name = "EffectBad")
	public String EffectBad;

	@JSONField(name = "EffectGood")
	public String EffectGood;

	@JSONField(name = "EffectMid")
	public String EffectMid;
	@JSONField(name = "ToDay")
	
	private Long Time_m;

	public Long getTime_m() {
		return Time_m;
	}

	public void setTime_m(Long time_m) {
		Time_m = time_m;
	}

	private int ToDay;
	public int getToDay() {
		return ToDay;
	}

	public void setToDay(int toDay) {
		ToDay = toDay;
	}

	// ***补充***
	private String date;
	private String type;

	public String id;

	// -----------------------------------------------------

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@JSONField(name = "PredictTime")
	public String getPredictTime() {
		return PredictTime;
	}

	@JSONField(name = "PredictTime")
	public void setPredictTime(String predictTime) {
		PredictTime = predictTime;
	}

	@JSONField(name = "State")
	public String getState() {
		return State;
	}

	@JSONField(name = "State")
	public void setState(String state) {
		State = state;
	}

	@JSONField(name = "Site")
	public String getSite() {
		return Site;
	}

	@JSONField(name = "Site")
	public void setSite(String site) {
		Site = site;
	}

	@JSONField(name = "Nfi")
	public String getNfi() {
		return Nfi;
	}

	@JSONField(name = "Nfi")
	public void setNfi(String nfi) {
		Nfi = nfi;
	}

	@JSONField(name = "Nature")
	public String getNature() {
		return Nature;
	}

	@JSONField(name = "Nature")
	public void setNature(String nature) {
		Nature = nature;
	}

	@JSONField(name = "Before")
	public String getBefore() {
		return Before;
	}

	@JSONField(name = "Before")
	public void setBefore(String before) {
		Before = before;
	}

	@JSONField(name = "Forecast")
	public String getForecast() {
		return Forecast;
	}

	@JSONField(name = "Forecast")
	public void setForecast(String forecast) {
		Forecast = forecast;
	}

	@JSONField(name = "Reality")
	public String getReality() {
		return Reality;
	}

	@JSONField(name = "Reality")
	public void setReality(String reality) {
		Reality = reality;
	}

	@JSONField(name = "Effect")
	public String getEffect() {
		return Effect;
	}

	@JSONField(name = "Effect")
	public void setEffect(String effect) {
		Effect = effect;
	}

	@JSONField(name = "IsOut")
	public boolean isOut() {
		return IsOut;
	}

	@JSONField(name = "IsOut")
	public void setOut(boolean isOut) {
		IsOut = isOut;
	}

	// 利多利空 EffectBad EffectGood EffectMid
	@JSONField(name = "EffectBad")
	public String getEffectBad() {
		return EffectBad;
	}

	@JSONField(name = "EffectBad")
	public void setEffectBad(String effectBad) {
		EffectBad = effectBad;
	}

	@JSONField(name = "EffectGood")
	public String getEffectGood() {
		return EffectGood;
	}

	@JSONField(name = "EffectGood")
	public void setEffectGood(String effectGood) {
		EffectGood = effectGood;
	}

	@JSONField(name = "EffectMid")
	public String getEffectMid() {
		return EffectMid;
	}

	@JSONField(name = "EffectMid")
	public void setEffectMid(String effectMid) {
		EffectMid = effectMid;
	}

	@Override
	public String toString() {
		return "CjInfo [PredictTime=" + PredictTime + ", State=" + State
				+ ", Site=" + Site + ", Nfi=" + Nfi + ", Nature=" + Nature
				+ ", Before=" + Before + ", Forecast=" + Forecast
				+ ", Reality=" + Reality + ", Effect=" + Effect + ", IsOut="
				+ IsOut + ", EffectBad=" + EffectBad + ", EffectGood="
				+ EffectGood + ", EffectMid=" + EffectMid + ", ToDay=" + ToDay
				+ ", date=" + date + ", type=" + type + ", id=" + id + "]";
	}

//	@Override
//	public String toString() {
//		return ""+ToDay;
//	}
	
	

}

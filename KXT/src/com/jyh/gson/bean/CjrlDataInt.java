package com.jyh.gson.bean;

import java.io.Serializable;

public class CjrlDataInt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// "Forecast": "3%",
	// "Nature": "低",
	// "AutoID": 74599,
	// "EffectType": 0,
	// "ShowKey": 0,
	// "Site": "",
	// "NFI": "第四季度GDP年率初值 ",
	// "Effect": "||",
	// "State": "韩国",
	// "Reality": "3%",
	// "Time": "2016-01-26 06:59:20",
	// "Date": "2016-01-26",
	// "PredictTime": "07:00",
	// "Type": "EconomicCalendars",
	// "Classify": 0,
	// "Before": "2.7%",
	// "ico_state":
	// "http://appapi.kxt.com/Public/cjrl-kxt-m/images/countries/hg.png"
	private String Forecast;
	private String Nature;
	private Integer AutoID;
	private Integer EffectType;
	private Integer ShowKey;
	private String Site;
	private String NFI;
	private String Effect;
	private String State;
	private String Reality;
	private String Time;
	private String Date;
	private String PredictTime;
	private String Type;
	private Integer Classify;
	private String Before;
	private String ico_state;

	public CjrlDataInt() {
	}

	public CjrlDataInt(String forecast, String nature, Integer autoID,
			Integer effectType, Integer showKey, String site, String nFI,
			String effect, String state, String reality, String time,
			String date, String predictTime, String type, Integer classify,
			String before, String ico_state) {
		super();
		Forecast = forecast;
		Nature = nature;
		AutoID = autoID;
		EffectType = effectType;
		ShowKey = showKey;
		Site = site;
		NFI = nFI;
		Effect = effect;
		State = state;
		Reality = reality;
		Time = time;
		Date = date;
		PredictTime = predictTime;
		Type = type;
		Classify = classify;
		Before = before;
		this.ico_state = ico_state;
	}

	public String getForecast() {
		return Forecast;
	}

	public void setForecast(String forecast) {
		Forecast = forecast;
	}

	public String getNature() {
		return Nature;
	}

	public void setNature(String nature) {
		Nature = nature;
	}

	public Integer getAutoID() {
		return AutoID;
	}

	public void setAutoID(Integer autoID) {
		AutoID = autoID;
	}

	public Integer getEffectType() {
		return EffectType;
	}

	public void setEffectType(Integer effectType) {
		EffectType = effectType;
	}

	public Integer getShowKey() {
		return ShowKey;
	}

	public void setShowKey(Integer showKey) {
		ShowKey = showKey;
	}

	public String getSite() {
		return Site;
	}

	public void setSite(String site) {
		Site = site;
	}

	public String getNFI() {
		return NFI;
	}

	public void setNFI(String nFI) {
		NFI = nFI;
	}

	public String getEffect() {
		return Effect;
	}

	public void setEffect(String effect) {
		Effect = effect;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getReality() {
		return Reality;
	}

	public void setReality(String reality) {
		Reality = reality;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getPredictTime() {
		return PredictTime;
	}

	public void setPredictTime(String predictTime) {
		PredictTime = predictTime;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public Integer getClassify() {
		return Classify;
	}

	public void setClassify(Integer classify) {
		Classify = classify;
	}

	public String getBefore() {
		return Before;
	}

	public void setBefore(String before) {
		Before = before;
	}

	public String getIco_state() {
		return ico_state;
	}

	public void setIco_state(String ico_state) {
		this.ico_state = ico_state;
	}
	//
	// @Override
	// public String toString() {
	// return "CjrlData [Forecast=" + Forecast + ", Nature=" + Nature
	// + ", AutoID=" + AutoID + ", EffectType=" + EffectType
	// + ", ShowKey=" + ShowKey + ", Site=" + Site + ", NFI=" + NFI
	// + ", Effect=" + Effect + ", State=" + State + ", Reality="
	// + Reality + ", Time=" + Time + ", Date=" + Date
	// + ", PredictTime=" + PredictTime + ", Type=" + Type
	// + ", Classify=" + Classify + ", Before=" + Before
	// + ", ico_state=" + ico_state + "]";
	// }

	@Override
	public String toString() {
		return "CjrlData [Nature=" + Nature + ", NFI=" + NFI + ", Time=" + Time
				+ "]";
	}
	
	

}
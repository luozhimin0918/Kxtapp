package com.jyh.gson.bean;

import java.io.Serializable;

public class Flash_CjInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * id : -76101 type : 2 title : 1月商品贸易帐(亿) state : 英国 predicttime :
	 * 1457688600 effect : 金银 英镑 石油|| before : -99.17 forecast : -103 reality :
	 * -102.89 importance : 中 time : 1457688555
	 */

	private String id;
	private String type;
	private String title;
	private String state;
	private int predicttime;
	private String effect;
	private String before;
	private String forecast;
	private String reality;
	private String importance;
	private int time;
	
	public String EffectGood;
	public String EffectMid;
	public String EffectBad;

	public String getEffectGood() {
		return EffectGood;
	}

	public void setEffectGood(String effectGood) {
		EffectGood = effectGood;
	}

	public String getEffectMid() {
		return EffectMid;
	}

	public void setEffectMid(String effectMid) {
		EffectMid = effectMid;
	}

	public String getEffectBad() {
		return EffectBad;
	}

	public void setEffectBad(String effectBad) {
		EffectBad = effectBad;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setPredicttime(int predicttime) {
		this.predicttime = predicttime;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public void setForecast(String forecast) {
		this.forecast = forecast;
	}

	public void setReality(String reality) {
		this.reality = reality;
	}

	public void setImportance(String importance) {
		this.importance = importance;
	}

	public void setTime(int time) {
		this.time = time;
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

	public String getState() {
		return state;
	}

	public int getPredicttime() {
		return predicttime;
	}

	public String getEffect() {
		return effect;
	}

	public String getBefore() {
		return before;
	}

	public String getForecast() {
		return forecast;
	}

	public String getReality() {
		return reality;
	}

	public String getImportance() {
		return importance;
	}

	public int getTime() {
		return time;
	}
}

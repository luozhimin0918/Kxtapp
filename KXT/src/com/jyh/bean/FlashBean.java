package com.jyh.bean;

public class FlashBean {
	// 公用字段
	private String type = "";// 1资讯 2财经
	private String id = "";
	private String title = "";
	private String time = "";
	// zx字段
	private String isStress;// 0不重要
	// cj字段
	private String nature = "";
	private String state = "";
	private String reality = "";
	private String before = "";
	private String forecast = "";
	private String effect = "";
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIsStress() {
		return isStress;
	}

	public void setIsStress(String isStress) {
		this.isStress = isStress;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getReality() {
		return reality;
	}

	public void setReality(String reality) {
		this.reality = reality;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getForecast() {
		return forecast;
	}

	public void setForecast(String forecast) {
		this.forecast = forecast;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	@Override
	public String toString() {
		return "FlashBean [type=" + type + ", id=" + id + ", title=" + title
				+ ", time=" + time + ", isStress=" + isStress + ", nature="
				+ nature + ", state=" + state + ", reality=" + reality
				+ ", before=" + before + ", forecast=" + forecast + ", effect="
				+ effect + ", EffectGood=" + EffectGood + ", EffectMid="
				+ EffectMid + ", EffectBad=" + EffectBad + "]";
	}

}

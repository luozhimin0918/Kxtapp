package com.jyh.bean;

import java.io.Serializable;
import java.util.List;

public class HqDataBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;// code
	private String name;// 名称
	private String last;// 最新价
	private String open;// 开盘价
	private String lastClose;// 昨收
	private String high;// 最高
	private String low;// 最低
	private String swing;// 涨跌
	private String swingRange;// 涨跌幅
	private String quoteTime;
	private String volume;
	private List<String> falgs;// 标示有改动的数据

	// public HqDataBean() {
	// }
	//
	// public HqDataBean(Parcel in) {
	// code = in.readString();
	// name = in.readString();
	// last = in.readString();
	// open = in.readString();
	// lastClose = in.readString();
	// high = in.readString();
	// low = in.readString();
	// swing = in.readString();
	// swingRange = in.readString();
	// quoteTime = in.readString();
	// volume = in.readString();
	// falgs = new ArrayList<String>();
	// in.readList(falgs, ClassLoader.getSystemClassLoader());
	// }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getLastClose() {
		return lastClose;
	}

	public void setLastClose(String lastClose) {
		this.lastClose = lastClose;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getSwing() {
		return swing;
	}

	public void setSwing(String swing) {
		this.swing = swing;
	}

	public String getSwingRange() {
		return swingRange;
	}

	public void setSwingRange(String swingRange) {
		this.swingRange = swingRange;
	}

	public String getQuoteTime() {
		return quoteTime;
	}

	public void setQuoteTime(String quoteTime) {
		this.quoteTime = quoteTime;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public List<String> getFalgs() {
		return falgs;
	}

	public void setFalgs(List<String> falgs) {
		this.falgs = falgs;
	}

	@Override
	public String toString() {
		return "HqDataBean [ name=" + name + "       " + last + "]";
	}

	// @Override
	// public int describeContents() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public void writeToParcel(Parcel dest, int flags) {
	// // TODO Auto-generated method stub
	// dest.writeString(code);
	// dest.writeString(name);
	// dest.writeString(last);
	// dest.writeString(open);
	// dest.writeString(lastClose);
	// dest.writeString(high);
	// dest.writeString(low);
	// dest.writeString(swing);
	// dest.writeString(swingRange);
	// dest.writeString(quoteTime);
	// dest.writeString(volume);
	// dest.writeList(falgs);
	// }
	//
	// public static final Parcelable.Creator<HqDataBean> CREATOR = new
	// Parcelable.Creator<HqDataBean>() {
	// public HqDataBean createFromParcel(Parcel in) {
	// return new HqDataBean(in);
	// }
	//
	// public HqDataBean[] newArray(int size) {
	// return new HqDataBean[size];
	// }
	// };

}

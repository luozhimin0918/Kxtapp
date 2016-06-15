package com.jyh.tool;

import com.jyh.gson.bean.ZxInfo;
import com.jyh.kxt.socket.CjInfo;

public interface TSTool {
	/**
	 * 用于推送是否开启
	 */
	void TSOpen();
	void TSZxInfo(ZxInfo object);

	void TSCaiJi(CjInfo obInfo);
}

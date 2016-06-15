package com.jyh.tool;

import java.util.Observable;



/**
 * 消息聊天观察者模如果在聊天界面知有聊天更新则获取信息刷新聊天信息并清除通知
 * 
 * @author zh
 * 
 *         2013-8-28
 */
public class NewChatObservable extends Observable {

	private Object view = null;

	public static NewChatObservable instance;

	private NewChatObservable() {

	}
	public static NewChatObservable getInstance() {
		if (instance == null) {
			instance = new NewChatObservable();
		}
		return instance;
	}

	public Object getView() {
		return view;
	}

	public void setData(Object view) {
		setChanged();
		notifyObservers(view);
		// 只有在setChange()被调用后，notifyObservers()才会去调用update()，否则什么都不干�?
	}

	public void setData() {
		setChanged();
		notifyObservers(view);
		// 只有在setChange()被调用后，notifyObservers()才会去调用update()，否则什么都不干�?
	}

}

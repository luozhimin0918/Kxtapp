package com.jyh.gson.bean;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 对Volley框架重新封装，返回fastjsonobject
 */
@SuppressWarnings("rawtypes")
public class FastJsonRequest<T> extends Request<T> {
	// 定义Response<Listener>

	private Response.Listener listener;
	// 定义Class
	private Class<T> clazz;
	private T t;
	// 接受params的传递数据
	private Map<String, String> map;

	// 自定构造方法接受class
	// 接受实际的T
	// 自己添加构造方法必须要复写
	public FastJsonRequest(int method, String url, Map<String, String> map,
			Class<T> clazz, Response.Listener listener,
			Response.ErrorListener errolistener) {
		// 这里的listener是error信息的返回
		this(method, url, clazz, listener, errolistener);
		this.map = map;
	}

	// 自定构造方法接受class
	// 接受实际的T
	// 自己添加构造方法必须要复写
	public FastJsonRequest(int method, String url, Class<T> clazz,
			Response.Listener listener, Response.ErrorListener errolistener) {
		// 这里的listener是error信息的返回
		this(method, url, listener, errolistener);
		this.clazz = clazz;
	}

	// 自己添加构造方法必须要复写
	public FastJsonRequest(int method, String url, Response.Listener listener,
			Response.ErrorListener errolistener) {
		// 这里的listener是error信息的返回
		super(method, url, errolistener);
		this.listener = listener;
	}

	// 父类的构造方法必须要复写
	public FastJsonRequest(int method, String url,
			Response.ErrorListener listener) {
		// 这里的listener是error信息的返回
		super(method, url, listener);

	}

	// 实现监听接口的方法回调
	@SuppressWarnings("unchecked")
	@Override
	protected void deliverResponse(T t) {
		if (listener != null)
			listener.onResponse(t);
	}

	// 实际需要传递的内容
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		// 加入FastJson的解析
		// 第一步：实际获取的内容data,是byte[]
		byte[] data = response.data;
		try {
			// 第二步:将byte[]转String(第二个参数：确定编码格式)
			String s = new String(data,
					HttpHeaderParser.parseCharset(response.headers));
			// 通过FastJson解析返回T
			Gson gson = new Gson();
			t = gson.fromJson(s, clazz);
			return Response.success(t,
					HttpHeaderParser.parseCacheHeaders(response));

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 针对Post的参数接受(由于构造方法直接接受了params的参数,内部复写了getParams的方法)
	// 外部就不需要重写 getParams
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		if (map != null) {
			return map;
		}
		return super.getParams();
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return super.getHeaders();
	}
}

package com.jyh.tool;

import it.sauronsoftware.base64.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
	/**
	 * 加密
	 * 
	 * @param data
	 *            原始数据
	 * @param key
	 *            加密Key
	 * @param expire
	 *            过期时间
	 * @return 加密后的数据
	 */

	public static String encrypt(String data, String key, int expire)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		long time = 0;
		if (expire != 0) {
			time = System.currentTimeMillis();
		}
		StringBuffer _time = time1((time + expire * 1000) / 1000);
		// System.out.println((System.currentTimeMillis() + expire * 1000) /
		// 1000);
		String _key = md5(key);
		// System.out.println("md5加密后的key：" + _key);
		String _data = it.sauronsoftware.base64.Base64.encode(_time + data);// data
		// System.out.println("Base64加密后的数据：" + _data);
		int x = 0;
		int dataLen = _data.length();
		int keyLen = _key.length();
		// System.out.println("dataLen=" + dataLen + "---" + "keyLen=" +
		// keyLen);
		StringBuffer sb1 = new StringBuffer();
		for (int i = 0; i < dataLen; i++) {
			if (x == keyLen)
				x = 0;
			sb1.append(_key.substring(x, x + 1));
			x++;
		}
		// System.out.println("sb1=" + sb1);
		byte[] bytes = new byte[dataLen];
		for (int i = 0; i < dataLen; i++) {
			int b = (int) _data.substring(i, i + 1).charAt(0)
					+ sb1.substring(i, i + 1).charAt(0) % 256;
			if (b > 127)
				b -= 256;
			bytes[i] = (byte) b;
			// System.out.print(bytes[i] + " ");
		}
		decrypt(new String(Base64.encode(bytes)).replace("+", "-")
				.replace("=", "").replace("/", "_"), key);
		return new String(Base64.encode(bytes)).replace("+", "-")
				.replace("=", "").replace("/", "_");

	}

	/*
	 * 格式化过期时间
	 */
	private static StringBuffer time1(long l) {
		StringBuffer time = new StringBuffer();
		StringBuffer _time = new StringBuffer();

		switch (time.append(l).length()) {
		case 1:
			_time.append("000000000").append(l);
			break;
		case 2:
			_time.append("00000000").append(l);
			break;
		case 3:
			_time.append("0000000").append(l);
			break;
		case 4:
			_time.append("000000").append(l);
			break;
		case 5:
			_time.append("00000").append(l);
			break;
		case 6:
			_time.append("0000").append(l);
			break;
		case 7:
			_time.append("000").append(l);
			break;
		case 8:
			_time.append("00").append(l);
			break;
		case 9:
			_time.append("0").append(l);
			break;
		case 10:
			_time.append(l);
			break;
		default:
			_time.append("0000000000");
			break;
		}
		return _time;
	}

	/*
	 * md5加密
	 */

	public static String md5(String key) {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes("utf-8"));
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_md5 = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return re_md5;
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            加密后的数据
	 * @param key
	 *            加密时用到的key
	 * @return 原始数据
	 */
	public static String decrypt(String data, String key)
			throws UnsupportedEncodingException {
		String _key = md5(key);
		String _data = data.replace("-", "+").replace("_", "/");
		int mod4 = _data.length() % 4;
		if (mod4 != 0) {
			_data += "====".substring(mod4);
		}
		// System.out.println("_data=" + _data);
		byte[] bytes = it.sauronsoftware.base64.Base64.decode(_data.getBytes());
		int dataLen = bytes.length;
		int x = 0;
		int keyLen = _key.length();
		// System.out.println("dataLen=" + dataLen + "---keyLen=" + keyLen);
		// System.out.println("md5加密后的key：" + _key);
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		for (int i = 0; i < dataLen; i++) {
			if (x == keyLen)
				x = 0;
			sb.append(_key.substring(x, x + 1));
			x++;
		}
		for (int i = 0; i < dataLen; i++) {
			if (bytes[i] < sb.substring(i, i + 1).charAt(0)) {
				sb2.append((char) (bytes[i] + 256 - sb.substring(i, i + 1)
						.charAt(0)));
			} else {
				sb2.append((char) (bytes[i] - sb.substring(i, i + 1).charAt(0)));
			}
		}
		// System.out.println("sb2=" + sb2);
		_data = Base64.decode(sb2.toString());
		StringBuffer sb3 = new StringBuffer();
		StringBuffer sb4 = new StringBuffer();
		for (int i = 0; i < _data.length(); i++) {
			// sb3.append((char)bytes[i]);
			if (i < 10) {
				// 将时间对应的ASCII码转换为时间
				sb3.append((char) _data.charAt(i));
			} else {
				// data
				sb4.append(_data.charAt(i));
			}
		}

		boolean isOut = time2(new String(sb3));
		_data = new String(sb4);
		// System.out.print( " 过期时间：" + time);
		@SuppressWarnings("unused")
		String str = null;
		if (isOut)
			str = "时间过长，数据失效";
		else
			str = _data;
		return _data;
	}

	/*
	 * 判断数据是否过期
	 */
	private static boolean time2(String _data) {
		long time = Long.parseLong(_data.substring(0, 10));
		if (System.currentTimeMillis() / 1000 > time) {
			return true;
		}
		return false;
	}

}

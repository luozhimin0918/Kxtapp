package com.jyh.tool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtils {

	public static InputStream httpConn(String path) {
		InputStream is = null;
		try {

			HttpURLConnection huc = (HttpURLConnection) new URL(path)
					.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			if (huc.getResponseCode() == 200) {
				is = huc.getInputStream();
				return is;
			}
			return null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getGetData(String path, String unicode) {

		HttpURLConnection conn = null;

		InputStream in = null;

		BufferedReader bReader = null;

		try {

			URL url = new URL(path);

			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");

			conn.setReadTimeout(8000);

			if (conn.getResponseCode() == 200) {

				in = conn.getInputStream();

				// utf-8
				bReader = new BufferedReader(new InputStreamReader(in, unicode));

				String line = "";

				StringBuffer sb = new StringBuffer();

				while ((line = bReader.readLine()) != null) {
					sb.append(line);
				}

				return sb.toString();

			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (bReader != null) {
					bReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		return null;
	}

	public static String getPostData(String path, Map<String, String> map) {

		String mapValue = getMapData(map);

		HttpURLConnection conn = null;

		PrintWriter pw = null;

		InputStream in = null;

		BufferedReader bReader = null;

		try {

			URL url = new URL(path);

			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestProperty("accept", "*/*");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Content-Length", mapValue.length() + "");

			conn.setDoOutput(true);

			conn.setDoInput(true);

			conn.setRequestMethod("POST");

			conn.setReadTimeout(8000);

			pw = new PrintWriter(conn.getOutputStream());

			pw.write(mapValue);

			pw.flush();

			StringBuffer sb = new StringBuffer();

			if (conn.getResponseCode() == 200) {

				in = conn.getInputStream();

				bReader = new BufferedReader(new InputStreamReader(in, "utf-8"));

				String line = "";

				while ((line = bReader.readLine()) != null) {
					sb.append(line);
				}

				return sb.toString();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pw != null) {
					pw.close();
				}
				if (in != null) {
					in.close();
				}
				if (bReader != null) {
					bReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		return null;
	}

	/**
	 * ƴ�ӳ�map�е�����
	 * 
	 * @param map
	 * @return String
	 */
	private static String getMapData(Map<String, String> map) {

		StringBuffer sb = new StringBuffer();

		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {

			Entry<String, String> entry = iterator.next();

			String key = entry.getKey();

			sb.append(key + "=");

			String value = entry.getValue();

			sb.append(value + "&");
		}

		if (sb.length() > 0) {

			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();

	}

	/**
	 * ����ͼƬ
	 * 
	 * @param httpPath
	 * @return
	 */
	public static byte[] getPicuterData(String httpPath) {

		HttpURLConnection conn = null;

		InputStream in = null;

		ByteArrayOutputStream byteOut = null;
		try {
			URL url = new URL(httpPath);

			conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");

			conn.setReadTimeout(8000);

			conn.connect();

			if (conn.getResponseCode() == 200) {

				in = conn.getInputStream();

				byteOut = new ByteArrayOutputStream();

				byte[] b = new byte[1024 * 4];
				int len;
				while ((len = in.read(b)) != -1) {
					byteOut.write(b, 0, len);
				}

				return byteOut.toByteArray();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (byteOut != null) {
					byteOut.close();
				}
				if (in != null) {
					in.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}

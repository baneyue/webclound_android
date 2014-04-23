package com.webcloud.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpUtil {

	private static final int TIMEOUT = 15 * 1000;
	private static final int HTTP_SUCCESS = 200;
	private static final int HTTP_NOT_FOUND = 404;

	public static String doGet(String url, Map<String, String> params)
			throws IOException {
		StringBuilder log = new StringBuilder("params:{");
		StringBuilder sb = new StringBuilder(url);
		if (null != params && !params.isEmpty()) {
			sb.append("?");
			for (Map.Entry<String, String> entry : params.entrySet()) {
				log.append(entry.getKey())
						.append("=")
						.append(null == entry.getValue() ? "null" : ""
								.equals(entry.getValue()) ? "\"\"" : entry
								.getValue().replace("\n", "")).append("; ");
				if (null == entry.getValue() || "".equals(entry.getValue())
						|| "null".equals(entry.getValue())) {
					continue;
				}
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(URLEncoder.encode(entry.getValue().replace("\n", ""),
						"UTF-8"));
				sb.append("&");
			}
			log.delete(log.length() - 2, log.length());
		}
		log.append("}");
		sb.deleteCharAt(sb.length() - 1);

		LogUtil.d("request get, " + log.toString());
		LogUtil.d(sb.toString());

		URL u = new URL(sb.toString());
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(TIMEOUT);
		if (HTTP_SUCCESS == conn.getResponseCode()) {
			byte[] data = StreamUtil.readInputStream(conn.getInputStream());
			FlowUtil.count(data.length);
			String result = new String(data);
			LogUtil.d("response: \n" + result);
			return result;
		} else {
			LogUtil.d("response: " + conn.getResponseCode() + " "
					+ conn.getResponseMessage());
		}
		return null;
	}

	public static byte[] doGet(String url) throws IOException {
		if (null == url || "".equals(url)) {
			return null;
		}
		LogUtil.d("request get: " + url);
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(TIMEOUT);
		if (HTTP_SUCCESS == conn.getResponseCode()) {
			byte[] data = StreamUtil.readInputStream(conn.getInputStream());
			FlowUtil.count(data.length);
			LogUtil.d("response: \n" + data);
			return data;
		} else {
			LogUtil.d("response: " + conn.getResponseCode() + " "
					+ conn.getResponseMessage());
		}
		return null;
	}

	public static String doPost(String url, Map<String, String> params) {
		HttpURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			StringBuilder log = new StringBuilder("params:[");
			StringBuilder sb = new StringBuilder();
			if (null != params && !params.isEmpty()) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					log.append(entry.getKey())
							.append("=")
							.append(null == entry.getValue() ? "null" : ""
									.equals(entry.getValue()) ? "\"\"" : entry
									.getValue().replace("\n", "")).append("; ");
					if (null == entry.getValue()
							|| "null".equals(entry.getValue())) {
						continue;
					}
					sb.append(entry.getKey());
					sb.append("=");
					sb.append(URLEncoder.encode(
							entry.getValue().replace("\n", ""), "UTF-8"));
					sb.append("&");
				}
				sb.deleteCharAt(sb.length() - 1);
				log.delete(log.length() - 2, log.length());
			}
			log.append("]");

			LogUtil.d("request post, " + log.toString());
			LogUtil.d(url + "?" + sb.toString());

			byte[] postData = sb.toString().getBytes();

			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(TIMEOUT);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(postData.length));
			os = conn.getOutputStream();
			os.write(postData);
			os.flush();
			if (HTTP_SUCCESS == conn.getResponseCode()) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int length = 0;
				while ((length = is.read(buffer)) != -1) {
					baos.write(buffer, 0, length);
				}
				byte[] data = baos.toByteArray();
				FlowUtil.count(data.length);
				String result = new String(data);
				LogUtil.d("response: \n" + result);
				return result;
			} else {
				LogUtil.d("response: " + conn.getResponseCode() + " "
						+ conn.getResponseMessage());
			}
		} catch (Exception e) {
			LogUtil.e(e);
		} finally {
			if (null != conn) {
				conn.disconnect();
				conn = null;
			}
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Deprecated
	public static String doPostByHttpClient(String url,
			Map<String, String> parmas) throws ClientProtocolException,
			IOException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (null != parmas && !parmas.isEmpty()) {
			for (Map.Entry<String, String> entry : parmas.entrySet()) {
				BasicNameValuePair bnvp = new BasicNameValuePair(
						entry.getKey(), entry.getValue());
				nameValuePairs.add(bnvp);
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs,
				"UTF-8");
		HttpPost post = new HttpPost(url);
		post.setEntity(entity);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(post);
		if (HTTP_SUCCESS == response.getStatusLine().getStatusCode()) {
			return response.getEntity().toString();
		}
		return null;
	}

	/**
	 * 
	 * @param address
	 *            http://www.xxx.com
	 * @param path
	 *            /xxx/yyy.jpg
	 * @param localPath
	 *            /aaa/bbb，最终存储为/aaa/bbb/xxx/yyy.jpg
	 * @return
	 */
	public static String download(String address, String path, String localPath) {
		if (null == address || "".equals(address) || null == path
				|| "".equals(path)) {
			return "";
		}
		if (null == localPath || "".equals(localPath)) {
			return address + path;
		}

		String filePath = localPath + path;
		File localFile = new File(filePath);
		if (localFile.exists() && localFile.isFile() && localFile.length() > 0) {
			return filePath;
		}

		String tempPath = filePath + ".temp";
		File tempFile = new File(tempPath);
		if (tempFile.exists() && tempFile.isFile()) {
			if (!tempFile.delete()) {
				FileUtil.chmod777(tempFile);
				tempFile.delete();
			}
		}
		if (localFile.exists() && localFile.isFile()) {
			if (!localFile.delete()) {
				FileUtil.chmod777(localFile);
				localFile.delete();
			}
		}

		boolean flag = FileUtil.createNewDir(localPath
				+ path.substring(0, path.lastIndexOf("/")));
		if (!flag) {
			LogUtil.d("download createNewDir fail " + localPath
					+ path.substring(0, path.lastIndexOf("/")));
		}
		String url = address + path;
		String result = url;

		HttpURLConnection conn = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(TIMEOUT);
			if (HTTP_SUCCESS == conn.getResponseCode()) {
				LogUtil.d("download " + url);
				bis = new BufferedInputStream(conn.getInputStream());
				fos = new FileOutputStream(tempFile);
				bos = new BufferedOutputStream(fos);
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = bis.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				bos.flush();
				tempFile.renameTo(localFile);
				FileUtil.chmod777(localFile);
				LogUtil.d("download success " + filePath);
				result = filePath;
			} else if (HTTP_NOT_FOUND == conn.getResponseCode()) {
				LogUtil.d("download not found 404 " + url);
			} else {
				LogUtil.d("response: " + conn.getResponseCode() + " "
						+ conn.getResponseMessage());
			}
		} catch (IOException e) {
			if (e instanceof SocketTimeoutException) {
				LogUtil.d("download timeout " + url);
			} else {
				LogUtil.d("download fail " + url);
				e.printStackTrace();
			}
		} finally {
			if (null != conn) {
				conn.disconnect();
			}
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public static InputStream download(String url) {
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(TIMEOUT);
			if (HTTP_SUCCESS == conn.getResponseCode()) {
				is = conn.getInputStream();
			}
		} catch (IOException e) {
			LogUtil.d("download fail " + url);
			e.printStackTrace();
		}
		return is;
	}
}

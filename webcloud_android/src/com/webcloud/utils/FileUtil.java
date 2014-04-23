package com.webcloud.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {

	public static boolean createNewDir(String path) {
		if (null == path || "".equals(path)) {
			return false;
		}
		boolean result = false;
		File dir = new File(path);
		if (dir.exists() && dir.isDirectory()) {
			result = true;
		} else {
			result = dir.mkdirs();
			chmod777(dir);
		}

		return result;
	}

	public static void chmod777(File file) {
		try {
			File parentFile = file.getParentFile();
			if (null != parentFile) {
				chmod777(parentFile);
			}
			Runtime.getRuntime().exec("chmod 777 " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readByInputStream(InputStream is) {
		StringBuffer sb = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			sb = new StringBuffer();
			String line = "";
			while (null != (line = br.readLine())) {
				sb.append(line);
			}
		} catch (IOException e) {
			sb = null;
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (null != sb) {
			return sb.toString();
		}

		return null;
	}

	public static boolean copyFile(String from, String to) {
		boolean result = false;
		if (null == from || null == to || "".equals(from) || "".equals(to)) {
			return result;
		}
		File fromFile = new File(from);
		File toFile = new File(to);
		if (toFile.exists() && toFile.isFile()) {
			if (!toFile.delete()) {
				return result;
			}
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(fromFile);
			fos = new FileOutputStream(toFile);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();
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
}

package com.funlib.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZipUtily {

	private final static int CacheSize = 1024;

	/***
	 * 压缩Zip
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] zipByte(byte[] data) {
		Deflater compresser = new Deflater();
		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		byte result[] = new byte[0];
		ByteArrayOutputStream o = new ByteArrayOutputStream(1);
		try {
			byte[] buf = new byte[CacheSize];
			int got = 0;
			while (!compresser.finished()) {
				got = compresser.deflate(buf);
				o.write(buf, 0, got);
			}

			result = o.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			compresser.end();
		}
		return result;
	}

	/***
	 * 压缩String
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] zipString(String data) {
		byte[] input = new byte[0];
		try {
			input = data.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		byte[] result = ZipUtily.zipByte(input);
		return result;
	}

	/***
	 * 解压Zip
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] unZipByte(byte[] data) {
		Inflater decompresser = new Inflater();
		decompresser.setInput(data);
		byte result[] = new byte[0];
		ByteArrayOutputStream o = new ByteArrayOutputStream(1);
		try {
			byte[] buf = new byte[CacheSize];
			int got = 0;
			while (!decompresser.finished()) {
				got = decompresser.inflate(buf);
				o.write(buf, 0, got);
			}
			result = o.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			decompresser.end();
		}
		return result;
	}

	/***
	 * 解压Zip数据为String
	 * 
	 * @param data
	 * @return
	 */
	public static String unZipByteToString(byte[] data) {
		byte[] result = unZipByte(data);
		String outputString = null;
		try {
			outputString = new String(result, 0, result.length, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return outputString;
	}
}

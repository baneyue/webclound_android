package com.funlib.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 文件操作类
 * 
 * @author taojianli
 * 
 */
public class FileUtily {

	/**
	 * 获取SD卡路径
	 * @return
	 */
	public static String getSDPath() {
		
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			String tmpPath = sdDir.getAbsolutePath();
			if(!tmpPath.endsWith(File.separator))
				tmpPath += File.separator;
			return tmpPath;
		}
		
		return null;
	}
	
	/**
	 * 获取应用在SD卡的路径
	 * @param name
	 * @return
	 */
	private static String sAppSDPath = null;
	public static String getAppSDPath(){
		
		return sAppSDPath;
	}
	
	public static boolean initAppSDPath(String name){
		
		try {
            String sdPath = getSDPath();
            if(TextUtils.isEmpty(sdPath) == false){
            	
            	sdPath += name + File.separator;
            	
            	File file = new File(sdPath);
            	if(file.exists() == false)
            		file.mkdir();
            	
            	sAppSDPath = sdPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return true;
	}

	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static boolean saveBytes(String filePath, byte[] data) {
		try {
		    if(data == null) return false;
		    
			File file = new File(filePath);
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(data);
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}
	
	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static boolean saveBytes(File file, byte[] data) {

		try {
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(data);
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}
	
	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static byte[] getBytes(String filePath) {

		try {

			File file = new File(filePath);
			if(!file.exists() || !file.canRead()) return null;
			FileInputStream inStream = new FileInputStream(file);
			byte bytes[] = new byte[inStream.available()];
			inStream.read(bytes);
			inStream.close();
			
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

	}
	
	
	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static boolean saveObject(String filePath, Object object) {

		try {

			File file = new File(filePath);
			FileOutputStream outStream = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(outStream);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return false;
	}
	
	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static boolean saveObject(File file, Object object) {

		try {
			FileOutputStream outStream = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(outStream);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return false;
	}
	
	/**
	 * 如果sdcard没有mounted，返回false
	 * 
	 * @param os
	 * @return
	 */
	public static Object getObject(String filePath) {

		try {

			File file = new File(filePath);
			if(!file.exists() || !file.canRead()) return null;
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			ois.close();
			fis.close();
			ois = null;
			fis = null;
			
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 创建文件夹
	 * @param dirPath
	 * @return
	 */
	public static boolean mkDir(String dirPath){
		
		File file = new File(dirPath);
		if(file.exists() == false){
			
			return file.mkdirs();
		}
		
		return true;
	}
	
	/**
	 * 创建临时文件
	 * @param fielPath
	 * @return
	 */
	public static File createTmpFile(Context context){
		
		String rootPath = context.getFilesDir().getAbsolutePath();
		if(rootPath.endsWith("/") == false){
			
			rootPath += "/";
		}
		
		try {
			
			String tmpPath = rootPath + new Date().hashCode();
			File file = new File(tmpPath);
			file.createNewFile();
			
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 应用退出时，删除所有临时文件
	 * @param context
	 */
	public static void deleteTmpFiles(Context context){
		
		File rootFiles = context.getFilesDir();
		File files[] = rootFiles.listFiles();
		if(files != null){
			
			int cnt = files.length;
			for(int i = 0 ; i < cnt ; ++i){
				
				File tmpFile = files[i];
				tmpFile.delete();
			}
		}
	}
}

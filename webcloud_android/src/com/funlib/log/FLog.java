package com.funlib.log;

import android.util.Log;

public class FLog {

	public final static boolean DEBUG = true;
	public final static String TAG = "[funlib]";

    private static StackTraceElement getCurrentStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    } 
 
    private static String getMethodName(StackTraceElement trace) {
        return String.format("%s>>%s>>%s" , trace.getFileName(),trace.getMethodName(),
                trace.getLineNumber());
    }
    
	public static void i(String msg) {

		if (DEBUG) {
			Log.i(TAG, "[" + getMethodName(getCurrentStackTraceElement()) + "]>>>>[" + msg + "]");
		}
	}
}

package com.webcloud.func.map.component;


/**
 * MessageDialog对话框
 * 
 * @author taojianli
 * 
 */
public interface StartEndMessageDialogListener {

	/**
	 * 
	 * @param dialog 自身
	 * @param which
	 *            MESSAGEDIALOG_OK or MESSAGEDIALOG_CANCEL
	 */
	public void onMessageDialogClick(StartEndMessageDialog dialog, int which);
}

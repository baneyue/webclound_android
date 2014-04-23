package com.webcloud.component;


/**
 * MessageDialog对话框
 * 
 * @author taojianli
 * 
 */
public interface NewMessageDialogListener {

	/**
	 * 
	 * @param dialog 自身
	 * @param which
	 *            MESSAGEDIALOG_OK or MESSAGEDIALOG_CANCEL
	 */
	public void onMessageDialogClick(NewMessageDialog dialog, int which);
}

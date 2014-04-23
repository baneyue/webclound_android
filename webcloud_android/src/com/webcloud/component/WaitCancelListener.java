package com.webcloud.component;

/**
 * MessageDialog对话框
 * 
 * @author taojianli
 * 
 */
public abstract class WaitCancelListener {
    protected Object obj;
    public WaitCancelListener(Object obj){
        this.obj = obj;
    }

	public abstract void onWaittingCancel();
}

package com.funlib.timer;

import android.os.Handler;

/**
 * 定时器类
 * @author taojianli
 *
 */
public class BestTimer implements Runnable{

	private Handler tickRequestHandler = new Handler();
	private int TIME_INTERVAL = 0;
	private BestTimerListener mBestTimerListener;
	private boolean bCancel;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(mBestTimerListener != null)
			mBestTimerListener.timerUp();
		if(bCancel == false){
			if(tickRequestHandler != null)
				tickRequestHandler.postDelayed(this, TIME_INTERVAL);
		}
	}
	
	/**
	 * 开启定时器
	 * @param interval
	 */
	public void startTimer(int interval){
		
		bCancel = false;
		TIME_INTERVAL = interval;
		tickRequestHandler = new Handler();
		tickRequestHandler.postDelayed(this,TIME_INTERVAL);
	}
	
	/**
	 * 取消定时器
	 */
	public void cancelTimer(){
		
		bCancel = true;
		if(tickRequestHandler != null){
			tickRequestHandler.removeCallbacks(this);
			tickRequestHandler = null;
		}
	}
	
	/**
	 * 设置定时器监听
	 * @param l
	 */
	public void setTimerListener(BestTimerListener l){
		
		mBestTimerListener = l;
	}
	
	public interface BestTimerListener{
		
		public void timerUp();
	}
}

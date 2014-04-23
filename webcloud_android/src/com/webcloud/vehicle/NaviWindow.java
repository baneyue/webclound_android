package com.webcloud.vehicle;

import com.webcloud.R;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RadioGroup;

public class NaviWindow {

	private WindowManager wm;
	private Handler aHandler;
	private WindowManager.LayoutParams params;
	private View layout;
	
	public NaviWindow(VehicleHomeActivity context, Handler handler){
		wm = context.getWindowManager();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		aHandler = handler;
		layout = inflater.inflate(R.layout.vehicle_navi, null);
		layout.findViewById(R.id.back).setOnClickListener(context);
		layout.findViewById(R.id.searchButton).setOnClickListener(context);
		((RadioGroup)layout.findViewById(R.id.naviType)).setOnCheckedChangeListener(context);
		params = new LayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;
		params.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_ALT_FOCUSABLE_IM;    
		params.type = LayoutParams.LAST_APPLICATION_WINDOW;
	}  
	
	public void show(){
		if(!layout.isShown()){
			wm.addView(layout, params);
		}
	}
	
	public boolean isShown(){
		return layout.isShown();
	}
	
	public void close(){
		if(layout.isShown()){
			wm.removeView(layout);
		}
	}
}

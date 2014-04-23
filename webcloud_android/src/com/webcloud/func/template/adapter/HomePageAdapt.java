package com.webcloud.func.template.adapter;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class HomePageAdapt extends PagerAdapter {
	private List<View> listViews;
	private Context context;
	public HomePageAdapt(Context context,List<View> listViews) {
		this.context = context;
		this.listViews = listViews;
	}
	
	@Override
	public int getCount() {
		return null!=listViews?listViews.size():0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(listViews.get(position));
		return listViews.get(position);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(listViews.get(position));
	}
}

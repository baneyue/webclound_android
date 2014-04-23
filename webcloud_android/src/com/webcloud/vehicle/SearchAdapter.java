package com.webcloud.vehicle;

import java.util.List;

import com.amap.api.services.help.Tip;
import com.webcloud.R;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {

	private List<Tip> data;
	private Activity activity;

	public SearchAdapter(List<Tip> data, Activity activity) {
		this.data = data;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (null != convertView) {
			holder = (Holder) convertView.getTag();
		} else {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.vehicle_search_item, null);
			holder = new Holder();
			holder.text = (TextView) convertView.findViewById(R.id.searchItem);
			convertView.setTag(holder);
		}
		Tip tip = data.get(position);
		if(null != tip){
			if(null!=tip.getName()&&!"".equals(tip.getName())){
				StringBuffer sb = new StringBuffer(tip.getName());
				if(null!=tip.getDistrict()&&!"".equals(tip.getDistrict())){
					sb.append("--");
					sb.append(tip.getDistrict());
				}
				holder.text.setText(sb.toString());		
			}else if(data.size() == 1 && position ==0){
				holder.text.setText("没有匹配的结果");	
			}
		}
		return convertView;
	}

	static class Holder {
		TextView text;
	}
}

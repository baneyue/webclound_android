package com.webcloud.func.email.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webcloud.R;


/**
 * @author ZhangZheng
 * @date 2014-01-19
 */
public class MenuListAdapter extends BaseAdapter {
	//左侧菜单名称
	private String[] itemNames;
	private LayoutInflater inflater;

	public MenuListAdapter(Context context, String[] itemNames) {
		this.itemNames = itemNames;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return itemNames.length;
	}

	@Override
	public Object getItem(int position) {
		return itemNames[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = inflater.inflate(R.layout.email_menu_list_item, null);
		}
		TextView text = (TextView) view.findViewById(R.id.menu_name);
		text.setText(itemNames[position]);
		return view;
	}

}

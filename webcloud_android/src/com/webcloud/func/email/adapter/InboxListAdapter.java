package com.webcloud.func.email.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.webcloud.R;
import com.webcloud.func.email.model.EmailVo;

public class InboxListAdapter extends BaseAdapter {
	private List<EmailVo> emailList;
	private LayoutInflater inflater;
	private int checkboxFlag = 0;//复选框是否显示的标记

	public InboxListAdapter(Context context, List<EmailVo> emailList) {
		this.emailList = emailList;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCheckboxFlag(int checkboxFlag) {
		this.checkboxFlag = checkboxFlag;
	}

	@Override
	public int getCount() {
		return emailList.size();
	}

	@Override
	public Object getItem(int position) {
		return emailList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = inflater.inflate(R.layout.email_inbox_fragment_item, null);
		}
		// 复选框
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.inboxCheckbox);
		if (checkboxFlag == 0) {
			checkbox.setVisibility(View.GONE);
		}
		if (checkboxFlag == 1) {
			checkbox.setVisibility(View.VISIBLE);
		}
		EmailVo vo = emailList.get(position);
		TextView name = (TextView) view.findViewById(R.id.tvInboxItemName);
		name.setText(vo.getFrom());
		// 附件标识
		ImageView attach = (ImageView) view
				.findViewById(R.id.ivInboxAttachment);
		if (!vo.isHasFile()) {
			attach.setVisibility(View.INVISIBLE);
		} else {
			attach.setVisibility(View.VISIBLE);
		}
		// 已读标识
		TextView hasRead = (TextView) view.findViewById(R.id.tvInboxEmailReaded);
		if ("0".equals(vo.getFlag())) {
			hasRead.setVisibility(View.VISIBLE);
		} else {
			hasRead.setVisibility(View.INVISIBLE);
		}
		TextView date = (TextView) view.findViewById(R.id.tvInboxItemDate);
		date.setText(vo.getSentDate());
		TextView title = (TextView) view.findViewById(R.id.tvInboxItemTitle);
		title.setText(vo.getSubject());
		TextView cont = (TextView) view.findViewById(R.id.tvInboxItemContent);
		String content = vo.getContent();
		if(content.length()>10){
			cont.setText(content.substring(0, 10)+".....");
		}else{
			cont.setText(content);
		}
		
		return view;
	}

}

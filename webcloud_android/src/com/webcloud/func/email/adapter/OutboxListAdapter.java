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

public class OutboxListAdapter extends BaseAdapter {
	private List<EmailVo> emailList;
	private LayoutInflater inflater;
	private int checkboxFlag = 0;// 复选框是否显示的标记

	public OutboxListAdapter(Context context, List<EmailVo> emailList) {
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
			view = inflater.inflate(R.layout.email_outbox_fragment_item, null);
		}
		// 复选框
		CheckBox checkbox = (CheckBox) view.findViewById(R.id.outboxCheckbox);
		if (checkboxFlag == 0) {
			checkbox.setVisibility(View.GONE);
		}
		if (checkboxFlag == 1) {
			checkbox.setVisibility(View.VISIBLE);
		}
		EmailVo vo = emailList.get(position);
		TextView name = (TextView) view.findViewById(R.id.tvOutboxItemName);
		name.setText(vo.getFrom());
		// 附件标识
		ImageView attach = (ImageView) view
				.findViewById(R.id.ivOutboxAttachment);
		if (!vo.isHasFile()) {
			attach.setVisibility(View.GONE);
		} else {
			attach.setVisibility(View.VISIBLE);
		}
		TextView date = (TextView) view.findViewById(R.id.tvOutboxItemDate);
		date.setText(vo.getSentDate());
		TextView title = (TextView) view.findViewById(R.id.tvOutboxItemTitle);
		title.setText(vo.getSubject());
		TextView cont = (TextView) view.findViewById(R.id.tvOutboxItemContent);
		cont.setText(vo.getContent());
		return view;
	}

}

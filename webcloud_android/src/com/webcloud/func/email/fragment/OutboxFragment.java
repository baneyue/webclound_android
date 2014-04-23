package com.webcloud.func.email.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.webcloud.R;
import com.webcloud.func.email.EmailDetailActivity;
import com.webcloud.func.email.EmailEditActivity;
import com.webcloud.func.email.adapter.OutboxListAdapter;
import com.webcloud.func.email.model.EmailVo;

/**
 * 发件箱
 * 
 * @author ZhangZheng
 * @date 2014-01-21
 */
public class OutboxFragment extends Fragment implements OnClickListener {

	// 发件箱列表适配器
	private OutboxListAdapter adapter;
	private List<EmailVo> dataList = new ArrayList<EmailVo>();
	private ImageView mWriteEmail;
	// 底部操作菜单
	private LinearLayout bottomMenuLay;
	private TextView mSelectAll;
	private TextView mMarkRead;
	private TextView mDelete;

	public void setDataList(List<EmailVo> dataList) {
		this.dataList = dataList;
	}

	public OutboxFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.email_outbox_fragment,
				container, false);
		ListView lvInbox = (ListView) rootView.findViewById(R.id.lvOutbox);
		lvInbox.setOnItemClickListener(itemListener);
		adapter = new OutboxListAdapter(getActivity(), dataList);
		lvInbox.setAdapter(adapter);
		// 打开菜单抽屉
		ImageView ivDrawerImg = (ImageView) rootView
				.findViewById(R.id.ivOutboxDrawerImg);
		ivDrawerImg.setOnClickListener(this);
		// 操作已发邮件
		ImageView ivEmailEdit = (ImageView) rootView
				.findViewById(R.id.ivOutboxEmailEdit);
		ivEmailEdit.setOnClickListener(this);
		// 编写新邮件
		mWriteEmail = (ImageView) rootView
				.findViewById(R.id.ivOutboxEmailWrite);
		mWriteEmail.setOnClickListener(this);
		bottomMenuLay = (LinearLayout) rootView
				.findViewById(R.id.layOutboxLayoutBottom);
		mSelectAll = (TextView) rootView.findViewById(R.id.tvOutboxSelectAll);
		mMarkRead = (TextView) rootView.findViewById(R.id.tvOutboxMarkRead);
		mDelete = (TextView) rootView.findViewById(R.id.tvOutboxDelete);
		mSelectAll.setOnClickListener(this);
		mMarkRead.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		refreshData();
		return rootView;
	}

	// 选项点击事件
	OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 进入邮件详情页面
			startActivity(new Intent(getActivity(), EmailDetailActivity.class));
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivOutboxEmailEdit:
			// 显示复选框
			adapter.setCheckboxFlag(1);
			adapter.notifyDataSetChanged();
			// 弹出底部菜单
			bottomMenuLay.setVisibility(View.VISIBLE);
			break;
		case R.id.ivOutboxDrawerImg:
			listenter.operateDrawer(true);
			break;
		case R.id.ivOutboxEmailWrite:
			startActivity(new Intent(getActivity(),EmailEditActivity.class));
			break;
		case R.id.tvOutboxSelectAll:
			// 全选
			Toast.makeText(getActivity(), "全选", Toast.LENGTH_LONG).show();
			break;
		case R.id.tvOutboxMarkRead:
			// 标记为已读
			Toast.makeText(getActivity(), "标记为已读", Toast.LENGTH_LONG).show();
			break;
		case R.id.tvOutboxDelete:
			// 删除
			Toast.makeText(getActivity(), "删除", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	/**
	 * 隐藏底部编辑菜单
	 * @return
	 */
	public boolean hiddenEdit(){
		if(bottomMenuLay.getVisibility() == View.VISIBLE){
			//重新适配数据并隐藏底部菜单
			adapter.setCheckboxFlag(0);
			adapter.notifyDataSetChanged();
			bottomMenuLay.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	public void refreshData() {
		List<EmailVo> emailList = new ArrayList<EmailVo>();
		EmailVo vo1 = new EmailVo();
		vo1.setFrom("李四");
		vo1.setSentDate("2014-01-18");
		vo1.setSubject("测试用例概要");
		vo1.setHasFile(true);
		vo1.setContent("zzzzzzzzzzzzzzzzzz");
		EmailVo vo2 = new EmailVo();
		vo2.setFrom("赵六");
		vo2.setSentDate("2014-01-24");
		vo2.setSubject("入职申请报告");
		vo2.setHasFile(false);
		vo2.setContent("ssssssssssssssssss");
		emailList.add(vo1);
		emailList.add(vo2);
		emailList.add(vo1);
		emailList.add(vo2);
		dataList.clear();
		dataList.addAll(emailList);
		adapter.notifyDataSetChanged();
	}

	onOutboxLoadedListener listenter;

	public interface onOutboxLoadedListener {
		public void onOutboxLoaded();
		public void operateDrawer(boolean open);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listenter = (onOutboxLoadedListener) activity;
		} catch (Exception e) {
			throw new RuntimeException(activity.toString()
					+ "must implements onOutboxLoadedListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}

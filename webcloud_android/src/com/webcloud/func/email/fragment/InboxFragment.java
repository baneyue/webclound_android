package com.webcloud.func.email.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

import com.alibaba.fastjson.JSONObject;
import com.funlib.http.HttpRequest;
import com.funlib.http.request.RequestListener;
import com.funlib.http.request.RequestStatus;
import com.funlib.http.request.Requester;
import com.funlib.json.JsonFriend;
import com.webcloud.R;
import com.webcloud.define.HttpUrlImpl;
import com.webcloud.func.email.EmailDetailActivity;
import com.webcloud.func.email.adapter.InboxListAdapter;
import com.webcloud.func.email.model.EmailDetailVo;
import com.webcloud.func.email.model.EmailVo;

/**
 * 收件箱
 * 
 * @author ZhangZheng
 * @date 2014-01-20
 */
public class InboxFragment extends Fragment implements OnClickListener{

	// 收件箱列表适配器
	private InboxListAdapter adapter;
	private List<EmailVo> dataList = new ArrayList<EmailVo>();
	private EmailVo emailVo;
	// 底部操作菜单
	private LinearLayout bottomMenuLay;
	private TextView mSelectAll;
	private TextView mMarkRead;
	private TextView mDelete;

	public void setDataList(List<EmailVo> dataList) {
		this.dataList = dataList;
	}

	public InboxFragment() {
		super();
	}
	
	public void refreshData(){	
		Map<String, String> params = new HashMap<String, String>();
		params.put("productKey", "meeting");
		params.put("ecCode", "340100900686669");
		params.put("mailUser", "055162681081@189.cn");
		params.put("mailPassword", "62681081");
		params.put("currentPage", "1");
		params.put("pageSize", "10");
		new Requester(getActivity()).request(requestInboxList,
                 HttpUrlImpl.EMAIL.GET_INBOX_EMAIL,
                 HttpUrlImpl.EMAIL.GET_INBOX_EMAIL.getUrl(),
                 params,
                 HttpRequest.GET,
                 false);
	}
	
	RequestListener requestInboxList = new RequestListener(){
		@Override
		public void requestStatusChanged(int statusCode, HttpUrlImpl requestId,
				String responseString, Map<String, String> requestParams) {			
			if (!(requestId instanceof HttpUrlImpl.EMAIL)) {
                return;
            }
            // 若无需业务处理就直接返回
            if (statusCode != RequestStatus.SUCCESS) {
                return;
            }            
            try {
				HttpUrlImpl.EMAIL v1 = (HttpUrlImpl.EMAIL)requestId;
				 switch (v1) {
				 	case GET_INBOX_EMAIL://
				 		if (!TextUtils.isEmpty(responseString)) {
							try {
								JSONObject jsRoot = JsonFriend.parseJSONObject(responseString);
								JsonFriend<EmailVo> jsDataList = new JsonFriend<EmailVo>(EmailVo.class);
								String retCode = jsRoot.getString("retcode");
								if ("0".equals(retCode)) {
									dataList.clear();
									List<EmailVo> emailList = jsDataList.parseArray(
											jsRoot.getJSONObject("retdata").getString("mailList"));
									dataList.addAll(emailList);
									adapter.notifyDataSetChanged();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
				 		}
				 	break;
				 	case GET_EMAIL_DETAIL:
				 		if (!TextUtils.isEmpty(responseString)) {
							try {
								JSONObject jsRoot = JsonFriend.parseJSONObject(responseString);
								JsonFriend<EmailDetailVo> jsDataList = new JsonFriend<EmailDetailVo>(EmailDetailVo.class);
								String retCode = jsRoot.getString("retcode");
								if ("0".equals(retCode)) {
									EmailDetailVo detialvo = jsDataList.parseObject(jsRoot.getString("message"));
									Intent intent = new Intent(getActivity(), EmailDetailActivity.class);
									Bundle bundle = new Bundle();     
									bundle.putSerializable("emailDetailVo",detialvo);     
									intent.putExtras(bundle); 
									//进入邮件详情页面
									startActivity(intent);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
				 		}
				 	break;
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}   
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.email_inbox_fragment,
				container, false);
		ListView lvInbox = (ListView) rootView.findViewById(R.id.lvInbox);
		lvInbox.setOnItemClickListener(itemListener);
		adapter = new InboxListAdapter(getActivity(), dataList);
		lvInbox.setAdapter(adapter);
		//打开抽屉图片
		ImageView ivDrawerImg = (ImageView) rootView.findViewById(R.id.ivInboxDrawerImg);
		ivDrawerImg.setOnClickListener(this);
		//编辑邮件(标记为已读,删除)
		ImageView ivEmailEdit = (ImageView) rootView.findViewById(R.id.ivInboxEmailEdit);
		ivEmailEdit.setOnClickListener(this);
		bottomMenuLay = (LinearLayout) rootView.findViewById(R.id.layInboxLayoutBottom);
		//底部菜单
		mSelectAll = (TextView) rootView.findViewById(R.id.tvInboxSelectAll);
		mMarkRead = (TextView) rootView.findViewById(R.id.tvInboxMarkRead);
		mDelete = (TextView) rootView.findViewById(R.id.tvInboxDelete);
		mSelectAll.setOnClickListener(this);
		mMarkRead.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		refreshData();
		return rootView;
	}
	
	// 邮件点击事件
	OnItemClickListener itemListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			emailVo = dataList.get(position);
			int emailId=emailVo.getId();
			getEmailDetailById(emailId);
		}
	};
	
	private void getEmailDetailById(int emailId){
		Map<String, String> params = new HashMap<String, String>();
		params.put("productKey", "meeting");
		params.put("ecCode", "340100900686669");
		params.put("messageNumber", emailId+"");
		params.put("filedownUrl", "");
		params.put("previewUrl", "");
		new Requester(getActivity()).request(requestInboxList,
                HttpUrlImpl.EMAIL.GET_EMAIL_DETAIL,
                HttpUrlImpl.EMAIL.GET_EMAIL_DETAIL.getUrl(),
                params,
                HttpRequest.GET,
                false);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivInboxEmailEdit:
			//显示复选框
			adapter.setCheckboxFlag(1);
			adapter.notifyDataSetChanged();
			//弹出底部菜单
			bottomMenuLay.setVisibility(View.VISIBLE);
			break;
		case R.id.ivInboxDrawerImg:
			listenter.operateDrawer(true);
			break;
		case R.id.tvInboxSelectAll:
			// 全选
			Toast.makeText(getActivity(), "全选", Toast.LENGTH_LONG).show();
			
			break;
		case R.id.tvInboxMarkRead:
			// 标记为已读
			Toast.makeText(getActivity(), "标记为已读", Toast.LENGTH_LONG).show();
			break;
		case R.id.tvInboxDelete:
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
	
	private onInboxLoadedListener listenter;
	
	public interface onInboxLoadedListener{
		public void onInboxLoaded();
		public void operateDrawer(boolean open);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			listenter = (onInboxLoadedListener)activity;
		} catch (Exception e) {
			throw new RuntimeException(activity.toString() + "must implements onInboxLoadedListener");
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
}

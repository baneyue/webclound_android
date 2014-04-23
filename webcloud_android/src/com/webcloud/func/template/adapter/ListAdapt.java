package com.webcloud.func.template.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webcloud.R;
import com.webcloud.define.Constants;
import com.webcloud.func.template.DetailActivity;
import com.webcloud.func.template.ImgListActivity;
import com.webcloud.model.Category;
import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.Content;
import com.webcloud.utils.ImageTool;
import com.webcloud.webservice.impl.IUserService;

public class ListAdapt extends BaseAdapter{

	private Context context;
	private List listDatas;
	private IUserService iUserService;
	private String cid;
	private String title;
	
	/**
	 * 数据类型
	 * type false:Category true:content
	 */
	private boolean dataType;
	
	public ListAdapt(Context context, List listDatas,String title) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listDatas = listDatas;
		this.title = title;
		iUserService = IUserService.getInstant();
		if(listDatas!=null&&listDatas.size()>0){
			if(listDatas.get(0) instanceof Category){
				dataType = false;
			}else if(listDatas.get(0) instanceof Content){
				dataType = true;
			}
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listDatas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(null==convertView){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem, null);
		}
		ImageView imagView = (ImageView) convertView.findViewById(R.id.mimage);
		TextView tvTitle = (TextView) convertView.findViewById(R.id.mview1);
		if(!dataType){
			Category category = (Category) listDatas.get(position);
			ImageTool.setImgView(context, imagView, category.getIcon(), false);
			tvTitle.setText(category.getTitle());
			convertView.setTag(category);
		}else{
			Content content =  (Content) listDatas.get(position);
			convertView.setTag(content);
			tvTitle.setText(content.getTitle());
			ImageTool.setImgView(context, imagView, content.getIcon(), false);
		}
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dataType){
					Content content = (Content) v.getTag();
					Intent intent = new Intent(context, DetailActivity.class);
					intent.putExtra("content", content);
					intent.putExtra("title", title);
					context.startActivity(intent);
				}else{
					Category category = (Category) v.getTag();
					cid = category.getId();
					new Thread(getCategoryAndContent).start();
				}
			}
		});
		return convertView;
	}

	
	Handler handler =  new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 101:
				Intent intent = null;
				CategoryAndContent categoryAndContent = (CategoryAndContent) msg.obj;
				if (categoryAndContent.getRenderStyle() == 1) { // 列表风格
					intent = new Intent(context,ListActivity.class);
				} else if (categoryAndContent.getRenderStyle() == 2) { // 缩略图风格
					intent = new Intent(context,ImgListActivity.class);
				}
				if (categoryAndContent.getType().equals("categories")) {
					intent.putParcelableArrayListExtra("categories",
							(ArrayList<? extends Parcelable>) categoryAndContent
									.getCategories());
				} else if (categoryAndContent.getType().equals("contents")) {
					intent.putParcelableArrayListExtra("content",
							(ArrayList<? extends Parcelable>) categoryAndContent
									.getContents());
				}
				context.startActivity(intent);
				break;

			default:
				break;
			}
		}
	};
	
	Runnable getCategoryAndContent = new Runnable() {

		@Override
		public void run() {
			Message msg = Message.obtain();
			CategoryAndContent categoryAndContent = iUserService
					.getCategorieOrContent(Constants.PRODUCTKEY, Constants.ECCODE,
							cid);
			msg.obj = categoryAndContent;
			msg.what = 101;
			handler.sendMessage(msg);
		}
	};
	
}

package com.webcloud.func.template.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webcloud.R;
import com.webcloud.define.Constants;
import com.webcloud.func.template.DetailActivity;
import com.webcloud.func.template.ImgListActivity;
import com.webcloud.model.Category;
import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.Content;
import com.webcloud.utils.ImageTool;
import com.webcloud.utils.LogUtil;
import com.webcloud.webservice.impl.IUserService;

/**
 * 
 * @author Administrator
 * 
 */
public class ImgListAdapt extends BaseAdapter{

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
	public ImgListAdapt(Context context, List listDatas,String title) {
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
		int count = listDatas.size() / 3;
		return listDatas.size() % 3 == 0 ? count : (count + 1);
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		LogUtil.i("position: " + position);
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.imglist_item, null);
		}
		for (int i = 0; i < ((LinearLayout) convertView).getChildCount(); i++) {
			LinearLayout lin = (LinearLayout) ((LinearLayout) convertView)
					.getChildAt(i);
			if (lin.getVisibility() == View.INVISIBLE) {
				lin.setVisibility(View.VISIBLE);
			}
			LogUtil.i( "listDatas:" + listDatas.size() + "position: "
					+ position + "count: " + (position * 3 + i));
			try {
				ImageView image = (ImageView) lin.getChildAt(0);
				Log.w("Tag", "width" + image.getLayoutParams().width);
				TextView tv = (TextView) lin.getChildAt(1);
				if(!dataType){
					Category category = (Category) listDatas.get(position * 3 + i);
					ImageTool.setImgView(context, image, category.getIcon(), false);
					tv.setText(category.getTitle());
					lin.setTag(category);
				}else{
					Content content =  (Content) listDatas.get(position * 3 + i);
					lin.setTag(content);
					ImageTool.setImgView(context, image, content.getIcon(), false);
					tv.setText(content.getTitle());
				}
				lin.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(dataType){
							Content content = (Content) v.getTag();
							Intent intent = new Intent(context, DetailActivity.class);
							intent.putExtra("content", content);
							intent.putExtra("Title", title);
							context.startActivity(intent);
						}else{
							Category category = (Category) v.getTag();
							cid = category.getId();
							new Thread(getCategoryAndContent).start();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				lin.setVisibility(View.INVISIBLE);
			}
		}

		return convertView;
	}

	Handler handler = new Handler(){
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
	
	
	public void addNewItem(Object object) {
		listDatas.add(object);
	}

	public void addNewsItem(List list) {
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				listDatas.add(list.get(i));
			}
		}
	}

	public int getItemCount() {
		return listDatas.size();
	}

}

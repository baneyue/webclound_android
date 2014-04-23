package com.webcloud.func.template;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.webcloud.BaseActivity;
import com.webcloud.R;
import com.webcloud.func.template.adapter.ListAdapt;


/** 
* @class:ListActivity.java  
* @author：Wangwei
* @date：Dec 17, 2013     
* @comment：        
*/
public class ListActivity extends BaseActivity implements View.OnClickListener{
	private ListView listView;
	private ListAdapt listAdapt;
	private List listData;
	private LinearLayout title_back;
	private TextView tv_title;
	private String title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initView();
		initData();
		init();
	}
	
	private void initView(){
		listView = (ListView)findViewById(R.id.listview);
		title_back = (LinearLayout) findViewById(R.id.title_back);
		title_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.title);
	}
	
	
	private void initData(){
		List categories = getIntent().getIntegerArrayListExtra("categories");
		List contents = getIntent().getIntegerArrayListExtra("contents");
		title = getIntent().getStringExtra("Title");
		if(null!=categories){
			listData = categories;
		}else if(null!=contents){
			listData = contents;
		}
	}

	private void init() {
		if(null!=listData){
			listAdapt = new ListAdapt(this,listData,title);
			listView.setAdapter(listAdapt);
			tv_title.setText(title);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back:
			finish();
			System.gc();
			break;

		default:
			break;
		}
	}
}

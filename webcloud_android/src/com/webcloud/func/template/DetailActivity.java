package com.webcloud.func.template;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webcloud.BaseActivity;
import com.webcloud.R;
import com.webcloud.model.Content;
import com.webcloud.utils.ImageTool;

public class DetailActivity extends BaseActivity{
	private Content content;
	private LinearLayout title_back;
	private TextView tv_title;
	private TextView tv_content;
	private ImageView img_content;
	private TextView title_content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		initView();
		
		initData();
	}
	
	private void initView(){
		tv_content = (TextView) findViewById(R.id.tv_content);
		img_content = (ImageView) findViewById(R.id.img_content);
		title_back = (LinearLayout) findViewById(R.id.title_back);
		title_content= (TextView) findViewById(R.id.title_content);
		tv_title = (TextView) findViewById(R.id.title);
		title_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				System.gc();
			}
		});
	}
	
	private void initData(){
		content = getIntent().getParcelableExtra("content");
		if(null!=content){
			tv_title.setText(content.getTitle());
			tv_content.setText(content.getContent());
			title_content.setText(content.getTitle());
			ImageTool.setImgView(this, img_content, content.getIcon(), false);
		}
	}
}

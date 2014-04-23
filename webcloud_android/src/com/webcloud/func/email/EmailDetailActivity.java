package com.webcloud.func.email;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.webcloud.BaseActivity;
import com.webcloud.R;
import com.webcloud.func.email.model.EmailDetailVo;

/**
 * 邮件详情页
 * 
 * @author ZhangZheng
 * @date 2014-01-22
 */
public class EmailDetailActivity extends BaseActivity implements OnClickListener{
	
	private ImageView mBackBtn;
	private TextView mTitle;
	private TextView mFrom;
	private TextView mContent;
	private ImageView mHasFile;
	private EmailDetailVo emailDetailVo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_detail);
		initViews();
	}
	
	private void initViews() {
		emailDetailVo = (EmailDetailVo) getIntent().getSerializableExtra("emailDetailVo");
		mBackBtn = (ImageView) findViewById(R.id.btnDetailBack);
		mBackBtn.setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.tvEmailTitle);
		mFrom = (TextView) findViewById(R.id.tvEmailFrom);
		mContent = (TextView) findViewById(R.id.tvEmailContent);
		mHasFile = (ImageView) findViewById(R.id.tvEmailFileFlag);
		if(emailDetailVo!=null){
			mTitle.setText(emailDetailVo.getSubject());
			mFrom.setText(emailDetailVo.getFrom());
			mContent.setText(emailDetailVo.getContent());
			boolean flag = emailDetailVo.isHasFile();
			if(flag){
				mHasFile.setVisibility(View.VISIBLE);				
			}else{
				mHasFile.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDetailBack:
			EmailDetailActivity.this.finish();
			break;

		default:
			break;
		}
	}
}

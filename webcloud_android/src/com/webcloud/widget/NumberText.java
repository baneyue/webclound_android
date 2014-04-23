package com.webcloud.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SubscriptSpan;
import android.widget.TextView;

/**
 * 详情显示大图
 */
public class NumberText extends TextView {

	private String currentPage;
	private String maxPage;
	private Context context;
	private SpannableString msp;

	public NumberText(Context context, String currentPage, String maxPage) {
		super(context);
		this.currentPage = currentPage;
		this.maxPage = maxPage;
		this.context = context;

		initText();
	}

	private void initText() {
		setMsp();

	}

	public void changePage(String page) {
		currentPage = page;
		setMsp();
	}

	private void setMsp() {
		msp = new SpannableString(currentPage + "/" + maxPage);
		msp.setSpan(new AbsoluteSizeSpan(20, true), 0, 1,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new SubscriptSpan(), 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		this.setText(msp);
	}

}

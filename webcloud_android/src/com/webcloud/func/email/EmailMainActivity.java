package com.webcloud.func.email;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.webcloud.R;
import com.webcloud.func.email.adapter.MenuListAdapter;
import com.webcloud.func.email.fragment.InboxFragment;
import com.webcloud.func.email.fragment.InboxFragment.onInboxLoadedListener;
import com.webcloud.func.email.fragment.OutboxFragment;
import com.webcloud.func.email.fragment.OutboxFragment.onOutboxLoadedListener;

/**
 * 邮箱主页
 * 
 * @author ZhangZheng
 * @date 2014-01-16
 */
public class EmailMainActivity extends FragmentActivity implements
		onInboxLoadedListener, onOutboxLoadedListener {

	private SlidingPaneLayout mSlidingLayout;

	public ListView mMenuList;// 左侧邮箱菜单列表
	private String[] menuNames;// 菜单名称
	// 碎片操作
	Fragment fragmentTemp;
	InboxFragment fragmentInbox;
	OutboxFragment fragmentOutbox;
	final String FRG_INBOX = "inbox", FRG_OUTBOX = "outbox";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_home);
		initViews();
	}

	// 初始化控件
	private void initViews() {
		mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
		mSlidingLayout.openPane();
		// 初始化左侧菜单适配器
		mMenuList = (ListView) findViewById(R.id.lvMenuList);
		menuNames = getResources().getStringArray(R.array.email_menu_arry);
		MenuListAdapter menuListAdapter = new MenuListAdapter(this, menuNames);
		mMenuList.setAdapter(menuListAdapter);
		mMenuList.setOnItemClickListener(menuListener);
		// 初始加载收件箱内容
		addOrReplaceFragmentToContent(FRG_INBOX);
	}

	// 菜单选项点击事件
	OnItemClickListener menuListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 关闭抽屉
			mSlidingLayout.closePane();
			// 根据菜单名称加载对应的内容碎片
			String menu = ((TextView) view.findViewById(R.id.menu_name))
					.getText().toString();
			if ("收件箱".equals(menu)) {
				addOrReplaceFragmentToContent(FRG_INBOX);
			}
			if ("发件箱".equals(menu)) {
//				startActivity(new Intent(EmailMainActivity.this,EmailEditActivity.class));
				addOrReplaceFragmentToContent(FRG_OUTBOX);
			}
		}
	};

	/**
	 * 添加碎片到内容处
	 * 
	 * @param tag
	 */
	private void addOrReplaceFragmentToContent(String tag) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (FRG_INBOX.equals(tag)) {
			if (fragmentInbox == null) {
				fragmentInbox = new InboxFragment();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragmentInbox, tag).commit();
			fragmentTemp = fragmentInbox;
		}
		if (FRG_OUTBOX.equals(tag)) {
			if (fragmentOutbox == null) {
				fragmentOutbox = new OutboxFragment();
			}
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragmentOutbox, tag).commit();
			fragmentTemp = fragmentOutbox;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 使用menu键打开或关闭侧边栏
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			/*
			 * if (mDrawerLayout.isDrawerOpen(lay)) {
			 * mDrawerLayout.closeDrawer(lay); } else {
			 * mDrawerLayout.openDrawer(lay); }
			 */
		}
		// 按下的如果是BACK，同时没有重复
		if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {
			if (fragmentTemp instanceof InboxFragment) {
				if (fragmentInbox.hiddenEdit())
					return true;
			}
			if (fragmentTemp instanceof OutboxFragment) {
				if (fragmentOutbox.hiddenEdit())
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onOutboxLoaded() {

	}

	@Override
	public void onInboxLoaded() {

	}

	@Override
	public void operateDrawer(boolean open) {
		if (open && !mSlidingLayout.isOpen()) {
			mSlidingLayout.openPane();
		}
	}
}

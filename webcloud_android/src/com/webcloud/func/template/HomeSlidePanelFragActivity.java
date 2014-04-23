package com.webcloud.func.template;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SlidingPaneLayout.PanelSlideListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.funlib.zxing.activity.CaptureActivity;
import com.webcloud.BaseFragmentActivity;
import com.webcloud.R;
import com.webcloud.component.LoadingDialog;
import com.webcloud.define.Constants;
import com.webcloud.func.email.EmailMainActivity;
import com.webcloud.func.template.adapter.FragSlideAtomListViewAdapter;
import com.webcloud.func.template.adapter.FragSlideMenuListViewAdapter;
import com.webcloud.func.template.fragment.ListFragment;
import com.webcloud.func.template.fragment.ListImgFragment;
import com.webcloud.model.Atom;
import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.Menu;
import com.webcloud.model.MenuAndAtom;
import com.webcloud.utils.LogUtil;
import com.webcloud.utily.BackUtil;
import com.webcloud.utily.Utily;
import com.webcloud.webservice.impl.IUserService;

/**
 * 抽屉风格的首页。
 * 
 * @author  bangyue
 * @version  [版本号, 2014-1-17]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class HomeSlidePanelFragActivity extends BaseFragmentActivity {
    
    private IUserService iUserService;
    
    private MenuAndAtom menuAndAtom;
    
    private SharedPreferences settings;
    
    private LoadingDialog loadingDialog;
    
    private String cid;// 内容编号
    
    private String menuTitle;//菜单名称
    
    /** 声明抽屉菜单控件 */
    //private SlidingMenu menu;
    
    private ListView lvMenu;
    
    private ListView lvAtom;
    
    private View selectedView;
    
    //private DrawerLayout layDrawer;
    
    private SlidingPaneLayout mSlidingLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_slide_panel);
        settings = this.getSharedPreferences("xml_login", 0);
        iUserService = IUserService.getInstant();
        initView();
        initData();
        initFragment();
    }
    
    private void initView() {
        //initSlideMenu();
        lvMenu = (ListView)findViewById(R.id.lvMenu);
        lvAtom = (ListView)findViewById(R.id.lvAtom);
       /* layDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        layDrawer.openDrawer(Gravity.LEFT);*/
        mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
        mSlidingLayout.setPanelSlideListener(new PanelSlideListener() {
            
            @Override
            public void onPanelSlide(View arg0, float arg1) {
                
            }
            
            @Override
            public void onPanelOpened(View arg0) {
                
            }
            
            @Override
            public void onPanelClosed(View arg0) {
            }
        });
        mSlidingLayout.openPane();
    }
    
    /** 初始化抽屉控件 */
    /*private void initSlideMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.home_slidemenu_left);
        //menu.setSecondaryMenu(R.layout.view_setting);
        menu.toggle();
    }*/
    
    //初始化fragment
    FragmentTransaction fragmentTransaction;
    
    FragmentManager fragmentManager;
    
    ListImgFragment listImgFrag;
    
    ListFragment listFrag;
    
    /** 
     * 初始化fragment.
     *
     * @see [类、类#方法、类#成员]
     */
    private void initFragment() {
        listImgFrag = new ListImgFragment();
        listFrag = new ListFragment();
        fragmentManager = this.getSupportFragmentManager();
    }
    
    //显示路况弹窗
    private void showFragment(Fragment fragment, Bundle bundle) {
        if (fragment.getArguments() == null) {
            fragment.setArguments(bundle);
        } else {
            fragment.getArguments().putAll(bundle);
        }
        if (!fragment.isAdded()) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
            fragmentTransaction.add(R.id.layContent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
    
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 101:
                    setMenuListView(menuAndAtom);
                    setAtomListView(menuAndAtom);
                    //				stopProgressDialog();
                    break;
                case 102:
                    //				stopProgressDialog();
                    Intent intent = null;
                    CategoryAndContent categoryAndContent = (CategoryAndContent)msg.obj;
                    if (null != categoryAndContent && categoryAndContent.getRetCode() == 0) {
                        Bundle data = new Bundle();
                        if (categoryAndContent.getType().equals("categories")) {
                            data.putParcelableArrayList("categories",
                                (ArrayList<? extends Parcelable>)categoryAndContent.getCategories());
                        } else if (categoryAndContent.getType().equals("contents")) {
                            data.putParcelableArrayList("contents",
                                (ArrayList<? extends Parcelable>)categoryAndContent.getContents());
                            
                        }
                        data.putString("Title", menuTitle);
                        if (categoryAndContent.getRenderStyle() == 1) { // 列表风格
                            /*intent =
                                new Intent(HomeSlideFragDrawActivity.this, ListActivity.class);*/
                            showFragment(listFrag, data);
                            mSlidingLayout.closePane();
                        } else if (categoryAndContent.getRenderStyle() == 2) { // 缩略图风格
                            /* intent =
                                 new Intent(HomeSlideFragDrawActivity.this, ImgListActivity.class);*/
                            showFragment(listImgFrag, data);
                            mSlidingLayout.closePane();
                        }
                    }
                    break;
            }
        };
    };
    
    //没有效果图，暂时不显示加载动画
    private void startProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.createDialog(this);
            loadingDialog.setMessage("正在加载中...");
        }
        
        loadingDialog.show();
    }
    
    //没有效果图，暂时不显示加载动画
    private void stopProgressDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
    
    private void initData() {
        //		startProgressDialog(); 
        new Thread(runnable).start();
    }
    
    Runnable runnable = new Runnable() {
        
        @Override
        public void run() {
            menuAndAtom =
                iUserService.getMenuAndAtom(Constants.PHONE_NUMBER,
                    Constants.PRODUCTKEY,
                    settings.getString("ECCode", ""));
            handler.sendEmptyMessage(101);
        }
    };
    
    public void setMenuListView(MenuAndAtom menuAndAtom) {
        if (null == menuAndAtom) {
            LogUtil.d("menuAndAtom is null");
            return;
        }
        List<Menu> menus = menuAndAtom.getMenus();
        if (null != menus && menus.size() > 0) {
            lvMenu.setAdapter(new FragSlideMenuListViewAdapter(this, menus));
            Utily.setListViewHeightBasedOnChildren(lvMenu);
            lvMenu.setOnItemClickListener(new OnItemClickListener() {
                
                @Override
                public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                    Menu menu = (Menu)(((ViewGroup)v).getChildAt(0).getTag());
                    menuOnClick(menu);
                    if (selectedView != null) {
                        selectedView.setSelected(false);
                    }
                    selectedView = v;
                    v.setSelected(true);
                }
            });
        }
    }
    
    public void setAtomListView(MenuAndAtom menuAndAtom) {
        if (null == menuAndAtom) {
            LogUtil.d("menuAndAtom is null");
            return;
        }
        List<Atom> atoms = menuAndAtom.getAtoms();
        if (atoms != null && atoms.size() > 0) {
            lvAtom.setAdapter(new FragSlideAtomListViewAdapter(this, atoms));
            Utily.setListViewHeightBasedOnChildren(lvAtom);
            lvAtom.setOnItemClickListener(new OnItemClickListener() {
                
                @Override
                public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                    Atom atom = (Atom)(((ViewGroup)v).getChildAt(0).getTag());
                    atomOnClick(atom);
                    if (selectedView != null) {
                        selectedView.setSelected(false);
                    }
                    selectedView = v;
                    v.setSelected(true);
                }
            });
        }
    }
    
    public void showMessage(String str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 420);
        toast.show();
    }
    
    /**
     * 菜单点击事件
     * 
     * @param menu
     */
    public void menuOnClick(Menu menu) {
        if (null == menu) {
            return;
        }
        if ("local".equals(menu.getMenuType())) { // 基础菜单
            Intent intent = null;
            try {
                //				intent = new Intent(this,
                //						Class.forName("com.webcloud.activity.ListActivity"));
                //				this.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } else if ("app".equals(menu.getMenuType())) { // 第三方应用
        
        } else if ("webview".equals(menu.getMenuType())) { // web界面
        
        } else if ("content".equals(menu.getMenuType())) { // 内容管理
            cid = menu.getMpackage();
            menuTitle = menu.getName();
            //			startProgressDialog();
            new Thread(getCategoryAndContent).start();
        }
    }
    
    Runnable getCategoryAndContent = new Runnable() {
        
        @Override
        public void run() {
            Message msg = Message.obtain();
            CategoryAndContent categoryAndContent =
                iUserService.getCategorieOrContent(Constants.PRODUCTKEY, Constants.ECCODE, cid);
            msg.obj = categoryAndContent;
            msg.what = 102;
            handler.sendMessage(msg);
        }
    };
    
    /**
     * 原子点击事件
     * 
     * @param atom
     */
    public void atomOnClick(Atom atom) {
        if (null == atom) {
            return;
        }
        if ("扫一扫".equals(atom.getName())) {
            Intent intent = new Intent(HomeSlidePanelFragActivity.this, CaptureActivity.class);
            startActivity(intent);
        }
        if ("邮件".equals(atom.getName())) {
            Intent intent = new Intent(HomeSlidePanelFragActivity.this, EmailMainActivity.class);
            startActivity(intent);
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (new BackUtil().doubleClickBackExit(this)) {
            super.onBackPressed();
        } else {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

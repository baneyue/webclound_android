package com.webcloud.func.template;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

import com.funlib.zxing.activity.CaptureActivity;
import com.webcloud.R;
import com.webcloud.ThemeStyleActivity;
import com.webcloud.component.LoadingDialog;
import com.webcloud.define.Constants;
import com.webcloud.func.email.EmailMainActivity;
import com.webcloud.func.template.adapter.SlideAtomListViewAdapter;
import com.webcloud.func.template.adapter.SlideMenuListViewAdapter;
import com.webcloud.func.template.slidemenu.SlidingMenu;
import com.webcloud.model.Atom;
import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.Menu;
import com.webcloud.model.MenuAndAtom;
import com.webcloud.utils.LogUtil;
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
public class HomeSlideMenuActivity extends ThemeStyleActivity {
    
    private IUserService iUserService;
    
    private MenuAndAtom menuAndAtom;
    
    private SharedPreferences settings;
    
    private LoadingDialog loadingDialog;
    
    private String cid;// 内容编号
    
    private String menuTitle;//菜单名称
    
    /** 声明抽屉菜单控件 */
    private SlidingMenu menu;
    private ListView lvMenu;
    private ListView lvAtom;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_slidemenu);
        settings = this.getSharedPreferences("xml_login", 0);
        iUserService = IUserService.getInstant();
        initView();
        initData();
    }
    
    private void initView() {
        initSlideMenu();
        lvMenu = (ListView)findViewById(R.id.lvMenu);
        lvAtom = (ListView)findViewById(R.id.lvAtom);
    }
    
    /** 初始化抽屉控件 */
    private void initSlideMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        /*menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);*/
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.home_slidemenu_left);
        //menu.setSecondaryMenu(R.layout.view_setting);
        menu.toggle();
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
                            if (categoryAndContent.getRenderStyle() == 1) { // 列表风格
                                intent =
                                    new Intent(HomeSlideMenuActivity.this, ListActivity.class);
                            } else if (categoryAndContent.getRenderStyle() == 2) { // 缩略图风格
                                intent =
                                    new Intent(HomeSlideMenuActivity.this, ImgListActivity.class);
                            }
                        
                        if (categoryAndContent.getType().equals("categories")) {
                            intent.putParcelableArrayListExtra("categories",
                                (ArrayList<? extends Parcelable>)categoryAndContent.getCategories());
                        } else if (categoryAndContent.getType().equals("contents")) {
                            intent.putParcelableArrayListExtra("contents",
                                (ArrayList<? extends Parcelable>)categoryAndContent.getContents());
                            
                        }
                        intent.putExtra("Title", menuTitle);
                        startActivity(intent);
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
            lvMenu.setAdapter(new SlideMenuListViewAdapter(this, menus));
            Utily.setListViewHeightBasedOnChildren(lvMenu);
        }
    }
    
    public void setAtomListView(MenuAndAtom menuAndAtom){
        if (null == menuAndAtom) {
            LogUtil.d("menuAndAtom is null");
            return;
        }
        List<Atom> atoms = menuAndAtom.getAtoms();
        if(atoms != null && atoms.size() > 0) {
            lvAtom.setAdapter(new SlideAtomListViewAdapter(this, atoms));
            Utily.setListViewHeightBasedOnChildren(lvAtom);
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
            Intent intent = new Intent(HomeSlideMenuActivity.this, CaptureActivity.class);
            startActivity(intent);
        }
        if ("邮件".equals(atom.getName())) {
            Intent intent = new Intent(HomeSlideMenuActivity.this, EmailMainActivity.class);
            startActivity(intent);
        }
        if ("天气预报".equals(atom.getName())) {
            Intent intent = new Intent(HomeSlideMenuActivity.this, WeatherActivity.class);
            startActivity(intent);
        }
    }

}

package com.webcloud.func.template;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.funlib.utily.Utily;
import com.funlib.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webcloud.R;
import com.webcloud.ThemeStyleActivity;
import com.webcloud.WebCloudApplication;
import com.webcloud.component.LoadingDialog;
import com.webcloud.define.Constants;
import com.webcloud.func.email.EmailMainActivity;
import com.webcloud.func.template.adapter.GridViewAdapter;
import com.webcloud.func.template.adapter.HomePageAdapt;
import com.webcloud.manager.client.ImageCacheManager;
import com.webcloud.model.Atom;
import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.Menu;
import com.webcloud.model.MenuAndAtom;
import com.webcloud.utils.LogUtil;
import com.webcloud.vehicle.VehicleHomeActivity;
import com.webcloud.webservice.impl.IUserService;

/**
 * 网格风格的首页。
 * 
 * @author  bangyue
 * @version  [版本号, 2014-1-17]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class HomeGridViewActivity extends ThemeStyleActivity {
    
    private ViewPager homePager;// 中间页面
    
    private LinearLayout linTab; // 底部分页标识
    
    private IUserService iUserService;
    
    private MenuAndAtom menuAndAtom;
    
    private SharedPreferences settings;
    
    private LoadingDialog loadingDialog;
    
    private String cid;// 内容编号
    
    private String menuTitle;//菜单名称
    
    /**网格菜单条目的宽度，每行四个*/
    public int itemWidth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_gridview);
        settings = this.getSharedPreferences("xml_login", 0);
        //itemWidth = settings.getInt("itemWidth", 0);
        if (itemWidth == 0) {
            itemWidth = (WebCloudApplication.screenWidth - Utily.dip2px(this, 20)) / 3;
            Editor ed = settings.edit();
            ed.putInt("itemWidth", itemWidth);
            ed.commit();
        }
        iUserService = IUserService.getInstant();
        initView();
        initData();
        
    }
    
    private void initView() {
        homePager = (ViewPager)findViewById(R.id.viewpager);
        linTab = (LinearLayout)findViewById(R.id.layTab);
        homePager.setOnPageChangeListener(new MOnPageChangeListener());
    }
    
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 101:
                    homePager.setAdapter(new HomePageAdapt(HomeGridViewActivity.this, getViewPageView(menuAndAtom)));
                    getAtomView(menuAndAtom);
                    //				stopProgressDialog();
                    break;
                case 102:
                    //				stopProgressDialog();
                    Intent intent = null;
                    CategoryAndContent categoryAndContent = (CategoryAndContent)msg.obj;
                    if (null != categoryAndContent && categoryAndContent.getRetCode() == 0) {
                        if (categoryAndContent.getRenderStyle() == 1) { // 列表风格
                            intent = new Intent(HomeGridViewActivity.this, ListActivity.class);
                        } else if (categoryAndContent.getRenderStyle() == 2) { // 缩略图风格
                            intent = new Intent(HomeGridViewActivity.this, ImgListActivity.class);
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
    
    /** 
     * 适配网格menu.
     *
     * @param menuAndAtom
     * @return
     */
    private List<View> getViewPageView(MenuAndAtom menuAndAtom) {
        List<View> listView = new ArrayList<View>();
        if (null == menuAndAtom) {
            LogUtil.d("menuAndAtom is null");
            return listView;
        }
        List<Menu> menus = menuAndAtom.getMenus();
        List<Atom> atoms = menuAndAtom.getAtoms();
        int pageNum = 9;
        if (null != menus && menus.size() > 0) {
            int pageCount = menus.size() % pageNum == 0 ? menus.size() / pageNum : (menus.size() / pageNum + 1);
            //每个页面的gridView放指定数目的menu
            for (int i = 0; i < pageCount; i++) {
                LinearLayout lin = (LinearLayout)getLayoutInflater().inflate(R.layout.home_gridview_page_item, null);
                listView.add(lin);
                
                int startIndex = i * pageNum;
                //结束索引如果大于列表的长度，那么取列表的最后索引
                int endIndex =
                    ((i + 1) * pageNum - 1) > (menus.size() - 1) ? (menus.size() - 1) : ((i + 1) * pageNum - 1);
                
                List<Menu> subMenu = menus.subList(startIndex, endIndex);
                GridViewAdapter adapter = new GridViewAdapter(this, subMenu);
                GridView gvMenu = (GridView)lin.findViewById(R.id.gvMenu);
                gvMenu.setNumColumns(3);
                gvMenu.setColumnWidth(itemWidth);
                gvMenu.setAdapter(adapter);
                //viewPager的tab
                ImageView imageView = new ImageView(this);
                imageView.setPadding(5, 0, 5, 0);
                imageView.setImageResource(R.drawable.home_switch);
                linTab.addView(imageView);
            }
            setSelsct(0);
        }
        return listView;
    }
    
    /** 
     * 适配底部原子menu.
     *
     * @param menuAndAtom
     */
    public void getAtomView(MenuAndAtom menuAndAtom) {
        if (null == menuAndAtom) {
            LogUtil.d("menuAndAtom is null");
        }
        List<Atom> atoms = menuAndAtom.getAtoms();
        LinearLayout layAtom = (LinearLayout)findViewById(R.id.layAtom);
        layAtom.removeAllViews();
        if (null != atoms && atoms.size() > 0) {
            for (int m = 0; m < atoms.size(); m++) {
                LinearLayout layAtomItem =
                    (LinearLayout)getLayoutInflater().inflate(R.layout.home_gridview_atom_item, null);
                ImageView ivAtom = (ImageView)layAtomItem.findViewById(R.id.ivAtom);
                try {
                    ivAtom.setTag(atoms.get(m));
                    /*ImageTool.setImgView(HomeActivity.this, btnAtom, atoms
                            .get(m).getIconUrl(), true);*/
                    ImageLoader imgLoader = mgr.imgCacheMgr.getImageLoader();
                    imgLoader.displayImage(atoms.get(m).getIconUrl(),
                        ivAtom,
                        mgr.imgCacheMgr.getOptions(ImageCacheManager.CACHE_MODE_CACHE),
                        null);
                    ivAtom.setOnClickListener(new View.OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                            Atom atom = (Atom)v.getTag();
                            atomOnClick(atom);
                            Toast.makeText(HomeGridViewActivity.this, atom.getName(), 500).show();
                        }
                    });
                    layAtom.addView(layAtomItem);
                    ViewGroup.LayoutParams lp = layAtomItem.getLayoutParams();
                    lp.height = itemWidth;
                    lp.width = itemWidth;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void setSelsct(int index) {
        if (null != linTab) {
            int childCount = linTab.getChildCount();
            if (childCount > 0 && index < childCount) {
                for (int i = 0; i < childCount; i++) {
                    ImageView imView = (ImageView)linTab.getChildAt(i);
                    if (i != index) {
                        imView.setImageResource(R.drawable.home_switch);
                    } else {
                        imView.setImageResource(R.drawable.home_switch_selected);
                    }
                }
            }
        }
    }
    
    public void showMessage(String str) {
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 420);
        toast.show();
    }
    
    private class MOnPageChangeListener implements ViewPager.OnPageChangeListener {
        
        @Override
        public void onPageScrollStateChanged(int index) {
        }
        
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        
        @Override
        public void onPageSelected(int index) {
            setSelsct(index);
        }
        
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
    private void atomOnClick(Atom atom) {
        if (null == atom) {
            return;
        }
        if ("扫一扫".equals(atom.getName())) {
            Intent intent = new Intent(HomeGridViewActivity.this, CaptureActivity.class);
            startActivity(intent);
        }else if ("邮件".equals(atom.getName())) {
            Intent intent = new Intent(HomeGridViewActivity.this, EmailMainActivity.class);
            startActivity(intent);
        }else if ("天气预报".equals(atom.getName())) {
            Intent intent = new Intent(HomeGridViewActivity.this, WeatherActivity.class);
            startActivity(intent);
        }else if("导航".equals(atom.getName())){
            Intent intent = new Intent(HomeGridViewActivity.this, VehicleHomeActivity.class);
            startActivity(intent);
        }
    }
    
}

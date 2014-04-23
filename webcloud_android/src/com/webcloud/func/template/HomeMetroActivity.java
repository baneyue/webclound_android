package com.webcloud.func.template;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.funlib.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.webcloud.R;
import com.webcloud.ThemeStyleActivity;
import com.webcloud.component.LoadingDialog;
import com.webcloud.define.Constants;
import com.webcloud.func.email.EmailMainActivity;
import com.webcloud.func.template.adapter.HomePageAdapt;
import com.webcloud.manager.client.ImageCacheManager;
import com.webcloud.model.Atom;
import com.webcloud.model.CategoryAndContent;
import com.webcloud.model.Menu;
import com.webcloud.model.MenuAndAtom;
import com.webcloud.utils.LogUtil;
import com.webcloud.webservice.impl.IUserService;

/**
 * metro风格的首页。
 * 
 * @author  bangyue
 * @version  [版本号, 2014-1-17]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class HomeMetroActivity extends ThemeStyleActivity {
    
    private ViewPager homePager;// 中间页面
    
    private LinearLayout linTab; // 底部分页标识
    
    private IUserService iUserService;
    
    private MenuAndAtom menuAndAtom;
    
    private SharedPreferences settings;
    
    private int[] meunsIds = new int[] {R.id.tvMenu1, R.id.tvMenu2, R.id.tvMenu3, R.id.tvMenu4, R.id.tvMenu5, R.id.tvMenu6};
    
    //private int[] atomIds = new int[] {R.id.atom1, R.id.atom2, R.id.atom3};
    
    private LoadingDialog loadingDialog;
    
    private String cid;// 内容编号
    
    private String menuTitle;//菜单名称
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_metro);
        settings = this.getSharedPreferences("xml_login", 0);
        iUserService = IUserService.getInstant();
        initView();
        initData();
        
    }
    
    private void initView() {
        homePager = (ViewPager)findViewById(R.id.viewpager);
        linTab = (LinearLayout)findViewById(R.id.lintab);
        homePager.setOnPageChangeListener(new MOnPageChangeListener());
    }
    
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 101:
                    homePager.setAdapter(new HomePageAdapt(HomeMetroActivity.this, getPageView(menuAndAtom)));
                    //				stopProgressDialog();
                    break;
                case 102:
                    //				stopProgressDialog();
                    Intent intent = null;
                    CategoryAndContent categoryAndContent = (CategoryAndContent)msg.obj;
                    if (null != categoryAndContent && categoryAndContent.getRetCode() == 0) {
                            if (categoryAndContent.getRenderStyle() == 1) { // 列表风格
                                intent =
                                    new Intent(HomeMetroActivity.this, ListActivity.class);
                            } else if (categoryAndContent.getRenderStyle() == 2) { // 缩略图风格
                                intent =
                                    new Intent(HomeMetroActivity.this, ImgListActivity.class);
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
    
    private List<View> getPageView(MenuAndAtom menuAndAtom) {
        List<View> listView = new ArrayList<View>();
        if (null == menuAndAtom) {
            LogUtil.d("menuAndAtom is null");
            return listView;
        }
        List<Menu> menus = menuAndAtom.getMenus();
        List<Atom> atoms = menuAndAtom.getAtoms();
        if (null != menus && menus.size() > 0) {
            int pageCount = menus.size() % 6 == 0 ? menus.size() / 6 : (menus.size() / 6 + 1);
            
            for (int i = 0; i < pageCount; i++) {
                LinearLayout lin = (LinearLayout)getLayoutInflater().inflate(R.layout.home_metro_page_item, null);
                listView.add(lin);
                for (int j = 0; j < 6; j++) {
                    TextView tvMenu = (TextView)lin.findViewById(meunsIds[j]);
                    try {
                        Menu menu = menus.get(i * 6 + j);
                        tvMenu.setVisibility(View.VISIBLE);
                        tvMenu.setTag(menu);
                        tvMenu.setText(menu.getName());
                        //ImageTool.setImgView(HomeActivity.this, btnMenu, menu.getIconUrl(), false);
                        ImageLoader imgLoader = mgr.imgCacheMgr.getImageLoader();
                        ImageView ivPic = new ImageView(getApplicationContext());
                        ivPic.setTag(tvMenu);
                        //tvMenu.setTag(j);
                        imgLoader.displayImage(menu.getIconUrl(),
                            ivPic,
                            mgr.imgCacheMgr.getOptions(ImageCacheManager.CACHE_MODE_CACHE),
                            new SimpleImageLoadingListener(){

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    TextView tvMenu = (TextView)view.getTag();
                                    Drawable[] d = tvMenu.getCompoundDrawables();
                                    if(d[0] != null){
                                        //left
                                        tvMenu.setCompoundDrawables(new BitmapDrawable(getResources(), loadedImage),null,null,null);
                                    } else {
                                        //top
                                        tvMenu.setCompoundDrawables(null,null,new BitmapDrawable(getResources(), loadedImage),null);
                                    }
                                }
                        });
                        tvMenu.setOnClickListener(new View.OnClickListener() {
                            
                            @Override
                            public void onClick(View v) {
                                Menu menu = (Menu)v.getTag();
                                menuOnClick(menu);
                                Toast.makeText(HomeMetroActivity.this, menu.getMsg(), 500).show();
                            }
                        });
                        
                    } catch (Exception e) {
                        tvMenu.setVisibility(View.INVISIBLE);
                    }
                }
                
                ScrollView layAtom = (ScrollView)lin.findViewById(R.id.layAtom);
                layAtom.removeAllViews();
                LinearLayout lin2 = new LinearLayout(getBaseContext());
                lin2.setOrientation(LinearLayout.VERTICAL);
                lin2.setGravity(Gravity.BOTTOM);
                for (int m = 0; m < atoms.size(); m++) {
                    
                    ImageView btnAtom = new ImageView(getBaseContext());
                    try {
                        btnAtom.setVisibility(View.VISIBLE);
                        btnAtom.setTag(atoms.get(m));
                        btnAtom.setScaleType(ScaleType.CENTER);
                        btnAtom.setPadding(0, 3, 0, 3);
                        /*ImageTool.setImgView(HomeActivity.this, btnAtom, atoms
                        		.get(m).getIconUrl(), true);*/
                        ImageLoader imgLoader = mgr.imgCacheMgr.getImageLoader();
                        imgLoader.displayImage(atoms.get(m).getIconUrl(),
                            btnAtom,
                            mgr.imgCacheMgr.getOptions(ImageCacheManager.CACHE_MODE_CACHE),
                            null);
                        btnAtom.setOnClickListener(new View.OnClickListener() {
                            
                            @Override
                            public void onClick(View v) {
                                Atom atom = (Atom)v.getTag();
                                atomOnClick(atom);
                                Toast.makeText(HomeMetroActivity.this, atom.getName(), 500).show();
                            }
                        });
                        lin2.addView(btnAtom);
                    } catch (Exception e) {
                        btnAtom.setVisibility(View.INVISIBLE);
                    }
                }
                layAtom.addView(lin2);
                
                //底部tab小方块
                ImageView imageView = new ImageView(this);
                imageView.setPadding(5, 0, 5, 0);
                imageView.setImageResource(R.drawable.home_switch);
                linTab.addView(imageView);
            }
            
            //默认显示第一页
            setSelsct(0);
        }
        return listView;
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
    private void menuOnClick(Menu menu) {
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
            Intent intent = new Intent(HomeMetroActivity.this, CaptureActivity.class);
            startActivity(intent);
        }
        if ("邮件".equals(atom.getName())) {
            Intent intent = new Intent(HomeMetroActivity.this, EmailMainActivity.class);
            startActivity(intent);
        }
        if ("天气预报".equals(atom.getName())) {
            Intent intent = new Intent(HomeMetroActivity.this, WeatherActivity.class);
            startActivity(intent);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //		stopProgressDialog();
    }
    

}

package com.webcloud;

import com.webcloud.define.ModelStyle;
import com.webcloud.func.template.HomeGridViewActivity;
import com.webcloud.func.template.HomeMetroActivity;
import com.webcloud.func.template.HomeSlideMenuActivity;
import com.webcloud.utily.BackUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

public class ThemeStyleActivity extends BaseActivity{
	
	public static final int THEME_SETINGS = 0;//主题设置菜单
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(ModelStyle.theme);
		super.onCreate(savedInstanceState);
	}
	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {        
	        if(keyCode==KeyEvent.KEYCODE_BACK){
	        	if (new BackUtil().doubleClickBackExit(this)) {
	                super.onBackPressed();
	            } 
	            return false;
	        }
	        //继续执行父类的其他点击事件
	        return super.onKeyDown(keyCode, event);
	    }

		@Override
		public boolean onCreateOptionsMenu(android.view.Menu menu) {
			menu.add(0,THEME_SETINGS,0,"主题设置").setIcon(android.R.drawable.ic_menu_delete); 
			return true;
		}
	    
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch(item.getItemId()){
	            //主题设置菜单项被选择
	            case THEME_SETINGS:
	                createSelectThemeStyleDialog();
	                break;
	            default:
	            	break;
	        }
	        return true;
	    }
		 
		 public int checked = 0;//默认选择的主题样式
		 
		 /**
		  * 创建选择主题样式对话框
		  */
	     private void createSelectThemeStyleDialog(){
	    	//3种样式
	    	final String[] styles ={"slidemenu","metro","gridview"} ;
	        Dialog dialog = new AlertDialog.Builder(this). 
	                setTitle("请选择主题样式")
	                .setSingleChoiceItems(styles, checked, new DialogInterface.OnClickListener() {  
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) { 
	                        checked = which; 
	                    } 
	                })
	                .setPositiveButton("确认", new DialogInterface.OnClickListener() { 
	                    @Override 
	                    public void onClick(DialogInterface dialog, int which) {
	                    	// 重新刷新加载主题样式
	                    	if(0==checked){//滑动样式
	                    		ModelStyle.theme = R.style.AppSlideTheme;
	                    		ThemeStyleActivity.this.finish();
	                    		startActivity(new Intent(getApplicationContext(), HomeSlideMenuActivity.class));
	                    	}
							if(1==checked){//色块样式
								ModelStyle.theme = R.style.AppMetroTheme;
								ThemeStyleActivity.this.finish();
	                    		startActivity(new Intent(getApplicationContext(), HomeMetroActivity.class));
							}
							if(2==checked){//九宫格样式]
								ModelStyle.theme = R.style.AppGridTheme;
								ThemeStyleActivity.this.finish();
								startActivity(new Intent(getApplicationContext(), HomeGridViewActivity.class));
							}
	                    } 
	                })
	                .create(); 
	        dialog.show();
	     }
	
}

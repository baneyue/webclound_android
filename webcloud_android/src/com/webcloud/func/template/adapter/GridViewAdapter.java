package com.webcloud.func.template.adapter;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.webcloud.R;
import com.webcloud.define.ModelStyle;
import com.webcloud.func.template.HomeGridViewActivity;
import com.webcloud.manager.SystemManager;
import com.webcloud.manager.client.ImageCacheManager;
import com.webcloud.model.Menu;

public class GridViewAdapter extends BaseAdapter {
	private List<Menu> data;
	private HomeGridViewActivity context;
	private LayoutInflater inflater;
	private ImageLoader imgLoader;
	private DisplayImageOptions options;

	public GridViewAdapter(HomeGridViewActivity context, List<Menu> data) {
		inflater = LayoutInflater.from(context);
		this.data = data;
		this.context = context;
		this.imgLoader = SystemManager.getInstance(context).imgCacheMgr.getImageLoader();
		//this.options = SystemManager.getInstance(context).imgCacheMgr.getOptions(ImageCacheManager.CACHE_MODE_CACHE);
		this.options =  new DisplayImageOptions.Builder().cacheInMemory(true)
            .cacheOnDisc(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.NONE)
            .build();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.home_gridview_menu_item, null);
			holder.ivMenu = (ImageView) convertView.findViewById(R.id.ivMenu);
			holder.tvMenu = (TextView) convertView.findViewById(R.id.tvMenu);
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Menu menu = data.get(position);
		try {
		    holder.ivMenu.setVisibility(View.VISIBLE);
		    holder.ivMenu.setTag(menu);
		    ViewGroup.LayoutParams lp = holder.ivMenu.getLayoutParams();
		    lp.width = context.itemWidth-20;
		    lp.height = context.itemWidth-20;
		    Log.d("GridViewAdapter", context.itemWidth+"");
		    holder.ivMenu.setBackgroundResource(ModelStyle.GRIDVIEW_BG_ID[position]);
		    //holder.btnMenu.setText(menu.getName());
		    holder.tvMenu.setText(menu.getName());
            //ImageTool.setImgView(HomeActivity.this, btnMenu, menu.getIconUrl(), false);
            ImageView ivPic = new ImageView(context);
            ivPic.setTag(holder.ivMenu);
            imgLoader.displayImage(menu.getIconUrl(),
                ivPic,
                options,
                new SimpleImageLoadingListener(){

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        ImageView ivMenu = (ImageView)view.getTag();
                        ivMenu.setImageBitmap(loadedImage);
                    }
            });
            holder.ivMenu.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Menu menu = (Menu)v.getTag();
                    context.menuOnClick(menu);
                    Toast.makeText(context, menu.getMsg(), 500).show();
                }
            });
        } catch (Exception e) {
            holder.ivMenu.setVisibility(View.INVISIBLE);
        }
		return convertView;
	}

	private class ViewHolder {
	    ImageView ivMenu;
		TextView tvMenu;
	}
}
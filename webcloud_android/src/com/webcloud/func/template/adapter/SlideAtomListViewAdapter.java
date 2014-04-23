package com.webcloud.func.template.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webcloud.R;
import com.webcloud.func.template.HomeSlideMenuActivity;
import com.webcloud.manager.SystemManager;
import com.webcloud.manager.client.ImageCacheManager;
import com.webcloud.model.Atom;
import com.webcloud.model.Menu;

/**
 * 抽屉应用菜单列表适配器。
 * 
 * @author  bangyue
 * @version  [版本号, 2014-1-17]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class SlideAtomListViewAdapter extends BaseAdapter {
	private List<Atom> data;
	private HomeSlideMenuActivity context;
	private LayoutInflater inflater;
	private ImageLoader imgLoader;
	private DisplayImageOptions options;

	public SlideAtomListViewAdapter(HomeSlideMenuActivity context, List<Atom> data) {
		inflater = LayoutInflater.from(context);
		this.data = data;
		this.context = context;
		this.imgLoader = SystemManager.getInstance(context).imgCacheMgr.getImageLoader();
		this.options = SystemManager.getInstance(context).imgCacheMgr.getOptions(ImageCacheManager.CACHE_MODE_CACHE);
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
			convertView = inflater.inflate(R.layout.home_slidemenu_atom_item, null);
			holder.ivAtom = (ImageView) convertView.findViewById(R.id.ivAtom);
			holder.tvAtom = (TextView) convertView.findViewById(R.id.tvAtom);
			holder.layAtom = convertView.findViewById(R.id.layAtom);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Atom atom = data.get(position);
		try {
		    holder.ivAtom.setTag(atom);
		    holder.tvAtom.setText(atom.getName());
            //ImageTool.setImgView(HomeActivity.this, btnMenu, menu.getIconUrl(), false);
            //ImageView ivPic = new ImageView(context);
            //ivPic.setTag(holder.ivAtom);
            imgLoader.displayImage(atom.getIconUrl(),
                holder.ivAtom,
                options,null
                /*new SimpleImageLoadingListener(){

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        ImageView ivAtom = (ImageView)view.getTag();
                        ivAtom.setBackgroundDrawable(new BitmapDrawable(context.getResources(), loadedImage));
                    }
            }*/);
            holder.layAtom.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Atom atom = (Atom)(((ViewGroup)v).getChildAt(0).getTag());;
                    context.atomOnClick(atom);
                    Toast.makeText(context, atom.getComments(), 500).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
		return convertView;
	}

	private static class ViewHolder {
	    ImageView ivAtom;
		TextView tvAtom;
		View layAtom;
	}
}
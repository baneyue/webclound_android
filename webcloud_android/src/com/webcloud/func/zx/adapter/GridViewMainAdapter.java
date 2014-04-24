package com.webcloud.func.zx.adapter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webcloud.R;

/**
 * 装修首页gridView适配器。
 * 
 * @author  bangyue
 * @version  [版本号, 2014-4-24]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class GridViewMainAdapter extends BaseAdapter{
    
    private Activity mContext;
    
    private List<Map<String,Object>> datas;
    
    public GridViewMainAdapter(Activity context,List<Map<String,Object>> datas){
        mContext = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if(datas!= null)
            return datas.size();
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        if(datas != null&& datas.size() > arg0){
            return datas.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView != null){
            vh = (ViewHolder)convertView.getTag();
        } else {
            vh = new ViewHolder();
            convertView = mContext.getLayoutInflater().inflate(R.layout.zx_main_gridview_item, null);
            vh.ivItemImg = (ImageView)convertView.findViewById(R.id.ivItemImg);
            vh.tvItemTitle = (TextView)convertView.findViewById(R.id.tvItemTitle);
            convertView.setTag(vh);
        }
        //封装数据
        Map<String,Object> data = datas.get(position);
        vh.ivItemImg.setImageResource((Integer)data.get("img"));
        vh.ivItemImg.setTag((Integer)data.get("img"));
        vh.tvItemTitle.setText((String)data.get("title"));
        return convertView;
    }
    
    static class ViewHolder{
        TextView tvItemTitle;
        ImageView ivItemImg;
    }
}

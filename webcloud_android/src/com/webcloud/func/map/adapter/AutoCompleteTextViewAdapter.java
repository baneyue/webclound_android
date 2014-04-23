package com.webcloud.func.map.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.webcloud.R;
import com.webcloud.model.PoiInfo;

/**
 * AutoCompleteTextView 下拉列表适配器
 * 
 * @author  zoubangyue
 * @version  [版本号, 2012-4-8]
 */
public class AutoCompleteTextViewAdapter extends BaseAdapter implements Filterable {
    public boolean isHistory() {
        return isHistory;
    }

    public static final String TAG = "AutoCompleteTextViewAdapter";
    
    List<PoiInfo> poiInfos;
    
    AutoShowFilter autoFilter;
    
    Activity context;
    
    LayoutInflater mInflater;
    
    boolean isHistory;
    
    public AutoCompleteTextViewAdapter(Activity context, List<PoiInfo> poiInfos) {
        this.context = context;
        this.poiInfos = poiInfos;
        this.mInflater = context.getLayoutInflater();
    }
    
    @Override
    public int getCount() {
        if (poiInfos != null)
            return poiInfos.size();
        return 0;
    }
    
    @Override
    public PoiInfo getItem(int position) {
        if (poiInfos != null) {
            return poiInfos.get(position);
        }
        return null;
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.vehicle_poi_search_item, null);
            vh = new ViewHolder();
            vh.ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
            vh.PoiName = (TextView)convertView.findViewById(R.id.poiName);
            vh.poiAddress = (TextView)convertView.findViewById(R.id.poiAddress);
            convertView.setTag(vh);
        } else{
            vh = (ViewHolder)convertView.getTag();
        }
         
        PoiInfo poi = poiInfos.get(position);
        vh.PoiName.setText(poi.getName());
        vh.poiAddress.setText(poi.getAddress());
        if(isHistory){
            vh.ivIcon.setImageResource(R.drawable.search_his_image);
        } else {
            vh.ivIcon.setImageResource(R.drawable.search_image);
        }
        return convertView;
    }
    
    static class ViewHolder{
        TextView PoiName;
        TextView poiAddress;
        ImageView ivIcon;
    }
    
    @Override
    public Filter getFilter() {
        if (autoFilter == null) {
            autoFilter = new AutoShowFilter();
        }
        return autoFilter;
    }
    
    /**
     * 不做筛选，只做下拉显示，把所有数据显示出来。 
     * 
     */
    class AutoShowFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence input) {
            //持有过滤操作完成之后的数据。该数据包括过滤操作之后的数据的值以及数量。 count:数量 values包含过滤操作之后的数据的值
            FilterResults results = new FilterResults();
            
            results.values = poiInfos;
            results.count = poiInfos.size();
            
            return results;
        }
        
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // 重新将与适配器相关联的List重赋值一下
            //poiInfos = (List<PoiInfo>)results.values;
            if (poiInfos.size() > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
    
    public void setHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }
}

package com.webcloud.func.template.fragment;

import java.util.ArrayList;
import java.util.List;

import com.webcloud.R;
import com.webcloud.func.template.adapter.ImgListAdapt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ListImgFragment extends Fragment implements OnScrollListener{
    private ListView listView;
    private ImgListAdapt imAdapt;
    private int visibleLastIndex; 
    private int dataSize;
    private List listData;
    private LinearLayout title_back;
    private TextView tv_title;
    private String title;
    private View view;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_imglist, null);
        //获取传递而来的数据，没传就为null
        Bundle bundle = getArguments();
        initView();
        initData(bundle);
        init();
        return view;
    }
    
    public void initData(Bundle bundle) {
        if(bundle == null){
            return;
        }
        List categories = bundle.getIntegerArrayList("categories");
        List contents = bundle.getIntegerArrayList("contents");
        title = bundle.getString("Title");
        if(null!=categories){
            listData = categories;
        }else if(null!=contents){
            listData = contents;
        }
        if(null!=listData){
            dataSize = listData.size() % 3 == 0 ? listData.size() / 3
                    : listData.size() / 3 + 1;
        }
        
    }

    private void initView() {
        listView = (ListView) view.findViewById(R.id.imglist);
        listView.setOnScrollListener(this);
        title_back = (LinearLayout) view.findViewById(R.id.title_back);
        //title_back.setOnClickListener(this);
        tv_title = (TextView) view.findViewById(R.id.title);
    }

    private void init() {
        if(null!=listData){
            imAdapt = new ImgListAdapt(this.getActivity(), getListClip(listData,
                    0, 30),title);
            listView.setAdapter(imAdapt);
            tv_title.setText(title);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
        if (totalItemCount == dataSize) {
            // Toast.makeText(this, "已经为最后一条数据", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        int itemsLastIndex = imAdapt.getCount() - 1; 
        int lastIndex = itemsLastIndex;
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && visibleLastIndex == lastIndex) {
            loadMoreData();
            imAdapt.notifyDataSetChanged();
        }
    }

    /**
     *  加载更多
     */
    private void loadMoreData() {
        int count = imAdapt.getItemCount();
        int end = count + 30 <= listData.size() ? count + 30 : listData
                .size();
        imAdapt.addNewsItem(getListClip(listData, count, end));

    }

    public static List getListClip(List list,int start,int end){
        
        Log.i("Tag2", "start: "+start+"end: "+end);
        if(null==list||start>list.size()-1){
            return null;
        }
        List list2 = new ArrayList();
        for (int i = start; i < (end<=list.size()?end:list.size()); i++) {
            list2.add(list.get(i));
        }
        return list2;
    }

}

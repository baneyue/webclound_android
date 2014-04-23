package com.webcloud.func.template.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.webcloud.R;
import com.webcloud.func.template.adapter.ListAdapt;

public class ListFragment extends Fragment{
    private ListView listView;
    private ListAdapt listAdapt;
    private List listData;
    private LinearLayout title_back;
    private TextView tv_title;
    private String title;
    private View view;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_list, null);
        //获取传递而来的数据，没传就为null
        Bundle bundle = getArguments();
        initView();
        initData(bundle);
        init();
        return view;
    }
    private void initView(){
        listView = (ListView)view.findViewById(R.id.listview);
        title_back = (LinearLayout) view.findViewById(R.id.title_back);
        tv_title = (TextView) view.findViewById(R.id.title);
    }
    
    
    private void initData(Bundle bundle){
        List categories = bundle.getIntegerArrayList("categories");
        List contents = bundle.getIntegerArrayList("contents");
        title = bundle.getString("Title");
        if(null!=categories){
            listData = categories;
        }else if(null!=contents){
            listData = contents;
        }
    }

    private void init() {
        if(null!=listData){
            listAdapt = new ListAdapt(this.getActivity(),listData,title);
            listView.setAdapter(listAdapt);
            tv_title.setText(title);
        }
    }
}

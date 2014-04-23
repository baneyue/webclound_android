package com.webcloud;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class BaseFragment extends Fragment {
    protected FragmentActivity context;
    protected WebCloudApplication application;
    protected View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        application = (WebCloudApplication)context.getApplication();
    }
}

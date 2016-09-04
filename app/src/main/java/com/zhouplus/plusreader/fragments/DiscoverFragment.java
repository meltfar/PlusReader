package com.zhouplus.plusreader.fragments;

import android.widget.TextView;

/**
 * Created by zhouplus
 * Time at 2016/9/4
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class DiscoverFragment extends BaseFragment {
    @Override
    protected void initViews() {
        setTitle("书城");
        TextView tv = new TextView(mainActivity);
        tv.setText("Discover");
        frame_featureFragment.addView(tv);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }
}

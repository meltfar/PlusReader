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
public class MineFragment extends BaseFragment {
    @Override
    protected void initViews() {
        setTitle("我的");
        TextView tv = new TextView(mainActivity);
        tv.setText("Mine");
        frame_featureFragment.addView(tv);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }
}

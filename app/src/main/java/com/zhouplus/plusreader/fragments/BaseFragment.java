package com.zhouplus.plusreader.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.activities.MainActivity;

/**
 * Created by zhouplus
 * Time at 2016/8/22
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public abstract class BaseFragment extends Fragment {
    protected MainActivity mainActivity;
    protected TextView tv_title;
    protected ImageButton ib_menu;
    protected AnimatedCircleLoadingView animatedLoading;
    public FrameLayout frame_featureFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        ib_menu = (ImageButton) view.findViewById(R.id.ib_menu);
        frame_featureFragment = (FrameLayout) view.findViewById(R.id.frame_featureFragment);
        animatedLoading = (AnimatedCircleLoadingView) view.findViewById(R.id.circle_loading_view);
        return view;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (tv_title != null)
            tv_title.setText(title);
    }

    /**
     * 设置菜单按钮的单击事件
     *
     * @param listener 监听事件
     */
    public void setMenuListener(View.OnClickListener listener) {
        ib_menu.setOnClickListener(listener);
    }

    /**
     * 显示加载中的界面
     *
     * @param bDeterminate 是否有确定的进度，如果有，需要使用{@link #setLoadingPercent(int)}
     */
    public void startLoading(boolean bDeterminate) {
        if (bDeterminate) {
            animatedLoading.startDeterminate();
        } else {
            animatedLoading.startIndeterminate();
        }
        animatedLoading.setTag(bDeterminate);
    }

    /**
     * 设置加载进度的百分比，如果等于了100，就表示成功
     *
     * @param percent 进度
     */
    public void setLoadingPercent(int percent) {
        boolean bDeterminate = (boolean) animatedLoading.getTag();
        if (bDeterminate) {
            animatedLoading.setPercent(percent);
        }
    }

    /**
     * 设置加载完成的事件
     *
     * @param bSuccess 是否成功完成
     */
    public void setLoadingOver(boolean bSuccess) {
        boolean bDeterminate = (boolean) animatedLoading.getTag();
        if (bDeterminate && bSuccess) {
            Log.e("AnimatedLoadingView", "设置进度为100即标识成功，不需要另行设置");
            animatedLoading.setPercent(100);
            return;
        }
        if (!bSuccess) {
            animatedLoading.stopFailure();
        } else {
            animatedLoading.stopOk();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initData();
        initEvent();
    }

    protected abstract void initViews();

    protected abstract void initData();

    protected abstract void initEvent();
}

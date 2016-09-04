package com.zhouplus.plusreader.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.zhouplus.plusreader.R;

/**
 * Created by zhouplus
 * Time at 2016/9/2
 * Project name PlusReader
 * Description : 维持着GridView的Fragment，单纯用于显示GridView
 * Author's email :
 * Version 1.0
 */
public class ShelfGridFragment extends android.support.v4.app.Fragment {
    public GridView gv_shelf;
    View baseView;
    ListAdapter adapter;
    private Handler shelfGridHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseView = View.inflate(getActivity(), R.layout.item_gridview_shelf, null);
        gv_shelf = (GridView) baseView.findViewById(R.id.gv_shelf);
        shelfGridHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                initGridView();
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new Thread() {
            @Override
            public void run() {
                //依靠这个逻辑，使得GridView在获得适配器之后，再给自己设置，而不会引发nullpointer
                while (adapter == null)
                    SystemClock.sleep(100);
                //不知道是不是因为这里的运行先后问题，导致了崩溃，所以换成发信息方法来试试
                shelfGridHandler.sendEmptyMessage(0);
            }
        }.start();
        return baseView;
    }

    private void initGridView() {
        gv_shelf.setAdapter(adapter);
        //// TODO: 2016/9/4 打开相应的小说 ，这就是阅读器的核心部分了
        gv_shelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_bookName, tv_readPercent;
                tv_bookName = (TextView) view.findViewById(R.id.tv_bookName);
                tv_readPercent = (TextView) view.findViewById(R.id.tv_readPercent);
                System.out.println("item click on" + " " + position);
                System.out.println(tv_bookName.getText() + "    " + tv_readPercent.getText());
            }
        });

        gv_shelf.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //// TODO: 2016/9/4 弹出菜单，允许删除或者改名
                return false;
            }
        });
    }


    /**
     * 设置GridView要使用的适配器
     * GridView会等待一段时间，直到本函数被调用，设置了适配器才会显示出来
     *
     * @param la 适配器
     */
    public void setGridAdapter(ListAdapter la) {
        adapter = la;
    }
}

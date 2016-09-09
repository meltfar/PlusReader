package com.zhouplus.plusreader.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.activities.ReadingActivity;
import com.zhouplus.plusreader.databases.DatabaseManager;
import com.zhouplus.plusreader.domains.PlusBook;

import java.io.File;

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
    ShelfFragment.MyAdapter adapter;
    public Handler shelfGridHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    initGridView();
                    break;
                case 2:
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseView = View.inflate(getActivity(), R.layout.item_gridview_shelf, null);
        gv_shelf = (GridView) baseView.findViewById(R.id.gv_shelf);
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
                shelfGridHandler.sendEmptyMessage(1);
            }
        }.start();
        return baseView;
    }

    public void refreshShelf() {
        shelfGridHandler.sendEmptyMessage(2);
    }

    private void initGridView() {
        gv_shelf.setAdapter(adapter);
        gv_shelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView tv_bookName, tv_readPercent;
                LinearLayout ll_book;
//                tv_bookName = (TextView) view.findViewById(R.id.tv_bookName);
//                tv_readPercent = (TextView) view.findViewById(R.id.tv_readPercent);
                ll_book = (LinearLayout) view.findViewById(R.id.ll_bookView);

                //启动小说阅读页面
                Intent intent = new Intent(getActivity(), ReadingActivity.class);
                intent.putExtra("BookInfo", (PlusBook) ll_book.getTag());
//                startActivity(intent);
                startActivityForResult(intent, 555);
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
    public void setGridAdapter(BaseAdapter la) {
        adapter = (ShelfFragment.MyAdapter) la;
    }
}

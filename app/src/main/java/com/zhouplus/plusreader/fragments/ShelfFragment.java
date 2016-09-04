package com.zhouplus.plusreader.fragments;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.databases.DatabaseManager;
import com.zhouplus.plusreader.domains.PlusBook;

import java.util.ArrayList;
import java.util.List;

public class ShelfFragment extends BaseFragment {

    private List<PlusBook> books = new ArrayList<>();
    public MyAdapter gv_adapter;
    private ShelfGridFragment gridFragment;
    private boolean isRefreshingShelf;
    DatabaseManager mDataMgr;

    @Override
    protected void initViews() {
        setTitle("书架");
        gridFragment = new ShelfGridFragment();
        FragmentManager ma = getChildFragmentManager();
        FragmentTransaction transaction = ma.beginTransaction();
        transaction.replace(R.id.frame_featureFragment, gridFragment);
        transaction.commit();
    }

    @Override
    protected void initData() {
        //因为需要的时间不长，所以先不用动画
        mDataMgr = new DatabaseManager(mainActivity, true);
        mDataMgr.setOnDataTransferedListener(new DatabaseManager.OnDataTransferedListener() {
            @Override
            public void OnDataTransfered(List<PlusBook> bookList) {
                books = bookList;
                gv_adapter.notifyDataSetChanged();
                System.out.println(books.size());
            }
        });

        //第一次设置好适配器和getbook的相应事件，以后直接调用refreshShelf即可

        gv_adapter = new MyAdapter();
        gridFragment.setGridAdapter(gv_adapter);

//        mDataMgr.getBooks();
        refreshShelf();
    }

    /**
     * 刷新书架，重新获取数据库中的书
     */
    public void refreshShelf() {
        if (isRefreshingShelf) {
            return;
        }
        isRefreshingShelf = true;
        mDataMgr.getBooks();
        isRefreshingShelf = false;
    }

    @Override
    protected void initEvent() {
        setMenuListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.menuDrawer.openMenu();
            }
        });
    }

    // TODO 该有的逻辑
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return books.size();
        }

        @Override
        public Object getItem(int position) {
            return books.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv_bookName, tv_readPercent;
            LinearLayout ll_bookView;

            if (convertView == null) {
                convertView = View.inflate(mainActivity, R.layout.item_grid_book, null);
            }
            // TODO: 2016/9/4 本应该设置书本View的高度为1/3的GridView大小,
            // TODO: 但是后来发现不设置反而挺好看，就先不设置了
            tv_bookName = (TextView) convertView.findViewById(R.id.tv_bookName);
            tv_readPercent = (TextView) convertView.findViewById(R.id.tv_readPercent);
            ll_bookView = (LinearLayout) convertView.findViewById(R.id.ll_bookView);
            tv_bookName.setText(books.get(position).name);
            //// TODO: 2016/9/4 这个数值应该是小说当前阅读进度
            String str = "12.2%";
            tv_readPercent.setText(str);

            return convertView;
        }
    }
}

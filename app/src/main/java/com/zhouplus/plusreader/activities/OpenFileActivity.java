package com.zhouplus.plusreader.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.databases.DatabaseManager;
import com.zhouplus.plusreader.domains.FileExplorer;
import com.zhouplus.plusreader.domains.PlusFile;
import com.zhouplus.plusreader.utils.ViewTools;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

public class OpenFileActivity extends AppCompatActivity {

    RecyclerView rv_file;
    List<PlusFile> files;
    FileExplorer explorer;
    FileAdapter mAdapter;
    Deque<String> mStackPaths;
    String mCurrentPath;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        initData();
        initEvents();
    }

    private void initViews() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        rv_file = (RecyclerView) findViewById(R.id.rv_file);
        rv_file.setLayoutManager(new LinearLayoutManager(this));
        rv_file.setItemAnimator(new DefaultItemAnimator());
//        rv_file.addItemDecoration(new SpaceItemDecoration(ViewTools.dip2px(20,
//                getResources().getDisplayMetrics())));
    }

    private void initData() {
        mStackPaths = new ArrayDeque<>();
        explorer = new FileExplorer(this, null);
        //// TODO: 2016/9/9 把获取文件夹目录的方法设置一个监听器，然后发信息报告完成事件
        files = explorer.getDefaultFiles();
    }

    private void initEvents() {
        mAdapter = new FileAdapter();
        rv_file.setAdapter(mAdapter);
        mCurrentPath = explorer.getLastHandled();
        toolbar.setTitle(mCurrentPath);
    }


    class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileAdapterHolder> {
        @Override
        public FileAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FileAdapterHolder(View.inflate(OpenFileActivity.this,
                    R.layout.item_recycleview_file, null));
        }

        @Override
        public void onBindViewHolder(FileAdapterHolder holder, int position) {
            //对holder中的各种控件做各种事情
            //比如onclick啥的
            PlusFile pf = files.get(position);
            holder.tv_file_lastModified.setText(decodeTime(pf.lastModified));
            String permission = String.valueOf(pf.permisson[0]) +
                    String.valueOf(pf.permisson[1]) + String.valueOf(pf.permisson[2]);
            holder.tv_file_permissions.setText(permission);
            if (pf.isDictionary) {
                holder.iv_recycleview_icon_text.setVisibility(View.INVISIBLE);
                holder.iv_recycleview_icon_folder.setVisibility(View.VISIBLE);
                holder.tv_file_name.setText(pf.name);
            } else {
                holder.iv_recycleview_icon_text.setVisibility(View.VISIBLE);
                holder.iv_recycleview_icon_folder.setVisibility(View.INVISIBLE);
                holder.tv_file_name.setText(getName(pf.absolutePath));
            }
        }

        @Override
        public int getItemCount() {
            return files == null ? 0 : files.size();
        }

        class FileAdapterHolder extends RecyclerView.ViewHolder {

            ImageView iv_recycleview_icon_folder, iv_recycleview_icon_text;
            TextView tv_file_name, tv_file_lastModified, tv_file_permissions;
            RelativeLayout rl_recycleview_base;

            public FileAdapterHolder(View itemView) {
                super(itemView);
                //在这里绑定各种控件(获取findViewById)
                iv_recycleview_icon_folder = (ImageView) itemView.
                        findViewById(R.id.iv_recycleview_icon_folder);
                iv_recycleview_icon_text = (ImageView) itemView.
                        findViewById(R.id.iv_recycleview_icon_text);
                tv_file_name = (TextView) itemView.findViewById(R.id.tv_file_name);
                tv_file_lastModified = (TextView) itemView.findViewById(R.id.tv_file_lastModified);
                tv_file_permissions = (TextView) itemView.findViewById(R.id.tv_file_permissions);
                rl_recycleview_base = (RelativeLayout) itemView.findViewById(R.id.rl_recycleview_base);
                rl_recycleview_base.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlusFile pf = files.get(getAdapterPosition());
                        System.out.println(pf);
                        if (pf.isDictionary) {
                            moveToNext(pf);
                        } else {
                            addBookToShelf(pf);
                        }
                    }
                });
            }
        }
    }

    /**
     * 把书本添加进去
     *
     * @param pf 书本文件
     */
    private void addBookToShelf(PlusFile pf) {
        DatabaseManager dm = new DatabaseManager(this, false);
        if (dm.TestBook(pf.name, pf.absolutePath)) {
            Toast.makeText(OpenFileActivity.this, "此书已经添加到书架啦", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dm.addBook(pf.name, null, (int) pf.length, pf.absolutePath)) {
            Toast.makeText(OpenFileActivity.this, pf.name + " 添加失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        setResult(1);
        finish();
    }

    /**
     * 转移到下一页
     *
     * @param pf 点击的文件
     */
    private void moveToNext(PlusFile pf) {
        mStackPaths.push(mCurrentPath);
        files = explorer.walkDictionary(pf.absolutePath);
        mCurrentPath = explorer.getLastHandled();
        toolbar.setTitle(mCurrentPath);
//                            System.out.println("new get current : " + mCurrentPath);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 回到上一页，也就是上一个浏览的文件夹
     *
     * @return 如果成功返回，返回True，如果没有上一页了，返回False
     */
    private boolean backToPrevious() {
        try {
            String path = mStackPaths.pop();
//            System.out.println("poped : " + path);
            files = explorer.walkDictionary(path);
            mCurrentPath = explorer.getLastHandled();
            toolbar.setTitle(mCurrentPath);
            mAdapter.notifyDataSetChanged();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * 当返回键按下时，返回上一页
     *
     * @param keyCode 1
     * @param event   1
     * @return 1
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return backToPrevious() || super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    //    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
//        private int space;
//
//        public SpaceItemDecoration(int space) {
//            this.space = space;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            if (parent.getChildLayoutPosition(view) != 0)
//                outRect.top = space;
//        }
//    }


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public String decodeTime(long time) {
        return sdf.format(new Date(time));
    }

    public String getName(String path) {
        int point = path.lastIndexOf('/');
        return path.substring(point + 1, path.length());
    }
}

package com.zhouplus.plusreader.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.databases.DatabaseManager;
import com.zhouplus.plusreader.fragments.DiscoverFragment;
import com.zhouplus.plusreader.fragments.FeatureFragment;
import com.zhouplus.plusreader.fragments.MineFragment;
import com.zhouplus.plusreader.fragments.ShelfFragment;
import com.zhouplus.plusreader.utils.PlusConstants;
import com.zhouplus.plusreader.views.TabItem;

import net.qiujuer.genius.widget.GeniusButton;
import net.simonvt.menudrawer.MenuDrawer;

/**
 * Created by zhouplus
 * Time at 2016/8/22
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class MainActivity extends AppCompatActivity {
    public MenuDrawer menuDrawer;
    FrameLayout fl_main;
    RadioGroup rg_tab;
    TabItem tab_shelf, tab_bookshop, tab_feature, tab_mine;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 为主界面设置菜单
        menuDrawer = MenuDrawer.attach(this);
        menuDrawer.setContentView(R.layout.activity_main);
        menuDrawer.setMenuView(R.layout.activity_menu);
        //设置不可以滑动出菜单
        menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);


        // 隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        manager = getSupportFragmentManager();

        initViews();
        initData();
        initEvents();
    }

    private void initData() {
        manager.beginTransaction().replace(R.id.fl_main, new ShelfFragment(), PlusConstants.SHELF_TAG).commit();
    }

    private void initEvents() {
        // 按下Tab按钮，切换各个页面
        rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    default:
                    case R.id.tab_shelf:
                        manager.beginTransaction().replace(R.id.fl_main, new ShelfFragment(), PlusConstants.SHELF_TAG).commit();
                        break;
                    case R.id.tab_bookshop:
                        manager.beginTransaction().replace(R.id.fl_main, new DiscoverFragment(), PlusConstants.DISCOVER_TAG).commit();
                        break;
                    case R.id.tab_feature:
                        manager.beginTransaction().replace(R.id.fl_main, new FeatureFragment(), PlusConstants.FEATURE_TAG).commit();
                        break;
                    case R.id.tab_mine:
                        manager.beginTransaction().replace(R.id.fl_main, new MineFragment(), PlusConstants.MINE_TAG).commit();
                        break;
                }
            }
        });
    }

    private void initViews() {
        fl_main = (FrameLayout) findViewById(R.id.fl_main);

        tab_shelf = (TabItem) findViewById(R.id.tab_shelf);
        tab_bookshop = (TabItem) findViewById(R.id.tab_bookshop);
        tab_feature = (TabItem) findViewById(R.id.tab_feature);
        tab_mine = (TabItem) findViewById(R.id.tab_mine);
        rg_tab = (RadioGroup) findViewById(R.id.rg_mainTab);

        tab_shelf.setChecked(true);

        GeniusButton test_btn_addBook = (GeniusButton) menuDrawer.getMenuView().findViewById(R.id.test_btn_addBook);
        test_btn_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager dm = new DatabaseManager(MainActivity.this, false);
                dm.addBook("燃烧的岛群", "a book 1", 1000, Environment.getExternalStorageDirectory().getPath()
                        + "/rsddq.txt");
                dm.addBook("西西呵呵哈", "a book 2", 3400, Environment.getExternalStorageDirectory().getPath()
                        + "/xxhhh.txt");
                Toast.makeText(MainActivity.this, "add the book to database", Toast.LENGTH_SHORT).show();
            }
        });

        GeniusButton test_btn_delBook = (GeniusButton) menuDrawer.getMenuView().findViewById(R.id.test_btn_delBook);
        test_btn_delBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager dm = new DatabaseManager(MainActivity.this, false);
                dm.removeBook("燃烧的岛群", Environment.getExternalStorageDirectory().getPath()
                        + "/rsddq.txt");
                Toast.makeText(MainActivity.this, "Delete the book from database", Toast.LENGTH_SHORT).show();
            }
        });


    }
}

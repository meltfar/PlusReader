package com.zhouplus.plusreader.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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

import java.io.File;

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
                String path = Environment.getExternalStorageDirectory().getPath() + "/novel/test.txt";
                long length = new File(path).length();
                dm.addBook("test", null, (int) length, path);
                Toast.makeText(MainActivity.this, "add the book to database", Toast.LENGTH_SHORT).show();
                ShelfFragment sf = (ShelfFragment) getSupportFragmentManager().findFragmentByTag(PlusConstants.SHELF_TAG);
                sf.refreshShelf();
            }
        });

        GeniusButton test_btn_delBook = (GeniusButton) menuDrawer.getMenuView().findViewById(R.id.test_btn_delBook);
        test_btn_delBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager dm = new DatabaseManager(MainActivity.this, false);
                String path = Environment.getExternalStorageDirectory().getPath() + "/novel/test.txt";
                dm.removeBook("test", path);
                Toast.makeText(MainActivity.this, "Delete the book from database", Toast.LENGTH_SHORT).show();
                ShelfFragment sf = (ShelfFragment) getSupportFragmentManager().findFragmentByTag(PlusConstants.SHELF_TAG);
                sf.refreshShelf();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShelfFragment sf = (ShelfFragment) getSupportFragmentManager().findFragmentByTag(PlusConstants.SHELF_TAG);
        sf.refreshShelf();
    }

    /////////////////////下方的代码就是为了在按下返回按钮的时候,后台执行
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }
}

package com.zhouplus.plusreader.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.zhouplus.plusreader.R;
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

    private final int REQUEST_CODE_FILE_ACTIVITY = 555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 为主界面设置菜单
        menuDrawer = MenuDrawer.attach(this);
        menuDrawer.setContentView(R.layout.activity_main);
        menuDrawer.setMenuView(R.layout.activity_menu);
        //设置不可以滑动出菜单
        menuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);

        //如果系统比较高，可以玩一个沉浸式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.argb(0xff, 0x3F, 0x51, 0xB5));
        }
        //// TODO: 2016/9/8 使用SystemBar 那个插件来完成低版本系统的状态栏


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
//                DatabaseManager dm = new DatabaseManager(MainActivity.this, false);
//                //// TODO: 2016/9/7
//                String path = Environment.getExternalStorageDirectory().getPath() + "/Download/girlfriend.txt";
//                long length = new File(path).length();
//                System.out.println("adding : " + path);
//                if (dm.addBook("girlfriend", null, (int) length, path))
//                    Toast.makeText(MainActivity.this, "add the book to database", Toast.LENGTH_SHORT).show();
//                refreshShelf();
                menuDrawer.closeMenu();
                menuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
                    @Override
                    public void onDrawerStateChange(int oldState, int newState) {
                        menuDrawer.setOnDrawerStateChangeListener(null);
                        startActivityForResult(new Intent(MainActivity.this,
                                OpenFileActivity.class), REQUEST_CODE_FILE_ACTIVITY);
                    }

                    @Override
                    public void onDrawerSlide(float openRatio, int offsetPixels) {

                    }
                });
            }
        });
    }

    /**
     * 更新书架
     */
    private void refreshShelf() {
        ShelfFragment sf = (ShelfFragment) getSupportFragmentManager().findFragmentByTag(PlusConstants.SHELF_TAG);
        if (sf != null)
            sf.refreshShelf();
    }

    /**
     * 设置这个用于在阅读小说返回时更新书架
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshShelf();
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

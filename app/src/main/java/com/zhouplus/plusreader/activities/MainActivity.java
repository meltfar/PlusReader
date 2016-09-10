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
 * <p/>
 * 一、仍然有大量的工程没有做呢
 * 1、首先是大批量的数据库的操作，当添加的小说很多时，可能管理上会有问题
 * 所以这里需要一个长按的菜单来管理书架
 * 2、然后是阅读器设置的保存按钮，这个还没有添加，不能总指望着返回按键啊
 * 3、小说的章节界面已经完成，章节解析、获取等等的事情都已经处理好了，接下来就是怎么把章节信息保存到文件中
 * 这个我想用 对象序列化保存，要不然就是json
 * 4、（搜索功能暂时不想要）
 * 5、~~~~~~~然后是小说阅读界面的滑动问题，是自制一个滑动效果，还是怎么样利用一下Viewpager
 * 是个需要抉择的事情
 * 6、Splash页面，当发现更新以后，应该可以下载安装了，我这个还没有弄。。。
 * 7、ShelfFragment那个页面的有关炫酷进度控件的东西该删除了，暂时用不上
 * <p/>
 * 二、还有一些小细节
 * 1、比如每一次打开小说后，它在书架上的位置，是不是应该提前啊
 * 这个还没想好该怎么实现
 * 2、很多页面的信息系统还没有建立，导致比如说文件浏览页面的数据都是等获取好了才去加载，这文件一多估计就要炸
 * 3、问题太多了，等想好了再写吧
 * <p/>
 * 三、等等。。。我还有三个页面没做呢。。。只写了个书架就累得要吐血
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

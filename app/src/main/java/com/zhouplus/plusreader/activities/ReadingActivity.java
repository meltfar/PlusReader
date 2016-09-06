package com.zhouplus.plusreader.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.databases.DatabaseManager;
import com.zhouplus.plusreader.domains.NovelFactory;
import com.zhouplus.plusreader.domains.PlusBook;
import com.zhouplus.plusreader.utils.PreferenceManager;
import com.zhouplus.plusreader.views.ReadingView;

import java.io.File;
import java.text.DecimalFormat;

public class ReadingActivity extends Activity {

    TextView tv_readingTitle, tv_readingTime, tv_readingPercent;
    ReadingView rv_main;
    LinearLayout ll_readingRoot;
    PlusBook relevantBook;

    int screenWidth, screenHeight;

    private final String TEXT_SIZE = "ReadingTextSize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //写在setContentView前面，用于设置全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reading);

        ActionBar bar = getActionBar();
        if (bar != null) bar.hide();

        initViews();
        initData();
        initEvents();
    }

    private void initEvents() {
        //设置一个全局的布局监听器，用于获取可阅读区域的大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rv_main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        rv_main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        //最后一步，设置View给他自己的大小，同时初始化工厂并加载文件
                        // 如果出错了，就会返回false
                        rv_main.setTextSize(PreferenceManager.getPreferenceInt(
                                ReadingActivity.this, TEXT_SIZE, 30));
                        if (!rv_main.setLayoutSize(rv_main.getWidth(), rv_main.getHeight())) {
                            Toast.makeText(ReadingActivity.this, "出现了错误！\n当前小说无法读取！",
                                    Toast.LENGTH_LONG).show();
                            setResult(2);
                            finish();
                        }
                    }
                }
            });
        }
    }

    private void initData() {
        relevantBook = (PlusBook) getIntent().getSerializableExtra("BookInfo");
        rv_main.setAssociatedActivity(this);
        rv_main.setBook(relevantBook);
        refreshViews(relevantBook);
    }

    private void initViews() {
        tv_readingTitle = (TextView) findViewById(R.id.tv_readingTitle);
        tv_readingTime = (TextView) findViewById(R.id.tv_readingTime);
        tv_readingPercent = (TextView) findViewById(R.id.tv_readingPercent);
        rv_main = (ReadingView) findViewById(R.id.rv_main);
        ll_readingRoot = (LinearLayout) findViewById(R.id.ll_readingRoot);

        //// TODO: 2016/9/6 可以选择的背景色和文字颜色
        ll_readingRoot.setBackgroundResource(R.color.readingBackground_day2);

        //// TODO: 2016/9/6 点击屏幕中间位置,弹出菜单
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    private void refreshViews(PlusBook book) {
        //// TODO: 2016/9/6 获取当前时间、阅读位置、最好还有电量
        tv_readingTitle.setText(book.name);
        setPercentText((float) book.read_begin / (float) book.length * 100);
    }

    /**
     * 设置百分比显示文本
     *
     * @param percent 单精度浮点数，当前百分比，是乘以100之后的
     */
    public void setPercentText(float percent) {
        DecimalFormat strPercent = new DecimalFormat("#0.00");
        String s = strPercent.format(percent) + "%";
        tv_readingPercent.setText(s);
    }

    /**
     * 设置当前阅读位置
     */
    public void setCurrentPercent(float p) {
        rv_main.setPercent(p);
        rv_main.invalidate();
        setPercentText(p);
    }

    /**
     * 如果点击到右侧和下侧,就是下一页,左侧上侧上一页,中间就是菜单
     *
     * @param event 点击事件
     * @return 是否消费掉事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getRawX();
            float y = event.getRawY();
            if (x >= screenWidth / 4 * 3) {
                //D
                rv_main.showNextPage();
            } else {
                // A B C E
                if (x >= screenWidth / 4 && y >= screenHeight / 3 * 2) {
                    //C
                    rv_main.showNextPage();
                    //A B E
                } else if (x < screenWidth / 4) {
                    // A
                    rv_main.showPreviousPage();
                    // B E
                } else if (y < screenHeight / 3) {
                    rv_main.showPreviousPage();
                } else {
                    //// TODO: 2016/9/6 显示菜单
                    showPopMenu();
                }
            }
            rv_main.invalidate();
        }
        return super.onTouchEvent(event);
    }

    private void showPopMenu() {
        //// TODO: 2016/9/6 菜单的显示
        System.out.println("Show Menu!");
    }

    /**
     * 软件后台，或者退出，此时应该保存当前位置
     */
    @Override
    protected void onPause() {
        super.onPause();

        int[] position = rv_main.getCurrentPosition();
        relevantBook.read_begin = position[0];
        relevantBook.read_end = position[1];

        DatabaseManager dm = new DatabaseManager(this, false);
        if (!dm.updateBook(relevantBook)) {
            Log.e("onPause", "Save book position failed!");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setResult(1);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}

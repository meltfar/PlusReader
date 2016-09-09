package com.zhouplus.plusreader.activities;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.broadcasts.BatteryReceiver;
import com.zhouplus.plusreader.databases.DatabaseManager;
import com.zhouplus.plusreader.domains.PlusBook;
import com.zhouplus.plusreader.utils.PreferenceManager;
import com.zhouplus.plusreader.views.ReadingView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReadingActivity extends Activity {

    TextView tv_readingTitle, tv_readingTime, tv_readingPercent, tv_readingBattery;
    ReadingView rv_main;
    LinearLayout ll_readingRoot;
    PlusBook relevantBook;
    ImageButton ib_actionBar_back, ib_actionBar_search, ib_actionBar_chapter, ib_actionBar_setting;
    RelativeLayout rl_reading_menu;

    int screenWidth, screenHeight;

    private final String TEXT_SIZE = "ReadingTextSize";
    private final String CHECKED_THEME = "ReadingTheme";

    Handler uiHandler;
    final int MSG_BATTERY = 1;
    final int MSG_TIME = 2;

    final int REQUEST_CODE_SETTING = 553;

    private boolean bSwitchingMenu = false;

    BatteryReceiver batteryReceiver;
    private boolean isRefreshingTime = true;

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

        //默认隐藏菜单
        //先把菜单放好位置
        rl_reading_menu.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                rl_reading_menu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setMenuTop(-(rl_reading_menu.getHeight() - 1));
                //如果这个控件被设置成了GONE，那么就无法测量到它的宽高了
                rl_reading_menu.setVisibility(View.GONE);
            }
        });
        //顶部各个actionbar的作用
        ib_actionBar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存退出
                setResult(1);
                finish();
            }
        });
        ib_actionBar_chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016/9/7 章节窗口
                System.out.println("open chapter window");
            }
        });
        //设置窗口
        ib_actionBar_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReadingActivity.this, ReadingSet.class);
                i.putExtra("Progress", (int) rv_main.getCurrentPercent());
                startActivityForResult(i, REQUEST_CODE_SETTING);
            }
        });
        ib_actionBar_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016/9/7  搜索窗口
                System.out.println("open search window");
            }
        });
    }

    private void initData() {
        //获取输的信息，并且设置给相应的View
        relevantBook = (PlusBook) getIntent().getSerializableExtra("BookInfo");
        //检查小说是否还在
        if (relevantBook == null || !checkBookFile(relevantBook)) {
            Toast.makeText(ReadingActivity.this, "小说读取发生错误！\n文件已发生未知改动",
                    Toast.LENGTH_LONG).show();
            setResult(2);
            finish();
        }

        rv_main.setAssociatedActivity(this);
        rv_main.setBook(relevantBook);

        //设置一个Handler,用于处理时间TextView等东西的更新工作
        uiHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_TIME:
                        if (tv_readingTime != null)
                            tv_readingTime.setText((String) msg.obj);
                        break;
                    case MSG_BATTERY:
                        if (tv_readingBattery != null) {
                            tv_readingBattery.setText((String) msg.obj);
                            tv_readingBattery.setTextColor(msg.arg1);
                        }
                        break;
                }
                return false;
            }
        });

        //设置一个广播接收者，用于接收电量事件
        batteryReceiver = new BatteryReceiver(uiHandler);
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);

        //设置界面，各个文本框的文字
        ConfigViews(relevantBook);
    }

    /**
     * 检查书的存在性，或者是否大小改变
     */
    private boolean checkBookFile(PlusBook plusBook) {
        File f = new File(plusBook.path);
        DatabaseManager dm = new DatabaseManager(this, false);
        if (!f.exists()) {
            dm.removeBook(plusBook.name, plusBook.path);
            return false;
        }
        if (f.length() != plusBook.length) {
            plusBook.length = (int) f.length();
            if (!dm.updateBook(plusBook)) {
                return false;
            } else {
                Toast.makeText(ReadingActivity.this, "文件已被修改", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void initViews() {
        //阅读页面的相关View
        tv_readingTitle = (TextView) findViewById(R.id.tv_readingTitle);
        tv_readingTime = (TextView) findViewById(R.id.tv_readingTime);
        tv_readingPercent = (TextView) findViewById(R.id.tv_readingPercent);
        rv_main = (ReadingView) findViewById(R.id.rv_main);
        ll_readingRoot = (LinearLayout) findViewById(R.id.ll_readingRoot);
        tv_readingBattery = (TextView) findViewById(R.id.tv_readingBattery);

        //顶端菜单的相关View
        rl_reading_menu = (RelativeLayout) findViewById(R.id.rl_reading_menu);
        ib_actionBar_back = (ImageButton) findViewById(R.id.ib_actionBar_back);
        ib_actionBar_setting = (ImageButton) findViewById(R.id.ib_actionBar_setting);
        ib_actionBar_chapter = (ImageButton) findViewById(R.id.ib_actionBar_chapter);
        ib_actionBar_search = (ImageButton) findViewById(R.id.ib_actionBar_search);

        //获取屏幕大小
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        setReaderTheme();
    }

    /**
     * 配置阅读器文本以外的界面环境，电量，时间，百分比
     *
     * @param book book
     */
    private void ConfigViews(PlusBook book) {
        tv_readingTitle.setText(book.name);
        setPercentText((float) book.read_begin / (float) book.length * 100);

        //更新时间,电量已经在消息分发中解决了
        refreshTime();
    }

    /**
     * 设置当前阅读器的主题（白天黑夜）
     */
    private void setReaderTheme() {
        int i = PreferenceManager.getPreferenceInt(this, CHECKED_THEME, 1);
        switch (i) {
            default:
            case 1:
                ll_readingRoot.setBackgroundResource(R.color.readingBackground_day1);
                rv_main.setTextColor(Color.argb(0xff, 0x33, 0x33, 0x33));
                break;
            case 2:
                ll_readingRoot.setBackgroundResource(R.color.readingBackground_day2);
                rv_main.setTextColor(Color.argb(0xff, 0x33, 0x33, 0x33));
                break;
            case 3:
                ll_readingRoot.setBackgroundResource(R.color.readingBackground_net);
                rv_main.setTextColor(Color.argb(0xff, 0x99, 0x99, 0x99));
                break;
        }
        rv_main.invalidate();
    }

    /**
     * 使用handler来更新时间
     */
    private void refreshTime() {
        new Thread() {
            @Override
            public void run() {
                while (isRefreshingTime) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
                    String time = sdf.format(new Date());
                    Message msg = uiHandler.obtainMessage();
                    msg.obj = time;
                    msg.what = MSG_TIME;
                    uiHandler.sendMessage(msg);
                    SystemClock.sleep(10000);
                }
            }
        }.start();
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
        //// TODO: 2016/9/7 如果是滑动手势，同样也要给翻页。。。
        //// TODO: 2016/9/7 当然，也可以考虑一下换成ViewPager(或者自己实现)
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
                    showPopMenu();
                }
            }
            rv_main.invalidate();
        }
        return super.onTouchEvent(event);
    }

    private void setMenuTop(int top) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) rl_reading_menu.getLayoutParams();
        lp.topMargin = top;
        rl_reading_menu.setLayoutParams(lp);
    }

    private void showPopMenu() {
        if (bSwitchingMenu) return;
        int h = rl_reading_menu.getHeight();
        if (rl_reading_menu.getVisibility() == View.VISIBLE) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, -h);
            valueAnimator.setTarget(rl_reading_menu);
            valueAnimator.setDuration(400);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setMenuTop((Integer) animation.getAnimatedValue());
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    bSwitchingMenu = true;
                    rl_reading_menu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    rl_reading_menu.setVisibility(View.GONE);
                    bSwitchingMenu = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            valueAnimator.start();
        } else {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(-h, 0);
            valueAnimator.setTarget(rl_reading_menu);
            valueAnimator.setDuration(400);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    setMenuTop((Integer) animation.getAnimatedValue());
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    bSwitchingMenu = true;
                    rl_reading_menu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    bSwitchingMenu = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            valueAnimator.start();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止循环时间
        isRefreshingTime = false;
        //窗口退出的时候，就直接注销了接受者
        if (batteryReceiver != null)
            unregisterReceiver(batteryReceiver);
    }

    /**
     * 当设置窗口返回后，接收一下返回的结果
     *
     * @param requestCode 1
     * @param resultCode  1
     * @param data        1
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING) {
            if (resultCode != RESULT_CANCELED) {
                //检查都什么改变了
                if ((resultCode & ~ReadingSet.TEXTSIZE_CHANGED) != resultCode) {
                    rv_main.setTextSize(PreferenceManager.getPreferenceInt(this, TEXT_SIZE, 30));
                    rv_main.invalidate();
                    showPopMenu();
                }
                if ((resultCode & ~ReadingSet.THEME_CHANGED) != resultCode) {
                    setReaderTheme();
                    showPopMenu();
                }
                if ((resultCode & ~ReadingSet.PROGRESS_CHANGED) != resultCode) {
                    int progress = data.getIntExtra("Progress", -1);
                    if (progress != -1) {
                        setCurrentPercent(progress);
                    }
                    showPopMenu();
                }
            }
        }
    }

}

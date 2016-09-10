package com.zhouplus.plusreader.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.applications.PlusApplication;
import com.zhouplus.plusreader.domains.NovelFactory;
import com.zhouplus.plusreader.views.ReadingView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChapterActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
//    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
//    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private RecyclerView mContentView;
    List<Integer> starts;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    //    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
//            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    //    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };


    private NovelFactory novelFactory;
    private Handler handler;
    LinkedHashMap<Integer, String> chapters;
    ChapterAdapter adapter;
    AnimatedCircleLoadingView loadingView;
    private ReadingView readingView;
    int currentChapter;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chapter);

//        mVisible = true;
//        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = (RecyclerView) findViewById(R.id.fullscreen_content);
        Button btn_chapter_back = (Button) findViewById(R.id.btn_chapter_back);

        btn_chapter_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        //使用这个来更新小说界面
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    adapter = new ChapterAdapter();
                    mContentView.setLayoutManager(new LinearLayoutManager(ChapterActivity.this));
                    mContentView.setItemAnimator(new DefaultItemAnimator());
                    loadingView.stopOk();

                    int position = getIntent().getIntExtra("com.zhouplus.plusreader.chapterPosition",
                            -1);
                    currentChapter = getCurrentChapter(position);
                    mContentView.scrollToPosition(currentChapter);

                    mContentView.setAdapter(adapter);
                    loadingView.setVisibility(View.GONE);
                }
                return false;
            }
        });

        PlusApplication pa = (PlusApplication) getApplication();
        novelFactory = pa.novelFactory;
        readingView = pa.readingView;
        //如果获取对象出错，就直接返回
        if (novelFactory == null) {
            Toast.makeText(ChapterActivity.this, "获取章节时出现错误！", Toast.LENGTH_LONG).show();
            setResult(RESULT_OK);
            finish();
        }

        loadingView = (AnimatedCircleLoadingView) findViewById(R.id.chapter_loading);
        loadingView.startIndeterminate();

        //获取章节信息的线程
        new Thread() {
            @Override
            public void run() {
                chapters = novelFactory.analyseChapter();
                Set<Integer> integers = chapters.keySet();
                starts = new ArrayList<>(integers);

                if (chapters != null) {
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("小说章节");
            actionBar.hide();
        }

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    /**
     * 设置当前所处的位置，以及当前正阅读的章节
     *
     * @param position 文本位置
     */
    private int getCurrentChapter(int position) {
        System.out.println("finding chapter at position : " + position);
        if (position == -1) {
            return 0;
        }
        int last = 0, chapterPos = 0;
        boolean bFind = false;
        for (int p : starts) {
            if (position < p && position >= last) {
                chapterPos = starts.indexOf(last);
                bFind = true;
                if (chapterPos == -1) {
                    chapterPos = 0;
                }
                break;
            }
            last = p;
        }
        if (!bFind) {
            chapterPos = starts.size() - 1;
        }
        return chapterPos;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
//        delayedHide(100);
    }

//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mControlsView.setVisibility(View.GONE);
//        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
//
//    @SuppressLint("InlinedApi")
//    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
        }
        return super.onKeyDown(keyCode, event);
    }

    class ChapterAdapter extends RecyclerView.Adapter<ChapterViewHolder> {

        @Override
        public ChapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ChapterViewHolder(LayoutInflater.from(ChapterActivity.this)
                    .inflate(R.layout.item_recyclerview_chapter, parent, false));
        }

        @Override
        public void onBindViewHolder(ChapterViewHolder holder, int position) {
            holder.tv_chapter_name.setText(chapters.get(starts.get(position)));
            if (position == currentChapter) {
                holder.tv_chapter_name.setTextColor(Color.argb(0xff, 0xFF, 0x40, 0x81));
            }
        }

        @Override
        public int getItemCount() {
            return chapters.size() == 0 ? 0 : chapters.size();
        }
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder {

        TextView tv_chapter_name;

        public ChapterViewHolder(View itemView) {
            super(itemView);
            tv_chapter_name = (TextView) itemView.findViewById(R.id.tv_chapter_name);
            tv_chapter_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newPos = starts.get(getAdapterPosition());
                    readingView.setPosition(newPos);
                    readingView.invalidate();
                    finish();
                }
            });
        }
    }
}

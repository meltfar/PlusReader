package com.zhouplus.plusreader.activities;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.utils.PreferenceManager;
import com.zhouplus.plusreader.views.PreviewText;

import net.qiujuer.genius.widget.GeniusButton;
import net.qiujuer.genius.widget.GeniusSeekBar;

public class ReadingSet extends Activity {

    private final String TEXT_SIZE = "ReadingTextSize";
    private final String CHECKED_THEME = "ReadingTheme";

    private int mTextSize;
    private int mCheckedTheme = 1;// 所选择的主题，默认是第一个

    PreviewText pt_preview;
    GeniusButton btn_textSize_down, btn_textSize_up;
    ImageButton ib_theme_1, ib_theme_2, ib_theme_3;
    ImageView iv_current_theme;
    GeniusSeekBar sb_reading_progress;
    TextView tv_progress_preview;

    private int RESULT_CHANGED = 0;

    public static final int PROGRESS_CHANGED = 0x00000001;
    public static final int TEXTSIZE_CHANGED = 0x00000010;
    public static final int THEME_CHANGED = 0x00000100;

    private final int ANIMATION_DURATION = 400;
    private boolean bRunningAnimation = false;

    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.popup_reading_setting);

        //准备出一个传回设置的Progress值的Intent
        resultIntent = new Intent();
        initViews();
        initEvents();
    }

    private void initEvents() {
        //字体方法按钮
        btn_textSize_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //字体大小改变，更新返回结果
                RESULT_CHANGED |= TEXTSIZE_CHANGED;
                setResult(RESULT_CHANGED, resultIntent);
                //改变大小
                mTextSize += 5;
                pt_preview.setTextSize(mTextSize);
                //写入配置文件中
                PreferenceManager.setPreferenceInt(ReadingSet.this, TEXT_SIZE, mTextSize);
            }
        });
        //字体减小按钮
        btn_textSize_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //字体大小改变，更新返回结果
                RESULT_CHANGED |= TEXTSIZE_CHANGED;
                setResult(RESULT_CHANGED, resultIntent);
                //改变大小
                mTextSize -= 5;
                pt_preview.setTextSize(mTextSize);
                //写入配置文件中
                PreferenceManager.setPreferenceInt(ReadingSet.this, TEXT_SIZE, mTextSize);
            }
        });

        //在布局时，就设置好现在是什么主题
        iv_current_theme.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                iv_current_theme.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) iv_current_theme.getLayoutParams();
                switch (mCheckedTheme) {
                    default:
                    case 1:
                        lp.leftMargin = ib_theme_1.getLeft();
                        break;
                    case 2:
                        lp.leftMargin = ib_theme_2.getLeft();
                        break;
                    case 3:
                        lp.leftMargin = ib_theme_3.getLeft();
                        break;
                }
                iv_current_theme.setLayoutParams(lp);
            }
        });

        //主题设置按钮1
        ib_theme_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRunningAnimation) return;
                if (iv_current_theme.getLeft() == ib_theme_1.getLeft()) return;

                ValueAnimator valueAnimator = ValueAnimator.ofInt(iv_current_theme.getLeft(), ib_theme_1.getLeft())
                        .setDuration(ANIMATION_DURATION);
                valueAnimator.setTarget(iv_current_theme);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        FrameLayout.LayoutParams lp =
                                (FrameLayout.LayoutParams) iv_current_theme.getLayoutParams();
                        lp.leftMargin = (int) animation.getAnimatedValue();
                        iv_current_theme.setLayoutParams(lp);
                    }
                });
                //监听动画开始结束事件，防止重入
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        bRunningAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bRunningAnimation = false;
                        PreferenceManager.setPreferenceInt(ReadingSet.this, CHECKED_THEME, 1);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                RESULT_CHANGED |= THEME_CHANGED;
                setResult(RESULT_CHANGED, resultIntent);

                valueAnimator.start();
            }
        });
        //主题设置按钮2
        ib_theme_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRunningAnimation) return;
                if (iv_current_theme.getLeft() == ib_theme_2.getLeft()) return;

                ValueAnimator valueAnimator = ValueAnimator.ofInt(iv_current_theme.getLeft(), ib_theme_2.getLeft())
                        .setDuration(ANIMATION_DURATION);
                valueAnimator.setTarget(iv_current_theme);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        FrameLayout.LayoutParams lp =
                                (FrameLayout.LayoutParams) iv_current_theme.getLayoutParams();
                        lp.leftMargin = (int) animation.getAnimatedValue();
                        iv_current_theme.setLayoutParams(lp);
                    }
                });
                //监听动画开始结束事件，防止重入
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        bRunningAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bRunningAnimation = false;
                        PreferenceManager.setPreferenceInt(ReadingSet.this, CHECKED_THEME, 2);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                RESULT_CHANGED |= THEME_CHANGED;
                setResult(RESULT_CHANGED, resultIntent);

                valueAnimator.start();
            }
        });
        //主题设置按钮3
        ib_theme_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRunningAnimation) return;
                if (iv_current_theme.getLeft() == ib_theme_3.getLeft()) return;

                ValueAnimator valueAnimator = ValueAnimator.ofInt(iv_current_theme.getLeft(), ib_theme_3.getLeft())
                        .setDuration(ANIMATION_DURATION);
                valueAnimator.setTarget(iv_current_theme);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        FrameLayout.LayoutParams lp =
                                (FrameLayout.LayoutParams) iv_current_theme.getLayoutParams();
                        lp.leftMargin = (int) animation.getAnimatedValue();
                        iv_current_theme.setLayoutParams(lp);
                    }
                });
                //监听动画开始结束事件，防止重入
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        bRunningAnimation = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        bRunningAnimation = false;
                        PreferenceManager.setPreferenceInt(ReadingSet.this, CHECKED_THEME, 3);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                RESULT_CHANGED |= THEME_CHANGED;
                setResult(RESULT_CHANGED, resultIntent);

                valueAnimator.start();
            }
        });

        sb_reading_progress.setOnSeekBarChangeListener(new GeniusSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(GeniusSeekBar geniusSeekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(GeniusSeekBar geniusSeekBar) {
                RESULT_CHANGED |= PROGRESS_CHANGED;
                setResult(RESULT_CHANGED, resultIntent);
            }

            @Override
            public void onStopTrackingTouch(GeniusSeekBar geniusSeekBar) {
                int progress = geniusSeekBar.getProgress();
                resultIntent.putExtra("Progress", progress);
            }
        });
    }

    private void initViews() {
        pt_preview = (PreviewText) findViewById(R.id.pt_preview);
        btn_textSize_up = (GeniusButton) findViewById(R.id.btn_textSize_up);
        btn_textSize_down = (GeniusButton) findViewById(R.id.btn_textSize_down);

        ib_theme_1 = (ImageButton) findViewById(R.id.ib_theme_1);
        ib_theme_2 = (ImageButton) findViewById(R.id.ib_theme_2);
        ib_theme_3 = (ImageButton) findViewById(R.id.ib_theme_3);
        iv_current_theme = (ImageView) findViewById(R.id.iv_current_theme);

        sb_reading_progress = (GeniusSeekBar) findViewById(R.id.sb_reading_progress);
        tv_progress_preview = (TextView) findViewById(R.id.tv_progress_preview);

        mTextSize = PreferenceManager.getPreferenceInt(this, TEXT_SIZE, 30);
        pt_preview.setTextSize(mTextSize);

        mCheckedTheme = PreferenceManager.getPreferenceInt(this, CHECKED_THEME, 1);

        iv_current_theme.setFocusableInTouchMode(false);

        int progress = getIntent().getIntExtra("Progress", -1);
        if (progress != -1) {
            sb_reading_progress.setProgress(progress);
        } else {
            Toast.makeText(this, "获取当前位置失败！", Toast.LENGTH_LONG).show();
        }
    }
}

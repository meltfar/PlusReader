package com.zhouplus.plusreader.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zhouplus.plusreader.activities.ReadingActivity;
import com.zhouplus.plusreader.domains.NovelFactory;
import com.zhouplus.plusreader.domains.PlusBook;
import com.zhouplus.plusreader.utils.PreferenceManager;

import java.util.Vector;

/**
 * Created by zhouplus
 * Time at 2016/9/6
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class ReadingView extends View {
    private ReadingActivity activity;
    private PlusBook relevantBook;
    NovelFactory novelFactory;
    int mGlobalWidth, mGlobalHeight;
    int mLineSpace = 15;
    int mTextSize = 30;
    Paint pText;

    private final String TEXT_SIZE = "ReadingTextSize";

    private Vector<String> mContainer;

    public ReadingView(Context context) {
        super(context);
    }

    public ReadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }


    public ReadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    /**
     * 类刚刚初始化就调用的方法
     */
    private void initViews() {
        pText = new Paint(Paint.ANTI_ALIAS_FLAG);
        setTextColor(Color.BLACK);
//        setTextSize(mTextSize);
    }


    /**
     * 调用以设置当前布局的大小,并初始化NovelFactory类(3)
     *
     * @param w 宽
     * @param h 高
     */
    public boolean setLayoutSize(int w, int h) {
        mGlobalWidth = w;
        mGlobalHeight = h;

        if (relevantBook != null) {
            //由文本工厂读取相应的小说内容，并作相应处理
            novelFactory = new NovelFactory(w, h, null);
            if (!novelFactory.openBook(relevantBook)) {
                return false;
            }
            novelFactory.setArguments(mTextSize, mLineSpace, pText);
            return true;
        } else
            return false;
    }

    /**
     * 设置文本大小，以像素为单位，默认30
     *
     * @param size 大小
     */
    public void setTextSize(int size) {
        this.mTextSize = size;
        pText.setTextSize(size);
        //行间距也跟着改变，值为文字大小的一半
        this.mLineSpace = Math.round(((float) size) / 2);
        if (novelFactory != null) {
            mContainer = novelFactory.setArguments(mTextSize, mLineSpace, pText);
        }
        if (activity != null) {
            PreferenceManager.setPreferenceInt(activity, TEXT_SIZE, size);
        }
    }

    /**
     * 设置文字颜色，默认黑色
     *
     * @param color 颜色
     */
    public void setTextColor(int color) {
        pText.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //如果在Android Studio的预览模式下，就直接退出，不画
        if (isInEditMode()) {
            return;
        }

        // 如果m_lines未被赋值
        if (mContainer == null || mContainer.size() == 0) {
            // 结束位置等于开始位置
            novelFactory.mPageEndPos = novelFactory.mPageBeginPos;
            // 获取下一页
            mContainer = novelFactory.pageDown();
        }

        if (mContainer != null && mContainer.size() > 0) {
            int y = 0;
            for (String line : mContainer) {
                // Cursor在移动
                y += mTextSize + mLineSpace/* 每行文字之间的空隙 */;
                // 绘画文字
                canvas.drawText(line, 0, y, pText);
            }
        }
    }

    /**
     * 这个方法必须被调用，用于设置这个View需要显示的Book(2)
     *
     * @param book plus book
     */
    public void setBook(PlusBook book) {
        this.relevantBook = book;
    }

    /**
     * 设置本Activity（1）
     *
     * @param ra 实例
     */
    public void setAssociatedActivity(ReadingActivity ra) {
        this.activity = ra;
    }

    public void setPercent(float percent) {
        mContainer = novelFactory.setPercent(percent);
    }

    /**
     * 返回现在阅读的位置，其实就是直接调用了NovelFactory的同名方法
     *
     * @return index 0 是begin, index 1 是end
     */
    public int[] getCurrentPosition() {
        return novelFactory.getCurrentPosition();
    }

    public void showNextPage() {
        mContainer = novelFactory.nextPage();
        int[] ps = novelFactory.getCurrentPosition();
        activity.setPercentText((float) ps[0] / (float) relevantBook.length * 100);
    }

    public void showPreviousPage() {
        mContainer = novelFactory.prePage();
        int[] ps = novelFactory.getCurrentPosition();
        activity.setPercentText((float) ps[0] / (float) relevantBook.length * 100);
    }

    public float getCurrentPercent() {
        return novelFactory.getCurrentPercent();
    }

}

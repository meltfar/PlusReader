package com.zhouplus.plusreader.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Time at 2016/9/8
 * Project name PlusReader
 * Description : 在阅读设置界面，用于预览文字大小的显示框
 * Author's email : zhouplus@qq.com
 * Version 1.0
 */
public class PreviewText extends View {

    int mGlobalWidth, mGlobalHeight;
    Paint pText;
    public String shownText = "我爱你";

    public PreviewText(Context context) {
        super(context);
    }

    public PreviewText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PreviewText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        pText = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mGlobalWidth = MeasureSpec.getSize(widthMeasureSpec);
        mGlobalHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = ((float) mGlobalWidth - pText.measureText(shownText)) / 2;
        float y = (float) mGlobalHeight / 2 + pText.getTextSize() / 2;
        canvas.drawText(shownText, x, y, pText);
    }

    public float getTextSize() {
        return pText.getTextSize();
    }

    public void setTextSize(float size) {
        pText.setTextSize(size);
        this.invalidate();
    }
}

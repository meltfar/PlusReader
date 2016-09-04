package com.zhouplus.plusreader.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RadioButton;

/**
 * Created by zhouplus
 * Time at 2016/9/1
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class TabItem extends RadioButton {

    //    protected final String XMLNS_Android = "http://schemas.android.com/apk/res/android";
    protected final String XMLNS_Custom = "http://schemas.android.com/apk/res-auto";

    protected int drawableWidth, drawableHeight;

    public TabItem(Context context) {
        super(context);
    }

    public TabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(attrs);
    }


    public TabItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(attrs);
    }

    protected void initData(AttributeSet attrs) {
        String drawableWidth = attrs.getAttributeValue(XMLNS_Custom, "drawableWidth");
        String drawableHeight = attrs.getAttributeValue(XMLNS_Custom, "drawableHeight");
        //从布局属性中解析大小
        if (drawableHeight != null) {
            this.drawableHeight = parseDimension(drawableHeight);
        }
        if (drawableWidth != null) {
            this.drawableWidth = parseDimension(drawableWidth);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (drawableWidth != 0 || drawableHeight != 0) {
            Drawable[] drawables = this.getCompoundDrawables();
            for (int i = 0; i < drawables.length; i++) {
                drawables[i] = resizeImage(drawables[i]);
            }
            this.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }

    /**
     * 从属性返回的字符串中解析出数值，最终都返回成PX
     *
     * @param dimen 属性字符串
     * @return 像素值 如果失败就返回0
     */
    private int parseDimension(String dimen) {
        int i;
        if (dimen.contains("dip")) {
            dimen = dimen.replaceAll("[a-z]", "");
            dimen = dimen.substring(0, dimen.indexOf("."));
            try {
                i = Integer.parseInt(dimen);
            } catch (NumberFormatException e) {
                Log.e("NumberFormat", "TabItem.class -> parseDimension() parse failed" +
                        dimen + "\n" + e.toString());
                i = 0;
            }
            i = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    i, getResources().getDisplayMetrics());
        } else if (dimen.contains("px")) {
            dimen = dimen.replaceAll("[a-z]", "");
            dimen = dimen.substring(0, dimen.indexOf("."));
            try {
                i = Integer.parseInt(dimen);
            } catch (NumberFormatException e) {
                Log.e("NumberFormat", "TabItem.class -> parseDimension() parse failed" +
                        dimen + "\n" + e.toString());
                i = 0;
            }
        } else {
            i = 0;
        }
        return i;
    }

    protected Drawable resizeImage(Drawable drawable) {
        if (drawable == null) return null;
        drawable.setBounds(0, 0,
                drawableWidth == 0 ? drawable.getIntrinsicWidth() : drawableWidth,
                drawableHeight == 0 ? drawable.getIntrinsicHeight() : drawableHeight);
        return drawable;
    }

    @Deprecated
    protected Drawable resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);
        return new BitmapDrawable(getResources(), resizedBitmap);
    }
}

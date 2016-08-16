package com.zhouplus.plusreader.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by zhouplus
 * Time at 2016/8/15
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class ViewTools {
    /**
     * 通过给定的DP值，返回PX值
     *
     * @param px
     * @param dm
     * @return
     */
    public static int dip2px(int px, DisplayMetrics dm) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, dm);
    }

    /**
     * 显示一个信息窗体，可以返回键取消
     *
     * @param context          宇宙惯例
     * @param msg              信息内容
     * @param title            可选。标题
     * @param cancelListener   可选，返回取消的监听器
     * @param positiveListener 确定按下监听器
     * @param negativeListener 可选，取消按下监听器，如果为null则不添加取消按钮
     */
    public static void MessageBox(Context context, String msg, String title,
                                  DialogInterface.OnCancelListener cancelListener,
                                  DialogInterface.OnClickListener positiveListener,
                                  DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (cancelListener != null)
            builder.setOnCancelListener(cancelListener);
        builder.setMessage(msg);
        if (title != null)
            builder.setTitle(title);
        if (negativeListener != null)
            builder.setNegativeButton("取消", negativeListener);
        builder.setPositiveButton("确定", positiveListener);
        builder.create().show();
    }
}

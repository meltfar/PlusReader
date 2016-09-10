package com.zhouplus.plusreader.applications;

import android.app.Application;

import com.zhouplus.plusreader.domains.NovelFactory;
import com.zhouplus.plusreader.views.ReadingView;

import org.xutils.x;

/**
 * Created by zhouplus
 * Time at 2016/8/15
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class PlusApplication extends Application {

    public NovelFactory novelFactory;
    public ReadingView readingView;

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}

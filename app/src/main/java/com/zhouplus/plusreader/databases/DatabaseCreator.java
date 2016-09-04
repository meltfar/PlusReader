package com.zhouplus.plusreader.databases;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zhouplus.plusreader.utils.PlusConstants;

/**
 * Created by zhouplus
 * Time at 2016/8/17
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class DatabaseCreator extends SQLiteOpenHelper {
    public DatabaseCreator(Context context) {
        this(context, PlusConstants.DATABASE_NAME, null, 1, null);
    }

    private DatabaseCreator(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    // 当数据库创建时调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("数据库创建");
        //创建书本信息数据库
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookshelf` ( \n" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                "`name` TEXT NOT NULL, \n" +
                "`description` TEXT, \n" +
                "`add_date` TEXT NOT NULL, \n" +
                "`length` INTEGER unsigned NOT NULL, \n" +
                "`path` TEXT NOT NULL \n" +
                ")");
        // 创建书签数据库
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmark` (\n" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "`name` TEXT NOT NULL, \n" +
                "`start` INTEGER unsigned NOT NULL,\n" +
                "`end` INTEGER unsigned NOT NULL,\n" +
                "`path` TEXT NOT NULL,\n" +
                "`bookid` INTEGER\n" +
                ")");
    }

    // 数据库升级的时候调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("哇塞，升级啦！");
    }
}

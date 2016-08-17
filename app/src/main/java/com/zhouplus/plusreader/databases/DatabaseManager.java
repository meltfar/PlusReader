package com.zhouplus.plusreader.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouplus
 * Time at 2016/8/17
 * Project name PlusReader
 * Description :
 * Author's email :
 * Version 1.0
 */
public class DatabaseManager {
    private DatabaseCreator mDataCreator;
    private SQLiteDatabase mDatabase;
    private final String BOOK_TABLE = "bookshelf";
    private boolean bforRead;

    public DatabaseManager(Context context, boolean forRead) {
        mDataCreator = new DatabaseCreator(context);
        if (forRead)
            mDatabase = mDataCreator.getReadableDatabase();
        else
            mDatabase = mDataCreator.getWritableDatabase();
        bforRead = forRead;
    }

    /**
     * 添加书
     *
     * @param name     书名称
     * @param desc     可选，描述
     * @param bookSize 书的字数
     * @param path     书的所在路径
     * @return 是否添加成功
     */
    public boolean addBook(String name, String desc, int bookSize, String path) {
        if (bforRead) {
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        if (desc != null)
            cv.put("description", desc);
        cv.put("length", bookSize);
        cv.put("path", path);
        cv.put("add_date", "datetime('now')");
        return mDatabase.insert(BOOK_TABLE, null, cv) != -1;
    }

    public boolean removeBook(String name, String path) {
        return !bforRead &&
                mDatabase.delete(BOOK_TABLE, "name=? AND path=?",
                        new String[]{name, path}) == 1;
    }

    public List<Bundle> getBooks() {
        Cursor cursor = mDatabase.rawQuery("SELECT id,name,description,add_date,length,path from "
                + BOOK_TABLE, null);
        List<Bundle> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Bundle b = new Bundle();
            b.putInt("id", cursor.getInt(1));
            b.putString("name", cursor.getString(2));
            b.putString("description", cursor.getString(3));
            b.putString("add_date", cursor.getString(4));
            b.putInt("length", cursor.getInt(5));
            b.putString("path", cursor.getString(6));

            list.add(b);
        }
        cursor.close();
        return list;
    }
}

package com.zhouplus.plusreader.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zhouplus.plusreader.domains.PlusBook;

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
    private boolean forRead;

    private OnDataTransferedListener listener;

    public DatabaseManager(Context context, boolean forRead) {
        mDataCreator = new DatabaseCreator(context);
        if (forRead)
            mDatabase = mDataCreator.getReadableDatabase();
        else
            mDatabase = mDataCreator.getWritableDatabase();
        this.forRead = forRead;
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
    public boolean addBook(String name, @Nullable String desc, int bookSize, String path) {
        if (forRead) {
            Log.e("DataManager->addBook", "database is not for write!");
            return false;
        }
        if (TestBook(name, path)) {
            Log.e("DataManager->addBook", "the book is already exist!");
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

    /**
     * 通过给定的参数，测试所要求的书是否存在于书架上
     *
     * @param name 书籍名称
     * @param path 书籍文本的所在路径
     * @return 是否存在了
     */
    public boolean TestBook(String name, String path) {
        Cursor cursor = mDatabase.rawQuery("SELECT name,path from " + BOOK_TABLE
                + " WHERE name = ? and path = ?", new String[]{name, path});

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else if (cursor.getCount() == 1) {
            cursor.close();
            return true;
        } else {
            Log.e("DatabaseManager", "书籍信息出现重复！");
            cursor.close();
            return true;
        }
    }

    // 删除书信息
    public boolean removeBook(String name, String path) {
        return !forRead &&
                mDatabase.delete(BOOK_TABLE, "name=? AND path=?",
                        new String[]{name, path}) == 1;
    }

    // 删除书信息2
    public boolean removeBook(int id) {
        return !forRead &&
                mDatabase.delete(BOOK_TABLE, "id=?", new String[]{String.valueOf(id)}) == 1;
    }

    // 返回所有的书信息
    public void getBooks() {
        new Thread() {
            @Override
            public void run() {
                Cursor cursor = mDatabase.rawQuery("SELECT id,name,description,add_date,length,path from "
                        + BOOK_TABLE, null);
                List<PlusBook> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    PlusBook b = new PlusBook();
                    b.id = cursor.getInt(0);
                    b.name = cursor.getString(1);
                    b.description = cursor.getString(2);
                    b.add_date = cursor.getString(3);
                    b.length = cursor.getInt(4);
                    b.path = cursor.getString(5);

                    list.add(b);
                }
                cursor.close();
                listener.OnDataTransfered(list);
            }
        }.start();
    }

    public void setOnDataTransferedListener(OnDataTransferedListener listener) {
        this.listener = listener;
    }

    public interface OnDataTransferedListener {
        void OnDataTransfered(List<PlusBook> books);
    }

}

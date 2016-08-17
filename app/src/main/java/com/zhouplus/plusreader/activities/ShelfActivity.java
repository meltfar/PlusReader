package com.zhouplus.plusreader.activities;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhouplus.plusreader.R;
import com.zhouplus.plusreader.databases.DatabaseCreator;

public class ShelfActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);

        // 隐藏ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

//        DatabaseCreator dc = new DatabaseCreator(this);
//        SQLiteDatabase database = dc.getWritableDatabase();
    }
}

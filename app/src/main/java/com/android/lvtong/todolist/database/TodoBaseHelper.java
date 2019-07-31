package com.android.lvtong.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.lvtong.todolist.database.TodoDbSchema.TodoTable;

public class TodoBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "todoBase.db";

    public TodoBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL创建
        db.execSQL("create table " + TodoTable.NAME + "(" + " _id integer primary key autoincrement, " +
                   TodoTable.Cols.UUID + ", " + TodoTable.Cols.TITLE + ", " + TodoTable.Cols.BEIZHU + ", " +
                   TodoTable.Cols.IMPORTANCE + ", " + TodoTable.Cols.DATE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //目前不需要使用
    }
}

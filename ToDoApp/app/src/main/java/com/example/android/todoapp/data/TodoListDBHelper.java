package com.example.android.todoapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rishw on 7/28/2017.
 */

public class TodoListDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "todolist.db";
    public static final int DATABASE_VERSION = 3;
    @Override
    public void onCreate(SQLiteDatabase db) {
            final String SQL_CREATE_TODOLIST_TABLE = "CREATE TABLE " + TodoListContract.TodoListEntry.TABLE_NAME+ " (" + TodoListContract.TodoListEntry.ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," + TodoListContract.TodoListEntry.COLUMN_TASK_NAME+ " TEXT NOT NULL, " + TodoListContract.TodoListEntry.COLUMN_PRIORITY + " INTEGER NOT NULL, "+ TodoListContract.TodoListEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+ TodoListContract.TodoListEntry.COLUMN_TASK_NOTES+ " TEXT DEFAULT NULL, "+ TodoListContract.TodoListEntry.COLUMN_TASKSTATUS + " INTEGER NOT NULL" + "); ";

        db.execSQL(SQL_CREATE_TODOLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TodoListContract.TodoListEntry.TABLE_NAME);
        onCreate(db);
    }

    public TodoListDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
}

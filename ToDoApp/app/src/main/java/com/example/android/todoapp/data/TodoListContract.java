package com.example.android.todoapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rishw on 7/28/2017.
 */

public class TodoListContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.todoapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TODO = "todo";
    public static final class TodoListEntry implements BaseColumns{
        public static final String TABLE_NAME = "TodoList";
        public static final String ID = BaseColumns._ID;
        public static final String COLUMN_TASK_NAME = "taskName";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_TASK_NOTES = "taskNotes";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TASKSTATUS = "taskstatus";


        public static final int HIGH_PRIORITY = 2;
        public static final int MEDIUM_PRIORITY = 1;
        public static final int LOW_PRIORITY = 0;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TODO);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

    }
}

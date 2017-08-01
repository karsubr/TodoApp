package com.example.android.todoapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by rishw on 7/30/2017.
 */

public class TodoProviders extends ContentProvider {


    private final static int TODO = 100;
    private final static  int TODO_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(TodoListContract.CONTENT_AUTHORITY,TodoListContract.PATH_TODO,TODO);
        sUriMatcher.addURI(TodoListContract.CONTENT_AUTHORITY,TodoListContract.PATH_TODO+"/#", TODO_ID);
    }

    private TodoListDBHelper dbHelper;
    @Override
    public boolean onCreate() {
        dbHelper = new TodoListDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase mDb = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case TODO:
            {
                cursor = mDb.query(TodoListContract.TodoListEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }

            case TODO_ID:
            {
                selection = TodoListContract.TodoListEntry.ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = mDb.query(TodoListContract.TodoListEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Cannot query" + uri);
            }
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int MATCH =  sUriMatcher.match(uri);
        switch (MATCH){
            case TODO:
                return TodoListContract.TodoListEntry.CONTENT_LIST_TYPE;
            case TODO_ID:
                return TodoListContract.TodoListEntry.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        int match = sUriMatcher.match(uri);

        switch (match){
            case TODO:
                return insert_data(uri,values);
            default:
                throw new IllegalArgumentException("Data cannot be inserted");
        }

    }

    private Uri insert_data(Uri uri, ContentValues values) {

        String name = values.getAsString(TodoListContract.TodoListEntry.COLUMN_TASK_NAME);
        if(name == null){
            throw new IllegalArgumentException("no taskname");
        }

        String notes = values.getAsString(TodoListContract.TodoListEntry.COLUMN_TASK_NOTES);
        if(notes == null){
            throw new IllegalArgumentException("no task notes");
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(TodoListContract.TodoListEntry.TABLE_NAME,null,values);

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase mDB = dbHelper.getWritableDatabase();
        int match  = sUriMatcher.match(uri);
        int delete;
        switch (match){
            case TODO: {
                delete = mDB.delete(TodoListContract.TodoListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TODO_ID:
            {
                selection = TodoListContract.TodoListEntry.ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                delete = mDB.delete(TodoListContract.TodoListEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Cannot delete");
            }

        }
        if(delete != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case TODO:
            {
                return updateTask(uri,values,selection,selectionArgs);
            }
            case TODO_ID:
            {
                selection = TodoListContract.TodoListEntry.ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                return updateTask(uri,values,selection,selectionArgs);
            }
            default:{
                throw new IllegalArgumentException("Cannot Update" + uri);
            }
        }

    }

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(TodoListContract.TodoListEntry.COLUMN_TASK_NAME)){
            String name = values.getAsString(TodoListContract.TodoListEntry.COLUMN_TASK_NAME);
            if(name == null){
                throw new IllegalArgumentException("no taskname");
            }
        }

        if(values.containsKey(TodoListContract.TodoListEntry.COLUMN_TASK_NOTES)){
            String notes = values.getAsString(TodoListContract.TodoListEntry.COLUMN_TASK_NOTES);
            if(notes == null){
                throw new IllegalArgumentException("no task notes");
            }
        }



        /*if(values.containsKey(TodoListContract.TodoListEntry.COLUMN_PRIORITY)){
            Integer priority = values.getAsInteger(TodoListContract.TodoListEntry.COLUMN_PRIORITY);
            if( priority == null ){
                throw new IllegalArgumentException("no date");
            }
        }



        if(values.containsKey(TodoListContract.TodoListEntry.COLUMN_TASKSTATUS)){
            Integer status = values.getAsInteger(TodoListContract.TodoListEntry.COLUMN_TASKSTATUS);
            if(status == null ){
                throw new IllegalArgumentException("no status");
            }
        }*/

        if(values.size() == 0){
            return 0;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int rowID = database.update(TodoListContract.TodoListEntry.TABLE_NAME,values,selection,selectionArgs);

        if(rowID == 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowID;
    }
}

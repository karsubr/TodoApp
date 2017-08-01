package com.example.android.todoapp;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.todoapp.data.TodoListContract;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_TAG = 1;
    private ToDoListAdapter mAdapter;
    private Toolbar mToolbar;
    private Spinner mSpinner;
    private int sortOrder = 0;
    private ListView listView;
    private TextView emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingadd);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DialogFrag.class);
                if(intent.resolveActivity(getPackageManager())!= null){
                    startActivity(intent);
                }

            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        emptyView = (TextView)findViewById(R.id.textempty);

        mToolbar.setTitle(getResources().getString(R.string.app_name));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setLogo(getDrawable(R.mipmap.ic_launcher));
        }
        setSpinnerAdapter();

        getLoaderManager().initLoader(LOADER_TAG,null,this);

        mAdapter = new ToDoListAdapter(this,null);

         listView = (ListView) findViewById(R.id.TodoListView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this,Tododetails.class);
                String taskName = cursor.getString(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TASK_NAME));
                String taskNotes = cursor.getString(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TASK_NOTES));
                int priority = cursor.getInt(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_PRIORITY));
                int taskstatus = cursor.getInt(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TASKSTATUS));
                long dateinMillis = cursor.getLong(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TIMESTAMP));
                long ID = id;
                intent.putExtra("taskname",taskName);
                intent.putExtra("tasknotes",taskNotes);
                intent.putExtra("priority",priority);
                intent.putExtra("taskstatus",taskstatus);
                intent.putExtra("date",dateinMillis);
                intent.putExtra("ID",ID);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        listView.setAdapter(mAdapter);

        listView.setEmptyView(emptyView);



    }

    @Override
    protected void onResume() {

        super.onResume();
    }





    private void setSpinnerAdapter() {
        mSpinner.setPrompt(getString(R.string.sortby));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sorttaskarray, R.layout.spinnertextview);
        adapter.setDropDownViewResource(R.layout.spinnertextview);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)){
                    if(selection.equals("Date")){
                        sortOrder = 1;
                        getLoaderManager().restartLoader(LOADER_TAG,null,MainActivity.this);
                    }else if(selection.equals("Priority")){
                        sortOrder = 2;
                        getLoaderManager().restartLoader(LOADER_TAG,null,MainActivity.this);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortOrder = 0;
            }
        });

    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {TodoListContract.TodoListEntry.ID, TodoListContract.TodoListEntry.COLUMN_TASK_NAME, TodoListContract.TodoListEntry.COLUMN_TIMESTAMP, TodoListContract.TodoListEntry.COLUMN_PRIORITY, TodoListContract.TodoListEntry.COLUMN_TASK_NOTES, TodoListContract.TodoListEntry.COLUMN_TASKSTATUS};



         if(sortOrder == 1){
            return new CursorLoader(this, TodoListContract.TodoListEntry.CONTENT_URI,projection,null,null, TodoListContract.TodoListEntry.COLUMN_TIMESTAMP + " ASC");
        }else if(sortOrder == 2){
            return new CursorLoader(this, TodoListContract.TodoListEntry.CONTENT_URI,projection,null,null, TodoListContract.TodoListEntry.COLUMN_PRIORITY + " DESC");
        }else {
             return new CursorLoader(this, TodoListContract.TodoListEntry.CONTENT_URI,projection,null,null,null);
         }

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
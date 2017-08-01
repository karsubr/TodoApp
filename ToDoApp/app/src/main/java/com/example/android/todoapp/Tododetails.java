package com.example.android.todoapp;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todoapp.data.TodoListContract;

import java.text.DateFormat;
import java.util.Date;

public class Tododetails extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTaskNameTextview;
    private TextView mTaskNotesTextview;
    private TextView mPriorityTextView;
    private TextView mDateTextView;
    private TextView mTaskStatusTextview;
    private Intent intentTodo;
    private Button mEditButton;
    private ImageButton mCloseButton;
    private ImageButton mDeleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tododetails);

        mToolbar = (Toolbar) findViewById(R.id.detailtoolbar);

        mToolbar.setTitle("Task Details");

        mTaskNameTextview = (TextView) findViewById(R.id.textnamedetails);
        mTaskNotesTextview = (TextView) findViewById(R.id.tasknotesdetails);
        mPriorityTextView = (TextView) findViewById(R.id.prioritydetails);
        mDateTextView = (TextView) findViewById(R.id.duedatedetails);
        mTaskStatusTextview = (TextView) findViewById(R.id.taskstatusdetails);
        mCloseButton = (ImageButton) findViewById(R.id.closeButton);
        mDeleteButton = (ImageButton) findViewById(R.id.deletebutton);
        mEditButton = (Button) findViewById(R.id.editButton);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialoginterface = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        delete();
                        finish();
                    }
                };
                showDeleteConfirmation(dialoginterface);
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = intentTodo.getLongExtra("ID",0);
                Intent intent = new Intent(Tododetails.this, DialogFrag.class);
                Uri uri = ContentUris.withAppendedId(TodoListContract.TodoListEntry.CONTENT_URI,id);
                intent.setData(uri);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }

            }
        });

        intentTodo = getIntent();
        updateUI();

    }
    private void showDeleteConfirmation(DialogInterface.OnClickListener dialogOnClickListener){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Sure want to delete the task? ");
        builder.setPositiveButton("DELETE",dialogOnClickListener);
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void delete(){
        long id = intentTodo.getLongExtra("ID",0);
        Uri uri = ContentUris.withAppendedId(TodoListContract.TodoListEntry.CONTENT_URI, id);

        int rowidDelete = getContentResolver().delete(uri,null,null);
        if(rowidDelete == 0){
            Toast.makeText(this,"Task delete unsucessful",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Task Deleted",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        mTaskNameTextview.setText(intentTodo.getStringExtra("taskname"));
        mTaskNotesTextview.setText(intentTodo.getStringExtra("tasknotes"));
        if(intentTodo.getIntExtra("priority",-1) == 2){
            mPriorityTextView.setText("HIGH");
        }else if(intentTodo.getIntExtra("priority", -1) == 1){
            mPriorityTextView.setText("MEDIUM");
        }else if (intentTodo.getIntExtra("priority",-1) == 0){
            mPriorityTextView.setText("LOW");
        }

        if(intentTodo.getIntExtra("taskstatus",-1) == 1){
            mTaskStatusTextview.setText("DONE");
        }else if(intentTodo.getIntExtra("taskstatus", -1) == 0){
            mTaskStatusTextview.setText("TO-DO");
        }

        long dateinmillis = intentTodo.getLongExtra("date",0);

        Date date = new Date(dateinmillis);

        DateFormat simpleDateformat = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        String dateDisplay = simpleDateformat.format(date);

        mDateTextView.setText(dateDisplay);
    }
}

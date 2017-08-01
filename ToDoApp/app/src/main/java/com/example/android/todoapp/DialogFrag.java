package com.example.android.todoapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.todoapp.data.TodoListContract;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rishw on 7/29/2017.
 */

public class DialogFrag extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private Toolbar mToolbar;
    private EditText mTaskNameEditView;
    private EditText mTaskNotesEditView;
    private Button setDateButton;
    private Button setTimeButton;
    private Spinner mPrioritySpinner;
    private Spinner mTaskStatusSpinner;
    private Button saveButton;
    private int priority = 0;
    private int taskStatus = 0;
    public static int year =Calendar.getInstance().get(Calendar.YEAR);
    public static int month = Calendar.getInstance().get(Calendar.MONTH) ;
    public static int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    public static int hour = Calendar.getInstance().get(Calendar.HOUR);
    public static int min = Calendar.getInstance().get(Calendar.MINUTE);

    private Intent intent;
    private  Uri intentUri;
    private boolean mTaskHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mTaskHasChanged = true;
            return false;
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add);
        mToolbar = (Toolbar) findViewById(R.id.edittoolbar);
        mPrioritySpinner = (Spinner)findViewById(R.id.priorityspinner);
        mTaskNotesEditView = (EditText) findViewById(R.id.tasknotes);
        mTaskNameEditView = (EditText) findViewById(R.id.taskname);
        mTaskStatusSpinner = (Spinner) findViewById(R.id.statusspinner);
        setDateButton = (Button) findViewById(R.id.addDateButton);
        setTimeButton = (Button) findViewById(R.id.addTimeButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        mPrioritySpinner.setOnTouchListener(mTouchListener);
        mTaskStatusSpinner.setOnTouchListener(mTouchListener);
        mTaskNameEditView.setOnTouchListener(mTouchListener);
        mTaskNotesEditView.setOnTouchListener(mTouchListener);
        setDateButton.setOnTouchListener(mTouchListener);
        setTimeButton.setOnTouchListener(mTouchListener);

        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)){
                    if(selection.equals("HIGH")){
                        priority = TodoListContract.TodoListEntry.HIGH_PRIORITY;

                    }else if(selection.equals("MEDIUM")){
                        priority = TodoListContract.TodoListEntry.MEDIUM_PRIORITY;
                    }else if(selection.equals("LOW")){
                        priority = TodoListContract.TodoListEntry.LOW_PRIORITY;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priority = 0;
            }
        });

        mTaskStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)){
                    if(selection.equals("To-Do")){
                        taskStatus = 0;
                    }else if(selection.equals("Done")){
                        taskStatus = 1;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                taskStatus = 0;
            }
        });

        setDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(),"DatePicker");
            }
        });

        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getSupportFragmentManager(),"Time Picker");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intentUri == null) {
                    insertData();
                }else{
                    updateData();
                }
                Intent intent = new Intent(DialogFrag.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        intent = getIntent();
        intentUri = intent.getData();
        //Log.d("Uri", intentUri.toString());
        if(intentUri == null){
            mToolbar.setTitle("Add a Task");
            Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
            day= calendar.get(Calendar.DAY_OF_MONTH);
            min = calendar.get(Calendar.MINUTE);
        }else{
            mToolbar.setTitle("Edit Task");
        }

        if(intentUri != null){
            setDateButton.setText("Edit date");
            setTimeButton.setText("Edit time");
        }else{
            setDateButton.setText("Add date");
            setTimeButton.setText("Add time");
        }

        getLoaderManager().initLoader(MainActivity.LOADER_TAG,null,this);


    }

    @Override
    public void onBackPressed() {
        if(!mTaskHasChanged) {
            super.onBackPressed();
        }
        DialogInterface.OnClickListener discardButtonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DialogFrag.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        };

        showUnsaveChanges(discardButtonListener);
    }

    private void showUnsaveChanges(DialogInterface.OnClickListener discardButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("DISCARD",discardButtonListener);
        builder.setNegativeButton("KEEP EDITING", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TodoListContract.TodoListEntry.COLUMN_TASK_NAME, TodoListContract.TodoListEntry.COLUMN_TASK_NOTES, TodoListContract.TodoListEntry.COLUMN_TIMESTAMP, TodoListContract.TodoListEntry.COLUMN_PRIORITY, TodoListContract.TodoListEntry.COLUMN_TASKSTATUS
        };

        if(intentUri != null){
            return new CursorLoader(this,intentUri,projection,null,null,null);
        }else{
            return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null || data.getCount() < 1){
            return;
        }

        if(data.moveToFirst()){
            String taskName = data.getString(data.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TASK_NAME));
            String taskNotes = data.getString(data.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TASK_NOTES));
            int priority = data.getInt(data.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_PRIORITY));
            int taskstatus = data.getInt(data.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TASKSTATUS));
            long dateinMillis = data.getLong(data.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TIMESTAMP));

            mTaskNameEditView.setText(taskName);
            mTaskNotesEditView.setText(taskNotes);
            switch (priority){
                case 0:
                    mPrioritySpinner.setSelection(2);
                    break;
                case 1:
                    mPrioritySpinner.setSelection(1);
                    break;
                case 2:
                    mPrioritySpinner.setSelection(0);
                    break;
            }

            switch (taskstatus){
                case 0:
                    mTaskStatusSpinner.setSelection(0);
                    break;
                case 1:
                    mTaskStatusSpinner.setSelection(1);
                    break;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateinMillis);
            hour = calendar.get(Calendar.HOUR);
            min = calendar.get(Calendar.MINUTE);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            mTaskNameEditView.setText("");
            mTaskStatusSpinner.setSelection(0);
            mPrioritySpinner.setSelection(0);
            mTaskNotesEditView.setText("");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            /*final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);*/

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, min,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            DialogFrag.hour = hourOfDay;
            DialogFrag.min = minute;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            /*final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);*/
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            //dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return  dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            DialogFrag.year = year;
            DialogFrag.month = month;
            DialogFrag.day = day;
        }
    }






    private Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void insertData(){
        Editable name = mTaskNameEditView.getText();
        String nameString = name.toString();
        Editable taskNotes = mTaskNotesEditView.getText();
        String taskNotesString = taskNotes.toString();
        ContentValues cv = new ContentValues();
        cv.put(TodoListContract.TodoListEntry.COLUMN_TASK_NAME,nameString);
        cv.put(TodoListContract.TodoListEntry.COLUMN_TASK_NOTES,taskNotesString);
        cv.put(TodoListContract.TodoListEntry.COLUMN_PRIORITY,priority);
        Date date = getDate(year,month,day);
        cv.put(TodoListContract.TodoListEntry.COLUMN_TIMESTAMP,date.getTime());
        cv.put(TodoListContract.TodoListEntry.COLUMN_TASKSTATUS,taskStatus);
        Uri uri = getContentResolver().insert(TodoListContract.TodoListEntry.CONTENT_URI,cv);
        if(uri == null){
            Toast.makeText(this,"Unable to save",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Task Saved",Toast.LENGTH_SHORT).show();
        }

    }

    private void updateData(){
        Editable name = mTaskNameEditView.getText();
        String nameString = name.toString();
        Editable taskNotes = mTaskNotesEditView.getText();
        String taskNotesString = taskNotes.toString();
        ContentValues cv = new ContentValues();
        cv.put(TodoListContract.TodoListEntry.COLUMN_TASK_NAME,nameString);
        cv.put(TodoListContract.TodoListEntry.COLUMN_TASK_NOTES,taskNotesString);
        cv.put(TodoListContract.TodoListEntry.COLUMN_PRIORITY,priority);
        Date date = getDate(year,month,day);
        cv.put(TodoListContract.TodoListEntry.COLUMN_TIMESTAMP,date.getTime());
        cv.put(TodoListContract.TodoListEntry.COLUMN_TASKSTATUS,taskStatus);
        int rowId = getContentResolver().update(intentUri,cv,null,null);
        if(rowId == 0){
            Toast.makeText(this,"Unable to update",Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(this,"Successfuly updated",Toast.LENGTH_SHORT).show();
        }
    }
}

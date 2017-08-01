package com.example.android.todoapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.todoapp.data.TodoListContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rishw on 7/28/2017.
 */

public class ToDoListAdapter extends CursorAdapter{



    public ToDoListAdapter(Context mContext, Cursor mCursor) {
       super(mContext,mCursor,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.todolist,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mTaskNameTextView = (TextView) view.findViewById(R.id.todoName);
        TextView mDateTextView = (TextView) view.findViewById(R.id.date);
        TextView mPriorityTextView = (TextView) view.findViewById(R.id.priority);

        long timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TIMESTAMP));

        String taskName = cursor.getString(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_TASK_NAME));
        Date date = new Date(timeStamp);
        long id = cursor.getInt(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.ID));
        int priority = cursor.getInt(cursor.getColumnIndexOrThrow(TodoListContract.TodoListEntry.COLUMN_PRIORITY));
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        String dateDisplay = df.format(date);



        mDateTextView.setText(dateDisplay);
        mTaskNameTextView.setText(taskName);



        switch (priority){
            case 0:
                mPriorityTextView.setText("LOW");
                mPriorityTextView.setBackgroundColor(Color.parseColor("#db2020"));
                break;
            case 1:
                mPriorityTextView.setText("MEDIUM");
                mPriorityTextView.setBackgroundColor(Color.parseColor("#e2850b"));
                break;
            case 2:
                mPriorityTextView.setText("HIGH");
                mPriorityTextView.setBackgroundColor(Color.parseColor("#149e32"));
        }


    }



    /*private Context mContext;
    private Cursor mCursor;

    public ToDoListAdapter(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.todolist,parent,false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }

        String todoName = mCursor.getString(mCursor.getColumnIndex(TodoListContract.TodoListEntry.COLUMN_TASK_NAME));
        String taskNotes = mCursor.getString(mCursor.getColumnIndex(TodoListContract.TodoListEntry.COLUMN_TASK_NOTES));
        int priority = mCursor.getInt(mCursor.getColumnIndex(TodoListContract.TodoListEntry.COLUMN_PRIORITY));
        long timeStamp = mCursor.getInt(mCursor.getColumnIndex(TodoListContract.TodoListEntry.COLUMN_TIMESTAMP));

        Date date = new Date(timeStamp);
        long id = mCursor.getInt(mCursor.getColumnIndex(TodoListContract.TodoListEntry._ID));

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        String dateDisplay = df.format(date);

        holder.nameTextView.setText(todoName);
        switch (priority){
            case 0:
                holder.priorityTextView.setText("LOW");
                holder.priorityTextView.setBackgroundColor(Color.parseColor("#db2020"));
                break;
            case 1:
                holder.priorityTextView.setText("MEDIUM");
                holder.priorityTextView.setBackgroundColor(Color.parseColor("#e2850b"));
                break;
            case 2:
                holder.priorityTextView.setText("HIGH");
                holder.priorityTextView.setBackgroundColor(Color.parseColor("#149e32"));
        }

        if(dateDisplay != null){
            holder.dateTextView.setText(dateDisplay);

        }else{
            holder.dateTextView.setText("");
        }


    }

    public void swapCursor(Cursor newCursor){
        if(mCursor != null) mCursor.close();
        mCursor = newCursor;
        if(newCursor != null){
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        TextView priorityTextView;
        TextView dateTextView;
        public TodoViewHolder(View itemView){
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.todoName);
            priorityTextView = (TextView) itemView.findViewById(R.id.priority);
            dateTextView = (TextView) itemView.findViewById(R.id.date);
        }
    }*/

}

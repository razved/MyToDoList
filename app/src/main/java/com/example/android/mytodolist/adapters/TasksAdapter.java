package com.example.android.mytodolist.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.mytodolist.ItemClickListener;
import com.example.android.mytodolist.R;
import com.example.android.mytodolist.TaskEditorActivity;
import com.example.android.mytodolist.Utils;
import com.example.android.mytodolist.data.TaskContract.TaskEntry;

import java.net.Inet4Address;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private static final String LOG_TAG = "TaskAdapter.java";
    Cursor cursor = null;
    Context context;

    public TasksAdapter(Context context) {
        this.context = context;

    }
    // Курсор из которого будем брать инфу для наполнения информацией
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.task_name_textbox) TextView taskName;
        @BindView(R.id.task_desc_textbox) TextView taskDesc;
        @BindView(R.id.task_checkbox) CheckBox taskCheckbox;
        @BindView(R.id.task_item_view) CardView taskItemView;

        private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }
        // Пишем в данные из курсора во вью
        public void bindModel(Cursor cursor) {
            String taskNameString = cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_TASK_NAME));
            String taskDescString = cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_TASK_DESC));
            int taskType = cursor.getInt(cursor.getColumnIndex(TaskEntry.COLUMN_TASK_TYPE));

            taskName.setText(taskNameString);
            taskDesc.setText(taskDescString);

            taskItemView.setBackgroundColor(Utils.getTypeColor(context, taskType));

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor-adapter
        View v = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        this.cursor.moveToPosition(position);
        holder.bindModel(this.cursor);
        // узнаем Id текущего пункта (task)
        final int currentItemId = this.cursor.getInt(cursor.getColumnIndex(TaskEntry._ID));
        // обрабатываем нажатие на пункт
        // если кликнули по этому пункту - вызываем активити редактирования
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Log.i(LOG_TAG, "onClick position: " + position + " isLong: " + isLongClick + "" +
                        " ID: " + currentItemId);
                Intent intent = new Intent(context, TaskEditorActivity.class);
                // Передаем в Активити редактора Uri с id кликнутого пункта
                intent.setData(ContentUris.withAppendedId(TaskEntry.CONTENT_URI, currentItemId));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        } else  {
            return cursor.getCount();
        }
    }

}

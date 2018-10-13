package com.example.android.mytodolist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.mytodolist.R;
import com.example.android.mytodolist.Utils;
import com.example.android.mytodolist.data.TaskContract.TaskEntry;

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.task_name_textbox) TextView taskName;
        @BindView(R.id.task_desc_textbox) TextView taskDesc;
        @BindView(R.id.task_checkbox) CheckBox taskCheckbox;
        @BindView(R.id.task_item_view) LinearLayout taskItemView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

package com.example.android.mytodolist.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.mytodolist.ItemClickListener;
import com.example.android.mytodolist.R;
import com.example.android.mytodolist.TasksListActivity;
import com.example.android.mytodolist.Utils;
import com.example.android.mytodolist.data.TaskContract;
import com.example.android.mytodolist.data.TaskContract.TaskTypeEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    public static final String LOG_TAG = "TypeAdaper.java";
    Cursor cursor = null;
    Context context;

    public TypeAdapter(Context context) {
        this.context = context;
    }


    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    private int getTasksCount(int typeId) {
        Uri uri = TaskContract.TaskEntry.CONTENT_URI;
        // Условия для запроса, выбрать все у кого тип typeId
        String selection = TaskContract.TaskEntry.COLUMN_TASK_TYPE + "=?";
        String[] selectionArgs = new String[]{String.valueOf(typeId)};
        Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs,
                null);
        return cursor.getCount();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.type_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        this.cursor.moveToPosition(position);
        viewHolder.bindModel(this.cursor);
        // Вычисляем текущий тип, чтобы обработать его если кликнули по этому пункту
        final int typeId = this.cursor.getInt(this.cursor.getColumnIndex(TaskTypeEntry._ID));
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                Log.i(LOG_TAG, "Click id " + typeId);
                Intent intent = new Intent(context, TasksListActivity.class);
                // передаем в новый активити данные (Uri), какой именно тип мы кликнули
                intent.setData(ContentUris.withAppendedId(TaskTypeEntry.CONTENT_URI, typeId));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        } else {
            return cursor.getCount();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @BindView(R.id.type_name)
        TextView typeName;
        @BindView(R.id.type_number_tasks)
        TextView numberTasks;
        @BindView(R.id.type_item_view)
        LinearLayout typeItemView;
        int typeId;
        private ItemClickListener itemClickListener;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
//            itemView.setOnClickListener(this);
        }

        // Пишем в данные из курсора во вью
        public void bindModel(Cursor cursor) {
            String typeNameString = cursor.getString(cursor.getColumnIndex(TaskTypeEntry.COLUMN_TYPE_NAME));
            typeId = cursor.getInt(cursor.getColumnIndex(TaskTypeEntry._ID));
            int tasksCount = getTasksCount(typeId);

            typeName.setText(typeNameString);
            numberTasks.setText(String.valueOf(tasksCount));

            typeItemView.setBackgroundColor(Utils.getTypeColor(context, typeId));
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

}

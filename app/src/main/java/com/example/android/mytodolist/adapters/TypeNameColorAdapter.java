package com.example.android.mytodolist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.mytodolist.R;
import com.example.android.mytodolist.Utils;
import com.example.android.mytodolist.data.TaskContract;

import butterknife.BindView;

public class TypeNameColorAdapter extends CursorAdapter {

    /**
     * Конструктор для класса
     * @param context контекст приложения
     * @param cursor курсор из которого берем данные
     */
    public TypeNameColorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * Создаем новый список
     * @param context контекст приложения
     * @param cursor курсор
     * @param viewGroup родительское вью
     * @return возвращаем вновь созданный вью
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.spinner_item_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvTypeName = (TextView) view.findViewById(R.id.textview_spinner_color);
        String typeName = cursor.getString(cursor.getColumnIndex(TaskContract.TaskTypeEntry.COLUMN_TYPE_NAME));
        int typeColor = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskTypeEntry.COLUMN_TYPE_COLOR));
        int color = Utils.getColorById(context, typeColor);

        tvTypeName.setText(typeName);
        tvTypeName.setBackgroundColor(color);
    }


}

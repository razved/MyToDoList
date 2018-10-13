package com.example.android.mytodolist;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.example.android.mytodolist.data.TaskContract.TaskTypeEntry;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public Utils() {}

    public static Integer[] colorIds = {
            TaskTypeEntry.TYPE_COLOR_WHITE,
            TaskTypeEntry.TYPE_COLOR_RED,
            TaskTypeEntry.TYPE_COLOR_PINK,
            TaskTypeEntry.TYPE_COLOR_BLUE,
            TaskTypeEntry.TYPE_COLOR_GREEN,
            TaskTypeEntry.TYPE_COLOR_YELLOW,
            TaskTypeEntry.TYPE_COLOR_ORANGE,
            TaskTypeEntry.TYPE_COLOR_BROWN
    };

    public static List<Integer> getListColors() {
        List<Integer> newList = new ArrayList<Integer>();
        for (int i = 0; i < colorIds.length; i++) {
            newList.add(colorIds[i]);
        }
        return newList;
    }

    /**
     * Возвращает цвет по Айди типа
     * @param context контекст приложения, чтобы выдернуть цвет из ресурса
     * @param typeId айди типа, цвет которого хотим вернуть
     * @return цвет типа
     */
    public static int getTypeColor (Context context, int typeId ) {
        int colorCode = getColorCode(context, typeId);
        return getColorById(context, colorCode);
    }

    /**
     * Функция по коду цвета возвращает его значение из ресурса
     * @param context контекст приложения, чтобы вынуть цвет из ресурса
     * @param colorId Айди цвета
     * @return возвращает цвет из ресурса
     */
    public static int getColorById(Context context, int colorId) {
        switch (colorId) {
            case TaskTypeEntry.TYPE_COLOR_WHITE:
                return context.getResources().getColor(R.color.type_background_white);
            case TaskTypeEntry.TYPE_COLOR_RED:
                return context.getResources().getColor(R.color.type_background_red);
            case TaskTypeEntry.TYPE_COLOR_PINK:
                return context.getResources().getColor(R.color.type_background_pink);
            case TaskTypeEntry.TYPE_COLOR_BLUE:
                return context.getResources().getColor(R.color.type_background_blue);
            case TaskTypeEntry.TYPE_COLOR_GREEN:
                return context.getResources().getColor(R.color.type_background_green);
            case TaskTypeEntry.TYPE_COLOR_YELLOW:
                return context.getResources().getColor(R.color.type_background_yellow);
            case TaskTypeEntry.TYPE_COLOR_ORANGE:
                return context.getResources().getColor(R.color.type_background_orange);
            case TaskTypeEntry.TYPE_COLOR_BROWN:
                return context.getResources().getColor(R.color.type_background_brown);
            default:
                return context.getResources().getColor(R.color.type_background_white);
        }
    }

    /**
     * Функция делает запрос в БД чтобы определить Айди цвета по Айди типа
     * @param context контекст, нужен чтобы сделать запрос к БД
     * @param typeId Айди типа, цвет которого мы хотим вернуть
     * @return
     */


    public static int getColorCode(Context context, int typeId) {
        Uri uri = ContentUris.withAppendedId(TaskTypeEntry.CONTENT_URI, typeId);
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int typeColor = cursor.getInt(cursor.getColumnIndex(TaskTypeEntry.COLUMN_TYPE_COLOR));
//            Log.i(LOG_TAG, "getColorCode - typeId " + typeId + ": Color " + typeColor);
            return typeColor;
        } else {
            return 0;
        }
    }

}

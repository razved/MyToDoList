package com.example.android.mytodolist.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;

import com.example.android.mytodolist.R;

public class TaskContract {

    private TaskContract() {}

    /** Content authority URI to access data in the provider */
    public static final String CONTENT_AUTHORITY = "com.example.android.mytodolist";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TASKS = "tasks";
    public static final String PATH_TYPES = "types";

    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        /**
         * Column names for SQLite table
         */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TASK_NAME = "task_name";
        public static final String COLUMN_TASK_DESC = "task_desc";
        public static final String COLUMN_TASK_TYPE = "task_type";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASKS);

        /**  The MIME type of the {@link #CONTENT_URI} for a list of tasks  */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;

        /** The MIME type of the {@link #CONTENT_URI} for a single task */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TASKS;
    }

    public static abstract class TaskTypeEntry implements BaseColumns {
        public static final String TABLE_NAME = "types";
        /**
         * Column names for SQLite table
         */
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TYPE_NAME = "type_name";
        public static final String COLUMN_TYPE_COLOR = "type_color";

        public static final int TYPE_COLOR_WHITE = 0;
        public static final int TYPE_COLOR_RED = 1;
        public static final int TYPE_COLOR_PINK = 2;
        public static final int TYPE_COLOR_BLUE = 3;
        public static final int TYPE_COLOR_GREEN = 4;
        public static final int TYPE_COLOR_YELLOW = 5;
        public static final int TYPE_COLOR_ORANGE = 6;
        public static final int TYPE_COLOR_BROWN = 7;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TYPES);

        /**  The MIME type of the {@link #CONTENT_URI} for a list of task types  */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TYPES;
        /**  The MIME type of the {@link #CONTENT_URI} for a single task type  */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TYPES;

        public static int getTypeColor (int colorCode) {
            switch (colorCode) {
                case TYPE_COLOR_WHITE:
                    return Resources.getSystem().getColor(R.color.type_background_white);
                case TYPE_COLOR_RED:
                    return Resources.getSystem().getColor(R.color.type_background_red);
                case TYPE_COLOR_PINK:
                    return Resources.getSystem().getColor(R.color.type_background_pink);
                case TYPE_COLOR_BLUE:
                    return Resources.getSystem().getColor(R.color.type_background_blue);
                case TYPE_COLOR_GREEN:
                    return Resources.getSystem().getColor(R.color.type_background_green);
                case TYPE_COLOR_YELLOW:
                    return Resources.getSystem().getColor(R.color.type_background_yellow);
                case TYPE_COLOR_ORANGE:
                    return Resources.getSystem().getColor(R.color.type_background_orange);
                case TYPE_COLOR_BROWN:
                    return Resources.getSystem().getColor(R.color.type_background_brown);
                default:
                    return Resources.getSystem().getColor(R.color.type_background_white);
            }
        }

    }

}

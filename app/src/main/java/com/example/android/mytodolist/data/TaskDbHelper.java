package com.example.android.mytodolist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.mytodolist.data.TaskContract.TaskTypeEntry;
public class TaskDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tasks.db";
    private static final String SQL_CREATE_TASKS_TABLE =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
            TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TaskContract.TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, " +
            TaskContract.TaskEntry.COLUMN_TASK_DESC + " TEXT, " +
            TaskContract.TaskEntry.COLUMN_TASK_TYPE + " INTEGER NOT NULL DEFAULT 0, " +
                    " FOREIGN KEY (" + TaskContract.TaskEntry.COLUMN_TASK_TYPE + ") REFERENCES " +
                    TaskTypeEntry.TABLE_NAME + "(" + TaskTypeEntry._ID + ")" +
            ");";
    // Foreigh key создает привязку между двумя таблицами
    private static final String SQL_CREATE_TYPE_TABLE =
            "CREATE TABLE " + TaskTypeEntry.TABLE_NAME + " (" +
            TaskTypeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TaskContract.TaskTypeEntry.COLUMN_TYPE_NAME + " TEXT NOT NULL, " +
            TaskTypeEntry.COLUMN_TYPE_COLOR + " INTEGER DEFAULT 0" +
            ");";
    // Так как у нас существует жесткая привязка к типам, создадим дефотный
    // тип с id 1 в таблице и со значением ALL
    private static final String SQL_ADD_DEFAULT_TYPE = "INSERT INTO " + TaskTypeEntry.TABLE_NAME +
            "(" + TaskTypeEntry.COLUMN_TYPE_NAME + ", " + TaskTypeEntry.COLUMN_TYPE_COLOR + ")" +
            " VALUES (\"My tasks\", " + TaskTypeEntry.TYPE_COLOR_WHITE + ");";


    private static final String SQL_DELETE_TASKS = "DROP TABLE IF EXISTS " +
            TaskContract.TaskEntry.TABLE_NAME;
    private static final String SQL_DELETE_TYPES = "DROP TABLE IF EXISTS " +
            TaskContract.TaskTypeEntry.TABLE_NAME;

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TASKS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TYPE_TABLE);
        // Добавим значение по умолнчаю для типов ( с id 1 и цветом белым)
        sqLiteDatabase.execSQL(SQL_ADD_DEFAULT_TYPE);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_TASKS);
        sqLiteDatabase.execSQL(SQL_DELETE_TYPES);
        onCreate(sqLiteDatabase);
    }
}

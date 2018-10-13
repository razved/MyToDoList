package com.example.android.mytodolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.mytodolist.R;
import com.example.android.mytodolist.data.TaskContract.TaskEntry;
import com.example.android.mytodolist.data.TaskContract.TaskTypeEntry;

public class TaskProvider extends ContentProvider {

    /** URI matcher code for the content URI for the tasks table */
    private static final int TASKS = 100;
    /** URI matcher code for the content URI for a single task in the tasks table*/
    private static final int TASK_ID = 101;
    /** URI matcher code for the content URI for the type table */
    private static final int TYPES = 200;
    /** URI matcher code for the content URI for a single type in the type table*/
    private static final int TYPE_ID = 201;

    /** Tag for the log messages */
    private static final String LOG_TAG = "TaskProvider.java";

    /** Database helper object */
    private TaskDbHelper dbHelper;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_ID);
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TYPES, TYPES);
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TYPES + "/#", TYPE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TaskDbHelper(getContext());
        return true;
    }

    /**
     * Проводим запрос по тому URI что у нас есть
     * @param uri УРИ для запроса
     * @param projection Проекция (поля таблицы бд которые нам нужны)
     * @param selection Поля по которым првоеряем условие выборки из бд
     * @param selectionArgs Условия для выборки по полям указанным в selection
     * @param sortOrder Как сортировать
     * @return Cursor с резулаьтатами запроса
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                // Если операция со всеми задачами, не выясняем условия выборки, просто делаем запрос
                cursor = database.query(TaskEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TASK_ID:
                // Если надо выбрать конкретную задачу
                // URI имеет вид: "content://com.example.android.mytodolist/tasks/3",
                // Запрос в переменной selection условие запроса вида "_id=?" и в selectionArgs условия выборки (ID)
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Делаем запрос с этим условием
                cursor = database.query(TaskEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TYPES:
                // Если операция со всеми типами задач (группой)
                cursor = database.query(TaskContract.TaskTypeEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TYPE_ID:
                // Если операция с конкретным типом (группой) по id
                selection = TaskTypeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Делаем запрос
                cursor = database.query(TaskContract.TaskTypeEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TaskEntry.CONTENT_LIST_TYPE;
            case TASK_ID:
                return TaskEntry.CONTENT_ITEM_TYPE;
            case TYPES:
                return TaskTypeEntry.CONTENT_LIST_TYPE;
            case TYPE_ID:
                return TaskContract.TaskTypeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return insertTask(uri, contentValues);
            case TYPES:
                return insertType(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);
        }
    }

    /**
     * insert new Task into the database with the given content values. Return the new content URI
     * for that specific row in the database
     */

    private Uri insertTask(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Sanity check of data
        //Check the header is not null
        String taskName = values.getAsString(TaskEntry.COLUMN_TASK_NAME);
        if (taskName == null) {
            throw new IllegalArgumentException("Task requires a name");
        }
        Integer taskType = values.getAsInteger(TaskEntry.COLUMN_TASK_TYPE);
        if (taskType == null || taskType < 0 ) {
            throw new IllegalArgumentException("Task type must be positive");
        }

        long id = db.insert(TaskEntry.TABLE_NAME, null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        if (id == -1) {
            Toast.makeText(getContext(), R.string.error_saving_new_task, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error with insert new Task for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Так как при добавлении новой задачи нам надо так же обновить и список типов
        // например количество задач в каждом из типов, то надо дернуть и URI типов
        getContext().getContentResolver().notifyChange(TaskTypeEntry.CONTENT_URI, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * insert new Task into the database with the given content values. Return the new content URI
     * for that specific row in the database
     */

    private Uri insertType(Uri uri, ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //Sanity check of data
        //Check the header is not null
        String typeName = values.getAsString(TaskTypeEntry.COLUMN_TYPE_NAME);
        if (typeName == null) {
            throw new IllegalArgumentException("Type requires a name");
        }
        Integer typeColor = values.getAsInteger(TaskTypeEntry.COLUMN_TYPE_COLOR);
        if (typeColor == null || typeColor < 0 ) {
            throw new IllegalArgumentException("Task color must be positive");
        }

        long id = database.insert(TaskTypeEntry.TABLE_NAME, null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        if (id == -1) {
            Toast.makeText(getContext(), R.string.error_saving_new_type, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error with insert new Task Type for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }



    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Get writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        //Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                // Delete a single row given by the ID in the URI
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TYPES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TaskContract.TaskTypeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TYPE_ID:
                // Удаляем те записи что удовлетворяют условиям (конкретный id который надо удалить
                // вычсиляем из URI)
                selection = TaskTypeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(TaskContract.TaskTypeEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TASK_ID:
                // извлекаем id из URI и пихаем туда то что лежит в contentValues
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TYPES:
                return updateType(uri, contentValues, selection, selectionArgs);
            case TYPE_ID:
                // извлекаем id из URI и пихаем туда то что лежит в contentValues
                selection = TaskContract.TaskTypeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateType(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Изменяем данные задачи, возвращаем количество измененных строк в таблице
     */

    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Если ключ {@link TaskEntry#COLUMN_TASK_NAME} присутствиует, проверяем что он не пустой
        if (values.containsKey(TaskEntry.COLUMN_TASK_NAME)) {
            String taskName = values.getAsString(TaskEntry.COLUMN_TASK_NAME);
            if (taskName == null) {
                throw new IllegalArgumentException("Task requires a name");
            }
        }

        // Если ключ {@link TaskEntry#COLUMN_TASK_TYPE} есть и он не отрицалтельный и не пустой
        if (values.containsKey(TaskEntry.COLUMN_TASK_TYPE)) {
            Integer taskType = values.getAsInteger(TaskEntry.COLUMN_TASK_TYPE);
            if (taskType == null || taskType < 0) {
                throw new IllegalArgumentException("Task requires valid Type");
            }
        }

        // Не проверяем {@link TaskEntry#COLUMN_TASK_DESC} так как оно может быть пустым
        // Если нечего апдэйтить просто возвращаем 0
        if (values.size() == 0) {
            return 0;
        }
        // А если есть то хреначим всё в базу данных
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Сохраняем количество измененных строк таблицы в переменную
        int rowsUpdated = db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
        // Проверяем есть ли измененные данные и даём об этом знать листенерам
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Изменяем данные типа
     * @return количество измененных строк в таблице
     */

    private int updateType(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Если ключ {@link TaskTypeEntry#COLUMN_TYPE_NAME} существует, проверяем что он не пустой
        if (values.containsKey(TaskTypeEntry.COLUMN_TYPE_NAME)) {
            String typeName = values.getAsString(TaskContract.TaskTypeEntry.COLUMN_TYPE_NAME);
            if (typeName == null) {
                throw new IllegalArgumentException("Type requires a name");
            }
        }
        // Если ключ {@link TaskTypeEntry#COLUMN_TYPE_COLOR} существует, проверяем что он не пустой
        // и не отрициательный

        if (values.containsKey(TaskContract.TaskTypeEntry.COLUMN_TYPE_COLOR)) {
            Integer typeColor = values.getAsInteger(TaskContract.TaskTypeEntry.COLUMN_TYPE_COLOR);
            if (typeColor == null || typeColor <0 ) {
                throw new IllegalArgumentException("Type color should be positive");
            }
        }
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Сохраняем количество измененных строк таблицы в переменную
        int rowsUpdated = db.update(TaskTypeEntry.TABLE_NAME, values, selection, selectionArgs);
        // Проверяем есть ли измененные данные и даём об этом знать листенерам
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}

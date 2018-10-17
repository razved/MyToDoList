package com.example.android.mytodolist;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.mytodolist.adapters.TypeNameColorAdapter;
import com.example.android.mytodolist.data.TaskContract.TaskTypeEntry;
import com.example.android.mytodolist.data.TaskContract.TaskEntry;
import com.example.android.mytodolist.data.TaskProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "TaskEditorActivity.java";
    // флаг были ли изменены данные
    private boolean taskHasChanged = false;

    // тач листенер, чтобы если пользователь тапнул по любому вью мы считали что он изменил данные
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            taskHasChanged = true;
            return false;
        }
    };

    // Если пользователь редактирует, а не создает новую задачу, в этом Uri будет храниться ссылка
    // на редактируемый таск, а если создает новый то будет null;
    Uri taskItemUri;
    // Значение типа, по дефолту 1, т.к. нумерация ID в БД начинается с 1
    private int taskType = 1;
    private static final int TYPE_LOADER = 1;
    private static final int TASK_LOADER = 2;

    // UI элементы активити
    @BindView(R.id.et_task_name) EditText etTaskName;
    @BindView(R.id.et_desctiption) EditText etDescription;
    @BindView(R.id.spinner_type) Spinner spinnerType;

    private Loader<Cursor> loaderTask;
    private Loader<Cursor> loaderType;

    TypeNameColorAdapter typeSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);
        ButterKnife.bind(this);

        etTaskName.setOnTouchListener(touchListener);
        etDescription.setOnTouchListener(touchListener);

        // настраиваем спиннер
        setupSpinner();

        taskItemUri = getIntent().getData();
        // если в переданных во вюь данных есть Uri то отправляем лоадер за данынми из БД
        if (taskItemUri == null) {
            setTitle(R.string.add_task);
            invalidateOptionsMenu();
            // если эта активити открыта из списка задач конкретного типа, то выясняем что это за тип
            // и устанавливаем спинер на этот тип сразу
            long typeId = getIntent().getLongExtra(Constants.TYPE_ID, -1);

            if (typeId != -1) {
                taskType = (int) typeId;
                Log.i(LOG_TAG, "FROM TaskListAcitvity type id: " + String.valueOf(taskType));
            }
        } else {
            setTitle(R.string.edit_task);
            //todo  раскоментировать следующую строку когда сделаю свайп с опцией редактирования
            Log.i(LOG_TAG, "onCreate - Data URI: " + taskItemUri);
            loaderTask = getSupportLoaderManager().initLoader(TASK_LOADER, null, this);
        }

       loaderType = getSupportLoaderManager().initLoader(TYPE_LOADER, null, this);
    }

    private void setupSpinner() {
        typeSpinnerAdapter = new TypeNameColorAdapter(this, null);
        spinnerType.setAdapter(typeSpinnerAdapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                taskType = position + 1;
                Log.i(LOG_TAG, "Spinner onItemSelected: selected " + position + " item");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                taskType = 1;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // пользователь выбрал пункт меню
        switch (item.getItemId()) {
            case R.id.action_task_save:
                saveTask();
                finish();
                return true;
            case R.id.action_task_delete:
                //todo херачим код
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (taskItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_task_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void saveTask() {
        ContentValues values = new ContentValues();
        // считываем данные из полей ввода активити
        String taskName = etTaskName.getText().toString().trim();
        if (TextUtils.isEmpty(taskName)) {
            Toast.makeText(this, R.string.wrong_task_name_error, Toast.LENGTH_SHORT).show();
            return;
        }
        String taskDesc = etDescription.getText().toString().trim();
        values.put(TaskEntry.COLUMN_TASK_NAME, taskName);
        values.put(TaskEntry.COLUMN_TASK_DESC, taskDesc);
        values.put(TaskEntry.COLUMN_TASK_TYPE, taskType);
        // если Uri не null занчит изменяем БД, если null то добавляем новый таск
        if (taskItemUri == null) {
            Uri uri = getContentResolver().insert(TaskEntry.CONTENT_URI, values);
            // возвращается uri добавленной в БД записи, либо null в случае ошибки
            if (uri == null) {
                Toast.makeText(this, R.string.adding_task_failed_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
            }
        } else {
            // если существует такая запись в БД Uri не null
            int rowsUpdated = getContentResolver().update(taskItemUri, values, null, null);
            // update возвращает количество измененных пунктов в БД или 0 если ничего не было изменено
            if (rowsUpdated == 0) {
                Toast.makeText(this, R.string.task_editing_failed_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, rowsUpdated + getString(R.string.count_items_updated), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {

        if (id == TYPE_LOADER) {
            String[] projection = {
                    TaskTypeEntry._ID,
                    TaskTypeEntry.COLUMN_TYPE_NAME,
                    TaskTypeEntry.COLUMN_TYPE_COLOR
            };
            return new CursorLoader(this, TaskTypeEntry.CONTENT_URI, projection, null, null, null);
        } else if (id == TASK_LOADER) {
            String[] projection = {
                    TaskEntry._ID,
                    TaskEntry.COLUMN_TASK_NAME,
                    TaskEntry.COLUMN_TASK_DESC,
                    TaskEntry.COLUMN_TASK_TYPE
            };
            if (taskItemUri == null) {
                return null;
            }
            return new CursorLoader(this, taskItemUri, projection, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == TYPE_LOADER) {
            typeSpinnerAdapter.swapCursor(cursor);
            spinnerType.setSelection(taskType - 1);
            // если мы открыли этот активити из какого-то конкретного типа, сразу
            // устанавливаем спинер на этот группу по умолчанию
            // делаем эту установку после лоадера, потому что лоадер только загрузил все типы

        }
        if (loader.getId() == TASK_LOADER) {
            // Если открыли редактирование уже существующего таска, то
            if (cursor.moveToFirst()) {
                // заполняем все вью данными из курсора
                int indexTaskName = cursor.getColumnIndex(TaskEntry.COLUMN_TASK_NAME);
                Log.i(LOG_TAG, "onLoadFinished - indexTaskName: " + indexTaskName);
                String currentTaskName = cursor.getString(indexTaskName);
                Log.i(LOG_TAG, "onLoadFinished - currentTaskName: " + currentTaskName);
                String currentTaskDesc = cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_TASK_DESC));
                int currentTaskType = cursor.getInt(cursor.getColumnIndex(TaskEntry.COLUMN_TASK_TYPE));

                etTaskName.setText(currentTaskName);
                etDescription.setText(currentTaskDesc);
                spinnerType.setSelection(currentTaskType - 1);
            }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == TYPE_LOADER) {
            typeSpinnerAdapter.swapCursor(null);
            spinnerType.setSelection(taskType - 1);
        }
        if (loader.getId() == TASK_LOADER) {
            etTaskName.setText("");
            etDescription.setText("");
            spinnerType.setSelection(0);
        }
    }
}

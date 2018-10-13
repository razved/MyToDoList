package com.example.android.mytodolist;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.mytodolist.adapters.TasksAdapter;
import com.example.android.mytodolist.data.TaskContract;
import com.example.android.mytodolist.data.TaskContract.TaskEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "TasksListActivity.java";

    @BindView(R.id.task_list) RecyclerView taskListRV;
    @BindView(R.id.fab_parent) FloatingActionButton fab;
    // нужно для загрузки task из базы данных через loaderManager
    private static final int TASK_LOADER = 0;
    // нужно для загрузки tasks types через loaderManager
    private static final int TYPE_LOADER = 1;
    TasksAdapter tasksAdapter;
    // Uri в который будем сохранять тип задач, если активити вызвано по клику на
    // конкретный тип задач
    Uri typeUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_list_activity);
        ButterKnife.bind(this);

        tasksAdapter = new TasksAdapter(this);
        taskListRV.setAdapter(tasksAdapter);
        taskListRV.setLayoutManager(new LinearLayoutManager(this));
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //taskListRV.setHasFixedSize(true);


        // Берём Uri переданный в качестве даных при вызове активити
        // если такой был
        typeUri = getIntent().getData();
        Log.i(LOG_TAG, "Start TasksListActivity - Uri: " + typeUri);

        // реакция на нажатие ФАБа
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TasksListActivity.this, TaskEditorActivity.class);
                if (typeUri != null) {
                    intent.putExtra(Constants.TYPE_ID, ContentUris.parseId(typeUri));
//                    intent.setData(typeUri);
                    Log.i(LOG_TAG, "TasksListActivity - Uri: " + ContentUris.parseId(typeUri));
                }
                Log.i(LOG_TAG, "Start TaskEditorActivity");
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(TASK_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                //todo сделать удаление или убрать
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertAnotherTestTask() {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_NAME, "Another Test Task ");
        values.put(TaskEntry.COLUMN_TASK_DESC, "My New Task just to test my task");
        values.put(TaskEntry.COLUMN_TASK_TYPE, 2);

        getContentResolver().insert(TaskEntry.CONTENT_URI, values);
        Toast.makeText(this, "New task added", Toast.LENGTH_SHORT).show();
    }

    private void insertTestTask() {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_TASK_NAME, "Test Task");
        values.put(TaskEntry.COLUMN_TASK_DESC, "My Test Task just to test my task");
        values.put(TaskEntry.COLUMN_TASK_TYPE, 1);

        getContentResolver().insert(TaskEntry.CONTENT_URI, values);
        Toast.makeText(this, "New task added", Toast.LENGTH_SHORT).show();
    }

    private void insertTestType() {
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskTypeEntry.COLUMN_TYPE_NAME, "New Type");
        values.put(TaskContract.TaskTypeEntry.COLUMN_TYPE_COLOR, TaskContract.TaskTypeEntry.TYPE_COLOR_BLUE);
        getContentResolver().insert(TaskContract.TaskTypeEntry.CONTENT_URI, values);
        Toast.makeText(this, "New task type added", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        //todo реализовать разный лоадер для разных ID
        String[] projection = {
                TaskEntry._ID,
                TaskEntry.COLUMN_TASK_NAME,
                TaskEntry.COLUMN_TASK_DESC,
                TaskEntry.COLUMN_TASK_TYPE
        };

        // Если был указан тип данных для задач
        if (typeUri != null) {
            String selection =  TaskContract.TaskEntry.COLUMN_TASK_TYPE + "=?";
            String[] args = { String.valueOf(ContentUris.parseId(typeUri)) };
            return new CursorLoader(this, TaskEntry.CONTENT_URI, projection, selection, args, null);

        } else {
            // Если тип не был указан, выводим всё
            return new CursorLoader(this, TaskEntry.CONTENT_URI, projection,
                    null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        tasksAdapter.setCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        tasksAdapter.setCursor(null);
    }


}

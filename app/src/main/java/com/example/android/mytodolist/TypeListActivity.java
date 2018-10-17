package com.example.android.mytodolist;

import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.android.mytodolist.adapters.TypeAdapter;
import com.example.android.mytodolist.data.TaskContract.TaskTypeEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.type_list) RecyclerView typeListRV;
    @BindView(R.id.fab_parent_type)  FloatingActionButton fab;

    // для загрузки типов через loaderManager
    public static final int TYPE_LOADER = 1;
    // для логов
    private static final String LOG_TAG = "TypeListActivity.java";
    TypeAdapter typeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_list);
        ButterKnife.bind(this);

        setupRecyclerView();

        // клик на ФАБ
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TypeListActivity.this, TypeEditorActivity.class);
                Log.i(LOG_TAG, "Start TypeEditorActivity");
                startActivity(intent);
            }
        });

        // Клик по пункту списка с типами задач
//        typeListRV.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//        });

        getLoaderManager().initLoader(TYPE_LOADER, null, this);
    }

    private void setupRecyclerView() {

        typeAdapter = new TypeAdapter(this);
        typeListRV.setAdapter(typeAdapter);
        typeListRV.setLayoutManager(new LinearLayoutManager(this));

    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        // projection вроде не нужны, поэтому передаём Null чтобы выдернуть все значения из таблицы
        return new CursorLoader(this, TaskTypeEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        typeAdapter.setCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        typeAdapter.setCursor(null);
    }

}

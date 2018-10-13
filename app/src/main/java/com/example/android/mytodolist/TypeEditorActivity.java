package com.example.android.mytodolist;

import android.content.ContentValues;
import android.net.Uri;
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

import com.example.android.mytodolist.adapters.TypeColorAdapter;
import com.example.android.mytodolist.data.TaskContract.TaskTypeEntry;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TypeEditorActivity extends AppCompatActivity {

    private static final String LOG_TAG = "TypeEditorActivity";
    // значение цвета по дефолту
    private int typeColor = TaskTypeEntry.TYPE_COLOR_WHITE;

    // флаг были ли данные измененеы
    private boolean typeHasChanged = false;
    // определим тач листенер, для того чтобы установить занчение флага "изменено"
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            typeHasChanged = true;
            return false;
        }
    };

    // Если пользователь редактирует наименование Типа то получаем его Uri
    // Если нет, оно будет null
    Uri typeItemUri;

    @BindView(R.id.spinner_color) Spinner typeColorSpinner;
    @BindView(R.id.edittext_type_name) EditText typeNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_editor);
        ButterKnife.bind(this);

        // настраиваем спиннер
        setupSpinner();

        // устанавливает тачлистенеры, чтобы понимать что данные были изменены
        typeColorSpinner.setOnTouchListener(touchListener);
        typeNameEdit.setOnTouchListener(touchListener);

        // пытаемся понять, эта активити вызвана для редактирования уже существуюего типа
        // или для добавления нового
        typeItemUri = getIntent().getData();
        if (typeItemUri == null) {
            setTitle(R.string.add_type);
            invalidateOptionsMenu();

        } else {
            setTitle(R.string.edit_type);
            //todo и запускаем лоадер который подгрузит всю инфу о этом пункте

        }


    }

    private void setupSpinner() {
        List<Integer> colorList = Utils.getListColors();

        TypeColorAdapter adapter = new TypeColorAdapter(this, colorList);
        typeColorSpinner.setAdapter(adapter);

        typeColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                typeColor = position;
//                Toast.makeText(TypeEditorActivity.this, "Selected " + position + " color", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "Spinner onItemSelected: selected " + position + " item");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                typeColor = TaskTypeEntry.TYPE_COLOR_WHITE;
            }
        });
    }

    // прикрепляем меню к активити
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_type_editor, menu);
        return true;
    }

    // подготавливаем меню, если добавляется новый Тип, не выводим в меню пункт удаление
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (typeItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_type_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    // реагируем на нажатие на меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // пользователь кликнул на меню
        switch (item.getItemId()) {
            case R.id.action_type_save:
                // сохраняем в БД
                saveType();
                // Закрываем активити добавления\редактирования
                finish();
                return true;
            case R.id.action_type_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        return ;
    }

    private void saveType() {
        ContentValues values = new ContentValues();
        //считываем данные из полей активити
        String typeName = typeNameEdit.getText().toString().trim();
        if (TextUtils.isEmpty(typeName)) {
            Toast.makeText(this, R.string.wrong_name_error, Toast.LENGTH_SHORT).show();
            return;
        }

        values.put(TaskTypeEntry.COLUMN_TYPE_NAME, typeName);
        // цвет меняется когда юзер выбирает что-то в спиннере
        values.put(TaskTypeEntry.COLUMN_TYPE_COLOR, typeColor);
        if (typeItemUri == null) {
            // если пользователь добавляет новый пункт
            Uri uri = getContentResolver().insert(TaskTypeEntry.CONTENT_URI, values);
            // возвращается uri добавленной в БД записи, либо null в случае ошибки
            if (uri == null) {
                Toast.makeText(this, R.string.adding_type_failed_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.item_saved, Toast.LENGTH_SHORT).show();
            }
        } else {
            // если пользователь редактирует уже существующий пункт
            int rowsUpdated = getContentResolver().update(typeItemUri, values, null, null);
            // update возвращает количество измененных пунктов или 0 если ошибка
            if (rowsUpdated == 0) {
                Toast.makeText(this, R.string.type_editing_failed_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, rowsUpdated + getString(R.string.count_items_updated), Toast.LENGTH_SHORT).show();
            }
        }
    }

}

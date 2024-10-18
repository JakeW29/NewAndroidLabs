package com.example.androidlabs;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.database.Cursor;
import androidx.appcompat.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<ToDoItems> todoList = new ArrayList<>();
    private ToDoAdapter adapter;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.todolist);
        EditText editText = findViewById(R.id.todoedittext);
        Switch urgentSwitch = findViewById(R.id.urgentswitch);
        Button addButton = findViewById(R.id.addbutton);

        //Initialize the database helper and get a writable database
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        //Load existing item from the database
        loadItemFromDatabase();

        adapter = new ToDoAdapter(this, todoList);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoText = editText.getText().toString();
                boolean isUrgent = urgentSwitch.isChecked();
                if (!todoText.isEmpty()) {
                    ToDoItems todoItem = new ToDoItems(todoText, isUrgent);
                    addItemToDatabase(todoItem);
                    todoList.add(todoItem);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.confirmdelete))
                        .setMessage(getString(R.string.selectedrow) + position)
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                            deleteItemFromDatabase(todoList.get(position));
                            todoList.remove(position);
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
                return true;
            }
        });
    }

    private void loadItemFromDatabase() {

        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));
            boolean isUrgent = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_URGENT)) == 1;
            ToDoItems todoItem = new ToDoItems(text, isUrgent);
            todoList.add(todoItem);
        }
        cursor.close();
    }

    private void addItemToDatabase(ToDoItems todoItem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TEXT, todoItem.getText());
        values.put(DatabaseHelper.COLUMN_URGENT, todoItem.isUrgent() ? 1 : 0);
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    private void deleteItemFromDatabase(ToDoItems todoItem) {
        String selection = DatabaseHelper.COLUMN_TEXT + " = ? AND " + DatabaseHelper.COLUMN_URGENT + " = ?";
        String[] selectionArgs = { todoItem.getText(), todoItem.isUrgent() ? "1" : "0" };
        db.delete(DatabaseHelper.TABLE_NAME, selection, selectionArgs);
    }
}

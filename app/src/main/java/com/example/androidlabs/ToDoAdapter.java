package com.example.androidlabs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ToDoAdapter extends BaseAdapter {
    private Context context;
    private List<ToDoItems> todoList;

    public ToDoAdapter(Context context, List<ToDoItems> todoList) {
        this.context = context;
        this.todoList = todoList;
    }

    @Override
    public int getCount() { // Returns number of items in list
        return todoList.size();
    }

    @Override
    public Object getItem(int position) { // Returns item at specified position
        return todoList.get(position);
    }

    @Override
    public long getItemId(int position) { // Returns position as ID
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.todo_item, parent, false);
        }

        ToDoItems todoItem = (ToDoItems) getItem(position);
        TextView textView = convertView.findViewById(R.id.todo_text);
        textView.setText(todoItem.getText());

        if (todoItem.isUrgent()) {
            convertView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}

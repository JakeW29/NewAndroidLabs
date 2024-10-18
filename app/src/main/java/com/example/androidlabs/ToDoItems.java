package com.example.androidlabs;

public class ToDoItems {
    private String text;
    private boolean urgent;

    public ToDoItems(String text, boolean isUrgent) {
        this.text = text;
        this.urgent = urgent;
    }

    public String getText() {
        return text;
    }

    public boolean isUrgent() {
        return urgent;
    }
}

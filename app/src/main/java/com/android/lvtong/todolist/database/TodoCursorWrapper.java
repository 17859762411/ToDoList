package com.android.lvtong.todolist.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.android.lvtong.todolist.Todo;
import com.android.lvtong.todolist.database.TodoDbSchema.TodoTable;

import java.util.Date;
import java.util.UUID;

public class TodoCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public TodoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Todo getTodo() {
        String uuidString = getString(getColumnIndex(TodoTable.Cols.UUID));
        String title = getString(getColumnIndex(TodoTable.Cols.TITLE));
        String beizhu = getString(getColumnIndex(TodoTable.Cols.BEIZHU));
        int importance = getInt(getColumnIndex(TodoTable.Cols.IMPORTANCE));
        long date = getLong(getColumnIndex(TodoTable.Cols.DATE));

        Todo todo = new Todo(UUID.fromString(uuidString));
        todo.setmTitle(title);
        todo.setmBeizhu(beizhu);
        todo.setmImportance(importance);
        todo.setmDate(new Date(date));

        return todo;
    }
}

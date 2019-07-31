package com.android.lvtong.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.lvtong.todolist.database.TodoBaseHelper;
import com.android.lvtong.todolist.database.TodoCursorWrapper;
import com.android.lvtong.todolist.database.TodoDbSchema.TodoTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class TodoLab {

    private static TodoLab sTodoLab;

    private SQLiteDatabase mDatabase;

    private TodoLab(Context context) {
        Context context1 = context.getApplicationContext();
        mDatabase = new TodoBaseHelper(context1).getWritableDatabase();
    }

    static TodoLab get(Context context) {
        if (sTodoLab == null) {
            sTodoLab = new TodoLab(context);
        }
        return sTodoLab;
    }

    List<Todo> getmTodos() {
        List<Todo> todos = new ArrayList<>();

        try (TodoCursorWrapper cursor = queryTodos(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                todos.add(cursor.getTodo());
                cursor.moveToNext();
            }
        }
        return todos;
    }

    private TodoCursorWrapper queryTodos(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(TodoTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new TodoCursorWrapper(cursor);
    }

    void addmTodo(Todo todo) {
        ContentValues values = getContentValues(todo);

        mDatabase.insert(TodoTable.NAME, null, values);
    }

    private static ContentValues getContentValues(Todo todo) {
        ContentValues values = new ContentValues();
        values.put(TodoTable.Cols.UUID, todo.getmId()
                                            .toString());
        values.put(TodoTable.Cols.TITLE, todo.getmTitle());
        values.put(TodoTable.Cols.BEIZHU, todo.getmBeizhu());
        values.put(TodoTable.Cols.IMPORTANCE, todo.getmImportance());
        values.put(TodoTable.Cols.DATE, todo.getmDate()
                                            .getTime());

        return values;
    }

    Todo getTodo(UUID id) {
        try (TodoCursorWrapper cursor = queryTodos(TodoTable.Cols.UUID + " = ?", new String[]{id.toString()})) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getTodo();
        }
    }

    void updateTodo(Todo todo) {
        String uuidString = todo.getmId()
                                .toString();
        ContentValues values = getContentValues(todo);
        mDatabase.update(TodoTable.NAME, values, TodoTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    //删除
    void removeTodo(Todo todo) {
        String uuidString = todo.getmId()
                                .toString();
        mDatabase.delete(TodoTable.NAME, TodoTable.Cols.UUID + " = ?", new String[]{uuidString});
    }
}

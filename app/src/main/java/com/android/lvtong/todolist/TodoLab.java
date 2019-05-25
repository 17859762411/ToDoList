package com.android.lvtong.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.lvtong.todolist.database.TodoBaseHelper;
import com.android.lvtong.todolist.database.TodoCursorWrapper;
import com.android.lvtong.todolist.database.TodoDbSchema;
import com.android.lvtong.todolist.database.TodoDbSchema.TodoTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TodoLab {
    private static TodoLab sTodoLab;

//    private List<Todo> mTodos;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static TodoLab get(Context context) {
        if (sTodoLab == null) {
            sTodoLab = new TodoLab(context);
        }
        return sTodoLab;
    }

    private TodoLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TodoBaseHelper(mContext).getWritableDatabase();
//        mTodos = new ArrayList<>();

    }

    public List<Todo> getmTodos() {
        List<Todo> todos = new ArrayList<>();

        TodoCursorWrapper cursor = queryTodos(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                todos.add(cursor.getTodo());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return todos;
    }
    public void addmTodo(Todo todo) {
        ContentValues values = getContentValues(todo);

        mDatabase.insert(TodoTable.NAME,null,values);
    }

    public Todo getTodo(UUID id) {
        TodoCursorWrapper cursor = queryTodos(
                TodoTable.Cols.UUID + " = ?",
                new String[]{ id.toString() }
        );
        try {
            if (cursor.getCount() == 0 ){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getTodo();
        }finally {
            cursor.close();
        }
    }

    public void updateTodo(Todo todo){
        String uuidString = todo.getmId().toString();
        ContentValues values = getContentValues(todo);
        mDatabase.update(TodoTable.NAME,values,
                TodoTable.Cols.UUID + " = ?",
                new String[]{ uuidString });
    }

    private TodoCursorWrapper queryTodos(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                TodoTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new TodoCursorWrapper(cursor);
    }


    private static ContentValues getContentValues(Todo todo){
        ContentValues values = new ContentValues();
        values.put(TodoTable.Cols.UUID,todo.getmId().toString());
        values.put(TodoTable.Cols.TITLE,todo.getmTitle());
        values.put(TodoTable.Cols.BEIZHU,todo.getmBeizhu());
        values.put(TodoTable.Cols.IMPORTANCE,todo.getmImportance());
        values.put(TodoTable.Cols.DATE,todo.getmDate().getTime());

        return values;
    }

}

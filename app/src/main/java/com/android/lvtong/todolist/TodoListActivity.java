package com.android.lvtong.todolist;

import android.support.v4.app.Fragment;

public class TodoListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new TodoListFragment();
    }
}

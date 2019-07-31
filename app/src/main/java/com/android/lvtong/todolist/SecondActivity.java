package com.android.lvtong.todolist;

import android.support.v4.app.Fragment;

public class SecondActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new SecondFragment();
    }
}

package com.android.lvtong.todolist.menu;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.lvtong.todolist.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
            actionBar.setTitle("关于");
        }

        TextView openSources = findViewById(R.id.open_source);
        openSources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, "暂无", Toast.LENGTH_SHORT)
                     .show();
            }
        });

        TextView checkUpdate = findViewById(R.id.check_update);
        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, "检查更新中", Toast.LENGTH_SHORT)
                     .show();
                //3秒后执行Runnable中的run方法
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AboutActivity.this, "已经是最新版本了", Toast.LENGTH_SHORT)
                             .show();
                    }
                }, 1000);
            }
        });
    }
}

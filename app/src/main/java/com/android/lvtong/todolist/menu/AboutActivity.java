package com.android.lvtong.todolist.menu;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.lvtong.todolist.R;

import java.util.Timer;

public class AboutActivity extends AppCompatActivity {
    private TextView mOpenSources;
    private TextView mCheckUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
            actionBar.setTitle("关于");
        }

        mOpenSources = findViewById(R.id.open_source);
        mOpenSources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, "暂无", Toast.LENGTH_SHORT).show();
            }
        });

        mCheckUpdate = findViewById(R.id.check_update);
        mCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, "检查更新中", Toast.LENGTH_SHORT).show();
                //3秒后执行Runnable中的run方法
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AboutActivity.this, "已经是最新版本了", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);

            }
        });
    }
}

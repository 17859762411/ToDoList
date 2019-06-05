package com.android.lvtong.todolist;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{

    private int recLen = 5;//跳过倒计时提示5秒
    private TextView tv;
    Timer timer = new Timer();
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //        //设置全屏
        getWindow().setFlags(flag,flag);
        //隐藏标题栏
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start);
        initView();
        //等待一秒，停留一秒
        timer.schedule(task,1000,1000);

        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                //从启动界面跳转到主界面
                Intent intent = new Intent(StartActivity.this,TodoListActivity.class);
                startActivity(intent);
                finish();
            }
            //延时5s发送handler信息
        },5000);
    }

    private void initView() {
        //跳过监听
        tv = findViewById(R.id.jump);
        tv.setOnClickListener(this);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recLen--;
                    tv.setText("跳过 "+recLen );
                    if (recLen < 0){
                        timer.cancel();
                        tv.setVisibility(View.GONE);
                    }
                }
            });
        }
    };
    //点击跳过
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jump:
                Intent intent = new Intent(StartActivity.this,TodoListActivity.class);
                startActivity(intent);
                finish();
                if (runnable != null){
                    handler.removeCallbacks(runnable);
                }
                break;
            default:
                break;
        }
    }
}


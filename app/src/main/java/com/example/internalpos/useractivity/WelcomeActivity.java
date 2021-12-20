package com.example.internalpos.useractivity;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.Nullable;


import com.example.internalpos.IPActivity;
import com.example.internalpos.R;

import com.example.internalpos.utils.StatusBarUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者: qgl
 * 创建日期：2021/2/1
 * 描述: 欢迎界面
 */
public class WelcomeActivity extends Activity{

    //延时操作
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
                //跳转到登录界面
                Intent intent = new Intent(WelcomeActivity.this, IPActivity.class);
                startActivity(intent);
                finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //导航栏设置
        StatusBarUtil.transparencyBar(this);
        //App打包apk安装后重复启动根界面的问题
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish();
                return;
            }
        }
        //xml界面
        setContentView(R.layout.welcome_activity);
        //延时操作
        Timer timer = new Timer();
        timer.schedule(task, 3000);//3秒后执行TimeTask的run方法
    }

}

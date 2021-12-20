package com.example.internalpos;

import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.internalpos.app.MyApp;
import com.example.internalpos.base.BaseActivity;
import com.example.internalpos.useractivity.LoginActivity1;

/**
 * 作者: qgl
 * 创建日期：2021/11/24
 * 描述:IP設置界面
 */
public class IPActivity  extends BaseActivity implements View.OnClickListener {

    private EditText ip_tv;
    private Button btn1,btn2;
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.ip_activity;
    }

    @Override
    protected void initView() {
        ip_tv = findViewById(R.id.ip_tv);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
    }

    @Override
    protected void initListener() {
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn1:
                ip_tv.setText("http://www.poshb.cn:8081/");
                break;
            case R.id.btn2:
                if (!Patterns.WEB_URL.matcher(ip_tv.getText().toString()).matches()) {
                    Toast.makeText(IPActivity.this, "请输入正确的IP地址", Toast.LENGTH_LONG).show();
                    return;
                }
                MyApp.getApp().setIpConfig(ip_tv.getText().toString());
                Intent intent = new Intent(this, LoginActivity1.class);
                startActivity(intent);
                break;
        }
    }
}
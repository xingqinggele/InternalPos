package com.example.internalpos.mefragment.setup;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.internalpos.R;
import com.example.internalpos.base.BaseActivity;
import com.example.internalpos.net.HttpRequest;
import com.example.internalpos.net.OkHttpException;
import com.example.internalpos.net.RequestParams;
import com.example.internalpos.net.ResponseCallback;
import com.jungly.gridpasswordview.GridPasswordView;

/**
 * 作者: qgl
 * 创建日期：2021/2/5
 * 描述: 修改支付密码
 */
public class MeModifyPayPassActivity extends BaseActivity implements View.OnClickListener {

    private GridPasswordView paypasspswView;
    private LinearLayout iv_back;
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color, false).init();
        return R.layout.memodifypaypass_activity;
    }

    @Override
    protected void initView() {
        paypasspswView = findViewById(R.id.paypasspswView);
        iv_back = findViewById(R.id.iv_back);
    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        paypasspswView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {

            }

            @Override
            public void onInputFinish(String psw) {
                posDate(psw);
            }
        });
    }

    @Override
    protected void initData() {

    }

    // 请求接口，判断原始密码是否正确
    public void posDate(String value){
        RequestParams params = new RequestParams();
        params.put("password",value);
        HttpRequest.getPay_ModifyPassword(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                startActivity(new Intent(MeModifyPayPassActivity.this, MeModifyPayPassTwoActivity.class));
                finish();
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(),failuer.getEmsg());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}

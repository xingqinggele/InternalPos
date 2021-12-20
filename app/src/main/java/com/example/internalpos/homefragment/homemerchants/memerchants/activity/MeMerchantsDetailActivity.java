package com.example.internalpos.homefragment.homemerchants.memerchants.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.internalpos.R;
import com.example.internalpos.base.BaseActivity;
import com.example.internalpos.fragment.HomeFragment;
import com.example.internalpos.homefragment.homemerchants.memerchants.bean.EquipmentEvnBusBean;
import com.example.internalpos.homefragment.homemerchants.memerchants.bean.TradingEvnBusBean;
import com.example.internalpos.homefragment.homemerchants.memerchants.fragment.EquipmentFragment;
import com.example.internalpos.homefragment.homemerchants.memerchants.fragment.TradingFragment;
import com.example.internalpos.homefragment.homewallet.activity.WithdrawalActivity;
import com.example.internalpos.net.HttpRequest;
import com.example.internalpos.net.OkHttpException;
import com.example.internalpos.net.RequestParams;
import com.example.internalpos.net.ResponseCallback;
import com.example.internalpos.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * 作者: qgl
 * 创建日期：2021/3/10
 * 描述:我的商户详情页
 */
public class MeMerchantsDetailActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    //列表ID
    private String MeMerchants_ID = "";
    //切换按钮容器
    private RadioGroup merchants_detail_radio_group;
    //返回键
    private LinearLayout iv_back;
    //Fragment 事务
    private FragmentTransaction transaction;
    //设备Fragment
    private EquipmentFragment equipmentFragment;
    //交易Fragment
    private TradingFragment tradingFragment;
    //商户姓名
    private TextView me_merchants_detail_name;
    //入网时间
    private TextView me_merchants_detail_time;
    //商户编号
    private TextView me_merchants_detail_number;
    //设备EvnBus实体类
    private EquipmentEvnBusBean evnBusBean;
    //商户转移按钮
    private TextView merchants_transfer_tv;
    //设备编号
    private String snCode = "";
    //商户名称
    private String merchantName = "";

    //当前界面
    public static MeMerchantsDetailActivity instance = null;
    //xml界面
    @Override
    protected int getLayoutId() {
        // 设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.memerchants_detail_activity;
    }

    //控件初始化
    @Override
    protected void initView() {
        instance = this;
        merchants_detail_radio_group = findViewById(R.id.merchants_detail_radio_group);
        iv_back = findViewById(R.id.iv_back);
        me_merchants_detail_name = findViewById(R.id.me_merchants_detail_name);
        me_merchants_detail_time = findViewById(R.id.me_merchants_detail_time);
        me_merchants_detail_number = findViewById(R.id.me_merchants_detail_number);
        merchants_transfer_tv = findViewById(R.id.merchants_transfer_tv);
    }

    //事件绑定
    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
        merchants_transfer_tv.setOnClickListener(this);
        merchants_detail_radio_group.setOnCheckedChangeListener(this);
        //默认选中第一按钮
        merchants_detail_radio_group.check(R.id.equipment_radio);
    }

    //数据处理
    @Override
    protected void initData() {
        //接受列表页传递的列表ID
        MeMerchants_ID = getIntent().getStringExtra("MeMerchants_id");
        //请求接口
        posData();
    }

    //请求接口-->设备信息
    public void posData() {
        RequestParams params = new RequestParams();
        params.put("id", MeMerchants_ID);
        HttpRequest.getMeMerchants_detailEquipment(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    merchantName = result.getJSONObject("data").getString("merchantName");
                    //显示商户姓名
                    me_merchants_detail_name.setText(merchantName);
                    //显示入网时间
                    me_merchants_detail_time.setText("入网时间：" + result.getJSONObject("data").getString("netTime"));
                    //商户编号
                    me_merchants_detail_number.setText("商户编号：" + result.getJSONObject("data").getString("merchCode"));
                    evnBusBean = new EquipmentEvnBusBean();
                    snCode = result.getJSONObject("data").getString("snCode");
                    evnBusBean.setSnCode("设备编号：" + snCode);
                    evnBusBean.setTimer("激活时间：" + result.getJSONObject("data").getString("activeTime"));
                    EventBus.getDefault().post(evnBusBean);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(),failuer.getEmsg());
            }
        });
    }

    //点击事件触发
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.merchants_transfer_tv:
                //根据商户名称判断是否可转移
                if (merchantName.equals(SPUtils.get(this, "nickName", "").toString())){
                    showToast(2,"此用户不可转移");
                    return;
                }
                Intent intent = new Intent(MeMerchantsDetailActivity.this,MeMerchantsTransferActivity.class);
                intent.putExtra("snCode",snCode);
                intent.putExtra("merchantName",merchantName);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        hideAllFragment();
        switch (checkedId) {
            //设备按钮
            case R.id.equipment_radio:
                if (equipmentFragment == null) {
                    equipmentFragment = new EquipmentFragment();
                    transaction.add(R.id.me_merchants_detail_content, equipmentFragment);
                } else {
                    transaction.show(equipmentFragment);
                }
                break;
            case R.id.trading_radio:
                if (tradingFragment == null) {
                    tradingFragment = new TradingFragment().newInstance(MeMerchants_ID);
                    transaction.add(R.id.me_merchants_detail_content, tradingFragment);
                } else {
                    transaction.show(tradingFragment);
                }
                break;
        }
        //事务提交
        transaction.commit();
    }

    //判断、隐藏
    private void hideAllFragment() {
        if (equipmentFragment != null) this.transaction.hide(equipmentFragment);
        if (tradingFragment != null) this.transaction.hide(tradingFragment);
    }
}

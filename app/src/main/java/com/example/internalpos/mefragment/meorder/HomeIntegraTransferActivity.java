package com.example.internalpos.mefragment.meorder;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.example.internalpos.R;
import com.example.internalpos.adapter.MyViewPageAdapter;
import com.example.internalpos.base.BaseActivity;
import com.example.internalpos.mefragment.meorder.fragment.HomeIntegerIntervalFragment;
import com.example.internalpos.mefragment.meorder.fragment.HomeIntegerSelectFragment;

import java.util.ArrayList;

/**
 * 作者: qgl
 * 创建日期：2021/2/24
 * 描述: 积分兑换发放极具
 */
public class  HomeIntegraTransferActivity extends BaseActivity implements View.OnClickListener {
    private TabLayout tab_layout;
    private ViewPager viewpager;
    private LinearLayout iv_back;
    ArrayList<String> titleDatas = new ArrayList<>();
    ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private String parentNum = ""; //划拨数量
    private String merchId = ""; //划拨接收者ID
    private String orderId = ""; //订单ID
    private String posId = "";//pos机类型
    private String orderNo = "";//
    private static final HomeIntegraTransferActivity h = null;
    @Override
    protected int getLayoutId() {
        //设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.home_integrat_ransfer_activity;
    }

    @Override
    protected void initView() {
        tab_layout = findViewById(R.id.tab_layout);
        viewpager = findViewById(R.id.viewpager);
        iv_back = findViewById(R.id.iv_back);
    }


    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        parentNum = getIntent().getStringExtra("parentNum");
        merchId = getIntent().getStringExtra("merchId");
        orderId = getIntent().getStringExtra("orderId");
        posId = getIntent().getStringExtra("posId");
        orderNo = getIntent().getStringExtra("orderNo");
        titleDatas.add("选择划拨");
        titleDatas.add("区间划拨");
        fragmentList.add(new HomeIntegerSelectFragment().newInstance(parentNum,merchId,orderId,posId,orderNo));
        fragmentList.add(new HomeIntegerIntervalFragment().newInstance(parentNum,merchId,orderId,posId));
        init();
    }


    private void init() {
        MyViewPageAdapter myViewPageAdapter = new MyViewPageAdapter(getSupportFragmentManager(), titleDatas, fragmentList);
        tab_layout.setSelectedTabIndicator(0);
        viewpager.setAdapter(myViewPageAdapter);
        tab_layout.setupWithViewPager(viewpager);
        tab_layout.setTabsFromPagerAdapter(myViewPageAdapter);
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

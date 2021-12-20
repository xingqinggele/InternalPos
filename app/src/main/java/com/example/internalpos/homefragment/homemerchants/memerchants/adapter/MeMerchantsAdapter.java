package com.example.internalpos.homefragment.homemerchants.memerchants.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.internalpos.R;
import com.example.internalpos.homefragment.homemerchants.memerchants.bean.MeMerchantsBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2021/3/9
 * 描述:我的商户Adapter
 */
public class MeMerchantsAdapter extends BaseQuickAdapter<MeMerchantsBean, BaseViewHolder> {

    public MeMerchantsAdapter(int layoutResId, @Nullable List<MeMerchantsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MeMerchantsBean item) {
        helper.setText(R.id.me_merchants_name,item.getName());
        helper.setText(R.id.me_merchants_price,new BigDecimal(item.getMonthTurnover()).toString());
    }
}

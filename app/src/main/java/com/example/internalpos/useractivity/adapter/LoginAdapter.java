package com.example.internalpos.useractivity.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.internalpos.R;
import com.example.internalpos.homefragment.hometeam.HomeTeamDetailsActivity;
import com.example.internalpos.homefragment.hometeam.bean.TeamBean;
import com.example.internalpos.useractivity.bean.LPersonBean;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by qgl on 2020/3/11.
 * Describe:
 */
public class LoginAdapter extends BaseQuickAdapter<LPersonBean, BaseViewHolder> {

    public LoginAdapter(Context context, int layoutResId, @Nullable List<LPersonBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, LPersonBean report) {


    }


}

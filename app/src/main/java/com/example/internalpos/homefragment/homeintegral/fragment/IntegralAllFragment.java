package com.example.internalpos.homefragment.homeintegral.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.internalpos.R;
import com.example.internalpos.base.BaseFragment;
import com.example.internalpos.datafragment.datapersonalresults.adapter.DataDayResultAdapter;
import com.example.internalpos.homefragment.homeequipment.bean.PersonBean;
import com.example.internalpos.homefragment.homeintegral.adpter.IntegralAllAdapter;
import com.example.internalpos.homefragment.homeintegral.bean.IntegralAllBean;
import com.example.internalpos.net.HttpRequest;
import com.example.internalpos.net.OkHttpException;
import com.example.internalpos.net.RequestParams;
import com.example.internalpos.net.ResponseCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: qgl
 * 创建日期：2021/2/19
 * 描述: 积分全部Fragment
 */
public class IntegralAllFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private IntegralAllAdapter dataDayResultAdapter;
    private SwipeRefreshLayout integralall_swipe;
    private RecyclerView integralall_list_view;
    private List<IntegralAllBean >mData = new ArrayList<>();

    @Override
    protected void initView(View rootView) {
        integralall_swipe = rootView.findViewById(R.id.integralall_swipe);
        integralall_list_view = rootView.findViewById(R.id.integralall_list_view);
        //下拉样式
        integralall_swipe.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        integralall_swipe.setOnRefreshListener(this);
        integralall_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        dataDayResultAdapter = new IntegralAllAdapter(getActivity());
        integralall_list_view.setAdapter(dataDayResultAdapter);
        getData();
    }

    @Override
    protected int getLayoutInflaterResId() {
        return R.layout.integralall_fragment;
    }


    //获取数据
    public void getData(){
        mData.clear();
        dataDayResultAdapter.clearData();
        RequestParams params = new RequestParams();
        params.put("userId",getUserId());
        params.put("pageNo", "-1");
        params.put("pageSize", "-1");
        HttpRequest.getTotal_scoreList(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                integralall_swipe.setRefreshing(false);
                //需要转化为实体对象
                 Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<IntegralAllBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<IntegralAllBean>>() {
                            }.getType());
                    mData.addAll(memberList);
                    dataDayResultAdapter.addAllData(mData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }
    @Override
    public void onRefresh() {
        integralall_swipe.setRefreshing(true);
        getData();
    }
}

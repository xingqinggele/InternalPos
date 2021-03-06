package com.example.internalpos.homefragment.homeequipment.fragment.record;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.internalpos.R;
import com.example.internalpos.base.BaseFragment;
import com.example.internalpos.homefragment.homeequipment.activity.RecordDetailsActivity;
import com.example.internalpos.homefragment.homeequipment.adapter.CallbackRecordAdapter;
import com.example.internalpos.homefragment.homeequipment.adapter.ChooserRecyclerAdapter;
import com.example.internalpos.homefragment.homeequipment.adapter.RecordListAdapter;
import com.example.internalpos.homefragment.homeequipment.bean.CallbackRecordBean;
import com.example.internalpos.homefragment.homeequipment.bean.TerminalBean;
import com.example.internalpos.homefragment.homemessage.adapter.BusinessMessageAdapter;
import com.example.internalpos.homefragment.homemessage.bean.BusinessMessageBean;
import com.example.internalpos.net.HttpRequest;
import com.example.internalpos.net.OkHttpException;
import com.example.internalpos.net.RequestParams;
import com.example.internalpos.net.ResponseCallback;
import com.example.internalpos.utils.SPUtils;
import com.example.internalpos.utils.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.entity.LocalMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.internalpos.utils.TimeUtils.getNowTime;

/**
 * ??????: qgl
 * ???????????????2020/12/25
 * ??????:????????????
 */
public class CallbackRecordFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener, View.OnClickListener {
    private CallbackRecordAdapter callbackRecordAdapter;
    private SwipeRefreshLayout callback_record_swipe;
    private RecyclerView callback_record_list_view;
    private List<CallbackRecordBean> mData = new ArrayList<>();
    private RecordListAdapter mAdapter;
    private int mCount = 1; //??????
    private int pageSize = 20;  // ??????????????????
    private TextView transfer_record_fragment_tv_time;


    @Override
    protected void initView(View rootView) {
        callback_record_swipe = rootView.findViewById(R.id.callback_record_swipe);
        callback_record_list_view = rootView.findViewById(R.id.callback_record_list_view);
        //????????????
        callback_record_swipe.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        callback_record_swipe.setOnRefreshListener(this);
        callback_record_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        callbackRecordAdapter = new CallbackRecordAdapter(getActivity());
        callback_record_list_view.setAdapter(callbackRecordAdapter);
        transfer_record_fragment_tv_time = rootView.findViewById(R.id.transfer_record_fragment_tv_time);
        transfer_record_fragment_tv_time.setText(getNowTime("month"));
        initList();
    }

    @Override
    protected int getLayoutInflaterResId() {
        return R.layout.callback_record_fragment;
    }

    @Override
    protected void initListener() {
        transfer_record_fragment_tv_time.setOnClickListener(this);
    }

    private void initList() {
        //????????????
        callback_record_swipe.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        callback_record_swipe.setOnRefreshListener(this);
        mAdapter = new RecordListAdapter(R.layout.item_callback_record_item_list_view, mData,false);
        mAdapter.openLoadAnimation();
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(this, callback_record_list_view);
        mAdapter.setEmptyView(LayoutInflater.from(getActivity()).inflate(R.layout.list_empty, null));
        callback_record_list_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        callback_record_list_view.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), RecordDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("SnCode", (Serializable) mData.get(position).getPosCodes());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        posData(true);
    }
    @Override
    public void onRefresh() {
        callback_record_swipe.setRefreshing(true);
        setRefresh();
    }

    //??????????????????
    public void setRefresh() {
        mData.clear();
        mCount = 1;
        posData(true);
    }

    //????????????
    @Override
    public void onLoadMoreRequested() {
        mCount = mCount + 1;
        posData(false);
    }

    //??????????????????
    public void posData(boolean isRefresh) {
        RequestParams params = new RequestParams();
        params.put("operType", "2"); //???????????????1-?????????2-??????
        params.put("userId", SPUtils.get(getActivity(), "userId", "-1").toString());
        params.put("pageNo", mCount + "");
        params.put("pageSize", pageSize + "");
        params.put("operateTime", transfer_record_fragment_tv_time.getText().toString().trim());
        HttpRequest.getRecords(params, SPUtils.get(getActivity(), "Token", "-1").toString(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                callback_record_swipe.setRefreshing(false);
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<CallbackRecordBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<CallbackRecordBean>>() {
                            }.getType());
                    //????????????????????????
                    if (isRefresh){
                        //???????????????????????????????????????????????????????????????????????????
                        if (mData != null){
                            mData.clear();
                        }
                    }
                    mData.addAll(memberList);
                    if (memberList.size() < pageSize) {
                        mAdapter.loadMoreEnd();
                    } else {
                        mAdapter.loadMoreComplete();
                    }
                    mAdapter.notifyDataSetChanged();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.transfer_record_fragment_tv_time:
                selectTime();
                break;
        }
    }


    /*****????????????********/
    private void selectTime() {
        TimePickerView pvTime = new TimePickerBuilder(mContext, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //????????????
                transfer_record_fragment_tv_time.setText(Utility.getTime2(date));
                onRefresh();

            }
        }).setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("???", "???", "???", "???", "???", "???")
                .build();
        pvTime.show();
    }
}

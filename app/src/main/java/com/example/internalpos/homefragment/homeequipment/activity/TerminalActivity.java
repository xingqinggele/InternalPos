package com.example.internalpos.homefragment.homeequipment.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.internalpos.R;
import com.example.internalpos.adapter.ChooseGridViewAdapter;
import com.example.internalpos.adapter.ChooseGridViewAdapter1;
import com.example.internalpos.adapter.ChooseGridViewAdapter2;
import com.example.internalpos.adapter.ChooseGridViewAdapter3;
import com.example.internalpos.base.BaseActivity;
import com.example.internalpos.homefragment.homeequipment.adapter.ChooserListAdapter;
import com.example.internalpos.homefragment.homeequipment.adapter.TerminalAdapter;
import com.example.internalpos.homefragment.homeequipment.bean.ScreeningBean;
import com.example.internalpos.homefragment.homeequipment.bean.TerminalActivityBean;
import com.example.internalpos.homefragment.homeequipment.bean.TerminalBean;
import com.example.internalpos.homefragment.homeintegral.bean.IntegralMostBean;
import com.example.internalpos.net.HttpRequest;
import com.example.internalpos.net.OkHttpException;
import com.example.internalpos.net.RequestParams;
import com.example.internalpos.net.ResponseCallback;
import com.example.internalpos.net.Utils;
import com.example.internalpos.views.MyGridView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ??????: qgl
 * ???????????????2020/12/21
 * ??????:????????????
 */
public class TerminalActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    //??????Layout
    private DrawerLayout drawer_layout;
    //??????
    private TextView terminal_screening;
    //?????????
    private LinearLayout iv_back;
    //????????????
    private String posType = "";
    //?????????
    private EditText terminal_ed_search;
    //????????????
    private SwipeRefreshLayout terminal_swipe_refresh_layout;
    //listView
    private RecyclerView terminal_list_view;
    //??????adapter
    private TerminalAdapter adapter;
    //????????????
    private TextView terminal_tv_number;
    //???????????????????????????
    private RadioButton terminal_determine_rb, terminal_cancel_rb;
    //??????Bean
    private List<TerminalBean> beans = new ArrayList<>();
    private int mCount = 1; //??????
    private int pageSize = 20;  // ??????????????????
    private String posCode = ""; //???????????????

    /*************************** ?????? ************************/
    //??????Item???
    private List<Integer> showTitle;
    //?????????????????????
    private List<String> menuList = new ArrayList<>();
    //??????????????????
    private List<IntegralMostBean.PosTypeList> homeList = new ArrayList<>();
    //?????????
    private List<IntegralMostBean> memberList = new ArrayList<>();
    //????????????adapter
    private ChooseGridViewAdapter2 madapter;
    //????????????adapter
    private ChooseGridViewAdapter3 madapter3;
    //???????????????GridView
    private MyGridView gvTest;
    //???????????????GridView
    private MyGridView gvTest2;
    @Override
    protected int getLayoutId() {
        //?????????????????????
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.terminal_activity;
    }

    @Override
    protected void initView() {
        terminal_determine_rb = findViewById(R.id.terminal_determine_rb);
        terminal_cancel_rb = findViewById(R.id.terminal_cancel_rb);
        terminal_tv_number = findViewById(R.id.terminal_tv_number);
        drawer_layout = findViewById(R.id.drawer_layout);
        iv_back = findViewById(R.id.iv_back);
        terminal_screening = findViewById(R.id.terminal_screening);
        terminal_ed_search = findViewById(R.id.terminal_ed_search);
        gvTest = findViewById(R.id.my_grid1);
        gvTest2 = findViewById(R.id.my_grid2);
        terminal_swipe_refresh_layout = findViewById(R.id.terminal_swipe_refresh_layout);
        terminal_list_view = findViewById(R.id.terminal_list_view);
        //????????????
        madapter = new ChooseGridViewAdapter2(TerminalActivity.this,menuList);
        gvTest.setAdapter(madapter);
        //????????????
        madapter3 = new ChooseGridViewAdapter3(TerminalActivity.this,homeList);
        gvTest2.setAdapter(madapter3);
        initList();
        postData2();
    }

    private void initList() {
        //????????????
        terminal_swipe_refresh_layout.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        terminal_swipe_refresh_layout.setOnRefreshListener(this);
        adapter = new TerminalAdapter(R.layout.item_terminal_activity, beans);
        adapter.openLoadAnimation();
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(this, terminal_list_view);
        adapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.list_empty, null));
        terminal_list_view.setLayoutManager(new LinearLayoutManager(this));
        terminal_list_view.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(TerminalActivity.this, TerminalDetailsActivity.class);
                intent.putExtra("var2", beans.get(position).getVar2());
                intent.putExtra("posCode", beans.get(position).getPosCode());
                intent.putExtra("var1", beans.get(position).getVar1());
                if (beans.get(position).getPosActivateStatus() == null) {
                    intent.putExtra("getPosActivateStatus", "null");
                } else {
                    intent.putExtra("getPosActivateStatus", beans.get(position).getPosActivateStatus());
                }
                intent.putExtra("posBindTime", beans.get(position).getPosBindTime());
                intent.putExtra("posActivity", beans.get(position).getPosActivity());
                intent.putExtra("posDeposit", beans.get(position).getPosDeposit());
                intent.putExtra("posCashback", beans.get(position).getPosCashback());
                intent.putExtra("posModel", beans.get(position).getPosModel());
                startActivity(intent);
            }
        });
        postData(true);
    }

    @Override
    protected void initListener() {
        terminal_screening.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        terminal_determine_rb.setOnClickListener(this);
        terminal_cancel_rb.setOnClickListener(this);
        gvTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                homeList.clear();
                homeList.addAll(memberList.get(position).getPosTypeList());
                madapter.setSelectorPosition(position);
                madapter.notifyDataSetInvalidated();
                madapter3.setaBoolean(false);
                madapter3.notifyDataSetInvalidated();
                posType = "";
            }
        });
        gvTest2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                shouLog("?????????------------>","YES");
                madapter3.setSelectorPosition(position);
                madapter3.setaBoolean(true);
                madapter3.notifyDataSetInvalidated();
                posType = homeList.get(position).getId();
            }
        });
        search();
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.terminal_screening:
                drawer_layout.openDrawer(GravityCompat.END);
                break;
            case R.id.terminal_determine_rb:
                posType = "";
                homeList.clear();
                homeList.addAll(memberList.get(0).getPosTypeList());
                madapter.newAdd();
                madapter3.newAdd();
                madapter3.setaBoolean(false);
                shouLog("terminalType", posType + "111");
                break;
            case R.id.terminal_cancel_rb:
                drawer_layout.closeDrawer(GravityCompat.END);
                shouLog("terminalType", posType + "222");
                onRefresh();
                break;
        }
    }

    //?????????
    private void search() {
        terminal_ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // ????????????????????????????????????
                    Utils.hideKeyboard(terminal_ed_search);
                    posCode = v.getText().toString().trim();
                    onRefresh();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * ????????????
     */
    @Override
    public void onRefresh() {
        terminal_swipe_refresh_layout.setRefreshing(true);
        setRefresh();
    }


    private void setRefresh() {
        mCount = 1;
        postData(true);
    }

    /**
     * ???????????????
     */
    @Override
    public void onLoadMoreRequested() {
        mCount = mCount + 1;
        postData(false);
    }

    // ??????????????????
    public void postData(boolean isRefresh) {
        RequestParams params = new RequestParams();
        params.put("userId", getUserId());
        params.put("pageNo", mCount + "");
        params.put("pageSize", pageSize + "");
        params.put("posCode", posCode);
        params.put("posType", posType);
        HttpRequest.getEquipmentList(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                terminal_swipe_refresh_layout.setRefreshing(false);
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<TerminalBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<TerminalBean>>() {
                            }.getType());
                    //????????????????????????
                    if (isRefresh) {
                        //???????????????????????????????????????????????????????????????????????????
                        if (beans != null) {
                            beans.clear();
                        }
                    }
                    beans.addAll(memberList);
                    terminal_tv_number.setText(beans.size() + "");
                    if (memberList.size() < pageSize) {
                        adapter.loadMoreEnd();
                    } else {
                        adapter.loadMoreComplete();
                    }
                    adapter.notifyDataSetChanged();
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

    // ??????????????????
    public void postData2() {
        RequestParams params = new RequestParams();
        HttpRequest.getPosBrandTypeAll(params, getToken(), new ResponseCallback() {
            @Override
            public void onSuccess(Object responseObj) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<IntegralMostBean>>() {
                            }.getType());
                    showTitle = new ArrayList<>();
                    for (int i = 0; i < memberList.size() ; i++) {
                        menuList.add(memberList.get(i).getBrandName());
                        showTitle.add(i);
                    }
                    homeList.addAll(memberList.get(0).getPosTypeList());
                    madapter.notifyDataSetChanged();
                    madapter3.notifyDataSetChanged();
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

}

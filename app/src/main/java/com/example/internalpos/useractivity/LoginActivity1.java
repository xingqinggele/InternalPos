package com.example.internalpos.useractivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.internalpos.MainActivity;
import com.example.internalpos.R;
import com.example.internalpos.app.MyApp;
import com.example.internalpos.base.BaseActivity;
import com.example.internalpos.homefragment.hometeam.HomeTeamActivity;
import com.example.internalpos.homefragment.hometeam.adapter.HomeTeamAdapter;
import com.example.internalpos.homefragment.hometeam.bean.TeamBean;
import com.example.internalpos.net.HttpRequest;
import com.example.internalpos.net.OkHttpException;
import com.example.internalpos.net.RequestParams;
import com.example.internalpos.net.ResponseCallback;
import com.example.internalpos.net.Utils;
import com.example.internalpos.socket.JWebSocketClientService;
import com.example.internalpos.useractivity.adapter.LoginAdapter;
import com.example.internalpos.useractivity.bean.LPersonBean;
import com.example.internalpos.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ??????: qgl
 * ???????????????2020/12/10
 * ??????:????????????
 */
public class LoginActivity1 extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //????????????JWebSocket
    private JWebSocketClientService.JWebSocketClientBinder binder;
    //JWebSocket????????????
    private JWebSocketClientService jWebSClientService;
    //????????????
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MainActivity", "???????????????????????????");
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MainActivity", "???????????????????????????");
        }
    };

    private LinearLayout iv_back;
    private SwipeRefreshLayout login_swipe_refresh;
    private RecyclerView login_listview;
    private EditText login_person_ed_search;
    private List<LPersonBean> beans = new ArrayList<>();
    private LoginAdapter LoginAdapter;
    private String search_value;

    //xml??????
    @Override
    protected int getLayoutId() {
        // ?????????????????????
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.loginactivity_main1;
    }

    //???????????????
    @Override
    protected void initView() {
        mContext = LoginActivity1.this;
        //????????????
        bindService();

        iv_back = findViewById(R.id.iv_back);
        login_swipe_refresh = findViewById(R.id.login_swipe_refresh);
        login_listview = findViewById(R.id.login_listview);
        login_person_ed_search = findViewById(R.id.login_person_ed_search);
        search();
        initList();

    }

    @Override
    protected void initListener() {
        iv_back.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    private void initList() {
        //????????????
        login_swipe_refresh.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        //?????????????????????
        login_swipe_refresh.setOnRefreshListener(this);
        //adapter??????data
        LoginAdapter = new LoginAdapter(LoginActivity1.this, R.layout.item_home_team, beans);
        //??????????????????
        LoginAdapter.openLoadAnimation();
        //????????????????????????
        LoginAdapter.setEnableLoadMore(false);
        //??????????????????????????????
//        LoginAdapter.setOnLoadMoreListener(this, login_listview);
        //??????????????????xml
        LoginAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.list_empty, null));
        //RecyclerView?????????????????????
        login_listview.setLayoutManager(new LinearLayoutManager(this));
        //RecyclerView??????adapter
        login_listview.setAdapter(LoginAdapter);
        LoginAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                getLogin(beans.get(position).getMerchid());
            }
        });
        //????????????
        postData();
    }
    //???????????????
    private void postData() {
        RequestParams params = new RequestParams();
        //?????????
        params.put("username", search_value);
        // ??????????????????Token
        HttpRequest.getLogin(params, "", new ResponseCallback() {
            //????????????
            @Override
            public void onSuccess(Object responseObj) {
                login_swipe_refresh.setRefreshing(false);
                //???????????????????????????
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<LPersonBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<LPersonBean>>() {
                            }.getType());

                        //???????????????????????????????????????????????????????????????????????????
                        if (beans != null){
                            beans.clear();
                        }
                    //???adapter List ????????? list
                    beans.addAll(memberList);
                    LoginAdapter.loadMoreEnd();
                    //??????adapter
                    LoginAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //????????????
            @Override
            public void onFailure(OkHttpException failuer) {
                // ?????????????????????????????????
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }


    /**
     * ????????????
     *
     * @param userName ?????????
     *
     */
    public void getLogin(String userName) {
        // ???????????????
        loadDialog.show();
        RequestParams params = new RequestParams();
        //?????????
        params.put("username", userName);

        // ??????????????????Token
        HttpRequest.getLogin(params, "", new ResponseCallback() {
            //????????????
            @Override
            public void onSuccess(Object responseObj) {
                // ???????????????
                loadDialog.dismiss();
                try {
                    //String ?????? JSONObject
                    JSONObject result = new JSONObject(responseObj.toString());
                    //??????
                    String token = result.getString("token");
                    //???????????????
                    String ticket = result.getString("ticket");
                    //??????ID
                    String userId = result.getJSONObject("loginUser").getJSONObject("user").getString("userId");
                    //?????????????????????
                    SPUtils.put(LoginActivity1.this, "userName", userName);
                    //??????????????????
                    SPUtils.put(LoginActivity1.this, "Token", token);
                    //????????????????????????
                    SPUtils.put(LoginActivity1.this, "ticket", ticket);
                    //??????????????????ID
                    SPUtils.put(LoginActivity1.this, "userId", userId);
                    //???????????????ID
                    SPUtils.put(LoginActivity1.this, "secretId", result.getString("secretId"));
                    //?????????????????????
                    SPUtils.put(LoginActivity1.this, "secretKey", result.getString("secretKey"));
                    //???????????????????????????
                    SPUtils.put(LoginActivity1.this, "bucketName", result.getString("bucketName"));
                    //????????????
                    SPUtils.put(LoginActivity1.this, "createTime", result.getJSONObject("loginUser").getJSONObject("user").getString("createTime"));
                    //???????????????
                    startActivity(new Intent(LoginActivity1.this, MainActivity.class));
                    //????????????
                    startJWebSClientService();
                    //????????????????????????
                    checkNotification(mContext);
                    //??????WebSocket
                    jWebSClientService.startWebSocket(userId);
                    //?????????????????????
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //????????????
            @Override
            public void onFailure(OkHttpException failuer) {
                // ???????????????
                loadDialog.dismiss();
                // ?????????????????????????????????
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }


    /**
     * ????????????
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

        }
    }
/************************************--?????????????????????--**********************************************/
    /**
     * ????????????
     */
    private void bindService() {
        Intent bindIntent = new Intent(mContext, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * ???????????????websocket??????????????????
     */
    private void startJWebSClientService() {
        Intent intent = new Intent(mContext, JWebSocketClientService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0????????????startForegroundService??????service
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    /**
     * ????????????????????????
     *
     * @param context
     */
    private void checkNotification(final Context context) {
        if (!isNotificationEnabled(context)) {
            new AlertDialog.Builder(context).setTitle("????????????")
                    .setMessage("???????????????????????????????????????????????????????????????????????????")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setNotification(context);
                        }
                    }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param context
     */
    private void setNotification(Context context) {
        Intent localIntent = new Intent();
        //?????????????????????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            //4.4???????????????app????????????????????????????????????Action???????????????????????????????????????,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    /**
     * ??????????????????,?????????????????????????????????
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /************************************--?????????????????????--**********************************************/
    //????????????
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //??????????????????
        unbindService(serviceConnection);
    }


    @Override
    public void onRefresh() {
        search_value = "";
        setRefresh();
    }
    public void setRefresh() {
        login_swipe_refresh.setRefreshing(true);
        postData();
    }

    private void search() {
        login_person_ed_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // ????????????????????????????????????
                    Utils.hideKeyboard(login_person_ed_search);
                    search_value = v.getText().toString().trim();
                    setRefresh();
                    return true;
                }

                return false;
            }
        });
    }
}
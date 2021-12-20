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
 * 作者: qgl
 * 创建日期：2020/12/10
 * 描述:登录界面
 */
public class LoginActivity1 extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //客户绑定JWebSocket
    private JWebSocketClientService.JWebSocketClientBinder binder;
    //JWebSocket客户服务
    private JWebSocketClientService jWebSClientService;
    //服务连接
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MainActivity", "服务与活动成功绑定");
            binder = (JWebSocketClientService.JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MainActivity", "服务与活动成功断开");
        }
    };

    private LinearLayout iv_back;
    private SwipeRefreshLayout login_swipe_refresh;
    private RecyclerView login_listview;
    private EditText login_person_ed_search;
    private List<LPersonBean> beans = new ArrayList<>();
    private LoginAdapter LoginAdapter;
    private String search_value;

    //xml界面
    @Override
    protected int getLayoutId() {
        // 设置状态栏颜色
        statusBarConfig(R.color.new_theme_color,false).init();
        return R.layout.loginactivity_main1;
    }

    //初始化控件
    @Override
    protected void initView() {
        mContext = LoginActivity1.this;
        //绑定服务
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
        //下拉样式
        login_swipe_refresh.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        //上拉刷新初始化
        login_swipe_refresh.setOnRefreshListener(this);
        //adapter配置data
        LoginAdapter = new LoginAdapter(LoginActivity1.this, R.layout.item_home_team, beans);
        //打开加载动画
        LoginAdapter.openLoadAnimation();
        //设置启用加载更多
        LoginAdapter.setEnableLoadMore(false);
        //设置为加载更多监听器
//        LoginAdapter.setOnLoadMoreListener(this, login_listview);
        //数据为空显示xml
        LoginAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.list_empty, null));
        //RecyclerView设置布局管理器
        login_listview.setLayoutManager(new LinearLayoutManager(this));
        //RecyclerView配置adapter
        login_listview.setAdapter(LoginAdapter);
        LoginAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                getLogin(beans.get(position).getMerchid());
            }
        });
        //请求数据
        postData();
    }
    //请求所有人
    private void postData() {
        RequestParams params = new RequestParams();
        //用户名
        params.put("username", search_value);
        // 登录时不需要Token
        HttpRequest.getLogin(params, "", new ResponseCallback() {
            //成功回调
            @Override
            public void onSuccess(Object responseObj) {
                login_swipe_refresh.setRefreshing(false);
                //需要转化为实体对象
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<LPersonBean> memberList = gson.fromJson(result.getJSONArray("data").toString(),
                            new TypeToken<List<LPersonBean>>() {
                            }.getType());

                        //判断数组是否为空、为空不需要清空，不为空才需要清空
                        if (beans != null){
                            beans.clear();
                        }
                    //在adapter List 中添加 list
                    beans.addAll(memberList);
                    LoginAdapter.loadMoreEnd();
                    //更新adapter
                    LoginAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //失败回调
            @Override
            public void onFailure(OkHttpException failuer) {
                // 根据失败返回码返回操作
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }


    /**
     * 用户登录
     *
     * @param userName 用户名
     *
     */
    public void getLogin(String userName) {
        // 开启加载框
        loadDialog.show();
        RequestParams params = new RequestParams();
        //用户名
        params.put("username", userName);

        // 登录时不需要Token
        HttpRequest.getLogin(params, "", new ResponseCallback() {
            //成功回调
            @Override
            public void onSuccess(Object responseObj) {
                // 关闭加载框
                loadDialog.dismiss();
                try {
                    //String 转换 JSONObject
                    JSONObject result = new JSONObject(responseObj.toString());
                    //秘钥
                    String token = result.getString("token");
                    //待用的秘钥
                    String ticket = result.getString("ticket");
                    //用户ID
                    String userId = result.getJSONObject("loginUser").getJSONObject("user").getString("userId");
                    //本地存储用户名
                    SPUtils.put(LoginActivity1.this, "userName", userName);
                    //本地存储秘钥
                    SPUtils.put(LoginActivity1.this, "Token", token);
                    //本地存储待用秘钥
                    SPUtils.put(LoginActivity1.this, "ticket", ticket);
                    //本地存储用户ID
                    SPUtils.put(LoginActivity1.this, "userId", userId);
                    //存储腾讯云ID
                    SPUtils.put(LoginActivity1.this, "secretId", result.getString("secretId"));
                    //存储腾讯云密钥
                    SPUtils.put(LoginActivity1.this, "secretKey", result.getString("secretKey"));
                    //存储腾讯存储桶名称
                    SPUtils.put(LoginActivity1.this, "bucketName", result.getString("bucketName"));
                    //注册时间
                    SPUtils.put(LoginActivity1.this, "createTime", result.getJSONObject("loginUser").getJSONObject("user").getString("createTime"));
                    //跳转到主页
                    startActivity(new Intent(LoginActivity1.this, MainActivity.class));
                    //启动服务
                    startJWebSClientService();
                    //检测通知是否开启
                    checkNotification(mContext);
                    //启动WebSocket
                    jWebSClientService.startWebSocket(userId);
                    //关闭当前登录页
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //失败回调
            @Override
            public void onFailure(OkHttpException failuer) {
                // 关闭加载框
                loadDialog.dismiss();
                // 根据失败返回码返回操作
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });
    }


    /**
     * 点击事件
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
/************************************--长连接方法开始--**********************************************/
    /**
     * 绑定服务
     */
    private void bindService() {
        Intent bindIntent = new Intent(mContext, JWebSocketClientService.class);
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 启动服务（websocket客户端服务）
     */
    private void startJWebSClientService() {
        Intent intent = new Intent(mContext, JWebSocketClientService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android8.0以上通过startForegroundService启动service
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    /**
     * 检测是否开启通知
     *
     * @param context
     */
    private void checkNotification(final Context context) {
        if (!isNotificationEnabled(context)) {
            new AlertDialog.Builder(context).setTitle("温馨提示")
                    .setMessage("你还未开启系统通知，将影响消息的接收，要去开启吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setNotification(context);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    /**
     * 如果没有开启通知，跳转至设置界面
     *
     * @param context
     */
    private void setNotification(Context context) {
        Intent localIntent = new Intent();
        //直接跳转到应用通知设置的代码：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
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
     * 获取通知权限,监测是否开启了系统通知
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

    /************************************--长连接方法结束--**********************************************/
    //界面销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除服务绑定
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
                    // 当按了搜索之后关闭软键盘
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
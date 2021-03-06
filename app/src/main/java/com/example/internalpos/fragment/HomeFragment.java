package com.example.internalpos.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.internalpos.MainActivity;
import com.example.internalpos.R;
import com.example.internalpos.base.BaseFragment;
import com.example.internalpos.fragment.bean.HomeBean;
import com.example.internalpos.homefragment.homeInvitepartners.HomeInvitePartnersActivity;
import com.example.internalpos.homefragment.homebagactivity.HomeBagActivity;
import com.example.internalpos.homefragment.homeequipment.HomeEquipmentActivity;
import com.example.internalpos.homefragment.homeintegral.HomeIntegralActivity;
import com.example.internalpos.homefragment.homelist.HomeListActivity;
import com.example.internalpos.homefragment.homemerchants.homenewmerchants.merchantstype.RealNameOnActivity;
import com.example.internalpos.homefragment.homemerchants.memerchants.activity.MeMerchantsActivity;
import com.example.internalpos.homefragment.homemessage.HomeMessageActivity;
import com.example.internalpos.homefragment.hometeam.HomeTeamActivity;
import com.example.internalpos.net.HttpRequest;
import com.example.internalpos.net.OkHttpException;
import com.example.internalpos.net.RequestParams;
import com.example.internalpos.net.ResponseCallback;
import com.example.internalpos.useractivity.HomeAdvPictureActivity;
import com.example.internalpos.utils.GlideImageLoader;
import com.example.internalpos.utils.SPUtils;
import com.example.internalpos.views.MyDialog1;
import com.example.internalpos.views.VpSwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * ??????: qgl
 * ???????????????2020/12/10
 * ??????:?????????Fragment
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    //????????????
    private VpSwipeRefreshLayout main_home_refresh;
    //?????????
    private String nickName;
    //?????????
    private String merchCode;
    //??????
    private String Token;
    //??????ID
    private String userId;
    //???????????????
    private String monthlyTransAmount;
    //???????????????
    private String monthlyNewPartnerCounts;
    //??????????????????
    private String monthlyNewMerchantCounts;
    //?????????
    private TextView main_home_price;
    //????????????
    private TextView main_home_new_partner;
    //????????????
    private TextView main_home_new_merchants;
    //?????????
    private Banner banner;
    //?????????2
    private Banner banner1;
    //?????????3
    private Banner banner2;
    //????????????
    private LinearLayout main_home_me_merchants;
    //????????????
    private LinearLayout main_home_invite_partners;
    //????????????
    private LinearLayout main_home_my_partner;
    //????????????
    private LinearLayout main_home_terminal_management;
    //?????????
    private LinearLayout main_home_list;
    //????????????
    private LinearLayout main_home_real_name;
    //????????????
    private LinearLayout main_home_gift_bag;
    //????????????
    private LinearLayout main_home_integral;
    //????????????1
    private ImageView main_home_message1;
    //????????????1
    private TextView main_home_name_tv1;
    //???????????????1??????2?????????
    private String Code = "1";
    //????????????????????????
    private List<HomeBean> list = new ArrayList<>();
    //????????????????????????
    private List<HomeBean> list2 = new ArrayList<>();
    //????????????????????????
    private List<HomeBean> list3 = new ArrayList<>();
    //?????????????????????
    private LinearLayout line_one;
    //??????????????????
    private LinearLayout line_two;
    //??????????????????
    private LinearLayout line_three;

    /**
     * ??????activity??????
     *
     * @param requestJson
     * @return
     */
    public static HomeFragment newInstance(String requestJson) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * ?????????xml
     *
     * @return
     */
    @Override
    protected int getLayoutInflaterResId() {
        return R.layout.home_fragment_main;
    }

    /**
     * ???????????????
     *
     * @param rootView
     */
    @Override
    protected void initView(View rootView) {
        EventBus.getDefault().register(this);
        main_home_refresh = rootView.findViewById(R.id.main_home_refresh);
        banner = rootView.findViewById(R.id.banner);
        banner1 = rootView.findViewById(R.id.banner1);
        banner2 = rootView.findViewById(R.id.banner2);
        main_home_name_tv1 = rootView.findViewById(R.id.main_home_name_tv1);
        main_home_price = rootView.findViewById(R.id.main_home_price);
        main_home_new_partner = rootView.findViewById(R.id.main_home_new_partner);
        main_home_new_merchants = rootView.findViewById(R.id.main_home_new_merchants);
        main_home_me_merchants = rootView.findViewById(R.id.main_home_me_merchants);
        main_home_invite_partners = rootView.findViewById(R.id.main_home_invite_partners);
        main_home_my_partner = rootView.findViewById(R.id.main_home_my_partner);
        main_home_terminal_management = rootView.findViewById(R.id.main_home_terminal_management);
        main_home_list = rootView.findViewById(R.id.main_home_list);
        main_home_real_name = rootView.findViewById(R.id.main_home_real_name);
        main_home_gift_bag = rootView.findViewById(R.id.main_home_gift_bag);
        main_home_integral = rootView.findViewById(R.id.main_home_integral);
        main_home_message1 = rootView.findViewById(R.id.main_home_message1);
        line_one = rootView.findViewById(R.id.line_one);
        line_two = rootView.findViewById(R.id.line_two);
        line_three = rootView.findViewById(R.id.line_three);
    }

    //????????????
    @Override
    protected void loadData() {
        //???????????????????????????
        Token = SPUtils.get(getActivity(), "Token", "").toString();
        //??????????????????ID
        userId = SPUtils.get(getActivity(), "userId", "").toString();
        //??????????????????
        posData();
        //???????????????
        GetAdvertising();
    }

    //?????????????????????
    @Override
    protected void initListener() {
        main_home_me_merchants.setOnClickListener(this);
        main_home_invite_partners.setOnClickListener(this);
        main_home_my_partner.setOnClickListener(this);
        main_home_terminal_management.setOnClickListener(this);
        main_home_list.setOnClickListener(this);
        main_home_real_name.setOnClickListener(this);
        main_home_gift_bag.setOnClickListener(this);
        main_home_integral.setOnClickListener(this);
        main_home_message1.setOnClickListener(this);
        line_one.setOnClickListener(this);
        line_two.setOnClickListener(this);
        line_three.setOnClickListener(this);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (!list.get(position).getNewsUrl().equals("")) {
                    Intent intent = new Intent(getActivity(), HomeAdvPictureActivity.class);
                    intent.putExtra("title", list.get(position).getNewsTitle());
                    intent.putExtra("iv", list.get(position).getNewsUrl());
                    startActivity(intent);
                }
            }
        });
        banner1.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
//                    Intent intent = new Intent(getActivity(), HomeAdvPictureDeleteActivity.class);
//                    startActivity(intent);
                Intent intent = new Intent(getActivity(), HomeAdvPictureActivity.class);
                intent.putExtra("title", "???????????????");
                intent.putExtra("iv", "https://cykj-1303987307.cos.ap-beijing.myqcloud.com/advertising/bankadetail2.png");
                startActivity(intent);
            }
        });
        banner2.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (!list3.get(position).getNewsUrl().equals("")) {
                    Intent intent = new Intent(getActivity(), HomeAdvPictureActivity.class);
                    intent.putExtra("title", list3.get(position).getNewsTitle());
                    intent.putExtra("iv", list3.get(position).getNewsUrl());
                    startActivity(intent);

                }
            }
        });
        //????????????????????????
        SwipeData();
    }

    //??????????????????
    public void SwipeData() {
        //????????????
        main_home_refresh.setColorSchemeResources(R.color.new_theme_color, R.color.green, R.color.colorAccent);
        //????????????
        main_home_refresh.setOnRefreshListener(this);
    }

    // ????????????
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //????????????
            case R.id.main_home_me_merchants:
                if (Code.equals("2")) {
                    showDialog();
                } else {
                    startActivity(new Intent(getActivity(), MeMerchantsActivity.class));
                }
                break;
            //????????????
            case R.id.main_home_invite_partners:
                if (Code.equals("2")) {
                    showDialog();
                } else {
                    startActivity(new Intent(getActivity(), HomeInvitePartnersActivity.class));
                }
                break;
            //????????????
            case R.id.main_home_my_partner:
                if (Code.equals("2")) {
                    showDialog();
                } else {
                    startActivity(new Intent(getActivity(), HomeTeamActivity.class));
                }
                break;
            //????????????
            case R.id.main_home_terminal_management:
                if (Code.equals("2")) {
                    showDialog();
                } else {
                    startActivity(new Intent(getActivity(), HomeEquipmentActivity.class));
                }
                break;
            //?????????
            case R.id.main_home_list:
                if (Code.equals("2")) {
                    showDialog();
                } else {
                    startActivity(new Intent(getActivity(), HomeListActivity.class));
                }
                break;
            //????????????
            case R.id.main_home_real_name:
                if (Code.equals("2")) {
                    //??????????????????????????????
                    Intent intent = new Intent(getActivity(), RealNameOnActivity.class);
                    intent.putExtra("infoCode", Code);
                    startActivity(intent);
                } else {
                    showToast("??????????????????");
                }
                break;
            //????????????
            case R.id.main_home_gift_bag:
                if (Code.equals("2")) {
                    showDialog();
                } else {
                    startActivity(new Intent(getActivity(), HomeBagActivity.class));
//                    startActivity(new Intent(getActivity(), ShoppingListActivity.class));
                }
                break;
            // ????????????
            case R.id.main_home_integral:
                if (Code.equals("2")) {
                    showDialog();
                } else {
                    startActivity(new Intent(getActivity(), HomeIntegralActivity.class));
                }
                break;
            //??????
            case R.id.main_home_message1:
                if (Code.equals("2")) {
                    showDialog();
                } else {
                    startActivity(new Intent(getActivity(), HomeMessageActivity.class));
                }
                break;
            case R.id.line_one:
            case R.id.line_two:
            case R.id.line_three:
                ((MainActivity) getActivity()).homeFragment();
                break;
        }
    }

    //??????
    @Override
    public void onRefresh() {
        //??????????????????
        main_home_refresh.setRefreshing(true);
        //??????????????????
        posData();
        //???????????????
        GetAdvertising();
    }

    //????????????????????????
    public void posData() {
        RequestParams params = new RequestParams();
        //??????ID
        params.put("userId", userId);
        //?????? params???token ?????????
        HttpRequest.getHomeDate(params, Token, new ResponseCallback() {
            //????????????
            @Override
            public void onSuccess(Object responseObj) {
                //???????????????
                main_home_refresh.setRefreshing(false);
                try {
                    //??????????????????
                    Code = "1";
                    //String ??? JSONObject
                    JSONObject result = new JSONObject(responseObj.toString());
                    //???????????????
                    monthlyTransAmount = new BigDecimal(result.getJSONObject("data").getString("monthlyTransAmount")).toPlainString();
                    //???????????????
                    monthlyNewPartnerCounts = result.getJSONObject("data").getString("monthlyNewPartnerCounts");
                    //??????????????????
                    monthlyNewMerchantCounts = result.getJSONObject("data").getString("monthlyNewMerchantCounts");
                    //??????
                    nickName = result.getJSONObject("data").getString("nickName");
                    //?????????
                    merchCode = result.getJSONObject("data").getString("merchCode");
                    //??????????????????
                    SPUtils.put(getActivity(), "nickName", nickName);
                    //?????????????????????
                    SPUtils.put(getActivity(), "merchCode", merchCode);
                    //??????????????????
                    setText();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //????????????
            @Override
            public void onFailure(OkHttpException failuer) {
                //???????????????
                main_home_refresh.setRefreshing(false);
                //?????????500001 ??????????????????????????????
                if (failuer.getEcode() == 500001) {
                    //??????????????????
                    Code = "2";
                    //??????????????????dialog
                    showDialog();
                } else {
                    //???????????????????????????
                    Failuer(failuer.getEcode(), failuer.getEmsg());
                }
            }
        });

    }

    //??????????????????
    private void setText() {
        main_home_name_tv1.setText("Hi???" + nickName);
//        main_home_name_tv2.setText("Hi???" + nickName);
        main_home_price.setText(monthlyTransAmount);
        main_home_new_partner.setText(monthlyNewPartnerCounts);
        main_home_new_merchants.setText(monthlyNewMerchantCounts);
    }

    /**
     * ??????????????????????????????????????????
     */
    public void showDialog() {
        //dialog xml
        View view = LayoutInflater.from(mContext).inflate(R.layout.perfect_dialog_layout, null);
        //???????????????
        Button perfect_into = view.findViewById(R.id.perfect_into);
        ImageView perfect_out = view.findViewById(R.id.perfect_out);
        //??????dialog????????????
        Dialog dialog = new MyDialog1(getActivity(), true, true, (float) 0.7).setNewView(view);
        //????????????dialog??????????????????
        dialog.setCanceledOnTouchOutside(false);
        //??????
        dialog.show();
        //????????????
        perfect_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????dialog
                dialog.dismiss();
            }
        });
        //????????????
        perfect_into.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????dialog
                dialog.dismiss();
                //???????????????????????????
                Intent intent = new Intent(getActivity(), RealNameOnActivity.class);
                startActivity(intent);
            }
        });
    }

    //????????????????????????????????????????????????
    public void onEventMainThread(HomeFragment ev) {
        //????????????
        onRefresh();
    }

    //???????????????
    private void GetAdvertising() {
        RequestParams params = new RequestParams();
        HttpRequest.getAdvertising(params, Token, new ResponseCallback() {
            //????????????
            @Override
            public void onSuccess(Object responseObj) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    JSONObject result = new JSONObject(responseObj.toString());
                    List<HomeBean> memberList = gson.fromJson(result.getJSONArray("data0").toString(),
                            new TypeToken<List<HomeBean>>() {
                            }.getType());
                    list.clear();
                    list2.clear();
                    list3.clear();
                    list.addAll(memberList);
                    //1
                    List<String> list_path1 = new ArrayList<>();
                    for (int i = 0; i < memberList.size(); i++) {
                        list_path1.add(memberList.get(i).getAdvPicture());
                    }
                    banner.setImages(list_path1)
                            .setImageLoader(new GlideImageLoader())
                            .setBannerAnimation(Transformer.Default)
                            //????????????????????????
                            .setDelayTime(5000)
                            //???????????????????????????????????????????????????
                            .isAutoPlay(true)
                            .start();
                    //2
                    List<HomeBean> memberList2 = gson.fromJson(result.getJSONArray("data1").toString(),
                            new TypeToken<List<HomeBean>>() {
                            }.getType());
                    list2.addAll(memberList2);
                    List<String> list_path2 = new ArrayList<>();
                    for (int i = 0; i < memberList2.size(); i++) {
                        list_path2.add(memberList2.get(i).getAdvPicture());
                    }
                    banner1.setImages(list_path2)
                            .setImageLoader(new GlideImageLoader())
                            .setBannerAnimation(Transformer.Default)
                            //????????????????????????
                            .setDelayTime(5000)
                            //???????????????????????????????????????????????????
                            .isAutoPlay(true)
                            .start();
                    //3
                    List<HomeBean> memberList3 = gson.fromJson(result.getJSONArray("data2").toString(),
                            new TypeToken<List<HomeBean>>() {
                            }.getType());
                    list3.addAll(memberList3);
                    List<String> list_path3 = new ArrayList<>();
                    for (int i = 0; i < memberList3.size(); i++) {
                        list_path3.add(memberList3.get(i).getAdvPicture());
                    }
                    banner2.setImages(list_path3)
                            .setImageLoader(new GlideImageLoader())
                            .setBannerAnimation(Transformer.Default)
                            //????????????????????????
                            .setDelayTime(5000)
                            //???????????????????????????????????????????????????
                            .isAutoPlay(true)
                            .start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            //????????????
            @Override
            public void onFailure(OkHttpException failuer) {
                Failuer(failuer.getEcode(), failuer.getEmsg());
            }
        });


    }


    /**
     * ????????????
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //??????EventBus??????
        EventBus.getDefault().unregister(this);
    }



}

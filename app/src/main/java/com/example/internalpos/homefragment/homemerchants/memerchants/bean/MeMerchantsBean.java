package com.example.internalpos.homefragment.homemerchants.memerchants.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 作者: qgl
 * 创建日期：2021/3/9
 * 描述:我的商户Bean
 */
public class MeMerchantsBean implements Serializable {
    @SerializedName("id")  // 根据接口自定义
    private String id;  //商户ID
    @SerializedName("merchantName")  // 根据接口自定义
    private String name;  //商户姓名
    @SerializedName("transAmount")  // 根据接口自定义
    private String monthTurnover;  //本月交易额

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonthTurnover() {
        return monthTurnover;
    }

    public void setMonthTurnover(String monthTurnover) {
        this.monthTurnover = monthTurnover;
    }

    @Override
    public String toString() {
        return "MeMerchantsBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", monthTurnover='" + monthTurnover + '\'' +
                '}';
    }
}

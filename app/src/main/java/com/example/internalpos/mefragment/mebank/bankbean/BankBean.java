package com.example.internalpos.mefragment.mebank.bankbean;

/**
 * 作者: qgl
 * 创建日期：2021/10/9
 * 描述:银行名称Bean
 */
public class BankBean {
    private String bankName;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return bankName;
    }
}
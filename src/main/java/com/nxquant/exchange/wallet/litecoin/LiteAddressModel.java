package com.nxquant.exchange.wallet.litecoin;

/**
 * Created by Administrator on 2018-06-07.
 */
public class LiteAddressModel {
    private String address;
    private Double amount;
    private String account;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

}

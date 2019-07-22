package com.nxquant.exchange.wallet.model;

/**
 * Created by Administrator on 2018-09-11.
 */
public class RippleAccountModel {
    private String account;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSecrete() {
        return secrete;
    }

    public void setSecrete(String secrete) {
        this.secrete = secrete;
    }

    private String secrete;
}

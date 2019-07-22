package com.nxquant.exchange.wallet.model;

/**
 * Created by Administrator on 2019-01-16.
 */
public class Receipt {
    private long gas;

    public long getGas() {
        return gas;
    }

    public void setGas(long gas) {
        this.gas = gas;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private boolean status;
}

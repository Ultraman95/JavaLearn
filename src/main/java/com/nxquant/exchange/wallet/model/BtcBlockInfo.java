package com.nxquant.exchange.wallet.model;

import java.util.HashMap;

public class BtcBlockInfo{
    /**
     * 以下两个为了支持多对多输入输出模式
     */
    private HashMap<String, Double> fromValue = new HashMap<String, Double>();
    private HashMap<String, Double> toValue = new HashMap<String, Double>();
    private String txid;
    private long blockNo;
    private double fee;

    public HashMap<String, Double> getFromValue() {
        return fromValue;
    }

    public HashMap<String, Double> getToValue() {
        return toValue;
    }

    public void addFromValue(String addr, double value) {
        this.fromValue.put(addr, value);
    }
    public void addToValue(String addr, double value) {
        this.toValue.put(addr, value);
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public long getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(long blockNo) {
        this.blockNo = blockNo;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}

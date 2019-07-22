package com.nxquant.exchange.wallet.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-06-13.
 */
public class TransactionInfo {
    private int conformations;
    private  String txid;

    public int getConformations() {
        return conformations;
    }

    public void setConformations(int conformations) {
        this.conformations = conformations;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public ArrayList<Double> getValue() {
        return value;
    }

    public void addValue(Double value) {
        this.value.add(value);
    }

    public ArrayList<String> getToAddress() {
        return toAddress;
    }

    public void addToAddress(String toAddress) {
        this.toAddress.add(toAddress);
    }

    private ArrayList<Double> value = new ArrayList<Double>();
    private ArrayList<String> toAddress = new ArrayList<String> ();
}

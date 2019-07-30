package com.nxquant.exchange.wallet.model;

import java.math.BigInteger;

public class BlockInfo {
    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Long getPropertyID() { return propertyID; }

    public void setPropertyID(Long propertyID) { this.propertyID = propertyID; }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public BigInteger getContractValue() {
        return contractValue;
    }

    public void setContractValue(BigInteger contractValue) {
        this.contractValue = contractValue;
    }

    public boolean isContractTx() {
        return isContractTx;
    }

    public void setContractTx(boolean contractTx) {
        isContractTx = contractTx;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    private String txid;
    private long blockNo;
    private double value;
    private String fromAddress;
    private String toAddress;
    private String contractAddress;
    private BigInteger contractValue;
    private double fee;
    private boolean isContractTx;
    //资产类型31为USDT
    private Long propertyID;
}
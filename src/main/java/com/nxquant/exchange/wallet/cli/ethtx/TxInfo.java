package com.nxquant.exchange.wallet.cli.ethtx;

public class TxInfo {
    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    private String fromAddress;
    private String toAddress;
    private String contract;
    private Integer tokenPrecision;
    private String amount;
    private String txid;

    public Integer getTokenPrecision() {
        return tokenPrecision;
    }

    public void setTokenPrecision(Integer tokenPrecision) {
        this.tokenPrecision = tokenPrecision;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }
}

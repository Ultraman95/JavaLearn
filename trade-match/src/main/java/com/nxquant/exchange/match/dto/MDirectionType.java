package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * MDirectionType
 */
public enum MDirectionType implements Serializable {
    //买
    DT_BUY("buy", 0),
    //卖
    DT_SELL("sell", 1);


    private String key;
    private int value;


    MDirectionType(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}

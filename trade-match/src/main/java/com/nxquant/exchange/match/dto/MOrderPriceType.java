package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * MOrderPriceType
 */
public enum MOrderPriceType implements Serializable {
    //限价单
    OPT_LIMIT("limit", 0),
    //市价单
    OPT_MARKET("market", 1);


    private String key;
    private int value;


    MOrderPriceType(String key, int value) {
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
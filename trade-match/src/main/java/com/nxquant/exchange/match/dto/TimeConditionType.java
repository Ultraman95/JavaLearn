package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * TimeConditionType
 */
public enum TimeConditionType implements Serializable {
    //正常单
    TCT_GTC("gtc", 0),
    //立即成交否则撤单
    TCT_IOC("ioc", 1),
    //全部成交否则撤单
    TCT_FOK("fok", 2);


    private String key;
    private int value;


    TimeConditionType(String key, int value) {
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

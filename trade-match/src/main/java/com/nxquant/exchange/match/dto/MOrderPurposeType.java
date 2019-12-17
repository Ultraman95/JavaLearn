package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * MOrderPurposeType
 */
public enum MOrderPurposeType implements Serializable {
    //正常单
    OPT_NORMAL("normal", 0),
    //被动委托
    OPT_POSTONLY("postOnly", 1);


    private String key;
    private int value;


    MOrderPurposeType(String key, int value) {
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

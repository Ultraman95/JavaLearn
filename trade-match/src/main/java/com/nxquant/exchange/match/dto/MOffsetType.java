package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * MOffsetType
 */
public enum MOffsetType implements Serializable {
    //开仓
    OT_OPEN("open", 0),
    //平仓
    OT_CLOSE("close", 1);


    private String key;
    private int value;


    MOffsetType(String key, int value) {
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

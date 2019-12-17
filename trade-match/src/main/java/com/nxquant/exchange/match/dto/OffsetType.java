package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * OffsetType
 */
public enum OffsetType implements Serializable {
    //开仓
    OT_OPEN("open", 0),
    //平仓
    OT_CLOSE("close", 1);


    private String key;
    private int value;


    OffsetType(String key, int value) {
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

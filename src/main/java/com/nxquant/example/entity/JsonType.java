package com.nxquant.example.entity;

import java.io.Serializable;

/**
 *  json方式
 */
public enum JsonType implements Serializable {
    //gson
    JT_GSON("gson", 0),
    //jackson
    JT_JACKSON("jackson", 1);


    private String key;
    private int value;


    JsonType(String key, int value) {
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

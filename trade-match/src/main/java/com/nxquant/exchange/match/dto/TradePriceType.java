package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * 成交价生成方式
 */
public enum TradePriceType implements Serializable {
    //对手价
    TCT_OPPONENT("opponent", 0),
    //三价取中
    TPT_THIRDMIDDLE("thirdMiddle", 1);


    private String key;
    private int value;


    TradePriceType(String key, int value) {
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

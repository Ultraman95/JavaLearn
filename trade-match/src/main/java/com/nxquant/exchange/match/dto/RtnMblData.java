package com.nxquant.exchange.match.dto;


public class RtnMblData implements IRtnInfo{
    private MblOpType mblOpType;

    private long volume;

    public MblOpType getMblOpType() {
        return mblOpType;
    }

    public void setMblOpType(MblOpType mblOpType) {
        this.mblOpType = mblOpType;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }
}

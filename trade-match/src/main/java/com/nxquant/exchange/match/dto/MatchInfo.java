package com.nxquant.exchange.match.dto;

public class MatchInfo {
    private MOrder matchOrder;
    private long remainVolume;

    public MOrder getMatchOrder() {
        return matchOrder;
    }

    public void setMatchOrder(MOrder matchOrder) {
        this.matchOrder = matchOrder;
    }

    public long getRemainVolume() {
        return remainVolume;
    }

    public void setRemainVolume(long remainVolume) {
        this.remainVolume = remainVolume;
    }
}

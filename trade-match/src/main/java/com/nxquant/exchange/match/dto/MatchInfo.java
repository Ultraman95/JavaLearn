package com.nxquant.exchange.match.dto;

public class MatchInfo {
    private Order matchOrder;
    private long remainVolume;

    public Order getMatchOrder() {
        return matchOrder;
    }

    public void setMatchOrder(Order matchOrder) {
        this.matchOrder = matchOrder;
    }

    public long getRemainVolume() {
        return remainVolume;
    }

    public void setRemainVolume(long remainVolume) {
        this.remainVolume = remainVolume;
    }
}

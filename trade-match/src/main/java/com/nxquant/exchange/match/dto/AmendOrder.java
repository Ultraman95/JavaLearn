package com.nxquant.exchange.match.dto;

public class AmendOrder implements Info{
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object order) {
        AmendOrder tmpOrder = (AmendOrder)order;
        return this.orderId.equals(tmpOrder.getOrderId());
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }

}

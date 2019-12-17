package com.nxquant.exchange.base.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author shilf
 * 订单类
 */
public class Order implements Serializable {
    private String instrumentId;

    private Long orderId;

    private Long comPrice;

    private BigDecimal price;

    private Long createTs;

    private Long incId;

    private Long volume;



    public Order(String instrumentId, Long orderId, BigDecimal price, Long comPrice, Long volume, Long incId){
        this.instrumentId = instrumentId;
        this.orderId = orderId;
        this.price = price;
        this.comPrice = comPrice;
        this.volume = volume;
        this.incId = incId;
    }

    @Override
    public boolean equals(Object order) {
        Order tmpOrder = (Order)order;
        return this.orderId.equals(tmpOrder.getOrderId());
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getIncId() {
        return incId;
    }

    public void setIncId(Long incId) {
        this.incId = incId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public Long getComPrice() {
        return comPrice;
    }

    public void setComPrice(Long comPrice) {
        this.comPrice = comPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Long createTs) {
        this.createTs = createTs;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

}

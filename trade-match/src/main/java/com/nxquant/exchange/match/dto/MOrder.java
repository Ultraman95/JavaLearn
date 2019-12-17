package com.nxquant.exchange.match.dto;

import java.math.BigDecimal;

/**
 * @author shilf
 * 撮合订单类
 */
public class MOrder implements Info {

    private String instrumentId;

    private Long orderId;

    private String orderLocalId;

    private Long comPrice;

    private BigDecimal price;

    private Long volume;

    private MOrderPriceType priceType;

    private MDirectionType direction;

    private MOffsetType offset;

    private MTimeConditionType timeCondition;

    private MOrderType orderType;

    private MOrderStatus orderStatus;

    private Long tradedVolume;

    private Long displayVolume;

    private MOrderPurposeType purposeType;

    private String clientId;

    private Long minBoundPrice;

    private Long maxBoundPrice;

    private Long inputTs;

    private Long createTs;

    private Long updateTs;

    private Long incId;

    public MOrder(){}

    @Override
    public boolean equals(Object order) {
        MOrder tmpMOrder = (MOrder)order;
        return this.orderId.equals(tmpMOrder.getOrderId());
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

    public MOrderPriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(MOrderPriceType priceType) {
        this.priceType = priceType;
    }

    public MDirectionType getDirection() {
        return direction;
    }

    public void setDirection(MDirectionType direction) {
        this.direction = direction;
    }

    public MOffsetType getOffset() {
        return offset;
    }

    public void setOffset(MOffsetType offset) {
        this.offset = offset;
    }

    public MTimeConditionType getTimeCondition() {
        return timeCondition;
    }

    public void setTimeCondition(MTimeConditionType timeCondition) {
        this.timeCondition = timeCondition;
    }

    public MOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(MOrderType orderType) {
        this.orderType = orderType;
    }

    public Long getInputTs() {
        return inputTs;
    }

    public void setInputTs(Long inputTs) {
        this.inputTs = inputTs;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getTradedVolume() {
        return tradedVolume;
    }

    public void setTradedVolume(Long tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    public Long getDisplayVolume() {
        return displayVolume;
    }

    public void setDisplayVolume(Long displayVolume) {
        this.displayVolume = displayVolume;
    }

    public MOrderPurposeType getPurposeType() {
        return purposeType;
    }

    public void setPurposeType(MOrderPurposeType purposeType) {
        this.purposeType = purposeType;
    }

    public String getOrderLocalId() {
        return orderLocalId;
    }

    public void setOrderLocalId(String orderLocalId) {
        this.orderLocalId = orderLocalId;
    }

    public MOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(MOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Long getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(Long updateTs) {
        this.updateTs = updateTs;
    }

    public Long getMinBoundPrice() {
        return minBoundPrice;
    }

    public void setMinBoundPrice(Long minBoundPrice) {
        this.minBoundPrice = minBoundPrice;
    }

    public Long getMaxBoundPrice() {
        return maxBoundPrice;
    }

    public void setMaxBoundPrice(Long maxBoundPrice) {
        this.maxBoundPrice = maxBoundPrice;
    }
}

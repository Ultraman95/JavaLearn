package com.nxquant.exchange.match.dto;

/**
 * @author shilf
 * 超级订单簿
 */
public class ExOrderBook implements Info {
    private String instrumentId;
    private OrderBook orderBook;
    private MarketData marketData;
    private TopicPartitionPosition inputTpp;
    private TopicPartitionPosition incTpp;

    public ExOrderBook(String instrumentId){
        this.instrumentId = instrumentId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    public MarketData getMarketData() {
        return marketData;
    }

    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }
}

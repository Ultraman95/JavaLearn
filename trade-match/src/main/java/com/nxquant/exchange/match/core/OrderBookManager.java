package com.nxquant.exchange.match.core;

import com.nxquant.exchange.match.dto.*;

import java.util.*;

/**
 * @author shilf
 * 订单簿管理器
 */
public class OrderBookManager {
    private Map<String, ExOrderBook> exOrderBookMap = new HashMap<>();
    private Map<Long, Order> cacheOrderMap = new HashMap<>();
    private List<IRtnInfo> rtnInfoList = new ArrayList<>();

    void init(List<ExOrderBook> exOrderBookList){
        if(exOrderBookList != null) {
            for (ExOrderBook exOrderBook : exOrderBookList) {
                addExOrderBookToOrderBookMap(exOrderBook);
                addExOrderBookToCacheOrderMap(exOrderBook);
            }
        }
    }

    private void addExOrderBookToOrderBookMap(ExOrderBook exOrderBook){
        exOrderBookMap.put(exOrderBook.getInstrumentId(), exOrderBook);
    }

    private void addExOrderBookToCacheOrderMap(ExOrderBook exOrderBook){
        if(exOrderBook.getOrderBook() != null) {
            for(PriceBook priceBook : exOrderBook.getOrderBook().getBuyOrders().values()){
                for(Order order : priceBook.getOrderSet()){
                    cacheOrderMap.put(order.getOrderId(), order);
                }
            }
            for(PriceBook priceBook : exOrderBook.getOrderBook().getSellOrders().values()){
                for(Order order : priceBook.getOrderSet()){
                    cacheOrderMap.put(order.getOrderId(), order);
                }
            }
        }
    }

    void addOrderToExOrderBookMap(Order order){
        ExOrderBook exOrderBook = exOrderBookMap.get(order.getInstrumentId());
        if(exOrderBook == null){
            exOrderBook = new ExOrderBook(order.getInstrumentId());
            exOrderBookMap.put(order.getInstrumentId(), exOrderBook);
        }
        OrderBook orderBook = exOrderBook.getOrderBook();
        if(orderBook == null){
            orderBook = new OrderBook();
            exOrderBook.setOrderBook(orderBook);
        }
        TreeMap<Long, PriceBook> relatedOrders;
        if(order.getDirection() == DirectionType.DT_BUY){
            relatedOrders = orderBook.getBuyOrders();
        }else {
            relatedOrders = orderBook.getSellOrders();
        }
        PriceBook priceBook = relatedOrders.get(order.getComPrice());
        if(priceBook == null){
            priceBook = new PriceBook();
            priceBook.setPrice(order.getComPrice());
            relatedOrders.put(order.getComPrice(), priceBook);
        }
        priceBook.getOrderSet().add(order);
        priceBook.setReallyVolume(priceBook.getReallyVolume() + order.getVolume() - order.getTradedVolume());
    }

    void addOrderToCacheOrderMap(Order order){
        cacheOrderMap.put(order.getOrderId(), order);
    }

    boolean cacheOrderMapContainsOrder(long orderId){
        return cacheOrderMap.containsKey(orderId);
    }

    Order getOrderFromCacheOrderMap(long orderId){
        return cacheOrderMap.get(orderId);
    }

    void removeOrderFromExOrderBookMap(Order order){
        ExOrderBook exOrderBook = exOrderBookMap.get(order.getInstrumentId());
        if(exOrderBook != null){
            TreeMap<Long, PriceBook> relatedOrders;
            OrderBook orderBook = exOrderBook.getOrderBook();
            if(orderBook != null) {
                if (order.getDirection() == DirectionType.DT_BUY) {
                    relatedOrders = orderBook.getBuyOrders();
                } else {
                    relatedOrders = orderBook.getSellOrders();
                }
                PriceBook priceBook = relatedOrders.get(order.getComPrice());
                if(priceBook != null) {
                    priceBook.getOrderSet().remove(order);
                    if (priceBook.getOrderSet().isEmpty()) {
                        //如果删除订单后，此价格位没有订单，则删除此价格位
                        relatedOrders.remove(priceBook.getPrice());
                    }else {
                        priceBook.setReallyVolume(priceBook.getReallyVolume() - (order.getVolume() - order.getTradedVolume()));
                    }
                }
            }
        }
    }

    void removeOrderFromCacheOrderMap(Order order){
        cacheOrderMap.remove(order.getOrderId());
    }

    ExOrderBook getExOrderBook(String instrumentId){
        return exOrderBookMap.get(instrumentId);
    }

    TreeMap<Long, PriceBook> getPartyOrders(String instrumentId, DirectionType direction){
        ExOrderBook exOrderBook = exOrderBookMap.get(instrumentId);
        if(exOrderBook == null){
            return null;
        }
        if(direction == DirectionType.DT_BUY){
            return exOrderBook.getOrderBook().getSellOrders();
        }else {
            return exOrderBook.getOrderBook().getBuyOrders();
        }
    }

    void addRtnInfo(IRtnInfo rtnInfo){
        rtnInfoList.add(rtnInfo);
    }

    List<IRtnInfo> getRtnInfoList() {
        return rtnInfoList;
    }

    void clearRtnInfoList(){
        rtnInfoList.clear();
    }
}

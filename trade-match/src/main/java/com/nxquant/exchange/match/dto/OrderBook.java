package com.nxquant.exchange.match.dto;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * @author shilf
 * 订单簿
 */
public class OrderBook implements Info {
    private TreeMap<Long, PriceBook> buyOrders;
    private TreeMap<Long, PriceBook> sellOrders;

    public OrderBook(){
        createOrderBook();
    }

    private void createOrderBook(){
        buyOrders = new TreeMap<>(new Comparator<Long>() {
            @Override
            public int compare(Long p1, Long p2) {
                if(p1 < p2){
                    return 1;
                }else if(p1 > p2){
                    return -1;
                }else {
                    return 0;
                }
            }
        });

        sellOrders = new TreeMap<>(new Comparator<Long>() {
            @Override
            public int compare(Long p1, Long p2) {
                if(p1 < p2){
                    return -1;
                }else if(p1 > p2){
                    return 1;
                }else {
                    return 0;
                }
            }
        });
    }


    public TreeMap<Long, PriceBook> getBuyOrders() {
        return buyOrders;
    }

    public TreeMap<Long, PriceBook> getSellOrders() {
        return sellOrders;
    }
}

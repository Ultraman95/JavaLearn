package com.nxquant.example.utils;

import com.nxquant.example.entity.Order;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class TestUtil {

    private static void testPriorityQueue(){
        AtomicLong incLong = new AtomicLong(0);
        PriorityQueue<Order> buyOrders = new PriorityQueue<>(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getComPrice() < o2.getComPrice()) {
                    return 1;
                } else if (o1.getComPrice() > o2.getComPrice()) {
                    return -1;
                } else {
                    long x = o1.getCreateTs() - o2.getCreateTs() != 0 ? (o1.getCreateTs() - o2.getCreateTs()) : (o1.getIncId() - o2.getIncId());
                    return (int)x;
                }
            }
        });

        Map<Long , Order> orderMap = new HashMap<>();

        String instrumentId = "XBTUSD";
        BigDecimal basePrice = new BigDecimal("2000");
        BigDecimal deltaPrice = new BigDecimal("0.0001");
        int times = 10000;
        long volume = 23;
        int priceCount = 30;
        int orderCount = 20;
        long bt = System.currentTimeMillis();
        for(int i = 0 ; i < priceCount ; i++){
            for(int j = 0 ; j < orderCount ; j++) {
                BigDecimal price = basePrice.add(deltaPrice.multiply(new BigDecimal(i)));
                long comPrice = price.multiply(new BigDecimal(times)).longValue();
                long incId = incLong.getAndIncrement();
                Order order = new Order(instrumentId, incId, price, comPrice, volume, incId);
                buyOrders.add(order);
                orderMap.put(order.getOrderId(), order);
            }
        }
        System.out.println("TotalTs1 :" + (System.currentTimeMillis() - bt));

        long totalTs = 0;
        for(int i = 0 ; i < 1000 ; i++){
            Random dom = new SecureRandom();
            long id = (long)dom.nextInt(priceCount*orderCount);
            bt = System.currentTimeMillis();
            buyOrders.remove(orderMap.get(id));
            totalTs += (System.currentTimeMillis() -bt);
        }
        System.out.println("TotalTs2 :" + totalTs);
    }


    public static void testTreeMap() {
        HashMap<Long,Order> orderMap = new HashMap<>();
        TreeMap<Long,Order> buyOrders = new TreeMap<>(new Comparator<Long>() {
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
        AtomicLong incLong = new AtomicLong(0);
        BigDecimal basePrice = new BigDecimal("2000");
        BigDecimal deltaPrice = new BigDecimal("0.0001");
        String instrumentId = "XBTUSD";
        int times = 10000;
        long volume = 23;
        int priceCount = 1;
        int orderCount = 2;
        long bt = System.currentTimeMillis();
        for(int i = 0 ; i < priceCount ; i++){
            for(int j = 0 ; j < orderCount ; j++) {
                BigDecimal price = basePrice.add(deltaPrice.multiply(new BigDecimal(i)));
                long comPrice = price.multiply(new BigDecimal(times)).longValue();
                long incId = incLong.getAndIncrement();
                Order order = new Order(instrumentId, incId, price, comPrice, volume, incId);
                orderMap.put(order.getOrderId(), order);
                buyOrders.put(order.getOrderId(), order);
            }
        }
        System.out.println("TotalTs1 :" + (System.currentTimeMillis() - bt));

        for (Map.Entry<Long, Order> entry : buyOrders.entrySet()) {
            Order order = entry.getValue();
        }

        long totalTs = 0;
        for(int i = 0 ; i < 1000 ; i++){
            Random dom = new SecureRandom();
            Long id = (long) dom.nextInt(priceCount*orderCount);
            bt = System.currentTimeMillis();
            buyOrders.remove(id);
            totalTs += (System.currentTimeMillis() -bt);
        }
        System.out.println("TotalTs2 :" + totalTs);
    }

}

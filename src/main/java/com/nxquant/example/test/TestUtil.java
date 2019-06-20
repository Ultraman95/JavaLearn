package com.nxquant.example.test;

import com.nxquant.example.entity.Storage;
import com.nxquant.example.utils.ApplicationContextTool;
import com.nxquant.example.entity.Order;
import com.nxquant.example.lifecycle.beanlife.CustomBean;
import com.nxquant.example.service.UserService;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.common.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TestUtil {
    @Autowired
    UserService userService;

    private void testPriorityQueue(){
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

    public void testTreeMap() {
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

    public void testGetBean(String beanName) {
        CustomBean bean = (CustomBean) ApplicationContextTool.getBean(beanName);
        bean = ApplicationContextTool.getBean(CustomBean.class);

        MeterRegistry meterRegistry = ApplicationContextTool.getBean(MeterRegistry.class);
        Storage storage = ApplicationContextTool.getBean(Storage.class);
    }

    public void testAsync(){
        for(int i = 0 ; i < 15 ; i++){
            userService.printAsync();
        }
    }

    public int testGetPartitionIdByClientId(String clientId){
        return Utils.toPositive(Utils.murmur2(clientId.getBytes(StandardCharsets.UTF_8))) % 50;
    }

    public void testTelnetFile(){
        try {
            // 相对路径，如果没有则要建立一个新的output.txt文件
            File writeName = new File("list.txt");
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            int count = 1000;
            for(int i = 0 ; i< count ; i++) {
                String ipStr = "192.168.1." + i + "|3100\r\n";
                out.write(ipStr);
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

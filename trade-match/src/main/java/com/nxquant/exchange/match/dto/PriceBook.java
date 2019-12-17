package com.nxquant.exchange.match.dto;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * @author shilf
 * 某个价格订单簿
 */
public class PriceBook implements Info {
    private long price;
    private long reallyVolume;
    private long displayVolume;
    private TreeSet<MOrder> orderSet;

    public PriceBook(){
        reallyVolume = 0;
        displayVolume = 0;
        createOrderSet();
    }

    private void createOrderSet(){
        orderSet =new TreeSet<>(new Comparator<MOrder>() {
            @Override
            public int compare(MOrder o1, MOrder o2) {
                long compareValue = o1.getCreateTs() - o2.getCreateTs() != 0 ? (o1.getCreateTs() - o2.getCreateTs()) : (o1.getIncId() - o2.getIncId());
                return (int)compareValue;
            }
        });
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getReallyVolume() {
        return reallyVolume;
    }

    public void setReallyVolume(long reallyVolume) {
        this.reallyVolume = reallyVolume;
    }

    public long getDisplayVolume() {
        return displayVolume;
    }

    public void setDisplayVolume(long displayVolume) {
        this.displayVolume = displayVolume;
    }

    public TreeSet<MOrder> getOrderSet() {
        return orderSet;
    }
}

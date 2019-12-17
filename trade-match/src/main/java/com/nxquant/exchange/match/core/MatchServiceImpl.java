package com.nxquant.exchange.match.core;

//import com.js.trade.directive.CancelOrder;
//import com.js.trade.directive.UpdateOrder;
import com.nxquant.exchange.match.dto.*;
import com.nxquant.exchange.match.dto.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;


/**
 * @author shilf
 * 撮合服务接口实现
 */
@Service
public class MatchServiceImpl implements MatchService {

    @Override
    public void insertOrder(MOrder order, boolean isRedo, OrderBookManager orderBookManager){
        TreeMap<Long, PriceBook> partyOrders = orderBookManager.getPartyOrders(order.getInstrumentId(), order.getDirection());
        if(partyOrders == null || partyOrders.isEmpty()){
            IRtnInfo rtnInfo = null;
            if(order.getPriceType() == MOrderPriceType.OPT_LIMIT){
                if(order.getTimeCondition() == MTimeConditionType.TCT_GTC){
                    orderBookManager.addOrderToExOrderBookMap(order);
                    orderBookManager.addOrderToCacheOrderMap(order);
                    rtnInfo = new RtnOrder();
                }else if(order.getTimeCondition() == MTimeConditionType.TCT_IOC){
                    //打回
                }else if(order.getTimeCondition() == MTimeConditionType.TCT_FOK){
                    //打回
                }
            }else if(order.getPriceType() == MOrderPriceType.OPT_MARKET){
                //打回
            }
            if(rtnInfo != null && !isRedo){
                orderBookManager.addRtnInfo(rtnInfo);
            }
        }else {
            long orderRemainVolume = order.getVolume();
            List<MatchInfo> matchInfoList = new ArrayList<>();
            for (PriceBook priceBook : partyOrders.values()) {
                for(MOrder partyOrder : priceBook.getOrderSet()) {
                    if (!isMatch(order, partyOrder)) {
                        break;
                    }
                    orderRemainVolume -= (partyOrder.getVolume() - partyOrder.getTradedVolume());
                    MatchInfo matchInfo = new MatchInfo();
                    matchInfoList.add(matchInfo);
                    matchInfo.setMatchOrder(partyOrder);
                    if (orderRemainVolume <= 0) {
                        matchInfo.setRemainVolume(-orderRemainVolume);
                        orderRemainVolume = 0;
                        break;
                    }
                    matchInfo.setRemainVolume(0);
                }
            }

            if (order.getPriceType() == MOrderPriceType.OPT_LIMIT && order.getTimeCondition() == MTimeConditionType.TCT_FOK && orderRemainVolume > 0 ) {
                //FOK没有全部成交，撤单
                if(!isRedo){

                }
                return;
            }

            if(order.getPurposeType() == MOrderPurposeType.OPT_POSTONLY && order.getVolume() != orderRemainVolume ){
                //被动委托，撤单
                if(!isRedo){

                }
                return;
            }


            if (orderRemainVolume == order.getVolume()) {
                IRtnInfo rtnInfo = null;
                if (order.getPriceType() == MOrderPriceType.OPT_LIMIT) {
                    //没有匹配，插入订单簿
                    orderBookManager.addOrderToExOrderBookMap(order);
                    orderBookManager.addOrderToCacheOrderMap(order);
                    rtnInfo = new RtnOrder();
                } else if (order.getPriceType() == MOrderPriceType.OPT_MARKET) {
                    //没有匹配
                    rtnInfo = new RtnOrder();
                }
                if(rtnInfo != null && !isRedo){
                    orderBookManager.addRtnInfo(rtnInfo);
                }
            }

            for (MatchInfo matchInfo : matchInfoList) {
                MOrder matchOrder = matchInfo.getMatchOrder();
                if (matchInfo.getRemainVolume() > 0) {
                    matchOrder.setTradedVolume(matchOrder.getVolume() - matchInfo.getRemainVolume());
                    matchOrder.setOrderStatus(MOrderStatus.OS_PARTFILLED);
                } else {
                    orderBookManager.removeOrderFromExOrderBookMap(matchOrder);
                    orderBookManager.removeOrderFromCacheOrderMap(matchOrder);
                }
                if(!isRedo) {
                    RtnTrade partyRtnTrade = new RtnTrade();
                    orderBookManager.addRtnInfo(partyRtnTrade);
                }
            }

            order.setTradedVolume(order.getVolume() - orderRemainVolume);
            if (orderRemainVolume == 0) {
                //全部成交
                order.setOrderStatus(MOrderStatus.OS_FILLED);
            } else {
                if (order.getTimeCondition() == MTimeConditionType.TCT_GTC) {
                    if (order.getPriceType() == MOrderPriceType.OPT_LIMIT) {
                        //部分成交，其余插入订单簿
                        order.setOrderStatus(MOrderStatus.OS_PARTFILLED);
                        orderBookManager.addOrderToExOrderBookMap(order);
                        orderBookManager.addOrderToCacheOrderMap(order);
                    }
                } else if (order.getTimeCondition() == MTimeConditionType.TCT_IOC) {
                    //部分成交，其余撤单
                    order.setOrderStatus(MOrderStatus.OS_PARTFILLEDCANCELED);
                }
            }
            if(!isRedo) {
                RtnTrade rtnTrade = new RtnTrade();
                orderBookManager.addRtnInfo(rtnTrade);
            }
        }
    }

    /*
    @Override
    public void cancelOrder(CancelOrder cancelOrder, boolean isRedo, OrderBookManager orderBookManager){
        long orderId = cancelOrder.getOrderId();
        if(orderBookManager.cacheOrderMapContainsOrder(orderId)){
            MOrder order = orderBookManager.getOrderFromCacheOrderMap(orderId);
            orderBookManager.removeOrderFromExOrderBookMap(order);
            orderBookManager.removeOrderFromCacheOrderMap(order);
        }else{
            //订单不存在
        }
    }


    @Override
    public void updateOrder(UpdateOrder updateOrder, boolean isRedo, OrderBookManager orderBookManager){
        long orderId = updateOrder.getOrderId();
        if(orderBookManager.cacheOrderMapContainsOrder(orderId)){
            MOrder order = orderBookManager.getOrderFromCacheOrderMap(orderId);
            orderBookManager.removeOrderFromExOrderBookMap(order);
            insertOrder(order, isRedo, orderBookManager);
        }else{
            //订单不存在
        }
    }*/


    private void addRtnMblData(MOrder order, OrderBookManager orderBookManager){
        long mblVolume = Math.min(order.getVolume() - order.getTradedVolume() , order.getDisplayVolume());
        RtnMblData rtnMblData = new RtnMblData();
        rtnMblData.setVolume(mblVolume);
        orderBookManager.addRtnInfo(rtnMblData);
    }

    //获取成交价格
    private BigDecimal getTradePrice(MTradePriceType tradePriceType){
        if(tradePriceType == null){
            tradePriceType = MTradePriceType.TCT_OPPONENT;
        }
        return null;
    }

    //是否能够匹配
    private boolean isMatch(MOrder order, MOrder partyOrder){
        if(order.getPriceType() == MOrderPriceType.OPT_LIMIT) {
            if (order.getDirection() == MDirectionType.DT_BUY) {
                if (order.getComPrice() < partyOrder.getComPrice()) {
                    return false;
                }
                return true;
            } else {
                if (order.getComPrice() > partyOrder.getComPrice()) {
                    return false;
                }
                return true;
            }
        }else{
            if(order.getMinBoundPrice() != null){
                if(partyOrder.getComPrice() < order.getMinBoundPrice()){
                    return false;
                }
            }
            if(order.getMaxBoundPrice() != null){
                if(partyOrder.getComPrice() > order.getMaxBoundPrice()){
                    return false;
                }
            }
            return true;
        }
    }
}

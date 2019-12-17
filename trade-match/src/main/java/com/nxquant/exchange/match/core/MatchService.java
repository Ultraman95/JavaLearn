package com.nxquant.exchange.match.core;

//import com.js.trade.directive.CancelOrder;
//import com.js.trade.directive.UpdateOrder;
import com.nxquant.exchange.match.dto.MOrder;
import org.springframework.stereotype.Service;

/**
 * @author shilf
 * 撮合服务接口
 */
@Service
public interface MatchService {
    /**
     * 报单
     * @param order
     * @param isRedo
     * @param orderBookManager
     */
    void insertOrder(MOrder order, boolean isRedo, OrderBookManager orderBookManager);

    /**
     * 撤单
     * @param order
     * @param isRedo
     * @param orderBookManager
     */
    //void cancelOrder(CancelOrder order, boolean isRedo, OrderBookManager orderBookManager);

    /**
     * 改单
     * @param order
     * @param isRedo
     * @param orderBookManager
     */
    //void updateOrder(UpdateOrder order, boolean isRedo, OrderBookManager orderBookManager);

}

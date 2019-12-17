package com.nxquant.exchange.match.core;


import com.nxquant.exchange.match.dto.ExOrderBook;
import com.nxquant.exchange.match.dto.Order;
import com.nxquant.exchange.match.dto.CancelOrder;
import com.nxquant.exchange.match.dto.AmendOrder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author shilf
 * 撮合服务接口
 */
@Service
public interface MatchService {
    void initOrderBookManager(List<ExOrderBook> exOrderBookList);

    int getRtnInfoListSize();

    void clearRtnInfoList();

    /**
     * 报单
     * @param order
     * @param isRedo
     */
    void insertOrder(Order order, boolean isRedo);

    /**
     * 撤单
     * @param order
     * @param isRedo
     */
    void cancelOrder(CancelOrder order, boolean isRedo);

    /**
     * 改单
     * @param order
     * @param isRedo
     */
    void amendOrder(AmendOrder order, boolean isRedo);

}

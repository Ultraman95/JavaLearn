package com.nxquant.exchange.walletservice.dao;

import com.nxquant.exchange.walletservice.entity.WithdrawEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WithdrawMapper {

    public List<WithdrawEntity> checkIsExist(String orderId);

    public int saveWithdraw(WithdrawEntity withdrawEntity);

    public void updateWithdraw(WithdrawEntity withdrawEntity);

    public List<WithdrawEntity> findAllUnchecked();

    public List<WithdrawEntity> findAll();
}

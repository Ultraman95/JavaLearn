package com.nxquant.exchange.walletservice.dao;

import com.nxquant.exchange.walletservice.entity.RechargeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RechargeMapper {
    public List<RechargeEntity> findAllUnfinish();

    public RechargeEntity findByTxId(String txId);

    public int addRecharge(RechargeEntity rechargeEntity);

    public int updateRecharge(RechargeEntity rechargeEntity);
}

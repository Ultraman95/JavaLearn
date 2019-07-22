package com.nxquant.exchange.walletservice.dao;

import com.nxquant.exchange.walletservice.entity.CoinInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CoinInfoMapper {
    public CoinInfoEntity findByCoinName(String coinname);

    public List<CoinInfoEntity> findAll();
}

package com.nxquant.exchange.walletservice.service;

import com.nxquant.exchange.walletservice.dao.CoinInfoMapper;
import com.nxquant.exchange.walletservice.entity.CoinInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinInfoService {
    @Autowired
    CoinInfoMapper coinInfoMapper;

    public CoinInfoEntity findByCoinName(String coinname) throws Exception {
        return coinInfoMapper.findByCoinName(coinname);
    }

    public List<CoinInfoEntity> findAll() throws Exception{
        return coinInfoMapper.findAll();
    }
}

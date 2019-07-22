package com.nxquant.exchange.walletservice.service;

import com.nxquant.exchange.wallet.bitcoin.BitCoinApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BtcService {
    @Autowired
    private BitCoinApi bitCoinApi;


    public String getAddress() throws Exception{
        return null;
    }
}

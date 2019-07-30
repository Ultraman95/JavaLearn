package com.nxquant.exchange.service;

import com.nxquant.exchange.exp.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainService {

    @Autowired
    TestUtil testUtil;


    public void start(){
        System.out.println("*********" + Thread.currentThread().getName() +" Begin*********");
        test();
    }

    private void test(){
        //testUtil.testAsync();
        //int partitionId = testUtil.testGetPartitionIdByClientId("9752877235");
        //testUtil.testMeterRegistry();
        //testUtil.testUserRegister();
        testUtil.testBitCoin();
    }
}

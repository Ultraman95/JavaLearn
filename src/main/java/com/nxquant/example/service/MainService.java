package com.nxquant.example.service;

import com.nxquant.example.test.TestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainService {

    @Autowired
    TestUtil testUtil;


    public void start(){
        System.out.println("*********AppLogic Begin*********");
        test();
    }

    private void test(){
        //testUtil.testAsync();
        //int partitionId = testUtil.testGetPartitionIdByClientId("9752877235");
        testUtil.testMeterRegistry();
    }
}

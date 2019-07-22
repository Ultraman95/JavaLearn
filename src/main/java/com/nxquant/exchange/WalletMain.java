package com.nxquant.exchange;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.nxquant.exchange.walletservice.dao")
@EnableScheduling
public class WalletMain {
    public static void main(String[] args) {
        SpringApplication.run(WalletMain.class, args);
    }
}

package com.nxquant.exchange.wallet.cli;

/**
 * Created by Administrator on 2018-08-07.
 */
public class QueryBalanceApi {
    private CoinBalanceQuery coinBalanceQuery = new CoinBalanceQuery();

    public double getBtcBalance(){
        return coinBalanceQuery.getBtcBalanceByUnspend();
    }

    public double getEthBalance(){
        return coinBalanceQuery.getETHBalanceByAddress("0xab59de3ea4dd1ee5c474aec01022b6a94da0e1bc");
    }

    public double getOmniBalance(){
        return coinBalanceQuery.getOmniAllBalance();
    }

    public static void main(String[] args) {
        QueryBalanceApi QueryBalanceApi =new QueryBalanceApi();
        double d =  QueryBalanceApi.getBtcBalance();
        d =QueryBalanceApi.getEthBalance();
        d =QueryBalanceApi.getOmniBalance();
    }
}

package com.nxquant.exchange.wallet.cli;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-08-03.
 */
public interface  DataStoreInterface {
    public ArrayList<String>  btcAddress = new ArrayList<String> ();
    public ArrayList<String>  ethAddress = new ArrayList<String> ();
    public ArrayList<String>  usdtAddress = new ArrayList<String> ();

    public void init(ArrayList<String> properties);
    public void readAddress();
    public void storeBtc(double amount);
    public void storeEth(String address, double amount);
    public void storeUsdt(String address, double amount);
}

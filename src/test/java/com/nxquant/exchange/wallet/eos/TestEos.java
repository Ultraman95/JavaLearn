package com.nxquant.exchange.wallet.eos;

import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.eos.messages.AccountInfo;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019-01-04.
 */
public class TestEos {

    ///需要根据实际情况配置
    private String walletUrl = "http://192.168.44.204:8081";
    private String nodeUrl = "http://13.230.27.206:8888";
    private EosApi eosApi = null;

    private void initialEos(){
        eosApi = new EosApi();
        eosApi.createConnection(nodeUrl, walletUrl);
    }

    @Test
    public void TestCreateWallet(){
        initialEos();
       String pwd  = eosApi.createWallet("test1");
       if(pwd == null){
           System.out.println(eosApi.getErrorMsg());
       } else {
           System.out.println(pwd);
       }
    }

    @Test
    public void TestCreateNewKey(){
        initialEos();
        String key = eosApi.createKey("test1");
        if(key == null){
            System.out.println(eosApi.getErrorMsg());
        } else {
            System.out.println(key);
        }
    }

    @Test
    public void TestUnlockWallet(){
        initialEos();
        boolean key = eosApi.unlockWallet("test1", "PW5KXCVKzwW2Xjg8y2uJDSXNwmctb8WkMYUxx1kEpUfp54eczYCnV", 10);
        if(key == false){
            System.out.println(eosApi.getErrorMsg());
        } else {
            System.out.println(key);
        }
    }

    @Test
    public void TestGetCurrencyBalance(){
        initialEos();
        double eosBalance = eosApi.getEosBalance("chenxiao1235");
        System.out.println(eosBalance);
    }

    @Test
    public void TestGetAccount(){
        initialEos();
        AccountInfo account =  eosApi.getAccount("chenxiao1222");
        if(account == null){
            System.out.println(eosApi.getErrorMsg());
            return;
        } else {
            System.out.println(account);
        }

    }

    @Test
    public void TestCreateAccount(){
        initialEos();
        String walletName = "chen";
        String pwd = "PW5KHqHZ8JEKPcSoMArxLfKhJWTLufTzuY36EmgX6NHyPdvfv8Fjx";
        boolean  bool2 =  eosApi.unlockWallet(walletName, pwd, 100);

        List<String> keys = new ArrayList<String>();
        String pubKey = "EOS5srtgyFUA5ptVtNw9qxCWksCTe1vJxZkbc5uJJQJodwP7zx124";
        keys.add(pubKey);

        String key = eosApi.createKey(walletName);
        if(key == null){
            System.out.println(eosApi.getErrorMsg());
            return;
        } else {
            System.out.println(key);
        }

        String txid = eosApi.createAccount(keys, "chenxiao1235", "chenxiao1222", key, key, 1.0);
        if(txid == null){
            System.out.println(eosApi.getErrorMsg());
            return;
        } else {
            System.out.println(txid);
        }
    }

    @Test
    public void TestTransaction(){
        initialEos();
        String walletName = "chen";
        String pwd = "PW5KHqHZ8JEKPcSoMArxLfKhJWTLufTzuY36EmgX6NHyPdvfv8Fjx";
        boolean  bool2 =  eosApi.unlockWallet(walletName, pwd, 100);

        List<String> keys = new ArrayList<String>();
        String pubKey = "EOS5srtgyFUA5ptVtNw9qxCWksCTe1vJxZkbc5uJJQJodwP7zx124";
        keys.add(pubKey);

        String from = "chenxiao1235";
        String to = "chenxiao1222";
        String memo = "hello";
        double amount = 1.0;
        String txid =  eosApi.transfer(from, to , amount, memo, keys);
        if(txid == null){
            System.out.println(eosApi.getErrorMsg());
            return;
        } else {
            System.out.println(txid);
        }
    }

    @Test
    public void TestBuyRam(){
        initialEos();
        String walletName = "chen";
        String pwd = "PW5KHqHZ8JEKPcSoMArxLfKhJWTLufTzuY36EmgX6NHyPdvfv8Fjx";
        boolean  bool2 =  eosApi.unlockWallet(walletName, pwd, 100);

        List<String> keys = new ArrayList<String>();
        String pubKey = "EOS5srtgyFUA5ptVtNw9qxCWksCTe1vJxZkbc5uJJQJodwP7zx124";
        keys.add(pubKey);

        String from = "chenxiao1235";
        String to = "chenxiao1235";
        double fee = 3.0;
        String txid =  eosApi.buyRam(from, to , fee, keys);
        if(txid == null){
            System.out.println(eosApi.getErrorMsg());
            return;
        } else {
            System.out.println(txid);
        }
    }

    @Test
    public void TestBuyCpuNet(){
        initialEos();
        String walletName = "chen";
        String pwd = "PW5KHqHZ8JEKPcSoMArxLfKhJWTLufTzuY36EmgX6NHyPdvfv8Fjx";
        boolean  bool2 =  eosApi.unlockWallet(walletName, pwd, 100);

        List<String> keys = new ArrayList<String>();
        String pubKey = "EOS5srtgyFUA5ptVtNw9qxCWksCTe1vJxZkbc5uJJQJodwP7zx124";
        keys.add(pubKey);

        String from = "chenxiao1235";
        String to = "chenxiao1235";
        double cpufee = 1.0;
        double netfee = 1.0;
        String txid =  eosApi.delegatebw(keys, from, to , cpufee, netfee,0);
        if(txid == null){
            System.out.println(eosApi.getErrorMsg());
            return;
        } else {
            System.out.println(txid);
        }
    }

    @Test
    public void TestGetBlock(){
        initialEos();
        ArrayList<BlockInfo>  blokcInfox =  eosApi.getBlock(11004308);
        if(blokcInfox == null){
            System.out.println(eosApi.getErrorMsg());
        }
    }
}

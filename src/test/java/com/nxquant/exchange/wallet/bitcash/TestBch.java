package com.nxquant.exchange.wallet.bitcash;

import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.model.TransactionInfo;
import com.nxquant.exchange.wallet.model.UnSpentInf;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018-06-13.
 */
public class TestBch {
    //bitcoind -regtest  -datadir=./data  -bind=127.0.0.1:10015   -server -rpcuser=name -rpcpassword=pwd -rpcport=18886  -rpcallowip=192.168.44.28 -rpcallowip=127.0.0.1
    private String testFaucet = "http://192.168.44.163:18886";
    private String username = "name";
    private String password = "pwd";

    @Test
    /**
     * 生成两个地址
     */
    public void TestCreateNewAddress()
    {
        BitCashApi bitCashApi = new BitCashApi();
        Boolean ret = bitCashApi.createConnection(username,password,testFaucet);

        String address_1 = bitCashApi.createNewAddress("");
        if(null == address_1)
        {
            System.out.println("create new address error:"+bitCashApi.getErrorMsg());
        }
        else
        {
            System.out.println("create  new address success  addresses:"+address_1);
        }
    }

    @Test
    /**
     * 测试获取钱包余额
     * 说明， 初始挖矿101块以后， 钱包就会有余额，以后每挖一块，就会有相应的增加
     */
    public void TestGetBalance(){
        BitCashApi bitCashApi = new BitCashApi();
        Boolean ret = bitCashApi.createConnection(username,password,testFaucet);

        BigDecimal balance =  bitCashApi.getWalletBalance(1);
        if(null == balance)
        {
            System.out.println("error:"+bitCashApi.getErrorMsg());
        }
        else
        {
            System.out.println("balances:"+balance);
        }
    }

    @Test
    /**
     * 测试秘钥获取，可以作为客户端秘钥导出
     */
    public void TestGetPrivKey()
    {
        String address = "qq607cefa2hvgcyy0hvtres52u77mankpq8ult40ga";
        BitCashApi bitCashApi = new BitCashApi();
        Boolean ret = bitCashApi.createConnection(username, password, testFaucet);

        String privkey = bitCashApi.getPrivKey(address);
        if(null == privkey)
        {
            System.out.println("TestGetPrivKey error:"+bitCashApi.getErrorMsg());
        }
        else
        {
            System.out.println("TestGetPrivKey success  privkey:"+privkey);
        }
    }

    @Test
    /**
     * 测试转账功能
     */
    public void TestTransfer()
    {
        String address = "qq607cefa2hvgcyy0hvtres52u77mankpq8ult40ga";
        double amount = 0.1;

        BitCashApi bitCashApi = new BitCashApi();
        Boolean ret = bitCashApi.createConnection(username, password, testFaucet);

        String txid = bitCashApi.transfer(address, amount);
        if(null == txid)
        {
            System.out.println("transfer error:"+bitCashApi.getErrorMsg());
            return  ;
        }

        System.out.println("transfer success  txid:"+txid);

        //查询收到情况, 第二个参数为确认块数， 一般6以上可以认为到账
        double recvd =  bitCashApi.getWalletBalance(0).doubleValue();
        System.out.println("WalletBalance, 0 confirm  recvd:"+recvd);
        //a68a737f6fd90f505dd5e9596a85ef13bf928942ab08241d09abf228f1f7bc99
    }

    @Test
    /**
     * 查询某交易txid的信息
     */
    public void  TestGetRawTransaction(){
        BitCashApi bitCashApi = new BitCashApi();
        Boolean ret = bitCashApi.createConnection(username,password,testFaucet);

        String txid = "25f400337bfbf85f626a30bf67728b9b1a771c1c0abba2397f07fac27e31e28c";
        TransactionInfo TransactionInfo =  bitCashApi.getRawTransaction(txid);
    }

    @Test
    /**
     * 获取区块信息，可以确认转账是否到账
     */
    public void TestGetBlock(){
        BitCashApi bitCashApi = new BitCashApi();
        Boolean ret = bitCashApi.createConnection(username,password,testFaucet);

        long height =  bitCashApi.getLatestBlockNum();
        ArrayList<BlockInfo>  BlockInfo = bitCashApi.getBlockByNumber(102);
        int h = 0;
    }

    @Test
    public void TestListUnspent() {
        BitCashApi bitCashApi = new BitCashApi();
        Boolean ret = bitCashApi.createConnection(username,password,testFaucet);

        //查询某个地址已经确认1个块以上的未花费信息
        ArrayList<UnSpentInf>  array1 = bitCashApi.listUnSpentByaddress("mmMwrKiuJsaNKV2PxakZV7muak71xfNymB", 1);
        if(null == array1)
        {
            System.out.println("transfer error:"+bitCashApi.getErrorMsg());
            return  ;
        }

        //查询钱包所有已经确认6个块以上的未花费信息，可能信息会比较大，一般不建议使用
        ArrayList<UnSpentInf>  array2 =  bitCashApi.listAllUnSpent();
        if(null == array2)
        {
            System.out.println("transfer error:"+bitCashApi.getErrorMsg());
            return  ;
        }
    }

    /**
     * D对手动签名交易进行测试
     */
    @Test
    public void TestSendRawtransaction() {
        BitCashApi bitCashApi = new BitCashApi();
        Boolean ret = bitCashApi.createConnection(username,password,testFaucet);

        //转出方地址
        String srcaddress = "qz8zsh5vx3hkg93wc4cxyg2k9kzjl2exkckme5rs0v";
        //转入方地址
        String toaddress = "qqj66phmzek8xnyjg3lwwc56x7alfvy7eudznh6nrf";

        //查询某个地址已经确认1个块以上的未花费信息
        ArrayList<UnSpentInf>  array1 = bitCashApi.listUnSpentByaddress(srcaddress, 1);
        if(null == array1){
            return;
        }

        if(array1.size() == 0){
            return;
        }

        ///以第一个交易记录作为转出方
        String txid = array1.get(0).getTxid();
        String spkey = array1.get(0).getScriptPubKey();
        String redeemScript = array1.get(0).getRedeemScript();
        int vout = array1.get(0).getVout();
        double amount = array1.get(0).getAmount();

        ///////////////////使用原始交易接口进行交易//////////////////////////
        //第一步：创建原始交易
        //必须与ListUnspent查询出来的结果一直
        UnSpentInf unSpentInf = new UnSpentInf();
        unSpentInf.setTxid(txid);
        unSpentInf.setVout(vout);
        unSpentInf.setAmount(amount);  //数量必须是 未花费信息里面的一个， 不能自己随意填写

        String  rawtransaction = bitCashApi.createRawtransaction(toaddress , srcaddress, unSpentInf, 0.05, 0.001);
        if(null == rawtransaction)
        {
            System.out.println("createrawtransaction error:"+bitCashApi.getErrorMsg());
            return  ;
        }
        System.out.println("createrawtransaction:"+rawtransaction);

        //第二步：对原始交易进行签名
        UnSpentInf unSpentInf1 = new UnSpentInf();
        unSpentInf1.setTxid(txid);
        unSpentInf1.setVout(vout);
        unSpentInf1.setScriptPubKey(spkey);
        unSpentInf1.setRedeemScript(redeemScript);  ///多重签名使用

        String privateKey = "cRd5HQTsEPCrRD8n2yBj9RefMJYYufptfcFv1vg8NghJUJsdRMaf";
        ArrayList<String>  privateKeyList = new  ArrayList<String>();
        privateKeyList.add(privateKey); //可为空， 也可以填入根据私钥是否 在客户端或者是否在服务端
        //为空地话， 不需要add

        String  signRawtransaction = bitCashApi.signRawtransaction(rawtransaction, unSpentInf1, privateKeyList);
        if(null == signRawtransaction)
        {
            System.out.println("signRawtransaction error:"+bitCashApi.getErrorMsg());
            return  ;
        }
        System.out.println("signRawtransaction:"+signRawtransaction);

        ///第三步：发送已经签名的原始交易
        String txidre =  bitCashApi.sendRawtransaction(signRawtransaction);
        if(null == txidre)
        {
            System.out.println("sendRawtransaction error:"+bitCashApi.getErrorMsg());
            return  ;
        }
        System.out.println("sendRawtransaction:"+txidre);
    }
}

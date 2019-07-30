package com.nxquant.exchange.wallet.litecoin;

import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.model.UnSpentInf;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;

import java.math.BigDecimal;

public class TestLiteCoin {
    // litecoind -regtest -datadir=./data -server -rpcuser=name -rpcpassword=pwd -rpcport=18889 -rpcallowip=127.0.0.1
    private String testFaucet = "http://192.168.44.163:18856";
    private String username = "name";
    private String password = "pwd";

    @Test
    public void testCreateConnection() {
        LiteCoinApi bitcoin = new LiteCoinApi();
        Boolean connected = bitcoin.createConnection(username, password, testFaucet);
        if (!connected) {
            System.out.println("Connection failure: "+bitcoin.getErrorMsg());
        }
    }

    @Test
    public void testCreateNewAddress() {
        LiteCoinApi bitcoin = new LiteCoinApi();
        Boolean connected = bitcoin.createConnection(username, password, testFaucet);
        if (!connected) {
            System.out.println("Connection failure: "+bitcoin.getErrorMsg());
            return;
        }
        String address = bitcoin.createNewAddress("");
        if (address == null) {
            System.out.println("Error: "+bitcoin.getErrorMsg());
            return;
        }
        assertTrue(bitcoin.validateAddress(address));
        System.out.println("Info: new address of "+address);
        String key = bitcoin.getPrivKey(address);
        System.out.println("Info: with private key "+key);
    }

    @Test
    public void testGetBlock() {
        LiteCoinApi bitcoin = new LiteCoinApi();
        Boolean connected = bitcoin.createConnection(username, password, testFaucet);
        if (!connected) {
            System.out.println("Connection failure: "+bitcoin.getErrorMsg());
            return;
        }
        long index = bitcoin.getLatestBlockNum();
        if (index < 0) {
            System.out.println("Error: "+bitcoin.getErrorMsg());
            return;
        }
        ArrayList<BlockInfo> blockinfos = bitcoin.getBlockByNumber(index);
        if (blockinfos == null) {
            System.out.println("Error: "+bitcoin.getErrorMsg());
            return;
        }
        System.out.format("Info: block info of #%d:%n%s%n", index, blockinfos);
    }

    @Test
    public void testTransfer() {
        LiteCoinApi bitcoin = new LiteCoinApi();
        Boolean connected = bitcoin.createConnection(username, password, testFaucet);
        if (!connected) {
            System.out.println("Connection failure: "+bitcoin.getErrorMsg());
            return;
        }
        String address = "QZagJBqpkdHUo5qWSA9nujkH47ioBHLn2T";

        double amount = 10;
        String txid = bitcoin.transfer(address, amount);
        if (txid == null) {
            System.out.println("Error: "+bitcoin.getErrorMsg());
            return;
        }
        System.out.println("Info: transaction id of"+txid);
        double received = bitcoin.getReceivedByAddress(address, 0);
        assertEquals(10., received, 0);
        System.out.format("Info: transfered %f%n", amount);
        //of41884e731556365bfa3525bcdf8fe92220663ebe05b877deb94d1409bf1b036b
    }

    /**
     * D对手动签名交易进行测试
     */
    @Test
    public void TestSendRawtransaction() {
        LiteCoinApi litecoin = new LiteCoinApi();
        Boolean connected = litecoin.createConnection(username, password, testFaucet);

        //转出方地址
        String srcaddress = "miJqZz2Jv7Tj9kwYHVfYxpCAwzx1ohnXC1";
        //转入方地址
        String toaddress = "QNgPuGYXa7tHbQodpLAeGsbYVfUHZjF5pW";

        //查询某个地址已经确认1个块以上的未花费信息
        ArrayList<UnSpentInf>  array1 = litecoin.listUnSpentByaddress(srcaddress, 1);
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

        String  rawtransaction = litecoin.createRawtransaction(toaddress , srcaddress, unSpentInf, 0.01, 0.0001);
        if(null == rawtransaction)
        {
            System.out.println("createrawtransaction error:"+litecoin.getErrorMsg());
            return  ;
        }
        System.out.println("createrawtransaction:"+rawtransaction);

        //第二步：对原始交易进行签名
        UnSpentInf unSpentInf1 = new UnSpentInf();
        unSpentInf1.setTxid(txid);
        unSpentInf1.setVout(vout);
        unSpentInf1.setScriptPubKey(spkey);
        unSpentInf1.setRedeemScript(redeemScript);  ///多重签名使用

        String privateKey = "cP2EoDB3jkRFBxEJyWKNwAXaGRTEsidEiLzRka3FYWxLMjb6pgbc";
        ArrayList<String>  privateKeyList = new  ArrayList<String>();
        privateKeyList.add(privateKey); //可为空， 也可以填入根据私钥是否 在客户端或者是否在服务端

        String  signRawtransaction = litecoin.signRawtransaction(rawtransaction, unSpentInf1,  privateKeyList);
        if(null == signRawtransaction)
        {
            System.out.println("signRawtransaction error:"+litecoin.getErrorMsg());
            return  ;
        }
        System.out.println("signRawtransaction:"+signRawtransaction);

        ///第三步：发送已经签名的原始交易
        String txidre =  litecoin.sendRawtransaction(signRawtransaction);
        if(null == txidre)
        {
            System.out.println("sendRawtransaction error:"+litecoin.getErrorMsg());
            return  ;
        }
        System.out.println("sendRawtransaction:"+txidre);
    }

    @Test
    public void testGetBalance() {
        LiteCoinApi bitcoin = new LiteCoinApi();
        Boolean connected = bitcoin.createConnection(username, password, testFaucet);
        if (!connected) {
            System.out.println("Connection failure: "+bitcoin.getErrorMsg());
            return;
        }
        BigDecimal balance = bitcoin.getWalletBalance(2);
        System.out.println(balance);
    }
}

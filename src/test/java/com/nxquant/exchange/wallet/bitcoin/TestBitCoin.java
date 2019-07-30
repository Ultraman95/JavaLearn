package com.nxquant.exchange.wallet.bitcoin;

import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.model.BtcBlockInfo;
import com.nxquant.exchange.wallet.model.UnSpentInf;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-06-19.
 */
public class TestBitCoin {
    //启动命令：bitcoind -regtest  -server -rpcuser=name -rpcpassword=pwd -rpcport=18883  -rpcallowip=192.168.44.28 -rpcallowip=127.0.0.1

    ///需要根据实际情况配置
    private String testFaucet = "http://192.168.44.163:18883";
    private String username = "name";
    private String password = "pwd";

    /**
     *创建地址
     */
    @Test
    public void TestCreateNewAddress()
    {
        BitCoinApi bitCoinApi = new BitCoinApi();
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucet);

        String newAddress = bitCoinApi.createNewAddress("");
        if(null == newAddress)
        {
            System.out.println("create new address error:"+bitCoinApi.getErrorMsg());
        }
        else
        {
            System.out.println("create  new address success  addresses:"+newAddress);
        }
    }

    /**
     *测试交易，转入方地址需要自己配置
     */
    @Test
    public void TestSetTxFee(){
        BitCoinApi bitCoinApi = new BitCoinApi();
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucet);

        double amount = 0.00005;

        Boolean res = bitCoinApi.setTxFee(amount);
        if(null == res)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }
        System.out.println("setTxFee success");
    }

    /**
     *测试交易，转入方地址需要自己配置
     */
    @Test
    public void TestTransfer(){
        BitCoinApi bitCoinApi = new BitCoinApi();
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucet);

        String address = "mpnUnvT1FwA4Leq3vgRnMcENLqP24EGYPk";   /////////转入方地址需要自己配置
        double amount = 0.001;
        double fee = 0.003;

        String txid = bitCoinApi.transfer(address, amount, fee);
        if(null == txid)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }
        System.out.println("transfer success  txid:"+txid);
    }

    /**
     *查询区块信息，交易列表
     */
    @Test
    public void TestQueryBlockInfo(){
        BitCoinApi bitCoinApi = new BitCoinApi();
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucet);

        //获取当前区块最大高度
        long height =  bitCoinApi.getLatestBlockNum();
        if(height < 0 ){
            return ;
        }

        //获取当前块的信息,交易列表
        ArrayList<BlockInfo>  BlockInfo = bitCoinApi.getBlockByNumber(1454861);
        if(null == BlockInfo)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }
    }

    /**
     *查询区块信息，交易列表
     */
    @Test
    public void TestQueryBlockInfo2(){
        BitCoinApi bitCoinApi = new BitCoinApi();
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucet);

        //获取当前区块最大高度
        long height =  bitCoinApi.getLatestBlockNum();
        if(height < 0 ){
            return ;
        }

        //获取当前块的信息,交易列表
        for(int i = 1454861 - 100; i<1454861 ;i++){
            ArrayList<BtcBlockInfo>  BtcBlockInfo = bitCoinApi.getBlockByNumber2(i);
            if(null == BtcBlockInfo)
            {
                System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
                return  ;
            }

            System.out.println(i);
        }

    }

    /**
     * 查询钱包未花费余额
     */
    @Test
    public void TestListUnspent() {
        BitCoinApi bitCoinApi = new BitCoinApi();
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucet);

        //查询某个地址已经确认1个块以上的未花费信息
        ArrayList<UnSpentInf>  array1 = bitCoinApi.listAllUnSpent(0);
        if(null == array1)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }

        //查询钱包所有已经确认6个块以上的未花费信息，可能信息会比较大，一般不建议使用
        ArrayList<UnSpentInf>  array2 =  bitCoinApi.listAllUnSpent(0);
        if(null == array2)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }
    }

    /**
     * 创建交易，手动签名使用
     */
    @Test
    public void TestCreateRawtransaction() {
        BitCoinApi bitCoinApi = new BitCoinApi();
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucet);

        UnSpentInf unSpentInf = new UnSpentInf();
        unSpentInf.setTxid("7a37f503f295dc3bac605fb4844136e287c081d2302d1d8d9a11764309969ac8");
        unSpentInf.setVout(0);

        ArrayList<UnSpentInf> unspentList = new ArrayList<UnSpentInf>();
        unspentList.add(unSpentInf);

        //创建原始交易
        String  rawtransaction = bitCoinApi.createRawtransaction(
                "2Mu3CRAuTgVxiJA9N7QeCFkzbED9Q895ov1","mmMwrKiuJsaNKV2PxakZV7muak71xfNymB", unspentList, 2.0, 0.1);

        if(null == rawtransaction)
        {
            System.out.println("createrawtransaction error:"+bitCoinApi.getErrorMsg());
            return  ;
        }
        System.out.println("createrawtransaction:"+rawtransaction);
    }

    /**
     * 对创建的交易进行签名， 其中 rawTransaction 需要TestCreateRawtransaction的输出
     */
    @Test
    public void TestSignRawtransaction() {
        BitCoinApi bitCoinApi = new BitCoinApi();
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucet);

        String rawTransaction = "0200000001c89a96094376119a8d1d2d30d281c087e2364184b45f60ac3bdc95f203f5377a0000000000ffffffff02802b530b000000001976a914401ca4068b39481ca2d5c1d5ddaf79ba62742da988ac00c2eb0b0000000017a91413a9f2de28a5174a60ce35634ee637df63deb8a18700000000";
        UnSpentInf unSpentInf = new UnSpentInf();
        unSpentInf.setScriptPubKey("2102b330ad4a7a7a06c57556f06caa55e6d8ba2be1c697d30fe36d76f8044d5ff324ac");
        unSpentInf.setTxid("7a37f503f295dc3bac605fb4844136e287c081d2302d1d8d9a11764309969ac8");
        unSpentInf.setVout(0);
        unSpentInf.setRedeemScript("");

        ArrayList<String>  privateKeyList = new  ArrayList<String>();
        privateKeyList.add(""); //可为空， 也可以填入根据私钥是否 在客户端或者是否在服务端
        //对原始交易进行签名
        //String  signRawtransaction = bitCoinApi.signRawtransaction(rawTransaction,unSpentInf,  privateKeyList);
        //if(null == signRawtransaction)
        //{
          //  System.out.println("signRawtransaction error:"+bitCoinApi.getErrorMsg());
          //  return  ;
       // }
       // System.out.println("signRawtransaction:"+signRawtransaction);
    }

    /**
     * D对手动签名交易进行测试
     */
    @Test
    public void TestSendRawtransaction() {
        BitCoinApi bitCoinApi = new BitCoinApi();
        String  testFaucetXXX = "http://192.168.44.163:16081";
        Boolean ret = bitCoinApi.createConnection(username, password, testFaucetXXX);

        //转出方地址
        String srcaddress =  "2MsTtkHbqaizVMj2d6ZfzkVsr73yuEJcNMy";
        //转入方地址
        String toaddress = "2N5879Fpn3i3icLNhFqozaB2MmNWHhMPvnu";

        //查询某个地址已经确认1个块以上的未花费信息
        ArrayList<UnSpentInf>  array1 = bitCoinApi.listUnSpentByaddress(srcaddress, 0);
        if(null == array1){
            return;
        }

        if(array1.size() == 0){
            return;
        }

        ///以第一个交易记录作为转出方
       // ret = bitCoinApi.createConnection(username, password, testFaucet);
        ///////////////////使用原始交易接口进行交易//////////////////////////
        //第一步：创建原始交易
        //必须与ListUnspent查询出来的结果一直

        String  rawtransaction = bitCoinApi.createRawtransaction(toaddress , srcaddress, array1, 0.05, 0.003);
        if(null == rawtransaction)
        {
            System.out.println("createrawtransaction error:"+bitCoinApi.getErrorMsg());
            return  ;
        }
        System.out.println("createrawtransaction:"+rawtransaction);

        //第二步：对原始交易进行签名
        String privateKey = "cTkQD32fLoJSHB2YLh11YYqoSyVzAC9cRQksBtZh5vMvV8jDwK65";
        ArrayList<String>  privateKeyList = new  ArrayList<String>();
       // privateKeyList.add(privateKey); //可为空， 也可以填入根据私钥是否 在客户端或者是否在服务端

        testFaucetXXX = "http://192.168.44.163:18883";
        ret = bitCoinApi.createConnection(username, password, testFaucetXXX);
        String  signRawtransaction = bitCoinApi.signRawtransaction(rawtransaction, array1, privateKeyList);
        if(null == signRawtransaction)
        {
            System.out.println("signRawtransaction error:"+bitCoinApi.getErrorMsg());
            return  ;
        }
        System.out.println("signRawtransaction:"+signRawtransaction);

        ///第三步：发送已经签名的原始交易
        testFaucetXXX = "http://192.168.44.163:16081";
        ret = bitCoinApi.createConnection(username, password, testFaucetXXX);
        String txidre =  bitCoinApi.sendRawtransaction(signRawtransaction);
        if(null == txidre)
        {
            System.out.println("sendRawtransaction error:"+bitCoinApi.getErrorMsg());
            return  ;
        }
        System.out.println("sendRawtransaction:"+txidre);
    }
}

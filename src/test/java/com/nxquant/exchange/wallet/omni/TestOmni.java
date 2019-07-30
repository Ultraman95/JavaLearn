package com.nxquant.exchange.wallet.omni;

import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.model.UnSpentInf;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TestOmni {
    //启动命令：omnicored -testnet -datadir=/data/com.unifex.chainapi.omni/test -server -rpcuser=name -rpcpassword=pwd -rpcport=18885  -rpcallowip=192.168.44.28 -rpcallowip=127.0.0.1
    ///需要根据实际情况配置
    private String testFaucet = "http://192.168.44.200:18885";
    private String username = "name";
    private String password = "pwd";
    //private String address1 = "myY5tD8hhdNVpXptyzWM7CScYjbTcro6xz";
    private String address1 = "mu1Tx4perCMQSriP65zbnTYCGmmTvXiH7o";
    //private String address1 =  "n38LntgP5Ga5cpLe2a3GCMFwWnpXQDf8os";
    private String address2 = "mgp9H1k6g4AFgm9r2n1sZidCWrmFusdV4f";
    private String address3 = "mpRrdnxAY9Prw7q8siYnNdXLvmoPyb7Wy5";

    private String  address4 = "mznSa848ZANLFr4FWUaffQaB2JGzC7UscF";

    @Test
    public void TestCreateAddress() {
        OmniApi omniApi = new OmniApi();
        Boolean ret = omniApi.createConnection(username,password,testFaucet);
        if(false == ret)
        {
            return;
        }

        String newAddress = omniApi.createNewAddress("");
        if(null == newAddress)
        {
            return ;
        }else{
            System.out.println(newAddress);
        }

        Boolean bre =  omniApi.validateAddress(newAddress);
    }

    @Test
    public void TestImportAddress() {
        OmniApi omniApi = new OmniApi();
        Boolean ret = omniApi.createConnection(username,password,testFaucet);
        if(false == ret)
        {
            return;
        }
        Boolean result  =  omniApi.importAddress(address1);
        if(null == result)
        {
            return ;
        }else{
            System.out.println(result);
        }
    }

    @Test
    public void TestGetBalance() {
        OmniApi omniApi = new OmniApi();
        Boolean ret = omniApi.createConnection(username,password,testFaucet);
        if(false == ret)
        {
            return;
        }
        BigDecimal balance =  omniApi.getBalance(address1, 1);
        if(null == balance)
        {
            return ;
        }else{
            System.out.println(balance);
        }
    }

    @Test
    public void TestTransaction() {
        OmniApi omniApi = new OmniApi();
        Boolean ret = omniApi.createConnection(username,password,testFaucet);
        if(false == ret)
        {
            return;
        }

        //  String txid = omniApi.transfer(address1, "mgcoQdqHcd1WT6d4vXpHhm3eW1A4gzH1LR", 0.01, 1);
        String txid = omniApi.transfer("mpRrdnxAY9Prw7q8siYnNdXLvmoPyb7Wy5", "mznSa848ZANLFr4FWUaffQaB2JGzC7UscF",
                0.01, 1);
        if(null == txid)
        {
            return ;
        } else {
            System.out.println(txid);
        }


        ArrayList<BlockInfo>  ba1 = omniApi.getBlockByNumber(1346814, 1);
        ArrayList<BlockInfo>  ba2 = omniApi.getBlockByNumber(1346815, 1);
        ArrayList<BlockInfo>  ba3 = omniApi.getBlockByNumber(1346816, 1);
        ArrayList<BlockInfo>  ba4 = omniApi.getBlockByNumber(1346817, 1);
        ArrayList<BlockInfo>  ba5 = omniApi.getBlockByNumber(1346818, 1);
    }

    @Test
    public void TestGetBlock() {
        OmniApi omniApi = new OmniApi();
        Boolean ret = omniApi.createConnection(username,password,testFaucet);
        if(false == ret)
        {
            return;
        }
        long height =  omniApi.getLatestBlockNum();
        if( height < 0)
        {
            return ;
        }else{
            System.out.println(height);
        }

        omniApi.getBlockByNumber(1346771-6, 1);
    }


    @Test
    public void TestSignTransaction() {
        OmniApi omniApi = new OmniApi();
        Boolean ret = omniApi.createConnection(username,password,testFaucet);
        if(false == ret)
        {
            return;
        }

        String payload = omniApi.createSendPayLoad(0.1, 1);
        if(null == payload)
        {
            return ;
        } else {
            System.out.println(payload);
        }

        //查询某个地址已经确认1个块以上的未花费信息
        ArrayList<UnSpentInf>  UnSpentInfList = omniApi.listUnSpentByaddress("mrvNPYpmHFUgbiEiXPa4Ah4YyCFJog27EF", 1);
        if(null == UnSpentInfList){
            return;
        }

        if(UnSpentInfList.size() == 0){
            return;
        }

        String rawData = omniApi.createRawTransaction(UnSpentInfList);
        if(null == rawData)
        {
            return ;
        } else {
            System.out.println(rawData);
        }

        String rawtxOpreturn =  omniApi.createRawtxOpreturn(payload,  rawData);
        if(null == rawtxOpreturn)
        {
            return ;
        } else {
            System.out.println(rawtxOpreturn);
        }

        String sddressrec = "mznSa848ZANLFr4FWUaffQaB2JGzC7UscF";
        String rawtxRecOpreturn =  omniApi.addReceiverToRawtxOpreturn(rawtxOpreturn,  sddressrec);
        if(null == rawtxRecOpreturn)
        {
            return ;
        } else {
            System.out.println(rawtxRecOpreturn);
        }

        String rawDataChange =  omniApi.addRawDataChange("mpRrdnxAY9Prw7q8siYnNdXLvmoPyb7Wy5", rawtxRecOpreturn, UnSpentInfList,0.0003);
        if(null == rawtxOpreturn)
        {
            return ;
        } else {
            System.out.println(rawtxOpreturn);
        }

       // String testFaucetXXX = "http://192.168.44.163:18862";
       // ret = omniApi.createConnection(username,password,testFaucetXXX);
        String signRawData =  omniApi.signRawTransaction(rawDataChange, UnSpentInfList);
        if(null == signRawData)
        {
            System.out.println(omniApi.getErrorMsg());
            return ;
        } else {
            System.out.println(signRawData);
        }

      //  ret = omniApi.createConnection(username,password,testFaucet);
        String txid =  omniApi.sendRawTransaction(signRawData);
        if(null == txid)
        {
            System.out.println(omniApi.getErrorMsg());
            return ;
        } else {
            System.out.println(txid);
        }

    }
}
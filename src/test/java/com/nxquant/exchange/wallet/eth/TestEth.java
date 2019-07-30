package com.nxquant.exchange.wallet.eth;

import com.nxquant.exchange.wallet.model.BlockInfo;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TestEth {
    private String ethaddress = "http://192.168.44.163:8545";
    private String fromAddress = "0x63e8b0ad3736e49a0fcbf419848fce55fc5034a8";
    private String fromAddressPWD = "123456";
    private String destAddress = "0x8cc999cbc1a268dc12264e983b5218d368da17b9";

    /**
     * 创建新账户
     */
    @Test
    public void TestCreateAccount(){
        EthApi ethApi = new EthApi();
        Boolean ret = ethApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }

        ///创建新账户， 需要输入密码
        String account = ethApi.createNewAccount("123456");
        if(null != account)
        {
            System.out.println("account:"+account);
        }
        else
        {
            System.out.println("createNewAccount error:"+ethApi.getErrorMsg());
        }
    }

    @Test
    public void TestGetBalance(){
        EthApi ethApi = new EthApi();
        Boolean ret = ethApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }

        BigDecimal  balance =  ethApi.getbalance(fromAddress);
        if(null != balance)
        {
            System.out.println("getbalance:"+balance);
        }
        else
        {
            System.out.println("getbalance error:"+ethApi.getErrorMsg());
        }
    }


    /**
     * 测试自动交易
     */
    @Test
    public void TestTransaction(){
        EthApi ethApi = new EthApi();
        Boolean ret = ethApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }
        //首先对账户进行解锁
        Boolean isunlocked = ethApi.unlockAccount("0xcc3377b624f3806cc2796d1ad43c7b8756bb0d49", fromAddressPWD);//("0xa9b01b60ec957f19f03511a1bcb041479627920d", fromAddressPWD);
        if(false == isunlocked)
        {
            System.out.println("unlock fail: "+ ethApi.getErrorMsg());
            return ;
        }

        String txid =  ethApi.transfer("0xcc3377b624f3806cc2796d1ad43c7b8756bb0d49", "0x5b00411111da4030bbf88c63ccd86fd6eb7c3b96", 0.02, 0.001);
        int h = 1;
    }

    /**
     * 手动签名交易测试
     */
    @Test
    public void TestSendSignTransaction(){
        EthApi ethApi = new EthApi();
        Boolean ret = ethApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }

        //首先对账户进行解锁
        Boolean isunlocked = ethApi.unlockAccount(fromAddress,fromAddressPWD);
        if(false == isunlocked)
        {
            System.out.println("unlock fail: "+ ethApi.getErrorMsg());
            return ;
        }

        //获取当前nonce值， nonce作为当前账户交易的排序号，在签名时必须输入 否则将无法签名
       // long  nonce =  ethApi.getNonce(fromAddress);
        long nonce = 32;
        String signData = ethApi.singnTransaction(fromAddress, destAddress, 0.1, 0.01, nonce);
        System.out.println(signData);
        String txid =  ethApi.sendSignTransaction(signData);
    }

    @Test
    public void TestGetBlockNumber(){
        EthApi ethApi = new EthApi();
        Boolean ret = ethApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }

        //获取当前区块高度
        long currentHeight = ethApi.getLatestBlockNum();
        if(currentHeight < 0) {
            return ;
        }

        ///获取当前区块交易信息
        ArrayList<BlockInfo>  blockInfoList = ethApi.getBlockByNumber(3929940);
        if(blockInfoList == null) {
            return ;
        }
    }
}

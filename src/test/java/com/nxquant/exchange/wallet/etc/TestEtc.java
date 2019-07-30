package com.nxquant.exchange.wallet.etc;

import com.nxquant.exchange.wallet.model.BlockInfo;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Administrator on 2018-06-12.
 */
public class TestEtc {
    // geth --datadir "./data" --nodiscover --dev -rpc --rpc-port 18880  --rpc-addr 0.0.0.0 --rpc-api="admin,debug,com.unifex.chainapi.eth,miner" --rpccorsdomain "*" --port 30305
    private String ethaddress = "http://192.168.44.163:18880";
    private String fromAddress = "0x63e65e955141278f539d06079c7446ce590f97ea";
    private String fromAddressPWD = "123456";
    private String destAddress = "0x1e80061e5f767f8e57136837de7a07b4d8ed7783";

    /**
     * 创建新账户
     */
    @Test
    public void TestCreateAccount(){
        EtcApi eetcApi = new EtcApi();
        Boolean ret = eetcApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }

        ///创建新账户， 需要输入密码
        String account = eetcApi.createNewAccount("123456");
        if(null != account)
        {
            System.out.println("account:"+account);
        }
        else
        {
            System.out.println("createNewAccount error:"+eetcApi.getErrorMsg());
        }
    }

    @Test
    public void TestGetBalance(){
        EtcApi eetcApi = new EtcApi();
        Boolean ret = eetcApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }

        BigDecimal balance =  eetcApi.getbalance(fromAddress);
        if(null != balance)
        {
            System.out.println("getbalance:"+balance);
        }
        else
        {
            System.out.println("getbalance error:"+eetcApi.getErrorMsg());
        }
    }

    /**
     * 测试自动交易
     */
    @Test
    public void TestTransaction(){
        EtcApi eetcApi = new EtcApi();
        Boolean ret = eetcApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }
        //首先对账户进行解锁
        Boolean isunlocked = eetcApi.unlockAccount(fromAddress, fromAddressPWD);
        if(false == isunlocked)
        {
            System.out.println("unlock fail: "+ eetcApi.getErrorMsg());
            return ;
        }

        String txid =  eetcApi.transfer(fromAddress, destAddress, 0.12, 0.01);
        System.out.println(txid);
    }

    /**
     * 手动签名交易测试
     */
    @Test
    public void TestSendSignTransaction(){
        EtcApi etcApi = new EtcApi();
        Boolean ret = etcApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }

        //首先对账户进行解锁
        Boolean isunlocked = etcApi.unlockAccount(fromAddress,fromAddressPWD);
        if(false == isunlocked)
        {
            System.out.println("unlock fail: "+ etcApi.getErrorMsg());
            return ;
        }

        //获取当前nonce值， nonce作为当前账户交易的排序号，在签名时必须输入 否则将无法签名
        long  nonce = etcApi.getNonce(fromAddress);
        String signData = etcApi.singnTransaction(fromAddress,destAddress, 0.12, 0.01, nonce);

        String txid =  etcApi.sendSignTransaction(signData);
    }

    @Test
    public void TestGetBlockNumber(){
        EtcApi etcApi = new EtcApi();
        Boolean ret = etcApi.createConnection(ethaddress);
        if(false == ret)
        {
            return;
        }

        //获取当前区块高度
        long currentHeight = etcApi.getLatestBlockNum();
        if(currentHeight < 0) {
            return ;
        }

        ///获取当前区块交易信息
        ArrayList<BlockInfo>  blockInfoList1 = etcApi.getBlockByNumber(4);
        ArrayList<BlockInfo>  blockInfoList2 = etcApi.getBlockByNumber(5);
        ArrayList<BlockInfo>  blockInfoList3 = etcApi.getBlockByNumber(17);
        ArrayList<BlockInfo>  blockInfoList4 = etcApi.getBlockByNumber(18);
        ArrayList<BlockInfo>  blockInfoList5 = etcApi.getBlockByNumber(19);
    }
}
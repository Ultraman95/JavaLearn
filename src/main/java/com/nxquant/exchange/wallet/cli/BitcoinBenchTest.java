package com.nxquant.exchange.wallet.cli;

import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.model.UnSpentInf;
import com.nxquant.exchange.wallet.bitcoin.BitCoinApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class BitcoinBenchTest {

    @Autowired
    static BitCoinApi bitCoinApi;

    public static void test1(String user, String pwd, String rpcAddress, String height){
        //获取当前块的信息,交易列表
        long start = System.currentTimeMillis();

        System.out.println("----------------  start test1 --------------");

        ArrayList<BlockInfo> BlockInfo = bitCoinApi.getBlockByNumber(Long.valueOf(height));
        if(null == BlockInfo)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }

        long end = System.currentTimeMillis();

        System.out.println("cost:" + (end - start));
        System.out.println("----------------  end test1 --------------");
    }

    public static void test2(String user, String pwd, String rpcAddress, String height){
        //获取当前块的信息,交易列表
        long start = System.currentTimeMillis();
        System.out.println("----------------  start test2 --------------");

        ArrayList<BlockInfo> BlockInfo = bitCoinApi.getBlockByNumberWithoutAnaForTest(Long.valueOf(height));
        if(null == BlockInfo)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }

        long end = System.currentTimeMillis();

        System.out.println("cost:" + (end - start));
        System.out.println("----------------  end test2 --------------");
    }

    public static void getUnspend(String user, String pwd, String rpcAddress){
        ArrayList<UnSpentInf>  txidList =  bitCoinApi.listAllUnSpent(0);
        if(null == txidList)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }

        for(UnSpentInf list : txidList) {
            System.out.println(list.getTxid() +", " +list.getAddress());
        }
    }

    public static void getAllTrans(String user, String pwd, String rpcAddress){
        ArrayList<String>   txidList = bitCoinApi.getAllTransactions();

        if(null == txidList)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }

        for(String list : txidList) {
            System.out.println(list);
        }
    }

    public static void getTran(String user, String pwd, String rpcAddress, String txid){
       String  txidList = bitCoinApi.getTransaction(txid);

        if(null == txidList)
        {
            System.out.println("transfer error:"+bitCoinApi.getErrorMsg());
            return  ;
        }

            System.out.println(txidList);
    }

    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];
        String testFaucet = args[2];
        String height = args[3];
        String txid = "";
        if(args.length > 4) {
            txid = args[4];
        }
       // BitcoinBenchTest.getAllTrans(username, password, testFaucet);
       // BitcoinBenchTest.getUnspend(username, password, testFaucet);
        BitcoinBenchTest.test1(username, password, testFaucet, height);

        /*
        BitcoinBenchTest.test2(username, password, testFaucet, height);
        System.out.println("sleep 2 seconds");
        try{
            Thread.sleep(3000);
        }catch (Exception ex){

        }
        BitcoinBenchTest.test2(username, password, testFaucet, height);

         */


    }
}

package com.nxquant.exchange.wallet.eth;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018-06-06.
 * ERC20API test
 */
public class TestERC20 {
    private static BigDecimal ERC20_UNIT = new BigDecimal("1000000000000000000"); //E18
    //private String ethaddress = "http://115.159.103.200:8545";
    private String ethaddress = "http://192.168.44.163:8545";
    private String contractAddress = "0x0f565ee8dcfeb0016d3c92158a5223378916e5a0";
    private String fromAddress = "0xdd9ef517fcf5763ee219f4862456837fb3c2ec50";
    private String fromAddressPWD = "123456";
    private String srcAddress = "0xa9b01b60ec957f19f03511a1bcb041479627920d";
    private String destAddress = "0x718ffff89e82ed74142646622e8c50bea9609fd6";
    private EthApi ethApi = new EthApi();
    private ERC20Api ERC20Api = new ERC20Api();
    private ERC20Api getERC20Api() {
        Boolean ret = ethApi.createConnection(ethaddress);
        if(false == ret)
        {
            return null;
        }

        ERC20Api.setEthApi(ethApi);

        return ERC20Api;
    }

    @Test
    public void testGetTokenSymbol() {
        String symbol =  getERC20Api().getTokenSymbol(fromAddress,contractAddress);
        if (symbol == null) {
            System.out.println(getERC20Api().getErrorMsg());
        }
        else
        {
            System.out.println(symbol);
        }
    }

    @Test
    public void testGetTokenName(){
        String name =  getERC20Api().getTokenName("0xac9fccaa490c6d58ebbaab5b5bbdfe09047da464",contractAddress);
        if (name == null) {
            System.out.println(getERC20Api().getErrorMsg());
        }
        else
        {
            System.out.println(name);
        }
    }

    @Test
    public void testGetBalance() {
        BigInteger balance =  getERC20Api().getBalance(fromAddress, contractAddress, "0xdd9ef517fcf5763ee219f4862456837fb3c2ec50");
        if (balance == null) {
            System.out.println(getERC20Api().getErrorMsg());
        }
        else
        {
            System.out.println(balance);
        }
    }

    @Test
    public void testTransfer() {
        //解锁账户
        Boolean isunlocked = getERC20Api().getEthApi().unlockAccount(fromAddress, "123456");
        if(false == isunlocked)
        {
            System.out.println("unlock fail: "+ getERC20Api().getErrorMsg());
            return ;
        }

        double damount = 10000;
        BigDecimal dfd = new BigDecimal(String.valueOf(damount)).multiply(ERC20_UNIT);
        BigInteger amount = dfd.toBigInteger();
        String txid =  getERC20Api().transfer(fromAddress, "0x8e5ba990f7a1c7f0733c0dfa85fb2716341acaf7", "0x5f91e843ac5deca3e332c9a0995982f6793540a0", amount,0.0012);
        if (txid == null) {
            System.out.println(getERC20Api().getErrorMsg());
        }
        else
        {
            System.out.println(txid);
        }
    }

    @Test
    public void testTransferFrom() {
        //解锁账户
        Boolean isunlocked = getERC20Api().getEthApi().unlockAccount(srcAddress, "123456");
        if(false == isunlocked)
        {
            System.out.println("unlock fail: "+ getERC20Api().getErrorMsg());
            return ;
        }
        double damount = 101;
        BigDecimal dfd = new BigDecimal(String.valueOf(damount)).multiply(ERC20_UNIT);
        BigInteger amount = dfd.toBigInteger();

        String txid =  getERC20Api().transferFrom(srcAddress, contractAddress, fromAddress, destAddress, amount, 0.0001);
        if (txid == null) {
            System.out.println(getERC20Api().getErrorMsg());
        }
        else
        {
            System.out.println(txid);
        }
    }


    @Test
    public void testAllowance() {
        ///解锁账户
        Boolean isunlocked = getERC20Api().getEthApi().unlockAccount(fromAddress,fromAddressPWD);
        if(false == isunlocked)
        {
            System.out.println("unlock fail: "+ getERC20Api().getErrorMsg());
           return ;
        }

        double damount = 10000;
        BigDecimal dfd = new BigDecimal(String.valueOf(damount)).multiply(ERC20_UNIT);
        BigInteger amount = dfd.toBigInteger();

         Boolean bool =   getERC20Api().allownace(fromAddress, contractAddress, srcAddress, amount);
         if (bool == null) {
             System.out.println(getERC20Api().getErrorMsg());
         }
         else
        {
            System.out.println(bool);
        }
    }

    @Test
    public void testGetAllowance() {
        BigInteger value =  getERC20Api().getAllowance(fromAddress, contractAddress, srcAddress, destAddress);
        if (value == null) {
            System.out.println(getERC20Api().getErrorMsg());
        }
        else
        {
            System.out.println(value);
        }
    }

    @Test
    public void testSignAllowance() {
        //解锁账户
        ethaddress = "http://192.168.44.163:8545";
        Boolean isunlocked = getERC20Api().getEthApi().unlockAccount(fromAddress, "123456");
        if(false == isunlocked)
        {
            System.out.println("unlock fail: "+ getERC20Api().getErrorMsg());
            return ;
        }

       // ethaddress = "http://192.168.44.163:8545";
        long  nonce =  11; //getERC20Api().getEthApi().getNonce(fromAddress);

        double damount = 10000;
        BigDecimal dfd = new BigDecimal(String.valueOf(damount)).multiply(ERC20_UNIT);
        BigInteger amount = dfd.toBigInteger();

        //ethaddress = "http://192.168.44.163:8555";
        String signdata =  getERC20Api().singnTransaction(fromAddress, contractAddress, "0x261c1401ac6ec950c533adb8217f4f85eededb63", amount, 0.0001,nonce);
        if (signdata == null) {
            System.out.println(getERC20Api().getErrorMsg());
            return ;
        }
        else
        {
            System.out.println(signdata);
        }
        ethaddress = "http://192.168.44.163:8545";
        String txid = getERC20Api().sendSignTransaction(signdata);
        if (txid == null) {
            System.out.println(getERC20Api().getErrorMsg());
        }
        else
        {
            System.out.println(txid);
        }
    }
}

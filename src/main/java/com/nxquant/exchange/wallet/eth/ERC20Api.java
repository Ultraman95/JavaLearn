package com.nxquant.exchange.wallet.eth;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018-06-04.
 */
public class ERC20Api {
    EthApi ethApi = null;
    private static BigDecimal ETHER = new BigDecimal("1000000000000000000"); //E18
    /**
     * 获取token 名字
     * @param fromAddress 发起方账户地址
     * @param contractAddress 合约地址
     * @return
     */
    public  String  getTokenName(String fromAddress, String contractAddress){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",fromAddress);
        map.put("to", contractAddress);
        map.put("data","0x06fdde03"); //name

        Object result;
        try{
            result =  ethApi.getClient().invoke("eth_call",new Object[]{map, "latest"}, Object.class);
        }catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return  null;
        }

        String token = result.toString().substring(2);
        String tokenName = hexStringToString(token);
        if(tokenName ==null)
        {
            return null;
        }
        return tokenName.trim();
    }

    /**
     * 获取token简称
     * @param fromAddress 发起方账户地址
     * @param contractAddress 合约地址
     * @return
     */
    public String  getTokenSymbol(String fromAddress, String contractAddress){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",fromAddress);
        map.put("to", contractAddress);
        map.put("data","0x95d89b41");   //symbol()

        Object result;
        try{
            result =  ethApi.getClient().invoke("eth_call",new Object[]{map, "latest"}, Object.class);
        }catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return  null;
        }

        String token = result.toString().substring(2);
        String tokenName = hexStringToString(token);
        if(tokenName ==null)
        {
            return null;
        }
        return tokenName.trim();
    }

    /**
     * 获取地址的token数量
     * @param fromAddress 查询发起方地址
     * @param contractAddress 合约地址
     * @param address 待查查询token地址
     */
    public BigInteger getBalance(String fromAddress, String contractAddress, String address) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",fromAddress);
        map.put("to", contractAddress);
        String funcNmae = "0x70a08231";  //balances(address)
        String parameter = formatParameter(address.substring(2));
        map.put("data",funcNmae + parameter);

        Object result;
        try{
            result =  ethApi.getClient().invoke("eth_call",new Object[]{map, "latest"}, Object.class);
        }catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return  null;
        }

        if(result.toString().length() <= 2){
            BigInteger balance = new BigInteger("0");
            return balance;
        }

        String balancestr = new BigInteger(result.toString().substring(2), 16).toString(10);
        BigInteger balance = new BigInteger(balancestr);
        return balance;
    }

    /**
     * 从发起方转账token到某地址
     * @param fromAddress 发起方地址
     * @param contractAddress 合约地址
     * @param destAddress 转账目标地址
     * @param amount 转账数量
     * @return
     */
    public String transfer(String fromAddress, String contractAddress, String destAddress, BigInteger amount, Double fee){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",fromAddress);
        map.put("to", contractAddress);
        String funcNmae = "0xa9059cbb";   //transfer(address,uint256)
        String parameterAddress = formatParameter(destAddress.substring(2));

        if(fee.doubleValue() > 0 && fee.doubleValue() < 1 ){
            long gas = 90000; //90000
            BigDecimal gasPriceWei = BigDecimal.valueOf(fee).multiply(ETHER).divide( BigDecimal.valueOf(gas),8,  BigDecimal.ROUND_HALF_UP );
            String gasPriceHex = ethApi.getHexString(gasPriceWei.longValue());
            String gasHex = ethApi.getHexString(gas);
            map.put("gas",gasHex);
            map.put("gasPrice",gasPriceHex);
        }

        String bigamountstr = new BigInteger(amount.toString() , 10).toString(16);
        String parameterAmount = formatParameter(String.valueOf(bigamountstr));

        map.put("data",funcNmae + parameterAddress + parameterAmount);

        Object result;
        try{
            result =  ethApi.getClient().invoke("eth_sendTransaction",new Object[]{map}, Object.class);
        } catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return  null;
        }

        return result.toString();
    }

    /**
     * 设定第三方允许转账token数量
     *  与 transferfrom 配合使用， 现在地址1调用allownace对地址2 的授权， 再在地址2发起到地址1的transferfrom
     * @param fromAddress 操作发起方地址
     * @param contractAddress 合约地址
     * @param spenderAddress 被允许地址
     * @param approveAmount 被允许转账token数量
     * @return
     */
    public Boolean allownace(String fromAddress, String contractAddress, String spenderAddress, BigInteger approveAmount){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",fromAddress);
        map.put("to", contractAddress);
        String funcNmae = "0x095ea7b3";   //approve(address,uint256)
        String parameterAddress = formatParameter(spenderAddress.substring(2));

        String bigapproveAmountStr =   new BigInteger(approveAmount.toString() , 10).toString(16);

        String parameterAmount = formatParameter(String.valueOf(bigapproveAmountStr));
        map.put("data",funcNmae + parameterAddress + parameterAmount);

        Object result;
        try{
            result =  ethApi.getClient().invoke("eth_sendTransaction",new Object[]{map}, Object.class);
        } catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return  null;
        }
        //成功返回
        return (Boolean)true;
    }

    /**
     * 获取账户之间转账允许tokens数量
     * @param fromAddress 操作发起方地址
     * @param contractAddress 合约地址
     * @param ownerAddress tokens所属地址
     * @param spenderAddress tokens被授权地址
     * @return
     */
    public BigInteger getAllowance(String fromAddress, String contractAddress, String ownerAddress, String spenderAddress) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",fromAddress);
        map.put("to", contractAddress);
        String funcNmae = "0xdd62ed3e";  //allowance(address,address)
        String parameterOwnerAddress = formatParameter(ownerAddress.substring(2));
        String parameterSpenderAddress = formatParameter(spenderAddress.substring(2));
        map.put("data",funcNmae + parameterOwnerAddress + parameterSpenderAddress);

        Object result;
        try{
            result =  ethApi.getClient().invoke("eth_call",new Object[]{map, "latest"}, Object.class);
        } catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return null;
        }

        String balancestr = new BigInteger(result.toString().substring(2), 16).toString(10);
        BigInteger balance = new BigInteger(balancestr);
        return balance;
    }

    /**
     * 发送 _value 数量的token从地址 _from 到 地址 _to
     * 与approve 配合使用， 现在地址1调用approve对地址2 的授权， 再在地址2发起到地址1的transferfrom
     * @param fromAddress
     * @param contractAddress
     * @param srcAddress
     * @param destAddress
     * @param amount
     * @return
     */
    public String transferFrom(String fromAddress, String contractAddress, String srcAddress, String destAddress ,BigInteger amount, Double fee){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",fromAddress);
        map.put("to", contractAddress);
        String funcNmae = "0x23b872dd";  //transferFrom(address,address,uint256)
        String parameterSrcAddress = formatParameter(srcAddress.substring(2));
        String parameterDestAddress = formatParameter(destAddress.substring(2));

        if(fee.doubleValue() > 0 && fee.doubleValue() < 1 ){
            long gas = 90000; //90000
            BigDecimal gasPriceWei = BigDecimal.valueOf(fee).multiply(ETHER).divide( BigDecimal.valueOf(gas),8,  BigDecimal.ROUND_HALF_UP );
            String gasPriceHex = ethApi.getHexString(gasPriceWei.longValue());
            String gasHex = ethApi.getHexString(gas);
            map.put("gas",gasHex);
            map.put("gasPrice",gasPriceHex);
        }

        String bigAmountStr =   new BigInteger(amount.toString() , 10).toString(16);
        String parameterAmount = formatParameter(String.valueOf(bigAmountStr));

        map.put("data",funcNmae + parameterSrcAddress + parameterDestAddress + parameterAmount);

        Object result;
        try{
            result =  ethApi.getClient().invoke("eth_sendTransaction",new Object[]{map}, Object.class);
        } catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    public String singnTransaction( String fromAddress, String contractAddress, String destAddress, BigInteger amount, Double fee, long nonce){
        long gas = 90000; //90000
        BigDecimal gaspricewei = BigDecimal.valueOf(fee).multiply(ETHER).divide( BigDecimal.valueOf(gas),8,  BigDecimal.ROUND_HALF_UP );
        String gasHex = ethApi.getHexString(gas);
        String gasPriceHex = ethApi.getHexString(gaspricewei.longValue());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",fromAddress);
        map.put("to", contractAddress);
        String funcNmae = "0xa9059cbb";   //transfer(address,uint256)
        String parameterAddress = formatParameter(destAddress.substring(2));

        String bigamountstr =   new BigInteger(amount.toString() , 10).toString(16);
        String parameterAmount = formatParameter(String.valueOf(bigamountstr));

        map.put("gas",gasHex);
        map.put("gasPrice",gasPriceHex);

        String nonceHex = ethApi.getHexString(nonce);

        map.put("nonce", nonceHex);
        map.put("data",funcNmae + parameterAddress + parameterAmount);

        LinkedHashMap result;
        try{
            result =   (LinkedHashMap)ethApi.getClient().invoke("eth_signTransaction",new Object[]{map}, Object.class);
        } catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return  null;
        }


        String singedData =  result.get("raw").toString();
        return singedData;
    }

    /**
     * 发送签名交易
     * @param signData
     * @return
     */
    public String sendSignTransaction(String signData){
        Object result;
        try{
            result =  ethApi.getClient().invoke("eth_sendRawTransaction",new Object[]{signData}, Object.class);
        }catch(Throwable ex) {
            ethApi.setErrorMsg(ex.getMessage());
            return  null;
        }

        return result.toString();
    }

    public EthApi getEthApi() {
        return ethApi;
    }

    public void setEthApi(EthApi ethApi) {
        this.ethApi = ethApi;
    }

    public String getErrorMsg() {
        return ethApi.getErrorMsg();
    }

    /**
     * 16进制转换成为string类型字符串
     * @param s
     * @return
     */
    public  String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            s = new String(baKeyword, "UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
        return s;
    }

    /**
     * 格式化字符串
     * @param parameter
     * @return
     */
    private String formatParameter(String parameter){
        int totalLen = 64;
        int parameterLen = parameter.length();
        String parameter_s = "";

        for(int  i = totalLen - parameterLen; i>0; i--) {
            parameter_s += "0";
        }

        parameter_s += parameter;
        return parameter_s;
    }
}

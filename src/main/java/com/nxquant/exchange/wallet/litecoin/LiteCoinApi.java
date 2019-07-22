package com.nxquant.exchange.wallet.litecoin;


import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import com.nxquant.exchange.wallet.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteCoinApi {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private JsonRpcHttpClient client = null;
    private String errorMsg = "";
    private static Base64 base64 = new Base64();
    /**
     * 与bitcoind 建立RPC连接
     * @param user 用户
     * @param pwd 密码
     * @param rpcAddress 地址http://localhost:8080
     * @return 是否新建成功
     */
    public Boolean createConnection(String user, String pwd, String rpcAddress){
        //String cred =  Base64.encode( (user+ ":" +pwd).getBytes() );
        String cred =   base64.encodeToString((user+ ":" +pwd).getBytes());
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", "Basic " + cred);
        try{
            client = new JsonRpcHttpClient(new URL(rpcAddress),headers);
        }catch(MalformedURLException ex){
            System.out.println(ex);
            return false;
        }catch(Exception ex) {
            System.out.println(ex);
            return false;
        }
        return true;
    }

    /**
     * 创建新地址
     * @param account  默认为""
     * @return
     */
    public String createNewAddress(String  account){
        if(account == null)
            account = "";

        Object result;
        try{
            result = client.invoke("getnewaddress",new Object[]{account}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 验证地址是否有效
     * @param address 待验证地址
     * @return  是否正确
     */
    public Boolean validateAddress(String address){
        if(address == null || address == "")
            return false;

        LinkedHashMap<String,Object> result= new LinkedHashMap<String,Object>();
        try{
            result = (LinkedHashMap<String,Object>)client.invoke("validateaddress",new Object[]{address}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }
        Boolean isvalid =  (Boolean)result.get("isvalid");
        return isvalid;
    }

    /**
     * 获取最新的地址高度
     * @return
     */
    public long getLatestBlockNum(){
        Integer result;
        try{
            result =  (Integer)client.invoke("getblockcount",new Object[]{}, Object.class);
        } catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  -1;
        }
        return result;
    }

    private String getblockhash(long blockNum){
        String blockHeadHash;
        try{
            blockHeadHash = (String)client.invoke("getblockhash",new Object[]{blockNum}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }

        return blockHeadHash;
    }

    private LinkedHashMap getblock( String blockHeadHash){
        LinkedHashMap result;
        try{
            result = (LinkedHashMap)client.invoke("getblock",new Object[]{blockHeadHash}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }

        return result;
    }
    /**
     * 获取指定区块信息
     * @param blockNum
     * @return
     */
    public ArrayList<BlockInfo> getBlockByNumber(long blockNum){
        String blockHeadHash = getblockhash(blockNum);
        if(blockHeadHash == null)
        {
            return null;
        }

        LinkedHashMap result =  getblock(blockHeadHash);
        if(result == null)
        {
            return null;
        }

        Integer height = (Integer)result.get("height");

        ArrayList<BlockInfo> BlockInfos = new ArrayList<BlockInfo>();

        ArrayList<String> transactions = (ArrayList<String>)result.get("tx");
        for(int i=0; i<transactions.size(); i++){
            String txid = (String)transactions.get(i);
            LinkedHashMap txidInfo;
            try{
                txidInfo = (LinkedHashMap)client.invoke("getrawtransaction",new Object[]{txid,true}, Object.class);
            }catch(Throwable ex) {
                setErrorMsg(ex.getMessage());
                logger.info("["+txid+"]"+ex.getMessage());
                continue;
            }

            ArrayList<LinkedHashMap> vouts = ( ArrayList<LinkedHashMap>)txidInfo.get("vout");
            for(int j=0; j<vouts.size(); j++){
                LinkedHashMap vout = vouts.get(j);
                Double value = (Double)vout.get("value");
                LinkedHashMap scriptPubKey = (LinkedHashMap)vout.get("scriptPubKey");
                if(scriptPubKey == null)
                {
                    continue;
                }

                ArrayList<String> addresses = ( ArrayList<String>)scriptPubKey.get("addresses");
                if(addresses == null)
                {
                    continue;
                }

                BlockInfo blockInfo = new BlockInfo();
                if(addresses.size() > 0 )
                {
                    blockInfo.setToAddress(addresses.get(0)); //目前只考虑一个地址转账操作
                }

                blockInfo.setTxid(txid);
                blockInfo.setBlockNo(height);
                blockInfo.setValue(value);

                BlockInfos.add(blockInfo);
            }
        }

        return BlockInfos;
    }

    /**
     * 获得当前钱包总金额
     * @param confirmations 已确认区块数量 大于0
     * @return
     */
    public BigDecimal getWalletBalance(int confirmations){
        Object result;
        try{
            result = client.invoke("getbalance",new Object[]{"", confirmations}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }
        double d = Double.parseDouble(result.toString());
        return BigDecimal.valueOf(d);
    }

    /**
     * 获取指定账户的余额
     * @param account , 可为""， 表示整个钱包所有账户
     * @param confirmations, 已确认区块数量，大于0
     * @return
     */
    public BigDecimal getAccountBalance(String account, int confirmations){
        Object result;
        try{
            result = client.invoke("getbalance",new Object[]{account, confirmations}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }
        double d = Double.parseDouble(result.toString());

        return BigDecimal.valueOf(d);
    }

    /**
     * 查询地址的金额
     * @param address
     * @param confirmations, 已确认区块数量，大于等于0
     * @return
     */
    public double getReceivedByAddress(String  address, int confirmations){
        if(confirmations <0)
        {
            return 0;
        }

        Object result;
        try{
            result = client.invoke("getreceivedbyaddress",new Object[]{address, confirmations}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  0;
        }
        double d = Double.parseDouble(result.toString());
        return d;
    }

    /**
     * 为地址指定账户，  注意：如果该地址已经被关联到指定帐户，将创建一个新的地址与该帐户关联
     * @param address
     * @param account
     * @return
     */
    public Boolean setAccountForAddress(String address, String account){
        Object result;
        try{
            result = client.invoke("setaccount",new Object[]{address, account}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  false;
        }
        return true;
    }

    /**
     *  返回指定账户<account>上收到至少<confirmations>确认的收款交易总金额
     * @param account
     * @param confirmations, 已确认区块数量，大于0
     * @return
     */
    public double getReceivedByAccount(String  account, int confirmations){
        Object result;
        try{
            result = client.invoke("getreceivedbyaccount",new Object[]{account, confirmations}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  0;
        }
        double d = Double.parseDouble(result.toString());
        return d;
    }

    /**
     * 获取指定账户的地址
     * @param account 为 "可以为""， 查询默认账户名称的地址
     * @return
     */
    public ArrayList<String> getAddressesAyAccount(String  account){
        ArrayList<String> result;
        try{
            result = (ArrayList<String>)client.invoke("getaddressesbyaccount",new Object[]{account}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result;
    }

    /**
     * 获取指定地址的账户
     * @param address
     * @return
     */
    public ArrayList<String>  getAccountByAddress(String address){
        ArrayList<String> result;
        try{
            result = (ArrayList<String>)client.invoke("getaccount",new Object[]{address}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result;
    }

    /**
     * 查询钱包内的所有在使用的地址， 注意不包括刚创建好，未参与交易输入的地址
     * @return 返回地址列表, 如果失败返回null
     */
    public ArrayList<LiteAddressModel>  getAllAddress(){
        ArrayList<LiteAddressModel> addList = new   ArrayList<LiteAddressModel> ();
        ArrayList<ArrayList<ArrayList<Object>>> result;
        try{
            result = ( ArrayList<ArrayList<ArrayList<Object>>>)client.invoke("listaddressgroupings",new Object[]{}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }

        for(int i=0; i<result.size(); i++)
        {
            ArrayList<ArrayList<Object>> hd1 = result.get(i);
            for(int j=0; j<hd1.size(); j++)
            {
                ArrayList<Object> hd2 = hd1.get(j);
                LiteAddressModel addressModel = new LiteAddressModel();
                addressModel.setAddress((String)hd2.get(0));
                if(hd2.size() == 3)
                {
                    addressModel.setAmount(Double.parseDouble(hd2.get(1).toString()));
                    addressModel.setAccount((String)hd2.get(2));
                }

                if(hd2.size() == 2)
                {
                    addressModel.setAmount(Double.parseDouble(hd2.get(1).toString()));
                }

                addList.add(addressModel);
            }
        }

        return addList;
    }

    /**
     * 获取指定地址的私钥
     * @param address
     * @return 返回私钥, 如果失败返回null
     */
    public String getPrivKey(String address){
        Object result;
        try{
            result = client.invoke("dumpprivkey",new Object[]{address}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 向特定地址转账
     * @param destAddress
     * @param amount
     * @return  返回txid, 如果失败返回null
     */
    public String transfer(String destAddress, double amount){
        Object result;
        try{
            result = client.invoke("sendtoaddress",new Object[]{destAddress,amount}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 查询某个地址的未花费数量，默认6块确认以上
     * @param address  需要查询的地址
     * @param  minConfirom 最小确认数，建议6以上
     * @return
     */
    public ArrayList<UnSpentInf> listUnSpentByaddress(String address, int minConfirom ){
        ArrayList<String> addressList = new  ArrayList<String>();
        addressList.add(address);
        int maxConfirom = 9999999;
        ArrayList<LinkedHashMap>  result;
        try{
            result = (  ArrayList<LinkedHashMap> )client.invoke("listunspent",new Object[]{minConfirom ,maxConfirom, addressList}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }

        ArrayList<UnSpentInf> unSpentInfList = new ArrayList<UnSpentInf>();
        for(int i=0; i<result.size(); i++){
            UnSpentInf unSpentInf = new UnSpentInf();
            String txid = (String)result.get(i).get("txid");
            String toaddress = (String)result.get(i).get("address");
            String scriptPubKey  = (String)result.get(i).get("scriptPubKey");
            Double amount = (Double)result.get(i).get("amount");
            Integer confirmations  =  (Integer)result.get(i).get("confirmations");
            Integer vout =  (Integer)result.get(i).get("vout");

            if(null != result.get(i).get("redeemScript")) {
                unSpentInf.setRedeemScript((String)result.get(i).get("redeemScript"));
            }

            unSpentInf.setAddress(toaddress);
            unSpentInf.setAmount(amount);
            unSpentInf.setConfirmations(confirmations);
            unSpentInf.setTxid(txid);
            unSpentInf.setScriptPubKey(scriptPubKey);
            unSpentInf.setVout(vout);

            unSpentInfList.add(unSpentInf);
        }

        return  unSpentInfList;
    }

    /**
     * 查询钱包所有未花费数量
     * @return
     */
    public ArrayList<UnSpentInf> listAllUnSpent(){
        int minConfirom = 1;
        ArrayList<LinkedHashMap>  result;
        try{
            result = (ArrayList<LinkedHashMap> )client.invoke("listunspent",new Object[]{minConfirom}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }

        ArrayList<UnSpentInf> unSpentInfList = new ArrayList<UnSpentInf>();
        for(int i=0; i<result.size(); i++){
            UnSpentInf unSpentInf = new UnSpentInf();
            String txid = (String)result.get(i).get("txid");
            String toaddress = (String)result.get(i).get("address");
            String scriptPubKey  = (String)result.get(i).get("scriptPubKey");
            Double amount = (Double)result.get(i).get("amount");
            Integer confirmations  =  (Integer)result.get(i).get("confirmations");
            Integer vout =  (Integer)result.get(i).get("vout");

            if(null != result.get(i).get("redeemScript")) {
                unSpentInf.setRedeemScript((String)result.get(i).get("redeemScript"));
            }

            unSpentInf.setAddress(toaddress);
            unSpentInf.setAmount(amount);
            unSpentInf.setConfirmations(confirmations);
            unSpentInf.setTxid(txid);
            unSpentInf.setScriptPubKey(scriptPubKey);
            unSpentInf.setVout(vout);

            unSpentInfList.add(unSpentInf);
        }

        return  unSpentInfList;
    }

    /**
     *
     * @param toAddress
     * @param refundAddress 找零退回的地址
     * @param unSpentInf
     * @param amount
     * @param fee
     * @return
     */
    public String  createRawtransaction(String toAddress, String refundAddress,  UnSpentInf unSpentInf,  double amount, double fee){

        ArrayList<  Map<String, Object> > txidList = new  ArrayList<  Map<String, Object> >();
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("txid",unSpentInf.getTxid());
        map1.put("vout", unSpentInf.getVout());
        txidList.add(map1);

        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put(toAddress,amount);

        BigDecimal bg = new BigDecimal(unSpentInf.getAmount() - amount - fee);
        double f1 = bg.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        map2.put(refundAddress, f1);

        Object result;
        try{
            result = client.invoke("createrawtransaction",new Object[]{txidList,map2}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return null;
        }

        return result.toString();
    }

    /**
     *
     * @param rawTransaction
     * @param unSpentInf
     * @param privateKeyList
     * @return
     */
    public String signRawtransaction(String rawTransaction, UnSpentInf unSpentInf, ArrayList<String>  privateKeyList){
        ArrayList<  Map<String, Object> > txidList = new  ArrayList<  Map<String, Object> >();
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("txid",unSpentInf.getTxid());
        map1.put("vout", unSpentInf.getVout());
        map1.put("scriptPubKey", unSpentInf.getScriptPubKey());
        map1.put("redeemScript", unSpentInf.getRedeemScript());
        txidList.add(map1);

        LinkedHashMap result;
        if(privateKeyList.size() >0 && unSpentInf.getRedeemScript() == ""){
            try{
                result = (LinkedHashMap)client.invoke("signrawtransaction",new Object[]{rawTransaction, txidList, privateKeyList}, Object.class);
            }catch(Throwable ex) {
                setErrorMsg(ex.getMessage());
                logger.info(ex.getMessage());
                return null;
            }
        } else {
            try{
                result = (LinkedHashMap)client.invoke("signrawtransaction",new Object[]{rawTransaction}, Object.class);
            }catch(Throwable ex) {
                setErrorMsg(ex.getMessage());
                logger.info(ex.getMessage());
                return null;
            }
        }

        Boolean complete = (Boolean)result.get("complete");
        if(complete == false)
        {
            String strError = "";
            ArrayList<LinkedHashMap> errors =  (ArrayList<LinkedHashMap> )result.get("errors");
            for(int i=0; i<errors.size(); i++){
                String error = (String)errors.get(i).get("error");
                strError = strError+ " "+error;
            }
            setErrorMsg(strError);
            logger.info(strError);
            return null;
        }
        String hex = result.get("hex").toString();
        return hex;
    }

    /**
     * 发送签名交易
     * @param signedTransaction，已经签名的交易信息
     * @return  返回交易ID
     */
    public String sendRawtransaction(String signedTransaction){

        Object result;
        try{
            result = client.invoke("sendrawtransaction",new Object[]{signedTransaction}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return null;
        }

        return result.toString();
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    private void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
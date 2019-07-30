package com.nxquant.exchange.wallet.omni;
import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.model.UnSpentInf;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OmniApi {
    private static Logger logger = LoggerFactory.getLogger(OmniApi.class);
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
     * 导入地址
     * @param address
     * @return
     */
    public Boolean importAddress(String address){
        if(address == null || address == "")
            return false;

        try{
            Object result = (LinkedHashMap<String,Object>)client.invoke("importaddress",new Object[]{address,"", false}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return null;
        }

        return true;
    }

    /**
     * 获取指定地址的USDT值
     * @param address
     * @param propertyid , 目前测试为1， 公网31 为USDT
     * @return
     */
    public BigDecimal getBalance(String address, int propertyid){
        LinkedHashMap result;
        try{
            result = (LinkedHashMap)client.invoke("omni_getbalance",new Object[]{address, propertyid}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }

        String blance = (String)result.get("balance");
        return new BigDecimal(blance);
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
    public ArrayList<UnSpentInf> listAllUnSpent(  int minConfirom ){
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

    /**
     * 获取指定区块信息
     * @param blockNum
     * @return
     */
    public ArrayList<BlockInfo> getBlockByNumber(long blockNum, long propertyid){
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
                txidInfo = (LinkedHashMap)client.invoke("omni_gettransaction",new Object[]{txid}, Object.class);
            }catch(Throwable ex) {
                setErrorMsg(ex.getMessage());
                logger.info("["+txid+"]"+ex.getMessage());
                continue;
            }

            boolean valid =  (Boolean) txidInfo.get("valid");
            if(valid == true)
            {
                if(txidInfo.get("propertyid") == null) {
                    continue;
                }

                Long propertyidres = Long.parseLong(txidInfo.get("propertyid").toString());
                if(propertyidres != propertyid) {
                    continue;
                }
                BlockInfo blockInfo = new BlockInfo();
                blockInfo.setTxid(txid);
                String amount = (String)txidInfo.get("amount");
                blockInfo.setValue(Double.valueOf(amount));
                blockInfo.setBlockNo(height);
                blockInfo.setPropertyID(propertyidres);
                blockInfo.setToAddress((String)txidInfo.get("referenceaddress"));
                BlockInfos.add(blockInfo);
            }else
            {
                logger.info(txid+"is not valid");
            }
        }
        return BlockInfos;
    }

    /**
     * 向特定地址转账
     * @param fromAddress
     * @param destAddress
     * @param amount
     * @param  propertyid, 目前测试为1， 公网31 为USDT
     * @return  返回txid, 如果失败返回null
     */
    public String transfer(String fromAddress, String destAddress, double amount, int propertyid){
        String amountstr = String.valueOf(amount);
        Object result;
        try{
            result = client.invoke("omni_send",new Object[]{fromAddress, destAddress, propertyid, amountstr}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 向特定地址转账并制定费用地址
     * @param fromAddress
     * @param destAddress
     * @param feeAddress 指定费用地址
     * @param amount
     * @param  propertyid, 目前测试为1， 公网31 为USDT
     * @return  返回txid, 如果失败返回null
     */
    public String transfer(String fromAddress, String destAddress, String feeAddress, double amount, int propertyid){
        String amountstr = String.valueOf(amount);
        Object result;
        try{
            result = client.invoke("omni_funded_send",new Object[]{fromAddress, destAddress, propertyid, amountstr, feeAddress}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 指定费用地址
     * @param fromAddress
     * @param destAddress
     * @param feeAddress
     * @param amount
     * @param propertyid
     * @return
     */
    public String transferByFeeAddress(String fromAddress, String destAddress,  String feeAddress, double amount, int propertyid){
        String amountstr = String.valueOf(amount);
        Object result;
        try{
            result = client.invoke("omni_funded_send",new Object[]{fromAddress, destAddress, propertyid, amountstr, feeAddress}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 创建原始交易
     * @param amount
     * @param propertyid
     * @return
     */
    public String createSendPayLoad(double amount, int propertyid){
        String amountstr = String.valueOf(amount);
        Object result;
        try{
            result = client.invoke("omni_createpayload_simplesend",new Object[]{propertyid, amountstr}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 创建交易
     * @param UnSpentInfList
     * @return
     */
    public String createRawTransaction(ArrayList<UnSpentInf> UnSpentInfList){
        ArrayList<  Map<String, Object> > txidList = new  ArrayList<  Map<String, Object> >();

        for(int i=0; i<UnSpentInfList.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("txid",UnSpentInfList.get(i).getTxid());
            map.put("vout", UnSpentInfList.get(i).getVout());
            txidList.add(map);
        }

        Map<String, Object> map2 = new HashMap<String, Object>();
        Object result;
        try{
            result = client.invoke("createrawtransaction",new Object[]{txidList, map2}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 对原始交易进行合并
     * @param payload
     * @param rawData
     * @return
     */
    public String createRawtxOpreturn(String payload, String rawData){
        Object result;
        try{
            result = client.invoke("omni_createrawtx_opreturn",new Object[]{rawData, payload}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 对原始交易添加接收地址
     * @param rawOptData
     * @param address
     * @return
     */
    public String addReceiverToRawtxOpreturn(String rawOptData, String address){
        Object result;
        try{
            result = client.invoke("omni_createrawtx_reference",new Object[]{rawOptData, address}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
    }

    /**
     * 新增返回额外交易信息
     * @param changedAddress
     * @param rawOptTxData
     * @param UnSpentInfList
     * @param fee
     * @return
     */
    public String addRawDataChange(String changedAddress, String rawOptTxData, ArrayList<UnSpentInf> UnSpentInfList, double fee){
        ArrayList<  Map<String, Object> > txidList = new  ArrayList<  Map<String, Object> >();

        for(int i=0; i<UnSpentInfList.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("txid",UnSpentInfList.get(i).getTxid());
            map.put("vout", UnSpentInfList.get(i).getVout());
            map.put("scriptPubKey", UnSpentInfList.get(i).getScriptPubKey());
            map.put("value", UnSpentInfList.get(i).getAmount());
            txidList.add(map);
        }

        Object result;
        try{
            result = client.invoke("omni_createrawtx_change",new Object[]{rawOptTxData, txidList, changedAddress, fee}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }

        return result.toString();
    }

    /**
     * 对交易进行签名
     * @param rawData
     * @return
     */
    public String signRawTransaction(String rawData, ArrayList<UnSpentInf> unSpentInf){
        ArrayList<  Map<String, Object> > txidList = new  ArrayList<  Map<String, Object> >();
        for(int i=0; i<unSpentInf.size(); i++)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("txid",unSpentInf.get(i).getTxid());
            map.put("vout", unSpentInf.get(i).getVout());
            map.put("scriptPubKey", unSpentInf.get(i).getScriptPubKey());
            map.put("redeemScript", unSpentInf.get(i).getRedeemScript());
            map.put("amount", unSpentInf.get(i).getAmount());
            txidList.add(map);
        }

        LinkedHashMap result;
        try{
            result = (LinkedHashMap)client.invoke("signrawtransaction",new Object[]{rawData, txidList}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }

        String hex = result.get("hex").toString();
        return hex;
    }

    /**
     * 发送签名交易
     * @param signRawTransaction
     * @return
     */
    public String sendRawTransaction(String signRawTransaction){
        Object result;
        try{
            result = client.invoke("sendrawtransaction",new Object[]{signRawTransaction}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }
        return result.toString();
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

    public String getErrorMsg() {
        return errorMsg;
    }

    private void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}

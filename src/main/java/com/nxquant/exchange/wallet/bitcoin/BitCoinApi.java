package com.nxquant.exchange.wallet.bitcoin;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.codec.binary.Base64;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nxquant.exchange.wallet.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author shilf
 * BCH--比特币
 */
@Lazy
@Component
public class BitCoinApi {
    private Logger logger = LoggerFactory.getLogger(getClass());

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private JsonRpcHttpClient client = null;
    private String errorMsg = "";
    private static Base64 base64 = new Base64();

    public BitCoinApi(@Value("${com.nxquant.wallet.btc.user}") String user, @Value("${com.nxquant.wallet.btc.pwd}")String pwd, @Value("${com.nxquant.wallet.btc.rpcAddress}")String rpcAddress){
        createClient(user, pwd, rpcAddress);
    }


    /**
     * 创建与bitcoind交互的rpcClient
     * @param user 用户
     * @param pwd 密码
     * @param rpcAddress 地址http://127.0.0.1:18883
     * @return 是否新建成功
     */
    Boolean createClient(String user, String pwd, String rpcAddress){
        try{
            String cred =   base64.encodeToString((user+ ":" +pwd).getBytes());
            Map<String, String> headers = new HashMap<String, String>(1);
            headers.put("Authorization", "Basic " + cred);
            client = new JsonRpcHttpClient(new URL(rpcAddress),headers);
        }catch(Exception ex) {
            logger.error("Error:BitCoinApi--createConnection !" + ex);
            return false;
        }
        return true;
    }

    /**
     * 创建新地址
     * @param reqInfo 请求参数
     * @return 新创建的地址
     */
    public String createNewAddress(CreateAddressReqInfo reqInfo){
        String result;
        try{
            result = client.invoke(BitCoinRpcMethod.GET_NEW_ADDRESS, reqInfo, String.class);
        }catch(Throwable ex) {
            System.out.println("result ---> " + ex.getMessage());
            setErrorMsg(ex.getMessage());
            return  null;
        }
        System.out.println("result ---> " + result);
        return result;
    }

    /**
     * 验证地址是否有效
     * @param reqInfo 请求参数
     * @return  是否正确
     */
    public Boolean validateAddress(ValidateAddressReqInfo reqInfo){
        ValidateAddressRspInfo result;
        try{
            result = client.invoke(BitCoinRpcMethod.VALIDATE_ADDRESS, reqInfo, ValidateAddressRspInfo.class);
        }catch(Throwable ex) {
            System.out.println("result ---> " + ex.getMessage());
            setErrorMsg(ex.getMessage());
            return null;
        }
        return result.getIsvalid();
    }

    /**
     * 获取最新的地址高度
     * @return 最新区块高度
     */
    public Long getLatestBlockHeight(){
        Long result;
        try{
            result =  client.invoke(BitCoinRpcMethod.GET_BLOCKCOUNT,null, Long.class);
        } catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return null;
        }
        return result;
    }

    /**
     * 获取区块的hash值
     * @param blockHeight 区块高度
     * @return  区块的hash值
     */
    private String getBlockHash(long blockHeight){
        String blockHeadHash;
        try{
            blockHeadHash = client.invoke(BitCoinRpcMethod.GET_BLOCKHASH,new Object[]{blockHeight}, String.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }
        return blockHeadHash;
    }

    /**
     * 获取区块信息
     * @param blockHeadHash 区块头hash
     * @return 区块信息
     */
    private LinkedHashMap getBlock( String blockHeadHash){
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
        String blockHeadHash = getBlockHash(blockNum);
        if(blockHeadHash == null)
        {
            return null;
        }

        LinkedHashMap result =  getBlock(blockHeadHash);
        if(result == null)
        {
            return null;
        }

        Integer height = (Integer)result.get("height");

        ArrayList<BlockInfo> blockInfoList = new ArrayList<BlockInfo>();

        ArrayList<String> transactions = (ArrayList<String>)result.get("tx");
        for(int i=0; i<transactions.size(); i++){
            String txId = transactions.get(i);
            LinkedHashMap txInfo;
            try{
                txInfo = (LinkedHashMap)client.invoke("getrawtransaction",new Object[]{txId,true}, Object.class);
            }catch(Throwable ex) {
                setErrorMsg(ex.getMessage());
                logger.info("["+txId+"]"+ex.getMessage());
                continue;
            }

            ArrayList<LinkedHashMap> vOutList = ( ArrayList<LinkedHashMap>)txInfo.get("vout");
            for(int j=0; j<vOutList.size(); j++){

                LinkedHashMap vOut = vOutList.get(j);
                Double value = (Double)vOut.get("value");
                LinkedHashMap scriptPubKey = (LinkedHashMap)vOut.get("scriptPubKey");
                if(scriptPubKey == null)
                {
                    continue;
                }

                ArrayList<String> addresses = (ArrayList<String>)scriptPubKey.get("addresses");
                if(addresses == null)
                {
                    continue;
                }
                BlockInfo blockInfo = new BlockInfo();
                if(addresses.size() > 0 )
                {
                    //目前只考虑一个地址转账操作
                    blockInfo.setToAddress(addresses.get(0));
                }
                blockInfo.setTxid(txId);
                blockInfo.setBlockNo(height);
                blockInfo.setValue(value);
                blockInfoList.add(blockInfo);
            }
        }
        return blockInfoList;
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
     * 查询地址所有收到的金额
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
    public ArrayList<AddressModel>  getAllAddress(){
        ArrayList<AddressModel> addList = new   ArrayList<AddressModel> ();
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
                AddressModel addressModel = new AddressModel();
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
	 * 获取指定交易的状态
	 * @param txid
	 * @return
	 */
	public LinkedHashMap getTranserStatus(String txid) {
		LinkedHashMap txidInfo = null;

		try{
			txidInfo = (LinkedHashMap)client.invoke("getrawtransaction",new Object[]{txid,true}, Object.class);
		}catch(Throwable ex) {
			setErrorMsg(ex.getMessage());
			logger.info("["+txid+"]"+ex.getMessage());
		}

		return txidInfo;
	}

	/**
	 * 获取指定交易的状态
	 * @param txid
	 * @return
	 */
	public LinkedHashMap getTransaction(String txid) {
		LinkedHashMap txidInfo = null;

		try{
			txidInfo = (LinkedHashMap)client.invoke("gettransaction",new Object[]{txid,true}, Object.class);
		}catch(Throwable ex) {
			setErrorMsg(ex.getMessage());
			logger.info("["+txid+"]"+ex.getMessage());
		}

		return txidInfo;
	}

	public ArrayList<TxDetail> queryTxDetailByBlock(long blockNum){
		String blockHeadHash = getBlockHash(blockNum);
		if(blockHeadHash == null) {
            return null;
        }

		LinkedHashMap result =  getBlock(blockHeadHash);
		if(result == null) {
            return null;
        }

		Integer height = (Integer)result.get("height");

		ArrayList<TxDetail> TxDetails = new ArrayList<TxDetail>();

		ArrayList<String> transactions = (ArrayList<String>)result.get("tx");
		for(int i=0; i<transactions.size(); i++){
			String txid = (String)transactions.get(i);
			LinkedHashMap txidInfo;
			try{
				txidInfo = (LinkedHashMap)client.invoke("gettransaction",new Object[]{txid,true}, Object.class);
			}catch(Throwable ex) {
				setErrorMsg(ex.getMessage());
				logger.info("["+txid+"]"+ex.getMessage());
				continue;
			}

            ArrayList<LinkedHashMap> details = ( ArrayList<LinkedHashMap>)txidInfo.get("details");

			for(int j = 0;j<details.size();j++){
                LinkedHashMap detail = (LinkedHashMap)details.get(j);
                TxDetail txDetail = new TxDetail();

                txDetail.setTxId(txid);
                txDetail.setBlockHash(txidInfo.get("blockhash").toString());
                txDetail.setBlockHeight(blockNum);
                txDetail.setConfirmations(Integer.parseInt(txidInfo.get("confirmations").toString()));
                txDetail.setCategory(detail.get("category").toString());
                txDetail.setTxAmount(BigDecimal.valueOf(Math.abs(Double.valueOf(detail.get("amount").toString()))));
                if(detail.containsKey("fee")) {
                    txDetail.setTxFee(BigDecimal.valueOf(Math.abs(Double.valueOf(detail.get("fee").toString()))));
                }
                else {
                    txDetail.setTxFee(BigDecimal.valueOf(0));
                }
                txDetail.setToAddress(detail.get("address").toString());

                String timeStamp = txidInfo.get("timereceived").toString();
				long nTime = Long.valueOf(timeStamp) * 1000L;
				Date dt = new Date(nTime);
                txDetail.setTxReceiveTime(simpleDateFormat.format(dt));

                TxDetails.add(txDetail);
            }

		}

		return TxDetails;
	}

    public String getErrorMsg() {
        return errorMsg;
    }

    private void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
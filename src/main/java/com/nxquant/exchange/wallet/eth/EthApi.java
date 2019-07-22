package com.nxquant.exchange.wallet.eth;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.nxquant.exchange.wallet.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class EthApi {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private JsonRpcHttpClient client = null;
    private String errorMsg = "";
    private static BigDecimal ETHER = new BigDecimal("1000000000000000000"); //E18
    private static long GWEI =  1000000000;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * ETH 建立RPC连接
     * @param rpcAddress 地址http://localhost:8080
     * @return 是否新建成功
     */
    public Boolean createConnection(String rpcAddress){
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("content-type", "application/json");

        try{
            client = new JsonRpcHttpClient(new URL(rpcAddress), headers);
        }catch(MalformedURLException ex){
            setErrorMsg(ex.getMessage());
            return false;
        }catch(Exception ex) {
            setErrorMsg(ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 获取所有账户
     * @return 所有账户
     */
    public ArrayList<String> getAllAccounts(){
        ArrayList<String> result;
        try{
            result = (ArrayList<String>)client.invoke("eth_accounts",new Object[]{}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }
        return result;
    }

    /**
     * 获取新的账户地址
     * @param passwd,账户的密码
     * @return, 新的账户地址
     */
    public String createNewAccount(String passwd){
        String result;
        try{
            result = (String)client.invoke("personal_newAccount",new Object[]{passwd}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }
        return result;
    }

    /**
     * 获取最新块的高度
     * @return
     */
    public long getLatestBlockNum(){
        String result;
        try{
            result = (String)client.invoke("eth_blockNumber",new Object[]{}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  -1;
        }
        String str = result.substring(2);
        Long iValue = Long.parseLong(str, 16);
        return iValue;
    }

    /**
     * 获取指定Blocknum的块信息
     * @param blockNum
     * @return
     */
    public ArrayList<BlockInfo> getBlockByNumber(long blockNum){
        LinkedHashMap result;
        String blockNumStr = getHexString(blockNum);
        try{
            result = (LinkedHashMap)client.invoke("eth_getBlockByNumber",new Object[]{blockNumStr,true}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }

        if(result == null)
        {
            setErrorMsg("unexpected error");
            return null;
        }

        ArrayList<LinkedHashMap> transactions = (ArrayList<LinkedHashMap>)result.get("transactions");
        ArrayList<BlockInfo> BlockInfos = new ArrayList<BlockInfo>();
        for(int i=0; i<transactions.size(); i++){
            BlockInfo blockInfo = new BlockInfo();
            String txid = (String)transactions.get(i).get("hash");
            String toaddress = (String)transactions.get(i).get("to");

            if(null == toaddress || "null" == toaddress)
            {
                continue;
            }
            blockInfo.setTxid(txid);
            blockInfo.setToAddress(toaddress);

            String input = (String)transactions.get(i).get("input");
            analysisInput(blockInfo, input, toaddress);

            String value = (String)transactions.get(i).get("value");
            BigInteger weivalue = new BigInteger(value.substring(2), 16);
            BigDecimal ethvalue = new BigDecimal(weivalue).divide(ETHER);
            blockInfo.setValue(ethvalue.doubleValue());
            blockInfo.setBlockNo(blockNum);
            BlockInfos.add(blockInfo);
        }

        return BlockInfos;
    }

    private void analysisInput(BlockInfo blockInfo, String input, String address){
        int dist = 64;
        int addressLen = 40;
        blockInfo.setContractTx(false);
        if(input.startsWith(Erc20MethodConstant.transfer)){
            try{
                String toAddress = "0x"+input.substring(Erc20MethodConstant.transfer.length() + (dist - addressLen), Erc20MethodConstant.transfer.length() + dist);
                blockInfo.setToAddress(toAddress);
                blockInfo.setContractAddress(address);

                String hexValue = input.substring(Erc20MethodConstant.transfer.length() + dist);
                BigInteger weivalue = new BigInteger(hexValue, 16);
                blockInfo.setContractValue(weivalue);
            }catch (Exception ex){
                blockInfo.setContractValue(new BigInteger("0"));
                logger.error(ex.getMessage());
            }

            blockInfo.setContractTx(true);

        } else if(input.startsWith(Erc20MethodConstant.transferFrom)){
            logger.info(input);
            try{
                String toAddress = "0x"+input.substring(Erc20MethodConstant.transfer.length() + (2*dist - addressLen), Erc20MethodConstant.transfer.length() + 2*dist);
                blockInfo.setToAddress(toAddress);
                blockInfo.setContractAddress(address);

                String hexValue = input.substring(Erc20MethodConstant.transfer.length() + 2*dist);
                BigInteger weivalue = new BigInteger(hexValue, 16);
                blockInfo.setContractValue(weivalue);
            }catch (Exception ex){
                blockInfo.setContractValue(new BigInteger("0"));
                logger.error(ex.getMessage());
            }

            blockInfo.setContractTx(true);
        }
        return ;
    }

    /**
     * 获取账户余额
     * @param address 待查询地址
     * @return 返回余额
     */
    public BigDecimal getbalance(String address){
        String result;
        try{
            result = (String)client.invoke("eth_getBalance",new Object[]{address, "latest"}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }

        String ss = result.toString().substring(2);
        BigInteger gas = new BigInteger(ss, 16);
        BigDecimal gasbigdecimal = new BigDecimal(gas.toString());
        BigDecimal estvalue = gasbigdecimal.divide(ETHER);
        return estvalue;
    }

    /**
     * 锁住账号
     * @param address
     * @return
     */
    public  boolean lockAccount(String address)
    {
        Boolean result;
        try{
            result = (Boolean)client.invoke("personal_lockAccount",new Object[]{address}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  false;
        }
        return result;
    }

    /**
     * 解锁账号,交易前需要操作
     * @param address,需要解锁地址
     * @param passphrase,地址密码
     * @return,是否成功
     */
    public  boolean unlockAccount(String address, String passphrase)
    {
        Boolean result;
        try{
            result = (Boolean)client.invoke("personal_unlockAccount",new Object[]{address, passphrase}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  false;
        }
        return result;
    }

    /**
     * 获取转账所需花费费用
     * @param frommAddress,源地址
     * @param toAddress,目标地址
     * @param value,ETH数量
     * @return, 费用
     */
    public BigDecimal getEstimateGas(String frommAddress, String toAddress, Double value){
        String hexvalue = getHexString(value);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from",frommAddress);
        map.put("to", toAddress);
        map.put("value","0x"+hexvalue);

        Object result;
        try{
            result =  client.invoke("eth_estimateGas",new Object[]{map}, Object.class);
        } catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }

        String ss = result.toString().substring(2);
        BigInteger gas = new BigInteger(ss, 16);
        BigDecimal gasbigdecimal = new BigDecimal(gas.toString());
        BigDecimal estvalue = gasbigdecimal.divide(ETHER);
        return estvalue;
    }

    /**
     * 转账
     * @param fromAddress,源地址
     * @param toAddress,目标地址
     * @param value,ETH数量
     * @param fee 交易费用，ETH数量
     * @return, 交易ID
     */
    public String transfer(String fromAddress, String toAddress, Double value, Double fee){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from", fromAddress);
        map.put("to",toAddress);

        if(fee.doubleValue() > 0 && fee.doubleValue() < 1 ){
            long gas = 21000; //21000
            BigDecimal gasPriceWei = BigDecimal.valueOf(fee).multiply(ETHER).divide( BigDecimal.valueOf(gas),8,  BigDecimal.ROUND_HALF_UP );
            String gasPriceHex = getHexString(gasPriceWei.longValue());
            String gasHex = getHexString(gas);
            map.put("gas",gasHex);
            map.put("gasPrice",gasPriceHex);
        }
        BigInteger bigValue = BigDecimal.valueOf(value).multiply(ETHER).toBigInteger();
        String bigValueHex = getHexString(bigValue);
        map.put("value",bigValueHex);

        Object result;
        try{
            result =  client.invoke("eth_sendTransaction",new Object[]{map}, Object.class);
        } catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
            return  null;
        }

        return result.toString();
    }

    /**
     * 获取当前地址交易等待的nonce值
     * @param address
     * @return
     */
    public long getNonce(String address){
        Object result;
        try{
            result =  client.invoke("eth_getTransactionCount",new Object[]{address, "pending"}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  -1;
        }

        String ss = result.toString().substring(2);
        long nonce =  Long.parseLong(ss, 16);
        return nonce;
    }

    /**
     * 获取最新nonce
     * @param address
     * @return
     */
    public long getLatestNonce(String address){
        Object result;
        try{
            result =  client.invoke("eth_getTransactionCount",new Object[]{address, "latest"}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  -1;
        }

        String ss = result.toString().substring(2);
        long nonce =  Long.parseLong(ss, 16);
        return nonce;
    }

    /**
     * 交易签名,返回签名后的data
     * @param fromAddress
     * @param toAddress
     * @param value
     * @param fee
     * @param nonce
     * @return
     */
    public String singnTransaction(String fromAddress, String toAddress, Double value, Double fee, long nonce){
        long gas = 21000; //21000
        BigDecimal gaspricewei = BigDecimal.valueOf(fee).multiply(ETHER).divide( BigDecimal.valueOf(gas),8,  BigDecimal.ROUND_HALF_UP );
        String gasHex = getHexString(gas);
        BigInteger bigValue = BigDecimal.valueOf(value).multiply(ETHER).toBigInteger();
        String bigValueHex = getHexString(bigValue);
        String gasPriceHex = getHexString(gaspricewei.longValue());
        String nonceHex = getHexString(nonce);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("from", fromAddress);
        map.put("to",toAddress);
        map.put("gas",gasHex);
        map.put("gasPrice",gasPriceHex);
        map.put("value", bigValueHex);
        map.put("nonce", nonceHex);
        LinkedHashMap result;
        try{
            result =  (LinkedHashMap)client.invoke("eth_signTransaction",new Object[]{map}, Object.class);
        } catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info(ex.getMessage());
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
            result =  client.invoke("eth_sendRawTransaction",new Object[]{signData}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            return  null;
        }

        return result.toString();
    }

    public LinkedHashMap getTransactionByTxid(String txid){
		LinkedHashMap txidInfo = null;

		try{
			txidInfo =  (LinkedHashMap)client.invoke("eth_getTransactionByHash",new Object[]{txid}, Object.class);
		}catch(Throwable ex) {
			setErrorMsg(ex.getMessage());
			return  null;
		}

		return txidInfo;
	}

	public ArrayList<TxDetail> queryTxDetailByBlock(long blockNum){

		LinkedHashMap result;
		String blockNumStr = getHexString(blockNum);
		try{
			result = (LinkedHashMap)client.invoke("eth_getBlockByNumber",new Object[]{blockNumStr,true}, Object.class);
		}catch(Throwable ex) {
			setErrorMsg(ex.getMessage());
			logger.info(ex.getMessage());
			return  null;
		}

		if(result == null)
		{
			setErrorMsg("unexpected error");
			return null;
		}

		ArrayList<LinkedHashMap> transactions = (ArrayList<LinkedHashMap>)result.get("transactions");
		ArrayList<TxDetail> TxDetails = new ArrayList<TxDetail>();

		String timeStamp = String.valueOf(result.get("timestamp")).substring(2);
		long nTime = Long.valueOf(timeStamp,16) * 1000L;
		Date dt = new Date(nTime);
		String sReceiveTime = simpleDateFormat.format(dt);

		for(int i=0; i<transactions.size(); i++){
			String txid = (String)transactions.get(i).get("hash");
			String toaddress = (String)transactions.get(i).get("to");
			if(null == toaddress || "null" == toaddress)
				continue;
			String value = (String)transactions.get(i).get("value");
			String gas = (String)transactions.get(i).get("gas");
			String gasPrice = (String)transactions.get(i).get("gasPrice");
			long nGas = Long.parseLong(gas.substring(2),16);
			long nGasPrice = Long.parseLong(gasPrice.substring(2),16);
			BigDecimal fee = BigDecimal.valueOf(nGas * nGasPrice).divide(ETHER);

			BigInteger weivalue = new BigInteger(value.substring(2), 16);
			BigDecimal ethvalue = new BigDecimal(weivalue).divide(ETHER);

			TxDetail txDetail = new TxDetail();
			txDetail.setTxId(txid);
			txDetail.setToAddress(toaddress);
			txDetail.setTxAmount(ethvalue);
			txDetail.setBlockHeight(blockNum);
			txDetail.setBlockHash((String)transactions.get(i).get("blockHash"));
			txDetail.setTxFee(fee);
			txDetail.setTxReceiveTime(sReceiveTime);
			txDetail.setCategory("");
			txDetail.setConfirmations(0);

			//判断是否是ERC20标准的合约
			String input = (String)transactions.get(i).get("input");
			analysisInputEx(txDetail, input, toaddress);


			TxDetails.add(txDetail);
		}

		return TxDetails;

	}

	private void analysisInputEx(TxDetail txDetail, String input, String address){
		int dist = 64;
		int addressLen = 40;
		txDetail.setContractTx(false);
        Receipt receipt = getReceipt(txDetail.getTxId());
        if(receipt == null){
            return;
        }

		if(input.startsWith(Erc20MethodConstant.transfer)){
			try{
				String toAddress = "0x"+input.substring(Erc20MethodConstant.transfer.length() + (dist - addressLen), Erc20MethodConstant.transfer.length() + dist);
				txDetail.setToAddress(toAddress);
				txDetail.setContractAddress(address);

				String hexValue = input.substring(Erc20MethodConstant.transfer.length() + dist);
				BigInteger weivalue = new BigInteger(hexValue, 16);
				txDetail.setContractValue(weivalue);
			}catch (Exception ex){
				txDetail.setContractValue(new BigInteger("0"));
				logger.error(ex.getMessage());
			}

            if(receipt.getStatus()  == false){
                txDetail.setContractValue(new BigInteger("0"));
            }

			txDetail.setContractTx(true);
		} else if(input.startsWith(Erc20MethodConstant.transferFrom)){
			logger.info(input);
			try{
				String toAddress = "0x"+input.substring(Erc20MethodConstant.transfer.length() + (2*dist - addressLen), Erc20MethodConstant.transfer.length() + 2*dist);
				txDetail.setToAddress(toAddress);
				txDetail.setContractAddress(address);

				String hexValue = input.substring(Erc20MethodConstant.transfer.length() + 2*dist);
				BigInteger weivalue = new BigInteger(hexValue, 16);
				txDetail.setContractValue(weivalue);
			}catch (Exception ex){
				txDetail.setContractValue(new BigInteger("0"));
				logger.error(ex.getMessage());
			}
            if(receipt.getStatus()  == false){
                txDetail.setContractValue(new BigInteger("0"));
            }
			txDetail.setContractTx(true);
		}
		return ;
	}

    /**
     * 获取交易的状态
     * @return
     */
    public Receipt getReceipt(String txid){
        Receipt receipt = new Receipt();
        LinkedHashMap result;
        try{
            result =  (LinkedHashMap)client.invoke("eth_getTransactionReceipt",new Object[]{txid}, Object.class);
        }catch(Throwable ex) {
            setErrorMsg(ex.getMessage());
            logger.info("" + ex.getMessage());
            return null;
        }

        if(result == null)
        {
            setErrorMsg("unexpected error");
            return null;
        }

        receipt.setStatus(true);
        if(result.get("status") != null){
            String status = (String)result.get("status") ;
            if(status.compareTo("0x1") != 0) {
                receipt.setStatus(false);
            }
        }

        if(result.get("gasUsed") != null){
            String gass = (String)result.get("gasUsed") ;
            String ss = gass.toString().substring(2);
            BigInteger gas = new BigInteger(ss, 16);
            receipt.setGas(gas.longValue());
        }

        return receipt;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private String getHexString(Double value){
        String str = String.valueOf(value);
        BigInteger total = (ETHER.multiply( new BigDecimal(str) ) ).toBigInteger();
        String hexvalue = total.toString(16);
        return "0x"+hexvalue;
    }

    public String getHexString(long value){
        String hexvalue =Long.toHexString(value);
        hexvalue = "0x"+hexvalue;
        return hexvalue;
    }

    public String getHexString(BigInteger value){
        String valueHex = new BigInteger(value.toString(), 10).toString(16);
        valueHex = "0x"+valueHex;
        return valueHex;
    }

    public JsonRpcHttpClient getClient() {
        return client;
    }

    public void setClient(JsonRpcHttpClient client) {
        this.client = client;
    }
}

package com.nxquant.exchange.wallet.ripple;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;

import com.nxquant.exchange.wallet.model.BlockInfo;
import com.nxquant.exchange.wallet.model.RippleAccountModel;

public class RippleApi {
    private JsonRpcHttpClient client = null;
    private String errorMsg = "";
    private double XRP_UNIT = 1000000;

    public String getErrorMsg() {
        return this.errorMsg;
    }

    private void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * @param rpcAddress
     * @return true if succeed
     */
    public Boolean createConnection(String rpcAddress) {
        try {
            client = new JsonRpcHttpClient(new URL(rpcAddress));
            client.setContentType("application/json");
        } catch(MalformedURLException exception) {
            setErrorMsg(exception.getMessage());
            return false;
        } catch(Exception exception) {
            setErrorMsg(exception.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @params 账户秘钥（秘钥为空则随机生成新的地址）
     * @return 地址
     */
    public RippleAccountModel createNewAddress(String secret) {
        RippleAccountModel rippleAccountModel = new RippleAccountModel();
        KeyInfo resp = new KeyInfo();
        try {
            Map<String, String> params = new HashMap<String, String>();
            if (secret != null && secret.length() > 0) {
                params.put("passphrase", secret);
            }
            resp = client.invoke("wallet_propose", new Object[]{params}, resp.getClass());
            rippleAccountModel.setAccount(resp.address);
            rippleAccountModel.setSecrete(resp.masterSeed);
        } catch(Throwable exception) {
            setErrorMsg(exception.getMessage());
            return null;
        }
        return rippleAccountModel;
    }

    /**
     * @params address 附属地址
     * @params secret 账户秘钥
     * @return true if succeed
     */
    public Boolean setAccountForAddress(String address, String secret) {
        Info resp = new Info();
        try {
            RippleAccountModel account = createNewAddress(secret);
            TxSetRegularKey tx = new TxSetRegularKey(account.getAccount(), address);
            String blob = signTransaction(secret, tx);
            String hash = submitTransaction(blob);
        } catch(Throwable exception) {
            setErrorMsg(exception.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @return 最新验证成功的区块高度(ledger_index in com.unifex.chainapi.ripple context)
     */
    public long getLatestBlockNum() {
        LedgerInfo resp = new LedgerInfo();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ledger_index", "validated");
            resp = client.invoke("ledger", new Object[]{params}, resp.getClass());
        } catch(Throwable exception) {
            setErrorMsg(exception.getMessage());
            return -1;
        }
        return resp.index;
    }

    /**
     * @param  index
     * @return 指定区块中的交易数组
     */
    public ArrayList<BlockInfo> getBlockByNumber(long index) {
        LinkedHashMap result;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ledger_index", index);
            params.put("transactions", true);
            params.put("expand", true);
            result  =(LinkedHashMap)client.invoke("ledger", new Object[]{params}, Object.class);
        } catch(Throwable exception) {
            setErrorMsg(exception.getMessage());
            return null;
        }

        ArrayList<BlockInfo> blocks = new ArrayList<BlockInfo>();
        boolean validated = (Boolean)result.get("validated");
        if( validated == false || result.get("status").toString().compareTo("success") !=0){
            return blocks;
        }

        LinkedHashMap ledger = (LinkedHashMap)result.get("ledger");
        Boolean accepted = (Boolean)ledger.get("accepted");
        if(false == accepted){
            return blocks;
        }

        ArrayList<LinkedHashMap> trans = (ArrayList<LinkedHashMap> )ledger.get("transactions");
        for (LinkedHashMap tx: trans) {
            String dest = (String)tx.get("Destination");
            if(dest == null){
                continue;
            }

            Object amount =  tx.get("Amount");
            if(amount instanceof String){
            }
            else{
                continue;
            }

            double damout = 0;
            if(amount != null){
                damout = Double.valueOf(amount.toString()) / XRP_UNIT;
            }

            BlockInfo block = new BlockInfo();
            block.setToAddress(dest);
            block.setTxid(tx.get("hash").toString());
            block.setValue(damout);
            block.setBlockNo(index);
            blocks.add(block);
        }

        return blocks;
    }

    /**
     * 获取账户余额
     * @param account, 账户地址, "r"开头的字符串
     * @return
     */
    public BigDecimal getAccountBalance(String account) {
        AccountInfo resp = new AccountInfo();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("account", account);
            params.put("ledger_index", "validated");

            resp = client.invoke("account_info", new Object[]{params}, resp.getClass());
            if (!resp.isValid) {
                throw new Throwable("accountInfo: "+resp.error);
            }
        } catch(Throwable exception) {
            setErrorMsg(exception.getMessage());
            return null;
        }
        BigDecimal b = BigDecimal.valueOf(resp.account.balance).divide(new BigDecimal(XRP_UNIT));
        return b;
    }

    /**
     * @param from 源地址
     * @param to 目标地址
     * @param secret 源地址私钥
     * @param amount 转账(xrp drop)
     * @param fee 费用(xrp drop)
     */
    public String transfer(String from, String to, String secret,  Double amount, Double fee) {
        String hash = "";
        try {
            double  damount = amount * XRP_UNIT;
            double  dfee = fee * XRP_UNIT;
            TxPayment tx = new TxPayment(from, to,  damount, dfee);
            String blob = signTransaction(secret, tx);
            hash = submitTransaction(blob);
        } catch (Throwable exception) {
            setErrorMsg(exception.getMessage());
            return null;
        }
        return hash;
    }

    private String signTransaction(String secret, Tx txBody) throws Throwable {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("secret", secret);
        params.put("tx_json", txBody);

        TxBlobInfo resp = new TxBlobInfo();
        resp = client.invoke("sign", new Object[]{params}, resp.getClass());
        if (!resp.isValid) {
            throw new Throwable("signTransation: "+resp.error);
        }
        return resp.blob;
    }

    private String submitTransaction(String blob) throws Throwable {
        TxInfo resp = new TxInfo();
        Map<String, String> params = new HashMap<String, String>();
        params.put("tx_blob", blob);
        resp = client.invoke("submit", new Object[]{params}, resp.getClass());
        if (!resp.isValid) {
            throw new Throwable("submitTransation: "+resp.error);
        }
        return resp.tx.hash;
    }
}
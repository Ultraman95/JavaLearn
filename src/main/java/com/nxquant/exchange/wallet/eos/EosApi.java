package com.nxquant.exchange.wallet.eos;

import com.nxquant.exchange.wallet.eos.messages.*;
import com.nxquant.exchange.wallet.eos.messages.BlockInfo;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2019-01-09.
 */
public class EosApi {
    private URL nodeURL = null;
    private URL walletURL = null;
    private RPCChain chain = null;
    private RPCWallet wallet = null;
    private String errorMsg;

    public Boolean createConnection(String nodeUrl, String walletUrl){
        try {
            nodeURL = new URL(nodeUrl);
            walletURL = new URL(walletUrl);

            EOSRPCAdapter eosrpcAdapter = new EOSRPCAdapter(nodeURL, walletURL);
            this.chain = new RPCChain(nodeURL, eosrpcAdapter);
            this.wallet = new RPCWallet(walletURL, eosrpcAdapter);
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return false;
        }
        return true;
    }

    public String createWallet(String walletName) {
        String pwd;
        try {
           pwd = this.wallet.create(walletName);
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return null;
        }
        return pwd;
    }

    public String createKey(String walletName) {
        String pubKey;
        try {
            pubKey = this.wallet.createKey(walletName);
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return null;
        }
        return pubKey;
    }

    public Boolean lockWallet(String walletName, String pwd, int timeoutSec) {
        Boolean bool;
        try {
            bool = this.wallet.lock(walletName);
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return false;
        }
        return bool;
    }

    public Boolean unlockWallet(String walletName, String pwd, int timeoutSec) {
        Boolean bool;
        try {
            bool = this.wallet.unlock(walletName, pwd);
            if( timeoutSec <= 0 || timeoutSec >= 3600 ){
                timeoutSec = 100;
            }

            boolean bool3 = wallet.setTimeout(walletName, timeoutSec);
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return false;
        }
        return bool;
    }

    public String buyRam( String payer, String receiver, double eosFee, List<String>  keys) {
        try {

            ChainInfo chainInfo =  chain.getInfo();
            BlockInfo blockInfo =  chain.getBlock(String.valueOf(chainInfo.head_block_num));
            String expireTimeout = addMinute(blockInfo.timestamp, 20);

            Transaction tran = new Transaction(expireTimeout, blockInfo.block_num, blockInfo.ref_block_prefix,
                    0,0, 0,null, null,null,null, null);

            Transaction.Action buyramaaction = getBuyRamAction(payer, receiver, eosFee);
            if(buyramaaction != null){
                tran.actions.add(buyramaaction);
            } else{
                return null;
            }

            SignedTransaction signedTransaction =  wallet.signTransaction(tran, keys, chainInfo.chain_id);
            Transaction.Response  rsp =  chain.pushTransaction(signedTransaction);
            return rsp.transaction_id;
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return null;
        }
    }

    public AccountInfo getAccount(String account) {
        try {
            AccountInfo  accountInfo =  chain.getAccount(account);
            return accountInfo;
        } catch (Exception ex) {
            ex.printStackTrace();
            setErrorMsg(ex.getMessage());
            return null;
        }
    }

    public String createAccount( List<String>  keys, String creator, String newAccount, String ownerKey, String activeKey, double eosFee) {
        try {
            Transaction tran = chain.createCreateAccountTransaction(creator, newAccount, ownerKey, activeKey);
            ChainInfo chainInfo =  chain.getInfo();
            BlockInfo blockInfo =  chain.getBlock(String.valueOf(chainInfo.head_block_num));

            String expireTimeout = addMinute(blockInfo.timestamp, 20);
            tran.expiration = expireTimeout;
            tran.ref_block_num =  blockInfo.block_num;
            tran.ref_block_prefix = blockInfo.ref_block_prefix;

            Transaction.Action buyramaaction = getBuyRamAction(creator, newAccount, eosFee);
            if(buyramaaction != null){
                tran.actions.add(buyramaaction);
            } else{
                return null;
            }

            SignedTransaction signedTransaction =  wallet.signTransaction(tran, keys, chainInfo.chain_id);
            Transaction.Response  rsp =  chain.pushTransaction(signedTransaction);
            return rsp.transaction_id;

        } catch (Exception ex) {
            ex.printStackTrace();
            setErrorMsg(ex.getMessage());
            return null;
        }
    }

    public  String delegatebw(List<String> keys, String from, String receiver, double eosForNet, double eosForCpu, int transferFlag){
        try {
            ChainInfo chainInfo =  chain.getInfo();
            BlockInfo blockInfo =  chain.getBlock(String.valueOf(chainInfo.head_block_num));

            Map<String, Object> argsMap = new HashMap<String, Object>();
            argsMap.put("from", from);
            argsMap.put("receiver", receiver);
            argsMap.put("stake_net_quantity", formatEosBalance(eosForNet));
            argsMap.put("stake_cpu_quantity", formatEosBalance(eosForCpu));
            argsMap.put("transfer", transferFlag);
            TransactionBinArgs TransactionBinArgs =  chain.abiJsonToBin("eosio","delegatebw", argsMap);

            Transaction.Action buyRawAction = new  Transaction.Action();
            buyRawAction.data =  TransactionBinArgs.binargs;
            buyRawAction.account = "eosio";
            buyRawAction.name = "delegatebw";

            List<Transaction.Authorization> authorization = new ArrayList();
            authorization.add(new Transaction.Authorization(from,"active"));
            buyRawAction.authorization = authorization;

            String expireTimeout = addMinute(blockInfo.timestamp, 20);
            Transaction tran = new Transaction(expireTimeout, blockInfo.block_num, blockInfo.ref_block_prefix,
                    0,0, 0,null, null,null,null, null);

            tran.actions.add(buyRawAction);

            SignedTransaction signedTransaction =  wallet.signTransaction(tran, keys, chainInfo.chain_id);
            Transaction.Response  rsp =  chain.pushTransaction(signedTransaction);
            return rsp.transaction_id;
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return null;
        }
    }

    private  Transaction.Action getBuyRamAction(String payer, String receiver, double eosFee){
        try {
            Map<String, Object> argsMap = new HashMap<String, Object>();
            argsMap.put("payer", payer);
            argsMap.put("receiver", receiver);
            argsMap.put("quant", formatEosBalance(eosFee));

            TransactionBinArgs TransactionBinArgs =  chain.abiJsonToBin("eosio","buyram", argsMap);

            Transaction.Action buyRawAction = new  Transaction.Action();
            buyRawAction.data =  TransactionBinArgs.binargs;
            buyRawAction.account = "eosio";
            buyRawAction.name = "buyram";

            List<Transaction.Authorization> authorization = new ArrayList();
            authorization.add(new Transaction.Authorization(payer,"active"));
            buyRawAction.authorization = authorization;

            return buyRawAction;
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return null;
        }
    }

    public double getEosBalance(String account){
        double eosBalance = 0;
        try {
            List<String> currencyList =  chain.getCurencyBalance(account,"eosio.token", "eos");
            if(currencyList.size() > 0){
                String eos = currencyList.get(0);
                String[] bs = eos.split(" ");
                if(bs.length > 0 )
                  eosBalance = Double.valueOf(bs[0]);
            }

        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
        }
        return eosBalance;
    }

    public String transfer(String form, String to, double amount, String memo, List<String> keys){
        try{
            Map<String, Object> argsMap = new HashMap<String, Object>();
            argsMap.put("from", form);
            argsMap.put("to", to);
            argsMap.put("quantity", formatEosBalance(amount));
            argsMap.put("memo", memo);
            TransactionBinArgs TransactionBinArgs =  chain.abiJsonToBin("eosio.token","transfer", argsMap);

            ChainInfo chainInfo =  chain.getInfo();
            BlockInfo blockInfo =  chain.getBlock(String.valueOf(chainInfo.head_block_num));

            List<Transaction.Authorization> authorization = new ArrayList();
            authorization.add(new Transaction.Authorization(form,"active"));
            Transaction.Action action = new  Transaction.Action("eosio.token", "transfer", authorization, TransactionBinArgs.binargs);

            List<Transaction.Action> actions = new ArrayList<>();
            actions.add(action);

            String expireTimeout = addMinute(blockInfo.timestamp, 20);

            Transaction transaction = new Transaction(expireTimeout, blockInfo.block_num, blockInfo.ref_block_prefix,
                    0,0, 0,null, actions,null,null, null);

            SignedTransaction signedTransaction =  wallet.signTransaction(transaction, keys, chainInfo.chain_id);
            Transaction.Response  rsp =  chain.pushTransaction(signedTransaction);
            return rsp.transaction_id;

        }catch (Exception ex){
            setErrorMsg(ex.getMessage());
            return null;
        }
    }

    //
    public ArrayList<com.nxquant.exchange.wallet.model.BlockInfo> getBlock(long blockNumber) {
        try {
            BlockInfo blockInfo =  chain.getBlock(String.valueOf(blockNumber));
            ArrayList<com.nxquant.exchange.wallet.model.BlockInfo> BlockInfoList = new ArrayList<com.nxquant.exchange.wallet.model.BlockInfo>();

            for(int i=0;i<blockInfo.transactions.size();i++){
                BlockInfo.BlockTransaction tx = blockInfo.transactions.get(i);
                anaEosTransfer(BlockInfoList, tx, blockNumber);  ///get com.unifex.chainapi.eos
            }
            return BlockInfoList;
        } catch (Exception ex) {
            setErrorMsg(ex.getMessage());
            return null;
        }
    }

    private void anaEosTransfer(ArrayList<com.nxquant.exchange.wallet.model.BlockInfo> BlockInfoList, BlockInfo.BlockTransaction tx, long blockNumber){
        if(tx.status.compareTo(EosConstant.Executed) == 0){
            List<Transaction.Action>  actions =  tx.trx.transaction.actions;

            for(int j = 0; j<actions.size(); j++){
                if( actions.get(j).account.compareTo(EosConstant.EOS_ACCOUNT) == 0 && actions.get(j).name.compareTo(EosConstant.EOS_ACTION_NAME) == 0 ) {
                    com.nxquant.exchange.wallet.model.BlockInfo mBlockInfo = new com.nxquant.exchange.wallet.model.BlockInfo();
                    mBlockInfo.setBlockNo(blockNumber);
                    mBlockInfo.setTxid(tx.trx.id);

                    if(actions.get(j).data != null) {
                        LinkedHashMap<String, String> mdata = (LinkedHashMap<String, String>)actions.get(j).data;
                        if(mdata.get("from") != null) {
                            mBlockInfo.setFromAddress(mdata.get("from"));
                        }

                        if(mdata.get("to") != null) {
                            mBlockInfo.setToAddress(mdata.get("to"));
                        }

                        if(mdata.get("quantity") != null) {
                            mBlockInfo.setValue(getFormatEosBalance(mdata.get("quantity")));
                        }
                    }
                    BlockInfoList.add(mBlockInfo);
                }
            }
        }
    }

    private String formatEosBalance(double balance){
        String strb =  String.format("%.4f", balance);
        strb = strb + " EOS";
        return strb;
    }

    private double getFormatEosBalance(String balance){
        String[] bs = balance.split(" ");
        if(bs.length > 0 ){
            double eosBalance = Double.valueOf(bs[0]);
            return eosBalance;
        }

        return 0;
    }

    private String addMinute(String timeStamp, int minute){
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");   //注意格式化的表达式
            timeStamp = timeStamp.replace("Z", " UTC");//注意是空格+UTC
            Date resDate = format.parse( timeStamp );
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(resDate);
            rightNow.add(Calendar.MINUTE,10);
            Date dt1=rightNow.getTime();
            String reStr = format.format(dt1) + ".000";
            return reStr;
        }catch (Exception ex){
            setErrorMsg(ex.getMessage());
        }

        return timeStamp;
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}

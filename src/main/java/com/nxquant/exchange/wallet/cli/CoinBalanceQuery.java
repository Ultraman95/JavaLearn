package com.nxquant.exchange.wallet.cli;

import com.nxquant.exchange.wallet.model.UnSpentInf;
import com.nxquant.exchange.wallet.bitcoin.BitCoinApi;
import com.nxquant.exchange.wallet.eth.EthApi;
import com.nxquant.exchange.wallet.omni.OmniApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Administrator on 2018-08-03.
 */
public class CoinBalanceQuery {
    private static Logger logger = LoggerFactory.getLogger(CoinBalanceQuery.class);
    DataStoreInterface DataStore;
    private static Properties props = new Properties();
    private static String btcip;
    private static String btcuser;
    private static String btcpwd;
    private static String omniip;
    private static String omniuser;
    private static String omnipwd;
    private static String ethip;

    @Autowired
    static BitCoinApi btcApi;
    private static EthApi ethApi = null;
    private static OmniApi omniApi  = null;

    static {
        InputStream stream;
        try {
            stream = new FileInputStream("./config.properties");
            System.out.println("init config success");
        } catch (FileNotFoundException e1) {
            stream = GetBalance.class.getClassLoader().getResourceAsStream("config.properties");
        }

        try {
            props.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        btcip = props.getProperty("BTCServer");
        btcuser = props.getProperty("BTCUser");
        btcpwd = props.getProperty("BTCPwd");
        omniip = props.getProperty("OMNIServer");
        omniuser = props.getProperty("OMNIUser");
        omnipwd = props.getProperty("OMNIPwd");
        ethip = props.getProperty("ETHServer");

        ethApi = new EthApi();
        Boolean connected = ethApi.createConnection(ethip);

        omniApi = new OmniApi();
        connected = omniApi.createConnection(omniuser,omnipwd,omniip);
    }

     CoinBalanceQuery(){
     }

    public double getBtcBalance(){
        BigDecimal balance = btcApi.getWalletBalance(3);
        if(balance == null){
            logger.error("btc wallet get balance error {}"+btcApi.getErrorMsg());
            return 0;
        }
        else
        {
            return balance.doubleValue();
        }
    }

    public double getBtcBalanceByUnspend(){
        double balance = 0;
        ArrayList<UnSpentInf> UnSpentInfList = btcApi.listAllUnSpent(3);
        if(UnSpentInfList == null){
            logger.error("btc wallet get balance error {}"+btcApi.getErrorMsg());
            return 0;
        }

        for(int i=0; i<UnSpentInfList.size(); i++){
            balance += UnSpentInfList.get(i).getAmount();
        }

        return balance;
    }

    public ArrayList<String> getETHAddresses(){
        ArrayList<String> accounts;
        accounts = ethApi.getAllAccounts();
        return accounts;
    }

    public double getETHBalanceByAddress(String addr){
        BigDecimal balance;
        balance = ethApi.getbalance(addr);
        System.out.println("com.unifex.chainapi.eth address:"+addr+" ,balance:"+balance);

        return balance.doubleValue();
    }

    public ArrayList<Double> getETHBalanceByAddress(ArrayList<String> addrs){
        FileDataStore fileDataStore = new FileDataStore("./ethall/");
        fileDataStore.createETALLFile();

        ArrayList<Double> balanceList = new   ArrayList<Double>();
        BigDecimal balance;
        for (int i = 1; i < addrs.size(); i++) {
            balance = ethApi.getbalance(addrs.get(i));
            String content = i+" :com.unifex.chainapi.eth address:"+addrs.get(i)+" ,balance:"+balance+"\n";
            fileDataStore.writeContent(content);
            System.out.print(content);
            balanceList.add(balance.doubleValue());
            try{
                Thread.sleep(10);
            }catch(Exception ex){
            }
        }

        return balanceList;
    }

    public double getEthAllBalance(){
        ArrayList<String> addrs = getETHAddresses();
        ArrayList<Double> balanceList = getETHBalanceByAddress(addrs);
        double totalBalance = 0;
        for(int i=0; i<balanceList.size(); i++){
            totalBalance += balanceList.get(i);
        }
        System.out.println("com.unifex.chainapi.eth total balance:" + totalBalance);
        return totalBalance;
    }

    public double getOmniAllBalance(){
        ArrayList<Double> balanceList = new ArrayList<Double>();
        ArrayList<String> addList =  getOmniAddress();
        double totalBalance = 0;
        HashMap<String, Integer> addressesMap = new  HashMap<String, Integer>();

        for(int i=0; i<addList.size(); i++){

            if(addressesMap.containsKey(addList.get(i))){
                continue;
            }

            addressesMap.put(addList.get(i), 1);

           double balance =  getOmniBalance(addList.get(i), 31);
            System.out.println("address:"+addList.get(i)+", balance:"+balance);
           totalBalance += balance;
        }

        return totalBalance;
    }

    public ArrayList<String>  getOmniAddress(){
        ArrayList<String> addressList = new   ArrayList<String>();
        ArrayList<UnSpentInf> UnSpentInfList =  omniApi.listAllUnSpent(3);
        for (int i = 1; i < UnSpentInfList.size(); i++) {
            addressList.add(UnSpentInfList.get(i).getAddress());
        }

        return addressList;
    }

    public double getOmniBalance(String addr, int propertyid){
        BigDecimal  balance = omniApi.getBalance(addr,propertyid);
        if(balance == null){
            System.out.println("com.unifex.chainapi.omni wallet get balance error: "+omniApi.getErrorMsg());
            return -1;
        }

        return balance.doubleValue();
    }

    public void getOmniBalance(ArrayList<String> addrs){
        for (int i = 1; i < addrs.size(); i++) {
            BigDecimal  balance = omniApi.getBalance(addrs.get(i),31);
            if(balance == null){
                System.out.println("com.unifex.chainapi.omni wallet get balance error: "+omniApi.getErrorMsg());
            }
            else
            {
                System.out.println("com.unifex.chainapi.omni wallet address:"+addrs.get(i)+" ,balance: "+balance);
            }
        }
    }

    public void test(){
        CoinBalanceQuery coinBalanceQuery = new CoinBalanceQuery();

        System.out.println("start get btc");
        double btcbalance = coinBalanceQuery.getBtcBalanceByUnspend();

        System.out.println("start get com.unifex.chainapi.eth by main address");
        double ethbalance = coinBalanceQuery.getETHBalanceByAddress("0xab59de3ea4dd1ee5c474aec01022b6a94da0e1bc");

        System.out.println("start get usdt");
        double omnibalance = coinBalanceQuery.getOmniAllBalance();

        System.out.println("start get com.unifex.chainapi.eth by all account");
        ethbalance = coinBalanceQuery.getEthAllBalance();
    }

    public static void main(String[] args) {
        FileDataStore fileDataStore = new FileDataStore("./data/");
        ArrayList<String> properties = new  ArrayList<String>();
        properties.add("./btc.txt");
        properties.add("./com.unifex.chainapi.eth.txt");
        properties.add("./usdt.txt");
        fileDataStore.init(properties);

        CoinBalanceQuery coinBalanceQuery = new CoinBalanceQuery();
        System.out.println("---------------start get btc----------------");
        double btcbalance = coinBalanceQuery.getBtcBalanceByUnspend();
        fileDataStore.storeBtc(btcbalance);
        System.out.println("------------- get btc success --------------");
        System.out.println("");

        System.out.println("----------------start get com.unifex.chainapi.eth by main address---------------");
        double ethbalance = coinBalanceQuery.getETHBalanceByAddress(props.getProperty("ETHMainAddress"));
        fileDataStore.storeEth(ethbalance);
        System.out.println("--------------- get com.unifex.chainapi.eth success ----------------");
        System.out.println("");

        System.out.println("--------------- start get usdt ----------------");
        double omnibalance = coinBalanceQuery.getOmniAllBalance();
        fileDataStore.storeUsdt(omnibalance);
        System.out.println("--------------- get usdt success ----------------");

        if(Integer.valueOf(props.getProperty("QUERY_ALL")) != 0){
            System.out.println("--------------- start get com.unifex.chainapi.eth by all account ----------------");
            ethbalance = coinBalanceQuery.getEthAllBalance();
            System.out.println("--------------- get com.unifex.chainapi.eth by all account success----------------");
        }
    }
}

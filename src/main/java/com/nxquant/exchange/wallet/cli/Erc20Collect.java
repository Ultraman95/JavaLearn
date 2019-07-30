package com.nxquant.exchange.wallet.cli;

import com.nxquant.exchange.wallet.eth.ERC20Api;
import com.nxquant.exchange.wallet.eth.EthApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Administrator on 2018-09-12.
 */
public class Erc20Collect {
    private static Logger logger = LoggerFactory.getLogger(Erc20Collect.class);
    private static Properties props = new Properties();
    private static  EthApi ethApi = new EthApi();
    private static com.nxquant.exchange.wallet.eth.ERC20Api ERC20Api = new ERC20Api();
    private static String ethip;
    private static String erc20AddressesPath ;
    private static String contractPath ;
    private static String toAdressesPath ;

    private static String collectAddress ;
    private static String collectAddressPwd ;
    private  FileOperator fileOperator = new FileOperator();

    private ArrayList<String> ethAddreses = new  ArrayList<String>();
    private ArrayList<Double> ethValue = new  ArrayList<Double>();

    private ArrayList<String> erc20Addreses = new  ArrayList<String>();
    private ArrayList<String> passwrd = new  ArrayList<String>();

    private ArrayList<String> contractAddress = new  ArrayList<String>();
    private ArrayList<Long> contractThreshold = new  ArrayList<Long>();
    private ArrayList<Integer> contractDecimal = new  ArrayList<Integer>();

    private static double EthTransferFee = -1.0;
    private static double Erc20TransferFee = -1.0;

    static {
        InputStream stream;
        try {
            stream = new FileInputStream("configEth.properties");
           // System.out.println("init config success");
        } catch (FileNotFoundException e1) {
            stream = Erc20Collect.class.getClassLoader().getResourceAsStream("configEth.properties");
        }

        try {
            props.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ethip = props.getProperty("ETHServer");
        collectAddress = props.getProperty("CollectAddress");
        erc20AddressesPath = props.getProperty("Erc20AddressPath");
        toAdressesPath = props.getProperty("EthAddressPath");
        contractPath = props.getProperty("ContractPath");
        collectAddressPwd = props.getProperty("CollectAddressPwd");

        EthTransferFee = Double.valueOf(props.getProperty("EthTransferFee"));
        Erc20TransferFee = Double.valueOf(props.getProperty("Erc20TransferFee"));

        Boolean ret = ethApi.createConnection(ethip);
        ERC20Api.setEthApi(ethApi);
    }

    public boolean loadData(){
        boolean ret1 = fileOperator.readAddressFile(erc20AddressesPath, erc20Addreses, passwrd);
        boolean ret2 = fileOperator.readContractFile(contractPath, contractAddress, contractThreshold, contractDecimal);

        return ret1 && ret2;
    }

    public void transferERC20(String remain){
        if(false == loadData()) {
            System.out.println("read file error!");
            return;
        }

        BufferedWriter failedOut =  fileOperator.createETALLFile("erc20_transfer_failed-");
        BufferedWriter successOut =  fileOperator.createETALLFile("erc20_transfer_success-");

        for(int i=0; i<contractAddress.size(); i++){
            String cconaddr = contractAddress.get(i);
            Long threshold = contractThreshold.get(i);
            Integer decimal = contractDecimal.get(i);
            System.out.println("begin collect contract:" + cconaddr+", threshold:" +threshold + ", decimal:"+ decimal);

            BigInteger bcontractThresh = new BigInteger(String.valueOf(threshold)).multiply( (  new BigInteger(String.valueOf(10))).pow(decimal) );

            BigInteger remainBig = new BigInteger(String.valueOf(remain)).multiply( (  new BigInteger(String.valueOf(10))).pow(decimal) );

            for(int j=0; j<erc20Addreses.size(); j++){
                String addr = erc20Addreses.get(j);
                String pwd = passwrd.get(j);

                System.out.println("begin transfer erc20address:" + addr);

                BigInteger balance =  ERC20Api.getBalance(addr, cconaddr,addr);
                if(balance == null){
                    String errmsg = "getbalance:" +addr + ", ErrMsg:"+ERC20Api.getErrorMsg()+"\n";
                    logger.error(errmsg);
                    fileOperator.writeContent(failedOut, errmsg);
                    continue;
                }

                if(balance.compareTo(remainBig) <= 0 ){
                    continue;
                }

                balance = balance.subtract(remainBig);

                if(balance.compareTo(bcontractThresh) <= 0){
                    System.out.println("transfer erc20address exceed threshold:"  + balance.toString());
                    continue;
                }

                boolean ret = ERC20Api.getEthApi().unlockAccount(addr, pwd);
                if(ret == false){
                    String errmsg = "failed unlock :"+addr +", ErrMsg:"+ ERC20Api.getErrorMsg()+"\n";
                    fileOperator.writeContent(failedOut, errmsg);
                    logger.error(errmsg);
                    continue;
                }

                String txid = ERC20Api.transfer(addr, cconaddr, collectAddress, balance, Erc20TransferFee);
                if(txid ==null){
                    String errmsg = "transfer from:" +addr +", to:"+collectAddress+", balance:"+balance+", ErrMsg:"+ ERC20Api.getErrorMsg()+"\n";
                    fileOperator.writeContent(failedOut, errmsg);
                    logger.error(errmsg);
                    continue;
                }

                String succmsg = "transfer from:" +addr +", to:"+collectAddress+", balance:"+balance+", txid:"+txid+"\n";
                fileOperator.writeContent(successOut, succmsg);
            }
        }

        try{
            failedOut.close();
            successOut.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void transferETH(){
        if(false == fileOperator.readToAddressFile(toAdressesPath, ethAddreses, ethValue) ){
            System.out.println("read file error!");
            return ;
        }

        BufferedWriter failedOut =  fileOperator.createETALLFile("eth_transfer_failed-");
        BufferedWriter successOut =  fileOperator.createETALLFile("eth_transfer_success-");

        Boolean isunlocked = ethApi.unlockAccount(collectAddress, collectAddressPwd);
        if(false == isunlocked) {
            String errmsg = "failed unlock :"+collectAddress +", ErrMsg:"+ ethApi.getErrorMsg()+"\n";
            fileOperator.writeContent(failedOut, errmsg);
            logger.error(errmsg);
            return;
        }

        long beginTime = System.currentTimeMillis();

        for(int i=0; i<ethAddreses.size(); i++){
            String addr = ethAddreses.get(i);
            double value = ethValue.get(i);

            long endTime = System.currentTimeMillis();
            if((endTime - beginTime) > 250*1000){
                beginTime = System.currentTimeMillis();
                isunlocked = ethApi.unlockAccount(collectAddress, collectAddressPwd);
                if(false == isunlocked) {
                    String errmsg = "failed unlock :"+collectAddress +", ErrMsg:"+ ethApi.getErrorMsg()+"\n";
                    fileOperator.writeContent(failedOut, errmsg);
                    logger.error(errmsg);
                    continue;
                }
            }

            String txid = ethApi.transfer(collectAddress, addr, value, EthTransferFee);
            if(txid == null){
                String errmsg = "transfer from:" +collectAddress +", to:"+addr+", balance:"+value+", ErrMsg:"+ ethApi.getErrorMsg()+"\n";
                fileOperator.writeContent(failedOut, errmsg);
                logger.error(errmsg);
                continue;
            }

            String succmsg = "transfer from:" +collectAddress +", to:"+addr+", balance:"+value+", txid:"+txid+"\n";
            fileOperator.writeContent(successOut, succmsg);
        }

        try{
            failedOut.close();
            successOut.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void queryETH(){
        if(false == fileOperator.readToAddressFile(toAdressesPath, ethAddreses, ethValue) ){
            System.out.println("read file error!");
            return ;
        }

        BufferedWriter failedOut =  fileOperator.createETALLFile("eth_query_failed-");
        BufferedWriter successOut =  fileOperator.createETALLFile("eth_query_success-");

        for(int i=0; i<ethAddreses.size(); i++) {
            String addr = ethAddreses.get(i);

            BigDecimal balance =  ethApi.getbalance(addr);
            if(null != balance)
            {
                String succmsg = addr +" : " +balance.toString()+"\n";
                fileOperator.writeContent(successOut, succmsg);
            }
            else
            {
                String errmsg = "failed getbalance :"+addr +", ErrMsg:"+ ethApi.getErrorMsg()+"\n";
                fileOperator.writeContent(failedOut, errmsg);
            }
        }

        try{
            failedOut.close();
            successOut.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void queryErc20(){
        if(false == loadData()) {
            System.out.println("read file error!");
            return;
        }

        BufferedWriter failedOut =  fileOperator.createETALLFile("erc20_query_failed-");
        BufferedWriter successOut =  fileOperator.createETALLFile("erc20_query_success-");

        for(int i=0; i<contractAddress.size(); i++) {
            String cconaddr = contractAddress.get(i);
            Integer decimal = contractDecimal.get(i);

            for (int j = 0; j < erc20Addreses.size(); j++) {
                String addr = erc20Addreses.get(j);

                BigInteger balance =  ERC20Api.getBalance(addr, cconaddr, addr);
                if (balance == null) {
                    String errmsg = "failed getbalance :"+addr +",in contract" + cconaddr +", ErrMsg:"+ ERC20Api.getErrorMsg()+"\n";
                    fileOperator.writeContent(failedOut, errmsg);
                }
                else
                {
                    BigDecimal balancedecimal = new BigDecimal(balance.toString()).divide(new  BigDecimal( ((new BigInteger(String.valueOf(10))).pow(decimal)).toString()  ));
                    String succmsg = "contract:" + cconaddr +", add: "+ addr +" : " +balancedecimal.toString()+"\n";
                    fileOperator.writeContent(successOut, succmsg);
                }
            }
        }

        try{
            failedOut.close();
            successOut.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


}

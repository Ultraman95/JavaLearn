package com.nxquant.exchange.wallet.cli;

import com.nxquant.exchange.wallet.model.UnSpentInf;
import com.nxquant.exchange.wallet.omni.OmniApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Administrator on 2018-09-25.
 */
public class OmniCollect {
    private static Logger logger = LoggerFactory.getLogger(OmniCollect.class);
    DataStoreInterface DataStore;
    private static Properties props = new Properties();
    private  FileOperator fileOperator = new FileOperator();

    private static String omniip;
    private static String omniuser;
    private static String omnipwd;

    private static OmniApi omniApi  = null;

    private static double Threshold_Amount = 100;

    private static String mainAddress = "18rCGFxafk4MJCrMLxkKGuUopQqf5E8qCS";

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

        omniip = props.getProperty("OMNIServer");
        omniuser = props.getProperty("OMNIUser");
        omnipwd = props.getProperty("OMNIPwd");
        omniApi = new OmniApi();
        boolean connected = omniApi.createConnection(omniuser,omnipwd,omniip);
    }

    public ArrayList<String> getOmniAddress(){
        ArrayList<String> addressList = new   ArrayList<String>();
        ArrayList<UnSpentInf> UnSpentInfList =  omniApi.listAllUnSpent(3);
        for (int i = 1; i < UnSpentInfList.size(); i++) {
            addressList.add(UnSpentInfList.get(i).getAddress());
        }

        return addressList;
    }

    public double getOmniBalance(String addr, int propertyid){
        BigDecimal balance = omniApi.getBalance(addr, propertyid);
        if(balance == null){
            System.out.println("com.unifex.chainapi.omni wallet get balance error: "+omniApi.getErrorMsg());
            return -1;
        }

        return balance.doubleValue();
    }

    public void Sleep(){
        try{
            Thread.sleep(1000);
        }catch (Exception ex){
        }
    }

    public void startCollect(){
        BufferedWriter failedOut =  fileOperator.createETALLFile("usdt_collect_failed-");
        BufferedWriter successOut =  fileOperator.createETALLFile("usdt_collect_success-");

        int MAXTX = 50;

        ArrayList<String> addList =  getOmniAddress();
        for(int i=0; i<addList.size() && MAXTX > 0; i++){
            if(mainAddress.compareTo(addList.get(i)) == 0){
                continue;
            }

            MAXTX--;

            double balance =  getOmniBalance(addList.get(i), 31);
            System.out.println(addList.get(i) + ", " +balance);

            if(balance >= Threshold_Amount && balance <= 20000) {
                String txid = omniApi.transferByFeeAddress(addList.get(i), mainAddress, mainAddress,  balance, 31);
                if (txid == null) {
                    String errmsg = "transfer :"+addList.get(i)  +", ErrMsg:"+ omniApi.getErrorMsg()+"\n";
                    fileOperator.writeContent(failedOut, errmsg);
                    continue;
                }

                String succmsg = "transfer :"+addList.get(i) +",balance:" + String.valueOf(balance) + ",txid:" +txid+"\n";
                fileOperator.writeContent(successOut, succmsg);
            }

            Sleep();
        }

        try{
            failedOut.close();
            successOut.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        OmniCollect omniCollect = new OmniCollect();

        long timer = 3600 * 6;

        while(true) {
            omniCollect.startCollect();

            try{
                Thread.sleep(timer * 1000);
            }catch (Exception ex){

            }
        }

    }
}

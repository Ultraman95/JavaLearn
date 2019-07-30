package com.nxquant.exchange.wallet.cli;

import com.nxquant.exchange.wallet.bitcoin.BitCoinApi;
import com.nxquant.exchange.wallet.eth.EthApi;
import com.nxquant.exchange.wallet.omni.OmniApi;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Administrator on 2018-08-06.
 */
public class CoinOperator {
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

    public static void setBtcAccount(){
        ArrayList<String> addreses = new  ArrayList<String>();
        readFile(addreses, "./btc.txt");

        for(int i=0; i<addreses.size(); i++){
            Boolean ret = btcApi.setAccountForAddress(addreses.get(i), "");
            if(ret == null){
                System.out.println("btc set account error:"+addreses.get(i));
            }

            System.out.println("btc set account:"+addreses.get(i));
        }
    }

    public static void setUsdtAccount(){
        ArrayList<String> addreses = new  ArrayList<String>();
        readFile(addreses, "./usdt.txt");

        for(int i=0; i<addreses.size(); i++){
            Boolean ret = omniApi.setAccountForAddress(addreses.get(i), "");
            if(ret == null){
                System.out.println("ustd set account error:"+addreses.get(i));
            }
        }
    }

    public static void readFile(ArrayList<String> addreses, String filePath){
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                System.out.println(tempString);
                addreses.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String line = reader.readLine();
            if (line == null || line.trim().equals(""))
                return;

            String[] vec = line.split(" ");
            char command = vec[0].charAt(0);

            switch (command) {
                case '1': {
                    setBtcAccount();
                    break;
                }
                case '2': {
                    setUsdtAccount();
                    break;
                }
                default:break;
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

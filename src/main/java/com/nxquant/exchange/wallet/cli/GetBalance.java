package com.nxquant.exchange.wallet.cli;

import com.nxquant.exchange.wallet.model.UnSpentInf;
import com.nxquant.exchange.wallet.bitcoin.BitCoinApi;
import com.nxquant.exchange.wallet.eth.EthApi;
import com.nxquant.exchange.wallet.omni.OmniApi;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.*;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Properties;

public class GetBalance {

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

    public static void printHelp() {
        System.out.println("--------------------------------------------------");
        System.out.println("1: get btc balance, usage: 1 address1 address2");
        System.out.println("2: get btc balance by unspent, usage: 2");
        System.out.println("3: get com.unifex.chainapi.eth balance by addressese, usage: 3 address1 address2");
        System.out.println("4: get usdt balance by addressese, usage: 4 address1 address2");
        System.out.println("--------------------------------------------------");
    }

    public static void getBtcBalance(){
        BigDecimal balance = btcApi.getWalletBalance(3);
        if(balance == null){
            System.out.println("btc wallet get balance error: "+btcApi.getErrorMsg());
        }
        else
        {
            System.out.println("btc wallet balance: "+balance);
        }
    }

    public static void getBtcBalanceByUnspend(){
        double balance = 0;
        ArrayList<UnSpentInf>  UnSpentInfList = btcApi.listAllUnSpent(1);
        if(UnSpentInfList == null){
            System.out.println("btc wallet get balance error: "+btcApi.getErrorMsg());
        }

        for(int i=0; i<UnSpentInfList.size(); i++){
            balance += UnSpentInfList.get(i).getAmount();
        }
        System.out.println("btc wallet balance: "+balance);
    }

    public static void getETHBalanceByAddress( String[] addrs){
        BigDecimal balance;
        for (int i = 1; i < addrs.length; i++) {
            balance = ethApi.getbalance(addrs[i]);
            System.out.println("com.unifex.chainapi.eth address:"+addrs[i]+" ,balance:"+balance);
        }
    }

    public static void getOmniBalance( String[] addrs){
        for (int i = 1; i < addrs.length; i++) {
            BigDecimal  balance = omniApi.getBalance(addrs[i],31);
            if(balance == null){
                System.out.println("com.unifex.chainapi.omni wallet get balance error: "+omniApi.getErrorMsg());
            }
            else
            {
                System.out.println("com.unifex.chainapi.omni wallet address:"+addrs[i]+" ,balance: "+balance);
            }
        }
    }

    public static void main(String[] args) {

        printHelp();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                String line = reader.readLine();
                if (line == null || line.trim().equals(""))
                    continue;
                String[] vec = line.split(" ");
                char command = vec[0].charAt(0);

                switch (command) {
                    case '1': {
                        getBtcBalance();
                        break;
                    }
                    case '2': {
                        getBtcBalanceByUnspend();
                        break;
                    }
                    case '3': {
                        getETHBalanceByAddress(vec);
                        break;
                    }
                    case '4': {
                        getOmniBalance(vec);
                        break;
                    }
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Please input new Command : ");
        }
    }
}

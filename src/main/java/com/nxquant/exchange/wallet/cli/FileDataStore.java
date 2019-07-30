package com.nxquant.exchange.wallet.cli;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2018-08-03.
 */
public class FileDataStore implements DataStoreInterface {
    public String btcPath = "";
    public String usdtpath = "";
    public String ethpath = "";
    public String staoreData = "./data/";
    public BufferedWriter out = null;

    public FileDataStore(String staoreData){
        this.staoreData = staoreData;
    }

    public void init(ArrayList<String> properties) {
        btcPath = properties.get(0);
        usdtpath = properties.get(1);
        ethpath = properties.get(2);
    }

    public void readAddress(){
        readFile(btcAddress);
        readFile(ethAddress);
        readFile(usdtAddress);
    }

    public void storeBtc(double amount){
        String filePath = staoreData + "btc_" + getDateTime();
        createFile(filePath);
        writeFile(filePath, String.valueOf(amount));
    }

    public void storeEth(String address, double amount){
        String filePath = staoreData + "eth_" + getDateTime();
        createFile(filePath);
        writeFile(filePath, address + ":"+String.valueOf(amount));
    }

    public void storeEth(double amount){
        String filePath = staoreData + "eth_totla_" + getDateTime();
        createFile(filePath);
        writeFile(filePath, String.valueOf(amount));
    }

    public void storeUsdt(String address, double amount){
        String filePath = staoreData + "usdt_" + getDateTime();
        createFile(filePath);
        writeFile(filePath, address + ":"+String.valueOf(amount));
    }

    public void storeUsdt(double amount){
        String filePath = staoreData + "usdt_total_" + getDateTime();
        createFile(filePath);
        writeFile(filePath, String.valueOf(amount));
    }

    private void readFile(ArrayList<String> addreses){
        File file = new File(btcPath);
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

    public static void writeFile(String filePath, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            System.out.println("file exits");
        } else {
            try {
                File fileParent = file.getParentFile();
                if (fileParent != null) {
                    if (!fileParent.exists()) {
                        fileParent.mkdirs();
                    }
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createETALLFile(){
        String filePath = staoreData +"eth_all_" + getDateTime();
        createFile(filePath);

        try {
            this.out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeContent(String content){

        try {
            this.out.write(content);
            this.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDateTime(){
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return dateFormat.format(calendar.getTime());
    }
}

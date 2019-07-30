package com.nxquant.exchange.wallet.cli;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2018-09-12.
 */
public class FileOperator {

    public String staoreData = "./transferlog/";

    public boolean readAddressFile(String path, ArrayList<String> addreses, ArrayList<String> passwrd){
        File file = new File(path);
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] account = tempString.split(",");
                if(account == null || account.length != 2){
                    System.out.println("readAddressFile error data in line");
                    continue;
                }

                addreses.add(account[0]);
                passwrd.add(account[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return true;
    }

    public boolean readContractFile(String path, ArrayList<String> contractAddres, ArrayList<Long> contractThreshold, ArrayList<Integer>  contractDecimal){
        File file = new File(path);
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] account = tempString.split(",");
                if(account == null || account.length != 3){
                    System.out.println("readContractFile error data in line");
                    continue;
                }

                contractAddres.add(account[0]);

                Long cthreshold = Long.valueOf(account[1]);
                contractThreshold.add(cthreshold);

                Integer dec = Integer.valueOf(account[2]);
                contractDecimal.add(dec);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return true;
    }

    public boolean readToAddressFile(String path, ArrayList<String> toAddreses, ArrayList<Double> ethValue){
        File file = new File(path);
        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {

                String[] account = tempString.split(",");
                if(account == null || account.length != 2){
                    System.out.println("readEthAddressFile error data in line");
                    continue;
                }

                toAddreses.add(account[0]);
                double v = Double.valueOf(account[1]);
                ethValue.add(v);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return true;
    }

    public BufferedWriter createETALLFile(String name){
        String filePath = staoreData + name + getDateTime();
        createFile(filePath);
        BufferedWriter out= null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return out;
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

    public void writeContent(BufferedWriter out, String content){
        try {
            out.write(content);
            out.flush();
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
